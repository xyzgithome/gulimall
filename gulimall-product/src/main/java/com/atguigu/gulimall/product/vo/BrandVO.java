package com.atguigu.gulimall.product.vo;

import com.atguigu.common.valid.group.AddGroup;
import com.atguigu.common.valid.group.UpdateGroup;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.Serializable;

/**
 * 商品属性
 * 
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-01 23:22:58
 */
@Data
public class BrandVO implements Serializable {
	/**
	 * 品牌id
	 */
	private Long brandId;

	/**
	 * 品牌名
	 */
	private String brandName;

}
