package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.dto.SkuReductionDTO;
import com.atguigu.common.dto.SpuBoundDTO;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.controller.ProductAttrValueController;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.spu.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.xml.internal.bind.v2.TODO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SpuImagesService spuImagesService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private CouponFeignService couponFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SpuSaveVO vo) {
        //1、保存spu基本信息    pms_spu_info
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        this.save(spuInfoEntity);

        //2、保存spu的描述图片  pms_spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(spuInfoEntity.getId());
        //将所有图片描述拼接起来，用逗号隔开
        descEntity.setDecript(String.join(",", decript));
        spuInfoDescService.save(descEntity);

        //3、保存spu的图片集    pms_spu_images
        spuImagesService.saveImages(spuInfoEntity.getId(), vo.getImages());

        //4、保存spu的规格参数  pms_product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(attr.getAttrId());
            AttrEntity byId = attrService.getById(attr.getAttrId());
            valueEntity.setAttrName(byId.getAttrName());
            valueEntity.setAttrValue(attr.getAttrValues());
            valueEntity.setQuickShow(attr.getShowDesc());
            valueEntity.setSpuId(spuInfoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);

        // 5、保存spu的积分信息  gulimall_sms -> sms_spu_bounds
        SpuBoundDTO spuBoundDTO = new SpuBoundDTO();
        BeanUtils.copyProperties(vo.getBounds(), spuBoundDTO);
        spuBoundDTO.setSpuId(spuInfoEntity.getId());
        // TODO 高级部分-分布式事务保证数据一致性
        if (couponFeignService.save(spuBoundDTO).getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }

        //6、保存当前spu对应的所有sku信息
        //6.1)、sku的基本信息  pms_sku_info
        List<Skus> skuList = vo.getSkus();
        if (CollectionUtils.isEmpty(skuList)) {
            return;
        }
        skuList.forEach(item -> {
            // 设置默认图片
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(item, skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
            skuInfoEntity.setSaleCount(0L);
            skuInfoEntity.setSpuId(spuInfoEntity.getId());
            skuInfoEntity.setSkuDefaultImg(getDefaultImage(item));
            skuInfoService.save(skuInfoEntity);

            //6.2)、sku的图片信息  pms_sku_images
            Long skuId = skuInfoEntity.getSkuId();
            List<SkuImagesEntity> imagesEntityList = item.getImages().stream()
                    .filter(img -> StringUtils.isNotBlank(img.getImgUrl()))
                    .map(img -> {
                        SkuImagesEntity imagesEntity = new SkuImagesEntity();
                        imagesEntity.setSkuId(skuId);
                        imagesEntity.setImgUrl(img.getImgUrl());
                        imagesEntity.setDefaultImg(img.getDefaultImg());
                        return imagesEntity;
                    }).collect(Collectors.toList());
            skuImagesService.saveBatch(imagesEntityList);

            //6.3)、sku的销售属性信息   pms_sku_sale_attr_value
            List<Attr> attrList = item.getAttr();
            List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attrList.stream().map(attr -> {
                SkuSaleAttrValueEntity saleAttrValueEntity = new SkuSaleAttrValueEntity();
                BeanUtils.copyProperties(attr, saleAttrValueEntity);
                saleAttrValueEntity.setSkuId(skuId);
                return saleAttrValueEntity;
            }).collect(Collectors.toList());
            skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

            // 6.4)、sku的优惠、满减等信息   gulimall_sms -> sms_sku_ladder/sms_sku_full_reduction/sms_member_price
            SkuReductionDTO skuReductionDTO = new SkuReductionDTO();
            BeanUtils.copyProperties(item, skuReductionDTO);
            skuReductionDTO.setSkuId(skuId);
            // TODO 高级部分-分布式事务保证数据一致性
            if (couponFeignService.saveInfo(skuReductionDTO).getCode() != 0) {
                log.error("远程保存sku优惠信息失败");
            }
        });
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{w.eq("id",key).or().like("spu_name",key);});
        }
        // status=1 and (id=1 or spu_name like xxx)
        String status = (String) params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }

        String brandId = (String) params.get("brandId");
        if(!StringUtils.isEmpty(brandId)&&!"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)&&!"0".equalsIgnoreCase(catelogId)){
            wrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

    private String getDefaultImage(Skus item) {
        String defaultImg = "";
        for (Images image : item.getImages()) {
            if (image.getDefaultImg() == 1) {
                defaultImg = image.getImgUrl();
                break;
            }
        }
        return defaultImg;
    }
}