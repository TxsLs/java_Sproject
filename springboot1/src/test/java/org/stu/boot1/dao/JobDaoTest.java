package org.stu.boot1.dao;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.stu.boot1.Entity;
import org.stu.boot1.entity.Dept;
import org.stu.boot1.entity.Job;

/**
 * <b>JobDaoTest。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */
@SpringBootTest
public class JobDaoTest {
	@Autowired
	private JobDao dao;

	/**
	 * <b>test_findAll。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 测试查询全部。
	 */
	@Test
	public void test_findAll() {
		List<? extends Entity> list = dao.findAll();
		Assertions.assertTrue(list.size() > 0);
		list.forEach(e -> System.out.println(e));
	}

	/**
	 * <b>test_findOne。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 查询单个。
	 */
	@Test
	public void test_findOne() {
		Job vo = dao.findOne(1);
		Assertions.assertNotNull(vo);
		System.out.println(vo);
	}

	/**
	 * <b>test_findByName。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 单个查询。
	 */
	@Test
	public void test_findByName() {
		Job vo = dao.findByName("f_code", "B002");
		Assertions.assertNotNull(vo);
		System.out.println(vo);
	}

	/**
	 * <b>test_insert。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 单个添加。
	 */
	@Test
	public void test_insert() {
		Job vo = new Job();
		vo.setId(13L);
		vo.setCode("sxc");
		vo.setName("txs");
		vo.setDescr("wadsasd111");
		int i = dao.insert(vo);
		Assertions.assertEquals(i, 1);
		System.out.println(i);
	}

	/**
	 * <b>test_update。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 更新记录。
	 */
	@Test
	public void test_update() {
		Job vo = new Job();
		vo.setId(13L);
		vo.setCode("gengx");
		vo.setName("txs");
		vo.setDescr("wage更新x111");
		int i = dao.update(vo);
		Assertions.assertEquals(i, 1);
		System.out.println(i);
	}

	/**
	 * <b>test_delete。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 删除记录。
	 */
	@Test
	public void test_delete() {
		int vo = dao.delete(13);
		Assertions.assertEquals(vo, 1);
		System.out.println(vo);
	}

}
