package org.stu.boot1.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.stu.boot1.Dao;
import org.stu.boot1.entity.Dept;

/**
 * <b>DeptDao。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */

@Mapper
public interface DeptDao extends Dao {
	/**
	 * <b>返回所有的实体列表。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 所有的实体列表
	 */
	@Select("select f_id as id,f_code as code,f_name as name from t_depts")
	public List<Dept> findAll();

	/**
	 * <b>查找一个实体。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param id 主键id
	 * @return 实体对象
	 */
	@Select("select f_id as id,f_code as code,f_name as name from t_depts where f_id=#{id}")
	public Dept findOne(long id);

	/**
	 * <b>根据指定的属性名和值返回一条数据。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param name 属性名称
	 * @param value 属性值
	 * @return 实体对象
	 */
	@Select("select f_id as id,f_code as code,f_name as name from t_depts where ${name}=#{value}")
	public Dept findByName(String name, Object value);

	/**
	 * <b>删除实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param id 要删除的实体主键id
	 * @return 影响数据条数
	 */
	@Delete("delete from t_depts where f_id=#{id}")
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
	@Insert("insert into t_depts (f_id,f_code,f_name,f_desc) values (#{id},#{code},#{name},#{desc})")
	public int insert(Dept entity);

	/**
	 * <b>更新实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param entity 实体对象
	 * @param ignoreNullValue 是否忽略空值
	 * @return 影响数据条数
	 */
	@Update("update t_depts set f_code=#{code},f_name=#{name},f_desc=#{desc} where f_id=#{id}")
	public int update(Dept entity);

}
