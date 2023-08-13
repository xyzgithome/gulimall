package com.atguigu.gulimall.ware.service.impl;

import com.atguigu.common.dto.SkuHasStockDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.ware.dao.WareSkuDao;
import com.atguigu.gulimall.ware.entity.WareSkuEntity;
import com.atguigu.gulimall.ware.service.WareSkuService;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            queryWapper.eq("sku_id", skuId);
        }

        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            queryWapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWapper);

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1、判断如果还没有库存记录新增
        WareSkuEntity wareSku = this.getOne(new QueryWrapper<WareSkuEntity>()
                .eq("sku_id", skuId).eq("ware_id", wareId));

        if (Objects.isNull(wareSku)) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            this.save(wareSkuEntity);
            return;
        }

        wareSku.setStock(wareSku.getStock() + skuNum);
        this.updateById(wareSku);
    }

    @Override
    public List<SkuHasStockDTO> getSkusHasStock(List<Long> skuIds) {
        Map<Long, List<WareSkuEntity>> wareSkuMap = this.list(
                new QueryWrapper<WareSkuEntity>().in("sku_id", skuIds))
                .stream().distinct().collect(Collectors.groupingBy(WareSkuEntity::getSkuId));

        return skuIds.stream().map(skuId -> {
            SkuHasStockDTO vo = new SkuHasStockDTO();
            vo.setSkuId(skuId);
            vo.setHasStock(hasStock(wareSkuMap.get(skuId)));
            return vo;
        }).collect(Collectors.toList());
    }

    private Boolean hasStock (List<WareSkuEntity> wareSkuEntityList) {
        if (CollectionUtils.isEmpty(wareSkuEntityList)) {
            return false;
        }

        return wareSkuEntityList.stream().mapToInt(item -> item.getStock() - item.getStockLocked()).sum() > 0;
    }




}