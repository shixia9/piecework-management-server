package com.jackson.modules.enterprise.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;


@Data
@TableName("steel_type")
public class SteelTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	@NotNull(message = "工件号ID不能为空",groups = {UpdateGroup.class})
	private Long steelId;
	/**
	 * 钢材类型
	 */
	@NotBlank(message = "工件名称不能为空",groups = {AddGroup.class})
	private String name;

	@TableField(exist = false)
	private String username;


	@NotNull(message = "状态不能为空", groups = {AddGroup.class})
	@ApiModelProperty("0:禁用 1:正常使用")
	@TableField("`status`")
	private Integer status;
	/**
	 * 
	 */
	@NotNull(message = "工件基数不能为空",groups = {AddGroup.class,UpdateGroup.class})
	private Integer baseNum;
	/**
	 *
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	private Date createTime;
	/**
	 * 
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@JsonIgnore
	private Date updateTime;
	/**
	 * 
	 */
	@JsonIgnore
	private Long updatorId;

	@JsonIgnore
	private Long createUserId;


	@JsonIgnore
	private Long enterpriseId;
}
