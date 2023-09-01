package org.teach.study.boot.service.impl;

import org.springframework.stereotype.Service;
import org.teach.study.boot.BaseService;
import org.teach.study.boot.dao.JobDao;
import org.teach.study.boot.entity.Job;
import org.teach.study.boot.service.JobService;

@Service
public class JobServiceImpl extends BaseService<Job, JobDao> implements JobService {

}
