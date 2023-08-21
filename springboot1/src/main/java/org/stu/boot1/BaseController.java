package org.stu.boot1;

import java.util.List;

import org.quincy.rock.core.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * <b>BaseController。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@Slf4j
public class BaseController<T extends Entity, S extends Service<T>> {
	@Autowired
	private S service;

	/**
	 * <b>获得Service接口。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return Service接口
	 */
	protected S getService() {
		return this.service;
	}

	@ApiOperation(value = "添加一个实体", notes = "该接口继承自BaseController")
	@ApiImplicitParams({ @ApiImplicitParam(name = "vo", value = "实体值对象", required = true) })
	@RequestMapping(value = "/add", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> add(@RequestBody T vo) {
		log.debug("call add");
		int result = service.insert(vo);
		return Result.of(result > 0);
	}

	@ApiOperation(value = "更新一个实体", notes = "该接口继承自BaseController")
	@ApiImplicitParams({ @ApiImplicitParam(name = "vo", value = "实体值对象", required = true) })
	@RequestMapping(value = "/update", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> update(@RequestBody T vo) {
		log.debug("call update");
		int result = service.update(vo);
		return Result.of(result > 0);
	}

	@ApiOperation(value = "查询所有实体", notes = "该接口继承自BaseController")
	@RequestMapping(value = "queryAll", method = RequestMethod.GET)
	public @ResponseBody Result<List<T>> queryAll() {
		log.debug("call queryAll");
		List<T> list = service.findAll();
		return Result.toResult(list);
	}

	@ApiOperation(value = "查询一个实体", notes = "该接口继承自BaseController")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "/queryOne", method = { RequestMethod.GET })
	public @ResponseBody Result<T> queryOne(@RequestParam long id) {
		log.debug("call queryOne");
		T vo = this.getService().findOne(id);
		return Result.toResult(vo);
	}

	@ApiOperation(value = "根据指定的属性名和属性值查询数据是否存在", notes = "该接口继承自BaseController")
	@ApiImplicitParams({ @ApiImplicitParam(name = "propName", value = "属性名", required = true),
			@ApiImplicitParam(name = "propValue", value = "属性值", required = true) })
	@RequestMapping(value = "/queryByName", method = { RequestMethod.GET })
	public @ResponseBody Result<T> queryByName(@RequestParam String propName, @RequestParam Object propValue) {
		log.debug("call queryByName");
		T vo = service.findByName(propName, propValue);
		return Result.toResult(vo);
	}

	@ApiOperation(value = "删除一个实体", notes = "该接口继承自BaseController")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "/remove", method = { RequestMethod.GET })
	public @ResponseBody Result<Boolean> remove(@RequestParam long id) {
		log.debug("call remove");
		int result = service.delete(id);
		return Result.of(result > 0);
	}
}
