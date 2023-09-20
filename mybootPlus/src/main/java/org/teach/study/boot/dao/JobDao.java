package org.teach.study.boot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.teach.study.boot.Dao;
import org.teach.study.boot.entity.Job;

@Mapper
public interface JobDao extends Dao<Job> {

}
