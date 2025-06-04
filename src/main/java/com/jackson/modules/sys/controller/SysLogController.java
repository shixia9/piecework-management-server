package com.jackson.modules.sys.controller;

import java.util.Arrays;
import java.util.Map;

import com.jackson.common.utils.Constant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jackson.modules.sys.entity.SysLogEntity;
import com.jackson.modules.sys.service.SysLogService;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import springfox.documentation.annotations.ApiIgnore;


@RestController
@RequestMapping("sys/syslog")
@Api(tags = "系统管理-日志")
public class SysLogController {
    @Autowired
    private SysLogService sysLogService;

    /**
     * 列表
     */
    @PostMapping("/page")
    @RequiresPermissions("sys:syslog:page")
    @ApiOperation("获取日志列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "操作类型", name = "operation", dataType = "string"),
            @ApiImplicitParam(value = "操作人", name = "name", dataType = "string"),
            @ApiImplicitParam(value = "操作开始时间", name = "startDate", dataType = "date"),
            @ApiImplicitParam(value = "操作结束时间", name = "endDate", dataType = "date"),

            @ApiImplicitParam(value = "日志内容", name = "text", dataType = "string"),
            @ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
            @ApiImplicitParam(value = "一页大小", name = Constant.LIMIT, dataType = "string")
    })
    public R list(@ApiIgnore @RequestParam Map<String, Object> params){
        PageUtils page = sysLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:syslog:info")
    public R info(@PathVariable("id") Long id){
		SysLogEntity sysLog = sysLogService.getById(id);

        return R.ok().put("sysLog", sysLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:syslog:save")
    public R save(@RequestBody SysLogEntity sysLog){
		sysLogService.save(sysLog);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:syslog:update")
    public R update(@RequestBody SysLogEntity sysLog){
		sysLogService.updateById(sysLog);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:syslog:delete")
    public R delete(@RequestBody Long[] ids){
		sysLogService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
