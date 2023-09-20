package org.teach.study.boot.controller;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;
import org.quincy.rock.core.dao.DaoUtil;
import org.quincy.rock.core.dao.sql.Predicate;
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
	@ApiOperation(value = "条件分页查询", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "searchCode", value = "代码(支持like)，允许null"),
			@ApiImplicitParam(name = "searchName", value = "名称(支持like)，允许null"),
			@ApiImplicitParam(name = "sort", value = "排序规则字符串"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "long"),
			@ApiImplicitParam(name = "pageSize", value = "页大小", required = true, dataType = "int") })
	@RequestMapping(value = "/queryPage", method = { RequestMethod.GET })
	public @ResponseBody Result<PageSet<? extends Entity>> queryPage(String searchCode, String searchName, String sort,
			@Min(1) @RequestParam("pageNum") long pageNum, @Min(1) @RequestParam int pageSize) {
		log.debug("call queryPage");
		Predicate where = DaoUtil.and();
		if (StringUtils.isNotEmpty(searchCode)) {
			where.like("code", searchCode);
		}
		if (StringUtils.isNotEmpty(searchName)) {
			where.like("name", searchName);
		}
		PageSet<? extends Entity> ps = this.service().findPage(where, Sort.parse(sort), pageNum, pageSize);
		return Result.toResult(ps);
	}

}
