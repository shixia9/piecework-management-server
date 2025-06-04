package com.jackson.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 系统用户
 */
@Data
@TableName("sys_user")
@ApiModel
public class SysUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@NotNull(message = "用户ID不能为空", groups = {UpdateGroup.class})
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 账号
	 */
	@ApiModelProperty(required = true)
	@NotBlank(message="账号不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String username;
	/**
	 * 账号
	 */
	@NotBlank(message="姓名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@TableField("`name`")
	private String name;
	/**
	 * 密码
	 */
	@NotBlank(message="密码不能为空", groups = AddGroup.class)
	private String password;
	/**
	 * 盐
	 */
	@JsonIgnore
	private String salt;

	/**
	 *
	 */
	@NotBlank(message="联系电话不能为空", groups = AddGroup.class)
	private String phone;
	/**
	 * 离线时间
	 */
	private Date logoutTime;
	/**
	 * 1：超级管理员；2：平台人员；3：用户
	 */

	public String getRoleIdDesc() {
		String desc = "";

		if(this.getRoleIdList().contains(Constant.RoleID.SUPERADMIN.getValue())) {
			desc = "超级管理员";
		}else if(this.getRoleIdList().contains(Constant.RoleID.ADMIN.getValue())) {
			desc = "系统管理员";
		}else if(this.getRoleIdList().contains(Constant.RoleID.ENTERPRISE_ADMIN.getValue() )) {
			desc = "企业管理员";
		}
		else {
			desc = "APP用户";
		}
		return desc;
	}

	/**
	 * 手机唯一识别码
	 */
	@JsonIgnore
	private Long imei ;
	/**
	 *
	 */
//	@NotNull(message="企业信息不能为空", groups = AddGroup.class)
	private Long enterpriseId;
	/**
	 * 状态  0：禁用   1：正常
	 */
	@NotNull(message="状态不能为空", groups = {AddGroup.class, UpdateGroup.class})
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
	 * 创建者ID
	 */
	private Long createUserId;

	/**
	 * 创建者姓名
	 */
	@TableField(exist=false)
	private String createUserName;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新者ID
	 */
	@JsonIgnore
	private Long updateUserId;

	/**
	 * 更新时间
	 */
	@JsonIgnore
	private Date updateTime;

	/**
	 * 角色ID列表
	 */
	@TableField(exist=false)
	@ApiModelProperty(value = "系统管理员为2,企业管理为 3, 钢材计数为4,barcode为5",required = true)
	private List<Long> roleIdList;

}
