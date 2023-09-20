package org.teach.study.boot.entity;

import java.math.BigDecimal;
import java.sql.Date;

import org.quincy.rock.core.dao.annotation.Column;
import org.quincy.rock.core.dao.annotation.IgnoreUpdate;
import org.quincy.rock.core.dao.annotation.JoinTable;
import org.quincy.rock.core.dao.annotation.JoinTables;
import org.quincy.rock.core.dao.annotation.MoreToOne;
import org.quincy.rock.core.dao.annotation.Table;
import org.teach.study.boot.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "用户实体(在执行更新操作时采取动态更新策略，如果属性值为空，则忽略该属性)")
@Table(name = "t_user", alias = "u", resultMap = "resultMap")
@JoinTables({ @JoinTable(name = "t_dept", alias = "d", onExpr = "d.f_id=u.f_dept_id", extraColumns = { "f_descr" }),
		@JoinTable(name = "t_job", alias = "j", onExpr = "j.f_id=u.f_job_id"),
		@JoinTable(name = "t_workstate", alias = "w", onExpr = "w.f_id=u.f_workstate_id") })
public class User extends Entity {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = 6839230938734074806L;

	@ApiModelProperty(value = "代码", required = true, position = 1)
	private String code;

	@ApiModelProperty(value = "名称", required = true, position = 2)
	private String name;

	@ApiModelProperty(value = "备注", position = 3)
	private String descr;

	@IgnoreUpdate
	@ApiModelProperty(value = "密码", position = 4)
	private String password;

	@ApiModelProperty(value = "身份证号", required = true, position = 5)
	private String sfzh;

	@ApiModelProperty(value = "性别(1-男,2-女)", position = 6)
	private Integer gender;

	@ApiModelProperty(value = "出生日期", required = true, position = 7)
	private Date birthday;

	@MoreToOne
	private Dept dept;

	@ApiModelProperty(value = "部门id", required = true, position = 8)
	private Long deptId;

	@ApiModelProperty(value = "部门代码", position = 9)
	@Column(value = "d.f_code", calculated = true)
	private String deptCode;

	@ApiModelProperty(value = "部门名称", position = 10)
	@Column(value = "d.f_name", calculated = true)
	private String deptName;

	@ApiModelProperty(value = "职务id", required = true, position = 11)
	private Long jobId;

	@ApiModelProperty(value = "职务代码", position = 12)
	@Column(value = "j.f_code", calculated = true)
	private String jobCode;

	@ApiModelProperty(value = "职务名称", position = 13)
	@Column(value = "j.f_name", calculated = true)
	private String jobName;

	@ApiModelProperty(value = "工作状态id", required = true, position = 14)
	private Long workstateId;

	@ApiModelProperty(value = "工作状态代码", position = 15)
	@Column(value = "w.f_code", calculated = true)
	private String workstateCode;

	@ApiModelProperty(value = "工作状态名称", position = 16)
	@Column(value = "w.f_name", calculated = true)
	private String workstateName;

	@ApiModelProperty(value = "电话", position = 17)
	private String phone;

	@ApiModelProperty(value = "电子邮箱", position = 18)
	private String email;

	@ApiModelProperty(value = "薪水", position = 19)
	private BigDecimal sale;

	@ApiModelProperty(value = "用户爱好id列表(使用逗号分隔)", position = 20)
	private String loves;

	@ApiModelProperty(value = "附件名称", position = 21)
	private String additionalName;

	@ApiModelProperty(value = "附件文件url", position = 22)
	private String additionalFile;

	@ApiModelProperty(value = "返回是否有照片", position = 23)
	@Column(value = "f_photo_file is not null", calculated = true)
	private Boolean hasPhoto;
}
