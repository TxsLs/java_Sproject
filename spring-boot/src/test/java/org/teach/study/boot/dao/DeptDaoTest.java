package org.teach.study.boot.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.teach.study.boot.entity.Dept;

@SpringBootTest
public class DeptDaoTest {

	@Autowired
	private DeptDao dao;

	@Test
	public void test_findOne() {
		Dept vo = dao.findOne(1);
		Assertions.assertNotNull(vo);
	}
	
	@Test
	public void test_findByName() {
		Dept vo=dao.findByName("code", "A001");
		Assertions.assertNotNull(vo);
	}
}
