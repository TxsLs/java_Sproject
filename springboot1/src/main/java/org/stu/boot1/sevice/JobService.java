package org.stu.boot1.sevice;

import java.util.List;

import org.quincy.rock.core.vo.PageSet;
import org.quincy.rock.core.vo.Sort;
import org.stu.boot1.Service;
import org.stu.boot1.entity.Job;

/**
 * <b>JobService。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public interface JobService extends Service<Job> {
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
	 * <b>findPageByCondition。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param code
	 * @param name
	 * @param sort
	 * @param pageNum
	 * @param pageSize
	 * @return
	 */
	public PageSet<Job> findPageByCondition(String code, String name, Sort sort,int pageNum,int pageSize);
	
}
