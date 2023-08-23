package org.stu.boot1.sevice.impl;

import java.util.List;
import org.quincy.rock.core.vo.Sort;
import org.quincy.rock.core.util.StringUtil;
import org.quincy.rock.core.vo.PageSet;
import org.springframework.stereotype.Service;
import org.stu.boot1.AppUtils;
import org.stu.boot1.dao.JobDao;
import org.stu.boot1.entity.Job;
import org.stu.boot1.sevice.JobService;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

/**
 * <b>JobServiceImpl。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@Service
public class JobServiceImpl extends AbstractService<Job, JobDao> implements JobService {

	@Override
	public int deleteMore(List<Long> ids) {
		return this.dao().deleteMore(ids);
	}

	
	@Override
	public PageSet<Job> findPageByCondition(String code, String name, Sort sort, int pageNum, int pageSize) {
		if (Sort.isSorted(sort)) {
			sort.setInterceptor(fName -> StringUtil.objName2DbName(fName, "f_", '_'));
			PageHelper.startPage(pageNum, pageSize, sort.getOrderby());
		} else
			PageHelper.startPage(pageNum, pageSize);
		Page<Job> p = this.dao().findPageByCondition(code, name);
		return AppUtils.toPageSet(p);
	}
	
	
}
