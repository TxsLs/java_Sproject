package org.stu.boot1.bootController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>testController。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */
@Api(tags = "测试。。")

@Slf4j
@Controller
@RequestMapping("/test")
public class testController {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@ApiOperation(value = "显示", notes = "测试方法")

	@RequestMapping(value = "/hello", method = { RequestMethod.GET })

	public @ResponseBody String hello() {
		log.info("boot test!!!");
		int v = jdbcTemplate.queryForObject("select 20230817", Integer.class);
		return "hello:" + v;
	}

}
