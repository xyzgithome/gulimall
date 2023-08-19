package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(CategoryEntity category) {
        this.updateById(category);
        // 同步更新其他关联数据
        if (StringUtils.isNotBlank(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    @Override
    public List<CategoryEntity> getLevelCategoryList() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
    }

    /**
     * 逻辑是
     * （1）根据一级分类，找到对应的二级分类
     * （2）将得到的二级分类，封装到Catelog2Vo中
     * （3）根据二级分类，得到对应的三级分类
     * （3）将三级分类封装到Catalog3List
     *
     * @return
     */
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        //1. 查出所有1级分类
        List<CategoryEntity> categoryOneList = getLevelCategoryList();

        //2. 封装数据
        return categoryOneList.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 根据一级分类id查询二级分类
            List<CategoryEntity> categoryTwoList = baseMapper.selectList(
                    new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));

            if (CollectionUtils.isEmpty(categoryTwoList)) {
                return Collections.emptyList();
            }

            // 封装上面的结果
            return categoryTwoList.stream().map(c2 -> {
                Catelog2Vo catelog2Vo = new Catelog2Vo(
                        v.getCatId().toString(), null, c2.getCatId().toString(), c2.getName());

                //1、找当前二级分类的三级分类封装vo
                List<CategoryEntity> categoryThreeList = baseMapper.selectList(
                        new QueryWrapper<CategoryEntity>().eq("parent_cid", c2.getCatId()));
                if (CollectionUtils.isEmpty(categoryThreeList)) {
                    return catelog2Vo;
                }

                List<Catelog2Vo.Catalog3Vo> categoryThreeVoList = categoryThreeList.stream().map(c3 ->
                        new Catelog2Vo.Catalog3Vo(c2.getCatId().toString(), String.valueOf(c3.getCatId()), c3.getName()))
                        .collect(Collectors.toList());

                catelog2Vo.setCatalog3List(categoryThreeVoList);

                return catelog2Vo;
            }).collect(Collectors.toList());
        }));
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