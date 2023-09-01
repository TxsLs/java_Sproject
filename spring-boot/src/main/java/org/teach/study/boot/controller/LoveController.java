package org.teach.study.boot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.teach.study.boot.BaseController;
import org.teach.study.boot.entity.Love;
import org.teach.study.boot.service.LoveService;

import io.swagger.annotations.Api;

/**
 * <b>LoveController。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */

@Api(tags = "爱好管理模块")
@Controller
@RequestMapping("/love")
public class LoveController extends BaseController<Love, LoveService>{

}
