package com.atguigu.gulimall.product.service.impl;
import com.atguigu.common.constant.ProductConstant;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrAttrgroupRelationService;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.AttrRelationReqVO;
import com.atguigu.gulimall.product.vo.AttrRspVO;
import com.atguigu.gulimall.product.vo.AttrVO;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;

import static com.atguigu.common.constant.ProductConstant.AttrTypeEnum.ATTR_TYPE_BASE;
import static com.atguigu.common.constant.ProductConstant.AttrTypeEnum.ATTR_TYPE_SALE;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    private AttrAttrgroupRelationService relationService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVO attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        if (Objects.equals(attr.getAttrType(), ATTR_TYPE_SALE.getCode())) {
            return;
        }

        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationService.save(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPageList(Map<String, Object> params, Long catelogId, String type) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", ProductConstant.AttrTypeEnum.getCodeByType(type));

        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }

        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and((wrapper -> wrapper.eq("attr_id", key).or().like("attr_name", key)));
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);

        List<Long> categoryIdList = page.getRecords().stream().distinct().map(AttrEntity::getCatelogId).collect(Collectors.toList());
        Map<Long, String> categoryMap = categoryService.getBaseMapper().selectBatchIds(categoryIdList)
                .stream().collect(Collectors.toMap(CategoryEntity::getCatId, CategoryEntity::getName));

        List<AttrRspVO> attrRspVOList = page.getRecords().stream().map(item -> {
            AttrRspVO attrRspVO = new AttrRspVO();
            BeanUtils.copyProperties(item, attrRspVO);

            String categoryName = categoryMap.get(item.getCatelogId());
            if (StringUtils.isNotBlank(categoryName)) {
                attrRspVO.setCatelogName(categoryName);
            }

            if (StringUtils.equalsIgnoreCase(type, ATTR_TYPE_SALE.getType())) {
                return attrRspVO;
            }

            AttrAttrgroupRelationEntity relationEntity = relationService
                    .getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", item.getAttrId()));
            if (Objects.nonNull(relationEntity)) {
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                attrRspVO.setGroupName(attrGroupEntity.getAttrGroupName());
            }
            return attrRspVO;
        }).collect(Collectors.toList());

        pageUtils.setList(attrRspVOList);
        return pageUtils;
    }

    @Override
    public AttrRspVO getAttrInfo(Long attrId) {
        AttrEntity attrEntity = this.getById(attrId);
        AttrRspVO attrRspVO = new AttrRspVO();
        BeanUtils.copyProperties(attrEntity, attrRspVO);
        attrRspVO.setCatelogPath(categoryService.queryCatelogPath(attrEntity.getCatelogId()));
        if (Objects.equals(attrEntity.getAttrType(), ATTR_TYPE_SALE.getCode())) {
            return attrRspVO;
        }
        AttrAttrgroupRelationEntity relationEntity = relationService.getOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        if (Objects.nonNull(relationEntity)) {
            AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
            attrRspVO.setAttrGroupId(attrGroupEntity.getAttrGroupId());
        }
        return attrRspVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttr(AttrVO attr) {
        // 修改规格参数
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.updateById(attrEntity);

        if (Objects.equals(attr.getAttrType(), ATTR_TYPE_SALE.getCode())) {
            return;
        }

        // 修改分组关联
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attr.getAttrId());
        UpdateWrapper<AttrAttrgroupRelationEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("attr_id", attr.getAttrId());
        // 由于在页面 所属分组字段属于非必填字段，所以修改时可能修改分组，也可能新增
        relationService.saveOrUpdate(relationEntity, wrapper);
    }

    @Override
    public List<AttrEntity> getRelateAttr(Long attrgroupId) {
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id", attrgroupId);
        List<Long> attrIdList = relationService.list(queryWrapper).stream()
                .map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(attrIdList)) {
            return Collections.emptyList();
        }

        return (List<AttrEntity>) this.listByIds(attrIdList);
    }

    @Override
    public void deleteAttrRelation(AttrRelationReqVO[] relationReqVOS) {
        for (AttrRelationReqVO attrVO : relationReqVOS) {
            QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("attr_id", attrVO.getAttrId());
            queryWrapper.eq("attr_group_id", attrVO.getAttrGroupId());
            relationService.remove(queryWrapper);
        }
    }

    @Override
    public PageUtils queryNotRelationAttrList(Map<String, Object> params, String attrgroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrgroupId);
        if (Objects.isNull(attrGroup)) {
            return new PageUtils();
        }

        // 获取cateId对应的所有的group
        List<Long> attrGroupIdList = attrGroupService.list(new QueryWrapper<AttrGroupEntity>()
                .eq("catelog_id", attrGroup.getCatelogId())).stream().map(AttrGroupEntity::getAttrGroupId)
                .collect(Collectors.toList());

        // 根据分组id获取所有的关联属性
        List<Long> relationAttrIdList = relationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .in("attr_group_id", attrGroupIdList)).stream().map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(relationAttrIdList)) {
            return new PageUtils();
        }

        // 剔除该分类下已经被关联的基本属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", attrGroup.getCatelogId())
                .eq("attr_type", ATTR_TYPE_BASE.getCode())
                .notIn("attr_id", relationAttrIdList);

        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(w -> w.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    @Override
    public List<Long> getSearchableAttrList(List<Long> attrIdList) {
        return this.list(new QueryWrapper<AttrEntity>()
                .eq("search_type", 1).in("attr_id", attrIdList))
                .stream().map(AttrEntity::getAttrId).collect(Collectors.toList());
    }
}