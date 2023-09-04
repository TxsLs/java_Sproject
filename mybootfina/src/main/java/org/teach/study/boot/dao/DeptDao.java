package org.teach.study.boot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.teach.study.boot.Dao;
import org.teach.study.boot.entity.Dept;

@Mapper
public interface DeptDao extends Dao<Dept> {
	
}
