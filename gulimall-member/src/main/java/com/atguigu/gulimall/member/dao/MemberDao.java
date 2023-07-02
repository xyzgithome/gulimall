package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author xyz
 * @email xyz@gmail.com
 * @date 2023-07-02 10:50:15
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
