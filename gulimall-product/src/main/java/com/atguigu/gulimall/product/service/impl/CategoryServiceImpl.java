package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> allList = baseMapper.selectList(null);

        if (CollectionUtils.isEmpty(allList)) {
            return new ArrayList<>();
        }

        return allList.stream().filter(CategoryEntity::isFirstCategory)
                .map(root -> root.childrenSet(allList))
                .sorted(Comparator.comparing(CategoryEntity::sorted))
                .collect(Collectors.toList());
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查当前删除的菜单是否被其他地方引用
        // 逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] queryCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> pathList = getPathList(catelogId, paths);

        Collections.reverse(pathList);

        return pathList.toArray(new Long[0]);
    }

    private List<Long> getPathList(Long catelogId, List<Long> paths) {
        paths.add(catelogId);

        CategoryEntity category = this.getById(catelogId);

        if (category.getParentCid() != 0) {
            getPathList(category.getParentCid(), paths);
        }
        return paths;
    }

}