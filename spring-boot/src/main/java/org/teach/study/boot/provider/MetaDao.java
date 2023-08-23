package org.teach.study.boot.provider;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.quincy.rock.core.vo.Sort;
import org.quincy.rock.core.vo.Vo;

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
public interface MetaDao<T extends Vo<Long>> {
	
	@DeleteProvider(type = MybatisSQLProvider.class, method = "deleteById")
	public int _deleteOne(Class<T> entityType, long id);
	
	@DeleteProvider(type = MybatisSQLProvider.class, method = "deleteMoreById")
	public int _deleteMore(Class<T> entityType, Iterable<Long> ids);
	
	@DeleteProvider(type = MybatisSQLProvider.class, method = "deleteAll")
	public long _deleteAll(Class<T> entityType);

	@SelectProvider(type = MybatisSQLProvider.class, method = "total")
	public long _total(Class<T> entityType);
	
	@SelectProvider(type = MybatisSQLProvider.class,method = "findById")
	public T _findOne(Class<T> entityType, long id);
	
	@SelectProvider(type = MybatisSQLProvider.class, method = "findMoreById")
	public List<T> _findMore(Class<T> entityType, Iterable<Long> ids);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findAll")
	public List<T> _findAll(Class<T> entityType, Sort sort);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findByName")
	public T _findOneByName(Class<T> entityType, String name, Object value);

	@SelectProvider(type = MybatisSQLProvider.class, method = "findByName")
	public List<T> _findAllByName(Class<T> entityType, String name, Object value, Sort sort);
	
	@SelectProvider(type = MybatisSQLProvider.class, method = "existsById")
	public boolean _exist(Class<T> entityType, long id);

	@SelectProvider(type = MybatisSQLProvider.class, method = "existsByName")
	public boolean _existByName(Class<T> entityType, String name, Object value, Long ignoreId);

	@SelectProvider(type = MybatisSQLProvider.class, method = "countByIds")
	public int _count(Class<T> entityType, Iterable<Long> ids);

	@InsertProvider(type = MybatisSQLProvider.class, method = "insertOne")
	public int _insertOne(T entity, boolean ignoreNullValue);

	@UpdateProvider(type = MybatisSQLProvider.class, method = "updateOne")
	public int _updateOne(T entity, boolean ignoreNullValue);

	@InsertProvider(type = MybatisSQLProvider.class, method = "updateSpecific")
	public int _updateSpecific(Class<T> entityType, Map<String, Object> voMap);
}
