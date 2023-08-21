package org.stu.boot1;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * <b>Service。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public interface Service<T extends Entity> {
	/**
	 * <b>返回所有的实体列表。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 所有的实体列表
	 */
	public List<T> findAll();

	/**
	 * <b>查找一个实体。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param id 主键id
	 * @return 实体对象
	 */
	public T findOne(long id);

	/**
	 * <b>根据指定的属性名和值返回一条数据。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param name 属性名称
	 * @param value 属性值
	 * @return 实体对象
	 */
	public T findByName(@Param("name") String name, @Param("value") Object value);

	/**
	 * <b>删除实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param id 要删除的实体主键id
	 * @return 影响数据条数
	 */
	public int delete(long id);

	/**
	 * <b>添加新实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param entity 实体对象
	 * @param ignoreNullValue 是否忽略空值
	 * @return 影响数据条数
	 */
	public int insert(T entity);

	/**
	 * <b>更新实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param entity 实体对象
	 * @param ignoreNullValue 是否忽略空值
	 * @return 影响数据条数
	 */
	public int update(T entity);
}
