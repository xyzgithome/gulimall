package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.CategoryEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-01 23:22:58
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryEntity> listWithTree();

    void removeMenuByIds(List<Long> asList);

    /**
     * 找到分类信息的完整路径
     *
     * @param catelogId 分类id
     * @return [父categoryId, 子categoryId, 孙categoryId]
     */
    Long[] queryCatelogPath(Long catelogId);

    void updateCategory(CategoryEntity category);
}

