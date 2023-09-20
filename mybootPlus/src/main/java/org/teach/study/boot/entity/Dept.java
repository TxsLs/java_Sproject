package org.teach.study.boot.entity;

import javax.validation.constraints.NotBlank;

import org.teach.study.boot.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "部门实体")
public class Dept extends Entity {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -2851997269867880911L;

	@ApiModelProperty(value = "代码", required = true, position = 1)
	@NotBlank(message = "{valid.dept.code.notblank.message}")
	private String code;

	@ApiModelProperty(value = "名称", required = true, position = 2)
	@NotBlank(message = "部门名称不能为空!")
	private String name;

	@ApiModelProperty(value = "备注", position = 3)
	private String descr;
}
