package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.AttrRelationReqVO;
import com.atguigu.gulimall.product.vo.AttrRspVO;
import com.atguigu.gulimall.product.vo.AttrVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-01 23:22:58
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVO attr);

    PageUtils queryBaseAttrPageList(Map<String, Object> params, Long catelogId, String type);

    AttrRspVO getAttrInfo(Long attrId);

    void updateAttr(AttrVO attr);

    List<AttrEntity> attrRelation(String attrgroupId);

    void deleteAttrRelation(AttrRelationReqVO[] attrVOS);
}

