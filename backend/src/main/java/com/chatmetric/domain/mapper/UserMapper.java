package com.chatmetric.domain.mapper;

import com.chatmetric.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
  @Select("SELECT id, username, org_code, org_name FROM users WHERE id = #{id}")
  User findById(long id);
}

