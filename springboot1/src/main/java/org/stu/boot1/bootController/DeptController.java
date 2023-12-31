package org.stu.boot1.bootController;

import java.util.List;


import org.quincy.rock.core.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.stu.boot1.BaseController;
import org.stu.boot1.dao.DeptDao;
import org.stu.boot1.entity.Dept;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>DeptController。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */
@Api(tags = "部门模块。")

@Slf4j
@Controller
@RequestMapping("/dept")
public class DeptController  {
	@Autowired
	private DeptDao dao;

	@ApiOperation(value = "查询所有实体", notes = "find")
	@RequestMapping(value = "queryAll", method = RequestMethod.GET)
	public @ResponseBody Result<List<Dept>> queryAll() {
		log.debug("call queryAll");
		List<Dept> list = dao.findAll();
		list.forEach(e -> System.out.println(e));
		return Result.toResult(list);
	}

	@ApiOperation(value = "查询一个实体", notes = "")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "queryOne", method = RequestMethod.GET)
	public @ResponseBody Result<Dept> queryOne(@RequestParam long id) {
		log.debug("call queryAll");
		Dept vo = dao.findOne(id);

		return Result.toResult(vo);
	}
	
	@ApiOperation(value = "根据指定的属性名和属性值查询数据是否存在", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "propName", value = "属性名", required = true),
			@ApiImplicitParam(name = "propValue", value = "属性值", required = true) })
	@RequestMapping(value = "/queryByName", method = { RequestMethod.GET })
	public @ResponseBody Result<Dept> queryByName(@RequestParam String propName, @RequestParam Object propValue) {
		log.debug("call queryByName");
		Dept vo = dao.findByName(propName, propValue);
		return Result.toResult(vo);
	}
	
	@ApiOperation(value = "删除一个实体", notes = "")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "/remove", method = { RequestMethod.GET })
	public @ResponseBody Result<Boolean> remove(@RequestParam long id) {
		log.debug("call remove");
		int result = dao.delete(id);
		return Result.of(result > 0);
	}
	
	@ApiOperation(value = "添加一个实体", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "vo", value = "实体值对象", required = true) })
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> add(@RequestBody Dept vo) {
		log.debug("call add");
		int result = dao.insert(vo);
		return Result.of(result > 0);
	}

	@ApiOperation(value = "更新一个实体", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "vo", value = "实体值对象", required = true) })
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> update(@RequestBody Dept vo) {
		log.debug("call update");
		int result = dao.update(vo);
		return Result.of(result > 0);
	}
	

}
