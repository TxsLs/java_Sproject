package org.teach.study.boot.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Update;
import org.quincy.rock.core.dao.DaoUtil;
import org.quincy.rock.core.dao.sql.Predicate;
import org.quincy.rock.core.dao.sql.Sort;
import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.vo.PageSet;
import org.quincy.rock.core.vo.Result;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.teach.study.boot.AppUtils;
import org.teach.study.boot.BaseController;
import org.teach.study.boot.entity.Photo;
import org.teach.study.boot.entity.User;
import org.teach.study.boot.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Api(tags = "用户管理模块")
@Controller
@RequestMapping("/user")
public class UserController extends BaseController<User, UserService> {

	@ApiOperation(value = "添加一个实体", notes = "允许文件上传")
	@ApiImplicitParams({ @ApiImplicitParam(name = "code", value = "代码", required = true, paramType = "form"),
			@ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "form"),
			@ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "form"),
			@ApiImplicitParam(name = "sfzh", value = "身份证号", required = true, paramType = "form"),
			@ApiImplicitParam(name = "gender", value = "性别(1-男,2-女)", required = true, paramType = "form", dataType = "int"),
			@ApiImplicitParam(name = "birthday", value = "出生日期(yyyy-MM-dd)", required = true, paramType = "form"),
			@ApiImplicitParam(name = "deptId", value = "部门id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "jobId", value = "职务id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "workstateId", value = "工作状态id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "phone", value = "电话", paramType = "form"),
			@ApiImplicitParam(name = "email", value = "电子邮箱", paramType = "form"),
			@ApiImplicitParam(name = "sale", value = "薪水", paramType = "form", dataType = "BigDecimal"),
			@ApiImplicitParam(name = "loves", value = "用户爱好id列表(使用逗号分隔)", paramType = "form"),
			@ApiImplicitParam(name = "additionalName", value = "附件名称", paramType = "form"),
			@ApiImplicitParam(name = "additionalFile", value = "附件文件url", paramType = "form") })
	@RequestMapping(value = "/addUser", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> addUser(@Validated({ Default.class }) @ApiIgnore User vo,
			@RequestPart(value = "photo", required = false) MultipartFile file) throws IOException {
		log.debug("call addUser");
		boolean result = this.service().insert(vo, true);
		if (result && file != null && !file.isEmpty()) {
			result = this.updatePhoto(vo.id(), file);
		}
		return Result.of(result);
	}

	@ApiOperation(value = "更新一个实体", notes = "允许文件上传")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id", value = "要更新实体的主键id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "code", value = "代码", required = true, paramType = "form"),
			@ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "form"),
			@ApiImplicitParam(name = "sfzh", value = "身份证号", required = true, paramType = "form"),
			@ApiImplicitParam(name = "gender", value = "性别(1-男,2-女)", required = true, paramType = "form", dataType = "int"),
			@ApiImplicitParam(name = "birthday", value = "出生日期(yyyy-MM-dd)", required = true, paramType = "form"),
			@ApiImplicitParam(name = "deptId", value = "部门id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "jobId", value = "职务id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "workstateId", value = "工作状态id", required = true, paramType = "form", dataType = "long"),
			@ApiImplicitParam(name = "phone", value = "电话", paramType = "form"),
			@ApiImplicitParam(name = "email", value = "电子邮箱", paramType = "form"),
			@ApiImplicitParam(name = "sale", value = "薪水", paramType = "form", dataType = "BigDecimal"),
			@ApiImplicitParam(name = "loves", value = "用户爱好id列表(使用逗号分隔)", paramType = "form"),
			@ApiImplicitParam(name = "additionalName", value = "附件名称", paramType = "form"),
			@ApiImplicitParam(name = "additionalFile", value = "附件文件url", paramType = "form") })
	@RequestMapping(value = "/updateUser", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> updateUser(@Validated({ Update.class, Default.class }) @ApiIgnore User vo,
			@RequestPart(value = "photo", required = false) MultipartFile file) throws IOException {
		log.debug("call updateUser");
		boolean result = this.service().update(vo, true, null);
		if (result && file != null && !file.isEmpty()) {
			result = this.updatePhoto(vo.id(), file);
		}
		return Result.of(result);
	}

	@ApiOperation(value = "条件分页查询", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "deptId", value = "部门id", dataType = "long"),
			@ApiImplicitParam(name = "jobId", value = "职务id", dataType = "long"),
			@ApiImplicitParam(name = "workstateId", value = "工作状态id", dataType = "long"),
			@ApiImplicitParam(name = "code", value = "工号(支持like)，允许null"),
			@ApiImplicitParam(name = "name", value = "名称(支持like)，允许null"),
			@ApiImplicitParam(name = "sort", value = "排序规则字符串"),
			@ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "long"),
			@ApiImplicitParam(name = "pageSize", value = "页大小", required = true, dataType = "int") })
	@RequestMapping(value = "/queryPage", method = { RequestMethod.GET })
	public @ResponseBody Result<PageSet<User>> queryPage(Long deptId, Long jobId, Long workstateId, String code,
			String name, String sort,@Min(1) @RequestParam long pageNum,@Min(1) @RequestParam int pageSize) {
		log.debug("call queryPage");
		Predicate where = DaoUtil.and();
		if (deptId != null)
			where.equal(DataType.LONG, "deptId", deptId.toString());
		if (jobId != null)
			where.equal(DataType.LONG, "jobId", jobId.toString());
		if (workstateId != null)
			where.equal(DataType.LONG, "workstateId", workstateId.toString());
		if (StringUtils.isNotEmpty(code))
			where.like("code", code);
		if (StringUtils.isNotEmpty(name))
			where.like("name", name);
		PageSet<User> ps = this.service().findPage(where, Sort.parse(sort), pageNum, pageSize, "password");
		return Result.toResult(ps);
	}

	@ApiOperation(value = "根据用户id更新自己的信息", notes = "当前用户可以修改自己少部分个人信息")
	@ApiImplicitParam(name = "vo", value = "要修改的用户信息", required = true)
	@RequestMapping(value = "/updateSelfInfo", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> updateSelfInfo(@Validated({ Update.class, Default.class }) @RequestBody User vo, @ApiIgnore HttpSession session) {
		log.debug("call updateSelfInfo");
		boolean ok = false;
		if (AppUtils.isLogin()) {
			String code = AppUtils.getLoginUser().getUsername();
			User user = service().findByCode(code);
			if (vo.equals(user)) {
				ok = this.service().updateSelfInfo(vo);
			}
		}
		return Result.of(ok);
	}

	@ApiOperation(value = "用户修改自己的密码", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "oldPassword", value = "旧密码", required = true),
			@ApiImplicitParam(name = "newPassword", value = "新密码", required = true) })
	@RequestMapping(value = "/changeSelfPassword", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> changeSelfPassword(@NotBlank @RequestParam String oldPassword,
			@NotBlank	@RequestParam String newPassword, @ApiIgnore HttpSession session) {
		log.debug("call changeSelfPassword");
		boolean ok = false;
		if (AppUtils.isLogin()) {
			String code = AppUtils.getLoginUser().getUsername();
			ok = this.service().changeSelfPassword(code, oldPassword, newPassword);
		}
		return Result.of(ok);
	}

	@ApiOperation(value = "重设密码", notes = "")
	@ApiImplicitParams({ @ApiImplicitParam(name = "code", value = "用户代码", required = true),
			@ApiImplicitParam(name = "password", value = "新密码", required = true) })
	@RequestMapping(value = "/resetPwd", method = { RequestMethod.POST })
	//@Secured({ "ROLE_ADMIN" })
	public @ResponseBody Result<Boolean> resetPwd(@NotBlank @RequestParam String code,@NotBlank @RequestParam String password) {
		log.debug("call resetPwd");
		boolean result = this.service().changePassword(code, password);
		return Result.of(result);
	}

	@ApiOperation(value = "上传用户照片", notes = "照片大小不要超过500K")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "/uploadPhoto", method = { RequestMethod.POST })
	public @ResponseBody Result<Boolean> uploadPhoto(@NotNull @RequestParam long id,
			@RequestPart(value = "photo", required = false) MultipartFile file) throws IOException {
		log.debug("call uploadPhoto");
		boolean result = this.updatePhoto(id, file);
		return Result.of(result);
	}

	@ApiOperation(value = "删除用户照片", notes = "")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "/deletePhoto", method = { RequestMethod.GET })
	public @ResponseBody Result<Boolean> deletePhoto(@NotNull @RequestParam long id) throws IOException {
		log.debug("call deletePhoto");
		boolean result = this.updatePhoto(id, null);
		return Result.of(result);
	}

	@ApiOperation(value = "下载用户照片")
	@ApiImplicitParam(name = "id", value = "主键id", required = true, dataType = "long")
	@RequestMapping(value = "/photo", method = { RequestMethod.GET })
	public ResponseEntity<byte[]> photo(@NotNull @RequestParam long id) {
		log.debug("call photo");
		Photo photo = this.service().getPhoto(id);
		if (Photo.isEmpty(photo))
			return null;
		//
		BodyBuilder builder = ResponseEntity.ok().contentLength(photo.length());
		builder.contentType(MediaType.APPLICATION_OCTET_STREAM);
		builder.header("Content-Disposition", "attachment; filename=" + photo.getPhotoFile());
		return builder.body(photo.getPhoto());
	}

	//更新照片
	private boolean updatePhoto(long id, MultipartFile file) throws IOException {
		Photo photo = new Photo();
		photo.setId(id);
		if (file != null && !file.isEmpty()) {
			photo.setPhoto(file.getBytes());
			String extName = FilenameUtils.getExtension(file.getOriginalFilename());
			String fileName = IOUtil.getUniqueFileName("up", "." + extName);
			photo.setPhotoFile(fileName);
		}
		return this.service().updatePhoto(photo);
	}
}
