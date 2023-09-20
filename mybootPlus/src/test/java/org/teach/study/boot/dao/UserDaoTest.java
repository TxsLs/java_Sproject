package org.teach.study.boot.dao;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.teach.study.boot.entity.User;

@SpringBootTest
public class UserDaoTest {

	@Autowired
	private UserDao dao;

	@Test
	public void test_findOne() {
		User vo = dao.findOne(1);
		Assertions.assertNotNull(vo);
	}
}
