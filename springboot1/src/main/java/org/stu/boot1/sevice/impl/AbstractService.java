package org.stu.boot1.sevice.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.stu.boot1.Dao;
import org.stu.boot1.Entity;
import org.stu.boot1.Service;

/**
 * <b>业务服务抽象基类。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public abstract class AbstractService<T extends Entity, D extends Dao<T>> implements Service<T> {

	@Autowired
	private D dao;

	protected D dao() {
		return dao;
	}

	@Override
	public List<T> findAll() {
		return dao.findAll();
	}

	@Override
	public T findOne(long id) {
		return dao.findOne(id);
	}

	@Override
	public T findByName(String name, Object value) {
		return dao.findByName(name, value);
	}

	@Override
	public int delete(long id) {
		return dao.delete(id);
	}

	@Override
	public int insert(T entity) {
		return dao.insert(entity);
	}

	@Override
	public int update(T entity) {
		return dao.update(entity);
	}
}
