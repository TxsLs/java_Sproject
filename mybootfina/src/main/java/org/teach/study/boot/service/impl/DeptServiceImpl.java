package org.teach.study.boot.service.impl;

import org.springframework.stereotype.Service;
import org.teach.study.boot.BaseService;
import org.teach.study.boot.dao.DeptDao;
import org.teach.study.boot.entity.Dept;
import org.teach.study.boot.service.DeptService;

@Service
public class DeptServiceImpl extends BaseService<Dept, DeptDao> implements DeptService {

}
