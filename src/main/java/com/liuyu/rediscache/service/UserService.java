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

/**
 * 参数选项：
 *	 	@Cacheable：根据方法的请求参数对其结果进行缓存，即第一次从数据库查询真实的数据
 * 	@CachePut：根据方法的请求参数对其结果进行缓存；和Cacheable不同的是，每次都会触发真实的方法调用 
 * 	@CachEvict:根据方法的请求参数对缓存进行清空
 * @author liuyu
 *
 */
@Service("userService")
@Transactional
public class UserService {

	@Autowired
	private UserDao userDao;

	/**
	 * 添加用户时，直接将key对应的数据存入到缓存
	 * @param user
	 */
//	@CacheEvict(value = "user", key = "'all'")
//	@CachePut(value = "user", key = "#user.id")
	public void addUser(User user) {
		userDao.save(user);
	}

	/**
	 * 如果缓存中有key对应的数据，则从缓存中取；如果没有则查询数据库
	 * @param id
	 * @return
	 */
	@Cacheable(value = "user", key = "#id")
	public User findById(String id) {
		return userDao.findById(id, User.class);
	}
	
//	@Cacheable(value = "user", key = "'all'")
	public List<User> list() {
		return userDao.list("from User");
	}

	/**
	 * 修改用户时，直接将key对应的数据更新到缓存
	 * @param user
	 */
//	@CacheEvict(value = "user", key="'all'")
	@CacheEvict(value = "user", key = "#user.id")
	public void updateUser(User user) {
		userDao.update(user);
	}

	/**
	 * 删除用户时，清空key对应的缓存
	 * @param id
	 * @return
	 */
//	@CacheEvict(value = "user", key = "'all'")
	@CacheEvict(value = "user", key = "#id")
	public String deleteUser(String id) {
		User user = userDao.findById(id, User.class);
		userDao.delete(user);
		return "success";
	}



}
