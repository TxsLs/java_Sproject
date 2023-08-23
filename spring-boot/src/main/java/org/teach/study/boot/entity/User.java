package org.teach.study.boot.entity;

import java.math.BigDecimal;
import java.sql.Date;

import org.quincy.rock.core.dao.annotation.IgnoreUpdate;
import org.quincy.rock.core.dao.annotation.JoinColumn;
import org.quincy.rock.core.dao.annotation.Temporary;
import org.teach.study.boot.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <b>用户实体类。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@Getter
@Setter
@ApiModel(description = "用户实体(在执行更新操作时采取动态更新策略，如果属性值为空，则忽略该属性)")
public class User extends Entity {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -2851997269867880911L;

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

	@ApiModelProperty(value = "部门id", required = true, position = 8)
	@JoinColumn(name = "f_id",joinTable = "t_dept",joinPKName = "f_id",joinFKName = "f_dept_id")
	private Long deptId;

	@ApiModelProperty(value = "部门代码", position = 9)
	@JoinColumn(name = "f_code",joinTable = "t_dept",joinPKName = "f_id",joinFKName = "f_dept_id")
	private String deptCode;

	@ApiModelProperty(value = "部门名称", position = 10)
	@JoinColumn(name = "f_name",joinTable = "t_dept",joinPKName = "f_id",joinFKName = "f_dept_id")
	private String deptName;

	@ApiModelProperty(value = "职务id", required = true, position = 11)
	@JoinColumn(name = "f_id",joinTable = "t_job",joinPKName = "f_id",joinFKName = "f_job_id")
	private Long jobId;

	@ApiModelProperty(value = "职务代码", position = 12)
	@JoinColumn(name = "f_code",joinTable = "t_job",joinPKName = "f_id",joinFKName = "f_job_id")
	private String jobCode;

	@ApiModelProperty(value = "职务名称", position = 13)
	@JoinColumn(name = "f_name",joinTable = "t_job",joinPKName = "f_id",joinFKName = "f_job_id")
	private String jobName;

	@ApiModelProperty(value = "工作状态id", required = true, position = 14)
	@JoinColumn(name = "f_id",joinTable = "t_workstate",joinPKName = "f_id",joinFKName = "f_workstate_id")
	private Long workstateId;

	@ApiModelProperty(value = "工作状态代码", position = 15)	
	@JoinColumn(name = "f_code",joinTable = "t_workstate",joinPKName = "f_id",joinFKName = "f_workstate_id")
	private String workstateCode;

	@ApiModelProperty(value = "工作状态名称", position = 16)
	@JoinColumn(name = "f_name",joinTable = "t_workstate",joinPKName = "f_id",joinFKName = "f_workstate_id")
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

	@Temporary
	@ApiModelProperty(value = "返回是否有照片", position = 23)
	private Boolean hasPhoto;
}
