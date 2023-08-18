package org.stu.boot1.dao;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.stu.boot1.Entity;
import org.stu.boot1.entity.Dept;

@SpringBootTest
public class DeptDaoTest {

	@Autowired
	private DeptDao dao;

	@Test
	public void test_findOne() {
		Dept vo = dao.findOne(1);
		Assertions.assertNotNull(vo);
		System.out.println(vo);
	}

	@Test
	public void test_findAll() {
		List<? extends Entity> list = dao.findAll();
		Assertions.assertTrue(list.size() > 0);
		list.forEach(e -> System.out.println(e));
	}

	@Test
	public void test_findByName() {
		Dept vo = dao.findByName("f_code", "A001");
		Assertions.assertNotNull(vo);
		System.out.println(vo);
	}

	@Test
	public void test_delete() {
		int vo = dao.delete(13);
		Assertions.assertEquals(vo, 0);
		System.out.println(vo);
	}

	@Test
	public void test_insert() {
		Dept vo = new Dept();
		vo.setId(13L);
		vo.setCode("sxc");
		vo.setName("txs");
		vo.setDesc("wadsasd111");
		int i = dao.insert(vo);
		Assertions.assertEquals(i, 1);
		System.out.println(i);
	}

	@Test
	public void test_update() {
		Dept vo = new Dept();
		vo.setId(13L);
		vo.setCode("gengx");
		vo.setName("txs");
		vo.setDesc("wagengxinxxxx111");
		int i = dao.update(vo);
		Assertions.assertEquals(i, 1);
		System.out.println(i);
	}

}
