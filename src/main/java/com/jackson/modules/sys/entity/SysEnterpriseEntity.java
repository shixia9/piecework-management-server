package com.jackson.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.jackson.common.utils.Constant;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.common.validator.group.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("enterprise")
@ApiModel
public class SysEnterpriseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	@TableId
	@NotNull(message = "企业ID不能为空", groups = {UpdateGroup.class})
	@JsonSerialize(using = ToStringSerializer.class)

	private Long id;
	/**
	 * 
	 */
	@ApiModelProperty("企业名字")
	@NotBlank(message = "企业名字不能为空",groups = {AddGroup.class})
	@TableField("`name`")
	private String name;
	/**
	 * 统一社会机构代码
	 */
	@ApiModelProperty("统一社会机构代码")
	@TableField("`code`")

	private String code;
	/**
	 * 联系地址
	 */
	@NotBlank(message = "联系地址不能为空", groups = {AddGroup.class})
	@ApiModelProperty("联系地址")
	private String adress;
	/**
	 * 联系人
	 */
	@NotBlank(message = "联系人不能为空", groups = {AddGroup.class})
	@ApiModelProperty("联系人")
	private String contact;
	/**
	 * 联系电话
	 */
	@NotBlank(message = "联系电话不能为空", groups = {AddGroup.class})
	private String phone;
	/**
	 * 租用起始时间
	 */
	@ApiModelProperty("租用起始时间")
	@NotNull(message = "租用起始时间不能为空", groups = {UpdateGroup.class})
	private Date rentStartTime;
	/**
	 * 租用结束时间
	 */
	@ApiModelProperty("租用结束时间")
	@NotNull(message = "租用结束时间不能为空", groups = {UpdateGroup.class})
	private Date rentEndTime;
	/**
	 * APP账号额度
	 */
	@ApiModelProperty("APP账号额度")
	@NotNull(message = "APP账号额度不能为空", groups = {UpdateGroup.class})
	private Integer accountLimit ;

	/**
	 * 可使用算法功能
	 */
	@ApiModelProperty("可使用算法功能 steel/barcode/steel,barcode 三种写法")
	@NotBlank(message = "可使用算法功能不能为空", groups = {UpdateGroup.class})
	@TableField("`function`")

	private String function;
	/**
	 * 0:禁用 1:正常使用
	 */
	@NotNull(message = "状态不能为空", groups = {AddGroup.class})
	@ApiModelProperty("0:禁用 1:正常使用")
	@TableField("`status`")
	private Integer status;

	public String getStatusDesc() {
		String desc = "";
		if(Constant.UserStatus.UP.getValue() == this.status) {
			desc = "正常";
		}else if(Constant.UserStatus.OFF.getValue() == this.status) {
			desc = "禁用";
		}
		return desc;
	}
	/**
	 * 图片储存额度
	 */
	@ApiModelProperty("图片储存额度")
	@NotNull(message = "图片储存额度不能为空", groups = {UpdateGroup.class})
	private Float storageLimit = 0f;

	public String getStorageLimitDesc() {
		//浮点数且保留小数点后两位
		String desc = String.format("%.2f", this.storageLimit / 1024.0 ) + "G";

		return desc;
	}
	/**
	 * 图片储存已用额度
	 */
	@ApiModelProperty("图片储存已用额度")
	private Float storageUsed = 0f;;

	public String getStorageUsedDesc() {
		//浮点数且保留小数点后两位
		String desc = String.format("%.2f", this.storageUsed / 1024.0 ) + "G";

		return desc;
	}
	/**
	 * APP账号已用额度
	 */
	@ApiModelProperty("APP账号已用额度")
	private Integer accountUsed;
	/**
	 * 创建人
	 */
	@JsonIgnore
	private Long createUserId;
	/**
	 * 创建时间
	 */
	@JsonIgnore
	private Date createTime;
	/**
	 * 更新人
	 */
	@JsonIgnore
	private Long updaterId;
	/**
	 * 更新时间
	 */
	@JsonIgnore
	private Date updateTime;
}
