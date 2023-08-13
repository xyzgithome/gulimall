package com.atguigu.gulimall.product.service;

import com.atguigu.gulimall.product.vo.spu.SpuSaveVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-01 23:22:58
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 发布商品保存SPU
     *
     * @param vo vo
     */
    void save(SpuSaveVO vo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);
}

