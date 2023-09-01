package org.teach.study.boot.controller;

import java.util.List;

import org.quincy.rock.core.dao.sql.Sort;
import org.quincy.rock.core.vo.PageSet;
import org.quincy.rock.core.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.teach.study.boot.BaseController;
import org.teach.study.boot.Entity;
import org.teach.study.boot.entity.Job;
import org.teach.study.boot.service.JobService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "职务管理模块")
@Controller
@RequestMapping("/job")
public class JobController extends BaseController<Job, JobService> {

	@ApiOperation(value = "查询所有实体", notes = "该接口继承自SimpleController")
	@ApiImplicitParam(name = "sort", value = "排序规则字符串")
	@RequestMapping(value = "/queryAll", method = { RequestMethod.GET })
	public @ResponseBody Result<List<? extends Entity>> queryAll(String sort) {
		log.debug("call queryAll");
		List<? extends Entity> list = this.service().findAll(null, Sort.parse(sort));
		return Result.toResult(list);
	}

	@ApiOperation(value = "分页查询", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "sort", value = "排序规则字符串"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int"),
			@ApiImplicitParam(name = "pageSize", value = "页大小", required = true, dataType = "int") })
	@RequestMapping(value = "/queryPage", method = { RequestMethod.GET })
	public @ResponseBody Result<PageSet<? extends Entity>> queryPage(String sort, @RequestParam int pageNum,
			@RequestParam int pageSize) {
		log.debug("call queryPage");
		PageSet<? extends Entity> ps = this.service().findPage(null, Sort.parse(sort), pageNum, pageSize);
		return Result.toResult(ps);
	}

}
