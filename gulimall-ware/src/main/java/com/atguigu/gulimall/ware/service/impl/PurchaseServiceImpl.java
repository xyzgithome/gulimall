package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.constant.WareConstant;
import com.atguigu.gulimall.ware.entity.PurchaseDetailEntity;
import com.atguigu.gulimall.ware.service.PurchaseDetailService;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.atguigu.gulimall.ware.vo.MergeVO;
import com.atguigu.gulimall.ware.vo.PurchaseDetailVO;
import com.atguigu.gulimall.ware.vo.PurchaseDoneVO;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.PurchaseDao;
import com.atguigu.gulimall.ware.entity.PurchaseEntity;
import com.atguigu.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    private PurchaseDetailService purchaseDetailService;

    @Autowired
    private WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        QueryWrapper<PurchaseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0).or().eq("status", 1);
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergePurchase(MergeVO mergeVo) {
        if (Objects.isNull(mergeVo.getPurchaseId())) {
            //1、新建一个
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseDetailService.batchUpdatePurchaseDetail(purchaseEntity.getId(), mergeVo.getItems());
            return;
        }

        // 确认采购单状态是0,1才可以合并
        PurchaseEntity purchase = this.getById(mergeVo.getPurchaseId());
        if (Objects.equals(WareConstant.PurchaseStatusEnum.CREATED.getCode(), purchase.getStatus())
                || Objects.equals(WareConstant.PurchaseStatusEnum.ASSIGNED.getCode(), purchase.getStatus())) {
            purchaseDetailService.batchUpdatePurchaseDetail(mergeVo.getPurchaseId(), mergeVo.getItems());
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setId(mergeVo.getPurchaseId());
            purchaseEntity.setUpdateTime(new Date());
            this.updateById(purchaseEntity);
        }
    }

    @Override
    public void received(List<Long> ids) {
        List<PurchaseEntity> purchaseList = this.listByIds(ids).stream()
//                TODO 验证采购单是否是自己的
//                .filter()
                // 保留当前采购单状态是新建或者已分配
                .filter(item ->
                        item.getStatus() == WareConstant.PurchaseStatusEnum.CREATED.getCode()
                                || item.getStatus() == WareConstant.PurchaseStatusEnum.ASSIGNED.getCode())
                .peek(item -> {
                    item.setStatus(WareConstant.PurchaseStatusEnum.RECEIVE.getCode());
                    item.setUpdateTime(new Date());
                }).collect(Collectors.toList());

        this.updateBatchById(purchaseList);

        //3、改变采购单采购项的状态
        purchaseList.forEach((item) -> {
            List<PurchaseDetailEntity> purchaseDetailList = purchaseDetailService
                    .list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", item.getId()));

            List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailList.stream().map(detail -> {
                PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
                detailEntity.setId(detail.getId());
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
                return detailEntity;
            }).collect(Collectors.toList());
            purchaseDetailService.updateBatchById(purchaseDetailEntityList);
        });
    }

    @Transactional
    @Override
    public void done(PurchaseDoneVO doneVo) {
        //2、改变采购项的状态
        boolean flag = true;
        List<PurchaseDetailVO> items = doneVo.getItems();
        List<PurchaseDetailEntity> purchaseDetailEntityList = new ArrayList<>();
        for (PurchaseDetailVO item : items) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            if (item.getStatus() == WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode()) {
                flag = false;
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode());
            } else {
                detailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.FINISH.getCode());
                //3、将成功采购的进行入库
                PurchaseDetailEntity entity = purchaseDetailService.getById(item.getItemId());
                wareSkuService.addStock(entity.getSkuId(), entity.getWareId(), entity.getSkuNum());
            }
            detailEntity.setId(item.getItemId());
            purchaseDetailEntityList.add(detailEntity);
        }
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        //1、改变采购单状态
        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(doneVo.getId());
        // 有一个采购需求失败，则整个采购单失败
        purchaseEntity.setStatus(
                flag ? WareConstant.PurchaseStatusEnum.FINISH.getCode()
                : WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        purchaseEntity.setUpdateTime(new Date());
        this.updateById(purchaseEntity);
    }
}