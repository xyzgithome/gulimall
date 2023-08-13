package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.dto.es.AttrEsModel;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.ProductAttrValueDao;
import com.atguigu.gulimall.product.entity.ProductAttrValueEntity;
import com.atguigu.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {
    @Autowired
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<AttrEsModel> getSearchableAttrListForSpu(Long spuId) {
        List<ProductAttrValueEntity> baseAttrList =
                this.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));

        List<Long> attrIdList = baseAttrList.stream().map(ProductAttrValueEntity::getAttrId)
                .distinct().collect(Collectors.toList());

        List<Long> searchableAttrIdList = attrService.getSearchableAttrList(attrIdList);

        return baseAttrList.stream()
                .filter(item -> searchableAttrIdList.contains(item.getAttrId()))
                .map(item -> {
                    AttrEsModel attrEsModel = new AttrEsModel();
                    BeanUtils.copyProperties(item, attrEsModel);
                    return attrEsModel;
                }).collect(Collectors.toList());
    }

}