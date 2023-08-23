package org.teach.study.boot;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.quincy.rock.core.dao.CommonOperate;
import org.quincy.rock.core.vo.Sort;
import org.teach.study.boot.provider.MetaDao;

/**
 * <b>Dao。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public interface Dao<T extends Entity> extends CommonOperate<T>, MetaDao<T> {

	default Class<T> entityType() {
		Class clazz = (Class) this.getClass().getGenericInterfaces()[0];
		ParameterizedType type = (ParameterizedType) clazz.getGenericInterfaces()[0];
		Type[] types = type.getActualTypeArguments();
		return (Class<T>) types[0];
	}

	@Override
	default int insert(T entity, boolean ignoreNullValue) {
		return this._insertOne(entity, ignoreNullValue);
	}

	@Override
	default int update(T entity, boolean ignoreNullValue) {
		return this._updateOne(entity, ignoreNullValue);
	}

	@Override
	default int updateMap(Map<String, Object> voMap) {
		return this._updateSpecific(entityType(), voMap);
	}

	@Override
	default int delete(long id) {
		return this._deleteOne(entityType(), id);
	}

	@Override
	default int deleteMore(List<Long> ids) {
		return this._deleteMore(entityType(), ids);
	}

	@Override
	default long deleteAll() {
		return this._deleteAll(entityType());
	}

	@Override
	default long total() {
		return this._total(entityType());
	}

	@Override
	default int count(List<Long> ids) {
		return this._count(entityType(), ids);
	}

	@Override
	default boolean exist(long id) {
		return this._exist(entityType(), id);
	}

	@Override
	default boolean existByName(String name, Object value, Long ignoreId) {
		return this._existByName(entityType(), name, value, ignoreId);
	}

	@Override
	default T findOne(long id) {
		return _findOne(entityType(), id);
	}

	@Override
	default List<T> findAll(Sort sort) {
		return _findAll(entityType(), sort);
	}

	@Override
	default T findByName(String name, Object value) {
		return _findOneByName(entityType(), name, value);
	}

	@Override
	default List<T> findAllByName(String name, Object value, Sort sort) {
		return _findAllByName(entityType(), name, value, sort);
	}

	@Override
	default List<T> findMore(List<Long> ids) {
		return _findMore(entityType(), ids);
	}
}
