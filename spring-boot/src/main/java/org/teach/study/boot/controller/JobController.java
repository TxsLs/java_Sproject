package org.teach.study.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.teach.study.boot.BaseController;
import org.teach.study.boot.entity.Job;
import org.teach.study.boot.service.JobService;

import io.swagger.annotations.Api;

/**
 * <b>JobController。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */

@Api(tags = "职务管理模块")
@Controller
@RequestMapping("/job")
public class JobController extends BaseController<Job, JobService> {

}
