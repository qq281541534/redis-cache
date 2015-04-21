package com.liuyu.rediscache.dao;

import org.springframework.stereotype.Repository;

import com.liuyu.rediscache.entity.User;

@Repository("userDao")
public class UserDao extends BaseDao<User>  {

}
