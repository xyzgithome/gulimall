package com.atguigu.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 商品三级分类
 *
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-01 23:22:58
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 分类id
     */
    @TableId
    private Long catId;
    /**
     * 分类名称
     */
    private String name;
    /**
     * 父分类id
     */
    private Long parentCid;
    /**
     * 层级
     */
    private Integer catLevel;
    /**
     * 是否显示[0-不显示，1显示]
     */
    private Integer showStatus;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 图标地址
     */
    private String icon;
    /**
     * 计量单位
     */
    private String productUnit;
    /**
     * 商品数量
     */
    private Integer productCount;

    /**
     * 子分类
     */
    // 属性为null，不序列化
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    // 表示当前属性不是数据库的字段，但在项目中必须使用，这样在新增等使用bean的时候，mybatis-plus就会忽略这个，不会报错
    @TableField(exist = false)
    private List<CategoryEntity> children;

    /**
     * 判断是否是一级分类
     *
     * @return true false
     */
    public boolean isFirstCategory() {
        return Objects.equals(parentCid, 0L);
    }

    private boolean isSecondOrThirdCategory(CategoryEntity root) {
        return Objects.equals(root.getCatId(), parentCid);
    }


    public List<CategoryEntity> getChildren(List<CategoryEntity> allList) {
        return allList.stream().filter(item -> item.isSecondOrThirdCategory(this))
                .map(item -> item.childrenSet(allList))
                .sorted(Comparator.comparingInt(CategoryEntity::sorted))
                .collect(Collectors.toList());
    }

    public CategoryEntity childrenSet(List<CategoryEntity> allList) {
		this.children = getChildren(allList);
        return this;
    }

    public Integer sorted() {
    	return Objects.isNull(sort) ? 0 : sort;
    }

}
