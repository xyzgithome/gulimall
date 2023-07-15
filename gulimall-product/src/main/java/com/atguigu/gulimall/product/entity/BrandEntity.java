package com.atguigu.gulimall.product.entity;

import com.atguigu.common.valid.AddGroup;
import com.atguigu.common.valid.UpdateGroup;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-01 23:22:58
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 品牌id
	 */
	@TableId
	@NotNull(message = "修改品牌id不能为空", groups = {UpdateGroup.class})
	@Null(message = "新增品牌id必须为空", groups = {AddGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名不能为空", groups = {UpdateGroup.class, AddGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "品牌logo地址不能为空", groups = {AddGroup.class})
	@URL(message = "品牌logo地址必须是一个合法的url", groups = {AddGroup.class, UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(message = "显示状态不能为空", groups = {AddGroup.class})
	@Range(min = 0, max = 1, message = "显示状态必须等于0或者1", groups = {AddGroup.class, UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "检索首字母不能为空", groups = {AddGroup.class})
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个字母", groups = {AddGroup.class, UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(message = "排序不能为空", groups = {AddGroup.class})
	@Min(value = 0, message = "排序必须大于等于0", groups = {AddGroup.class, UpdateGroup.class})
	private Integer sort;
}
