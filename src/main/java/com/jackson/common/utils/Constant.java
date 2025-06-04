package com.jackson.common.utils;

/**
 * 常量
 */
public class Constant {
	/** 超级管理员ID */
	public static final long SUPER_ADMIN = 1l;
    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    /**
     *  文件上传根目录(访问时不需要验证)
     */
    public static final String BASE_ATTACHMENT_DIR = "baseAttachmentDir";

	/**
	 * 菜单类型
	 * 
	 * @author chenshun
	 * @email sunlightcs@gmail.com
	 * @date 2016年11月15日 下午1:24:29
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 账号状态
     */
    public enum UserStatus {
        /**
         * 启用
         */
        UP(1),
        /**
         * 禁用
         */
        OFF(0);

        private int value;

        UserStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 算法类型
     */
    public enum AlgorithmType {
        /**
         * 工件计数
         */
        STEEL("steel"),
        /**
         * 条形码识别
         */
        BARCODE("barcode");

        private String value;

        AlgorithmType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 角色ID
     */
    public enum RoleID {
        /**
         * 起级管理员角色
         */
        SUPERADMIN(1l),
        /**
         * 系统管理员角色
         */
        ADMIN(2l),
        /**
         * 企业管理员角色
         */
        ENTERPRISE_ADMIN(3l),
        /**
         * 钢材计数角色
         */
        WORKER_STEEL(4l),
        /**
         * 条形码角色
         */
        WORKER_BARCODE(5l);

        private Long value;

        RoleID(Long value) {
            this.value = value;
        }

        public Long getValue() {
            return value;
        }
    }

}
