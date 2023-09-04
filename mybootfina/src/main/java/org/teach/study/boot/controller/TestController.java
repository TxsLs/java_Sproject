package org.teach.study.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Api(tags = "测试模块")
@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@ApiOperation(value = "显示hello", notes = "测试方法")
	@RequestMapping(value = "/hello", method = { RequestMethod.GET })
	public @ResponseBody String hello() {
		log.info("call test");
		int v = jdbcTemplate.queryForObject("select 2023", Integer.class);
		return "hello:" + v;
	}
}
