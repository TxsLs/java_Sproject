package org.teach.study.boot.service.impl;

import org.springframework.stereotype.Service;
import org.teach.study.boot.BaseService;
import org.teach.study.boot.dao.LoveDao;
import org.teach.study.boot.entity.Love;
import org.teach.study.boot.service.LoveService;

@Service
public class LoveServiceImpl extends BaseService<Love, LoveDao> implements LoveService {

}
