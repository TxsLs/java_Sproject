package org.teach.study.boot.entity;

import org.teach.study.boot.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "工作状态实体")
public class Workstate extends Entity {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -7748107279747664901L;

	@ApiModelProperty(value = "代码", required = true, position = 1)
	private String code;

	@ApiModelProperty(value = "名称", required = true, position = 2)
	private String name;

	@ApiModelProperty(value = "备注", position = 3)
	private String descr;
}
