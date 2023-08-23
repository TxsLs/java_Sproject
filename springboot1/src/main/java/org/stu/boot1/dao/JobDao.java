package org.stu.boot1.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.stu.boot1.Dao;
import org.stu.boot1.entity.Dept;
import org.stu.boot1.entity.Job;

import com.github.pagehelper.Page;

/**
 * <b>JobDao。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */

@Mapper
public interface JobDao extends Dao<Job> {

	
	/**
	 * <b>删除多个实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param ids 主键id列表
	 * @return 影响数据条数
	 */
	public int deleteMore(List<Long> ids);
	
	/**
	 * <b>组合条件查询。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param code 编码(支持like)，允许null
	 * @param name 名称(支持like)，允许null
	 * @return 一页数据列表
	 */
	public Page<Job> findPageByCondition(String code, String name);
	
	
	
	
	
	
	
	
	
	
	/**
	 * <b>返回所有的实体列表。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 所有的实体列表
	 */
//	@Select("select f_id as id,f_code as code,f_name as name from t_job")
//	public List<Job> findAll();

	/**
	 * <b>查找一个实体。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param id 主键id
	 * @return 实体对象
	 */
//	@Select("select f_id as id,f_code as code,f_name as name from t_job where f_id=#{id}")
//	public Job findOne(long id);

	/**
	 * <b>根据指定的属性名和值返回一条数据。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param name 属性名称
	 * @param value 属性值
	 * @return 实体对象
	 */
//	@Select("select f_id as id,f_code as code,f_name as name from t_job where ${name}=#{value}")
//	public Job findByName(@Param("name") String name,@Param("value") Object value);

	/**
	 * <b>添加新实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param entity 实体对象
	 * @param ignoreNullValue 是否忽略空值
	 * @return 影响数据条数
	 */
//	@Insert("insert into t_job (f_id,f_code,f_name,f_descr) values (#{id},#{code},#{name},#{descr})")
//	public int insert(Job entity);

	/**
	 * <b>更新实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param entity 实体对象
	 * @param ignoreNullValue 是否忽略空值
	 * @return 影响数据条数
	 */
//	@Update("update t_job set f_code=#{code},f_name=#{name},f_descr=#{descr} where f_id=#{id}")
//	public int update(Job entity);

	/**
	 * <b>删除实体对象。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param id 要删除的实体主键id
	 * @return 影响数据条数
	 */
//	@Delete("delete from t_job where f_id=#{id}")
//	public int delete(long id);

}
