package com.liuyu.rediscache.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.liuyu.rediscache.dao.UserDao;
import com.liuyu.rediscache.entity.User;

@Service("userService")
@Transactional
public class UserService {

	@Autowired
	private UserDao userDao;

//	@CacheEvict(value = "user", key = "'all'")
	@CachePut(value = "user", key = "#user.id")
	public void addUser(User user) {
		userDao.save(user);
	}

	@Cacheable(value = "user", key = "#id")
	public User findById(String id) {
		return userDao.findById(id, User.class);
	}
	
//	@Cacheable(value = "user", key = "'all'")
	public List<User> list() {
		return userDao.list("from User");
	}

//	@CacheEvict(value = "user", key="'all'")
	@CachePut(value = "user", key = "#user.id")
	public void updateUser(User user) {
		userDao.update(user);
	}

//	@CacheEvict(value = "user", key = "'all'")
	@CacheEvict(value = "user", key = "#id")
	public String deleteUser(String id) {
		User user = userDao.findById(id, User.class);
		userDao.delete(user);
		return "success";
	}



}
