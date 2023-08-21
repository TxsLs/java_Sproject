package org.stu.boot1.service;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.stu.boot1.entity.Job;
import org.stu.boot1.sevice.JobService;

@SpringBootTest
public class JobServiceTest {
	@Autowired
	private JobService service;

	@Test
	public void test_findOne() {
		Job vo = service.findOne(1);
		Assertions.assertNotNull(vo);
	}

	@Test
	public void test_deleteMore() {
		Job vo = new Job();
		vo.setId(101L);
		vo.setCode("aaa1");
		vo.setName("aaa2");
		service.insert(vo);
		vo.setId(102L);
		vo.setCode("aaa3");
		vo.setName("aaa4");
		service.insert(vo);
		int n = service.deleteMore(Arrays.asList(101L, 102L));
		Assertions.assertEquals(n, 2);
	}
}
