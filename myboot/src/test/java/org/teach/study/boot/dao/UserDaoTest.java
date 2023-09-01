package org.teach.study.boot.dao;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.teach.study.boot.entity.User;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

@SpringBootTest
public class UserDaoTest {

	@Autowired
	private UserDao dao;

	@Test
	public void test_findOne() {
		User vo = dao.findOne(1);
		Assertions.assertNotNull(vo);
	}

	@Test
	public void test_findPageByCondition() {
		PageHelper.startPage(1, 10);
		PageHelper.orderBy("a.f_code asc");
		Map<String, Object> map = new HashMap<>();
		map.put("code", "w");
		Page<User> p = dao.findPageByCondition(map);
		Assertions.assertTrue(p.getTotal() > 0);
	}

	@Test
	public void test_findByCode() {
		User vo = dao.findByCode("wks");
		Assertions.assertNotNull(vo);
	}
}
