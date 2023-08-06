package com.atguigu.gulimall.coupon.service.impl;

import com.atguigu.common.dto.SkuReductionDTO;
import com.atguigu.gulimall.coupon.controller.SkuFullReductionController;
import com.atguigu.gulimall.coupon.entity.MemberPriceEntity;
import com.atguigu.gulimall.coupon.entity.SkuLadderEntity;
import com.atguigu.gulimall.coupon.service.MemberPriceService;
import com.atguigu.gulimall.coupon.service.SkuLadderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.coupon.dao.SkuFullReductionDao;
import com.atguigu.gulimall.coupon.entity.SkuFullReductionEntity;
import com.atguigu.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.ApplicationScope;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    private SkuLadderService skuLadderService;

    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveInfo(SkuReductionDTO skuReductionDTO) {
        if (skuReductionDTO.getFullCount() > 0) {
            //sms_sku_ladder
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            skuLadderEntity.setSkuId(skuReductionDTO.getSkuId());
            skuLadderEntity.setFullCount(skuReductionDTO.getFullCount());
            skuLadderEntity.setDiscount(skuReductionDTO.getDiscount());
            skuLadderEntity.setAddOther(skuReductionDTO.getCountStatus());
            skuLadderService.save(skuLadderEntity);
        }

        if (skuReductionDTO.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
            //sms_sku_full_reduction
            SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionDTO, reductionEntity);
            this.save(reductionEntity);
        }

        // 3、sms_member_price
        List<MemberPriceEntity> memberPriceEntityList = skuReductionDTO.getMemberPrice().stream()
                .filter(item -> item.getPrice().compareTo(new BigDecimal(0)) > 0)
                .map(item -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuReductionDTO.getSkuId());
                    memberPriceEntity.setMemberLevelId(item.getId());
                    memberPriceEntity.setMemberLevelName(item.getName());
                    memberPriceEntity.setMemberPrice(item.getPrice());
                    memberPriceEntity.setAddOther(1);
                    return memberPriceEntity;
                }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(memberPriceEntityList)) {
            memberPriceService.saveBatch(memberPriceEntityList);
        }
    }
}