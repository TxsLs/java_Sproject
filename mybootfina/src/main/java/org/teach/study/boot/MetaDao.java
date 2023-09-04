package org.teach.study.boot;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.quincy.rock.core.dao.CommonOperate;
import org.quincy.rock.core.dao.sql.Predicate;
import org.quincy.rock.core.dao.sql.Sort;
import org.quincy.rock.core.vo.PageSet;
import org.quincy.rock.core.vo.Vo;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * <b>MetaDao。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public interface MetaDao<T extends Vo<Long>> extends CommonOperate<T> {

	@DeleteProvider(type = MybatisSQLProvider.class, method = "delete")
	public int _delete(@Param("entityType") Class<T> entityType, @Param("id") long id);

	@DeleteProvider(type = MybatisSQLProvider.class, method = "deleteMore")
	public int _deleteMore(@Param("entityType") Class<T> entityType, @Param("ids") Iterable<Long> ids);

	@DeleteProvider(type = MybatisSQLProvider.class, method = "deleteAll")
	public long _deleteAll(@Param("entityType") Class<T> entityType, @Param("where") Predicate where);

	@SelectProvider(type = MybatisSQLProvider.class, method = "count")
	public long _count(@Param("entityType") Class<T> entityType, @Param("where") Predicate where);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findOne")
	public T _findOne(@Param("entityType") Class<T> entityType, @Param("id") long id);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findMore")
	public List<T> _findMore(@Param("entityType") Class<T> entityType, @Param("ids") Iterable<Long> ids);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findAll")
	public List<T> _findAll(@Param("entityType") Class<T> entityType, @Param("where") Predicate where,
			@Param("sort") Sort sort);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findAll")
	public Page<T> _findPage(@Param("entityType") Class<T> entityType, @Param("where") Predicate where,
			@Param("sort") Sort sort);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findByName")
	public T _findByName(@Param("entityType") Class<T> entityType, @Param("name") String name,
			@Param("value") Object value);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findByName")
	public List<T> _findAllByName(@Param("entityType") Class<T> entityType, @Param("name") String name,
			@Param("value") Object value, @Param("sort") Sort sort);

	@SelectProvider(type = MybatisSQLProvider.class, method = "exists")
	public boolean _exist(@Param("entityType") Class<T> entityType, @Param("id") long id);

	@SelectProvider(type = MybatisSQLProvider.class, method = "existsByName")
	public boolean _existByName(@Param("entityType") Class<T> entityType, @Param("name") String name,
			@Param("value") Object value, @Param("ignoreId") Long ignoreId);

	@SelectProvider(type = MybatisSQLProvider.class, method = "existsByWhere")
	public boolean _existByWhere(@Param("entityType") Class<T> entityType, @Param("where") Predicate where);

	@InsertProvider(type = MybatisSQLProvider.class, method = "insert")
	public int _insert(@Param("entity") T entity, @Param("ignoreNullValue") boolean ignoreNullValue);

	@UpdateProvider(type = MybatisSQLProvider.class, method = "update")
	public int _update(@Param("entity") T entity, @Param("ignoreNullValue") boolean ignoreNullValue,
			@Param("where") Predicate where);

	@InsertProvider(type = MybatisSQLProvider.class, method = "updateSpecific")
	public int _updateSpecific(@Param("entityType") Class<T> entityType, @Param("voMap") Map<String, Object> voMap,
			@Param("where") Predicate where);

	@Override
	default int insert(T entity, boolean ignoreNullValue) {
		return _insert(entity, ignoreNullValue);
	}

	@Override
	default int update(T entity, boolean ignoreNullValue, Predicate where) {
		return _update(entity, ignoreNullValue, where);
	}

	@Override
	default int updateMap(Map<String, Object> voMap, Predicate where) {
		return _updateSpecific(null, voMap, where);
	}

	@Override
	default int delete(long id) {
		return _delete(null, id);
	}

	@Override
	default int deleteMore(List<Long> ids) {
		return _deleteMore(null, ids);
	}

	@Override
	default long deleteAll(Predicate where) {
		return _deleteAll(null, where);
	}

	@Override
	default long count(Predicate where) {
		return _count(null, where);
	}

	@Override
	default boolean exist(long id) {
		return _exist(null, id);
	}

	@Override
	default boolean existByName(String name, Object value, Long ignoreId) {
		return _existByName(null, name, value, ignoreId);
	}

	@Override
	default boolean existByWhere(Predicate where) {
		return _existByWhere(null, where);
	}

	@Override
	default T findOne(long id) {
		return _findOne(null, id);
	}

	@Override
	default List<T> findMore(List<Long> ids) {
		return _findMore(null, ids);
	}

	@Override
	default List<T> findAll(Predicate where, Sort sort) {
		return _findAll(null, where, sort);
	}

	@Override
	default T findByName(String name, Object value) {
		return _findByName(null, name, value);
	}

	@Override
	default List<T> findAllByName(String name, Object value, Sort sort) {
		return _findAllByName(null, name, value, sort);
	}

	@Override
	default PageSet<T> findPage(Predicate where, Sort sort, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<T> page = _findPage(null, where, sort);
		return AppUtils.toPageSet(page);
	}
}
