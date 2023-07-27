package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.BrandDao;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");

        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            wrapper.eq("brand_id", key).or().like("name", key);
        }

        IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBrand(BrandEntity brand) {
        // 保证品牌冗余字段数据的一致性
        this.updateById(brand);
        if (StringUtils.isNotBlank(brand.getName())) {
            // 同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());

            // TODO 更新其他关联的数据一致性
        }
    }

}