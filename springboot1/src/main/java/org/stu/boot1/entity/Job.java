package org.stu.boot1.entity;

import org.stu.boot1.Entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <b>Job。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */

@Getter
@Setter
@ApiModel(description = "职务实体")
public class Job extends Entity {

	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -4647481492203601816L;

	@ApiModelProperty(value = "代码", required = true, position = 1)
	private String code;

	@ApiModelProperty(value = "名称", required = true, position = 2)
	private String name;

	@ApiModelProperty(value = "备注", position = 3)
	private String descr;

}
