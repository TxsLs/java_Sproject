package org.stu.boot1.sevice.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.stu.boot1.dao.JobDao;
import org.stu.boot1.entity.Job;
import org.stu.boot1.sevice.JobService;

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

}
