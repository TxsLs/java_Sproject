package org.teach.study.boot;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.quincy.rock.core.dao.CommonOperate;
import org.quincy.rock.core.dao.sql.Predicate;
import org.quincy.rock.core.dao.sql.Sort;
import org.quincy.rock.core.vo.PageSet;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

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
		return _insertOne(entity, ignoreNullValue);
	}

	@Override
	default int update(T entity, boolean ignoreNullValue, Predicate where) {
		return _updateOne(entity, ignoreNullValue, where);
	}

	@Override
	default int updateMap(Map<String, Object> voMap, Predicate where) {
		return _updateSpecific(entityType(), voMap, where);
	}

	@Override
	default int delete(long id) {
		return _deleteOne(entityType(), id);
	}

	@Override
	default int deleteMore(List<Long> ids) {
		return _deleteMore(entityType(), ids);
	}

	@Override
	default long deleteAll(Predicate where) {
		return _deleteAll(entityType(), where);
	}

	@Override
	default long count(Predicate where) {
		return _count(entityType(), where);
	}

	@Override
	default boolean exist(long id) {
		return _exist(entityType(), id);
	}

	@Override
	default boolean existByName(String name, Object value, Long ignoreId) {
		return _existByName(entityType(), name, value, ignoreId);
	}

	@Override
	default boolean existByWhere(Predicate where) {
		return _existByWhere(entityType(), where);
	}

	@Override
	default T findOne(long id) {
		return _findOne(entityType(), id);
	}

	@Override
	default List<T> findMore(List<Long> ids) {
		return _findMore(entityType(), ids);
	}

	@Override
	default List<T> findAll(Predicate where, Sort sort) {
		return _findAll(entityType(), where, sort);
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
	default PageSet<T> findPage(Predicate where, Sort sort, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<T> page = _findPage(entityType(), where, sort);
		return AppUtils.toPageSet(page);
	}

}
