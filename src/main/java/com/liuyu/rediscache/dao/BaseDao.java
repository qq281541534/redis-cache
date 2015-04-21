package com.liuyu.rediscache.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository("baseDao")
public class BaseDao<T>  {

	@Autowired
	private HibernateTemplate hibernateTemplate;

	public int getTotalCount(String hql) {
		return hibernateTemplate.find(hql).size();
	}

	public void save(T t) {
		hibernateTemplate.save(t);
	}
	public void update(T t) {
		hibernateTemplate.update(t);
	}

	public void saveOrUpdate(T t) {
		hibernateTemplate.saveOrUpdate(t);
	}

	public void delete(T t) {
		hibernateTemplate.delete(t);
	}

	@SuppressWarnings("unchecked")
	public List<T> list(String hql) {
		return hibernateTemplate.find(hql);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public T findById(String id, Class clazz) {
		if (id == null || "".equals(id.trim()))
			return null;
		if (clazz == null)
			throw new RuntimeException("查询主体不能为空");
		String hql = "from " + clazz.getSimpleName() + " t where t.id=? ";
		List<T> list = hibernateTemplate.find(hql, id);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T lockByid(String id, Class clazz) {
		if (id == null || "".equals(id.trim()))
			return null;
		if (clazz == null)
			throw new RuntimeException("查询主体不能为空");
		String hql = "from " + clazz.getSimpleName()
				+ " t where t.id=? for update";
		List<T> list =  hibernateTemplate.find(hql, id);
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}


}
