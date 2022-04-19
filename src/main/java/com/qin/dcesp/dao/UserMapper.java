package com.qin.dcesp.dao;

import com.qin.dcesp.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User searchUserById(int id);

    User searchUserByName(String username);

    int insertUser(User user);

    int updatePassword(int id,String password);
}
