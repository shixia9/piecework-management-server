package com.jackson.modules.sys.controller;

import com.jackson.common.utils.Constant;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.common.validator.ValidatorUtils;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.common.validator.group.UpdateGroup;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("sys/enterprise")
@Api(tags = "系统管理-企业")
public class SysEnterpriseController extends AbstractController{
    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private SysUserService sysUserService;

    /**
     * 列表
     */
    @PostMapping ("/list")
    @RequiresPermissions("sys:enterprise")
    @ApiOperation("获取企业列表分页")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "企业名称", name = "name", dataType = "string"),
            @ApiImplicitParam(value = "到期时间", name = "rentEndTime", dataType = "date"),
            @ApiImplicitParam(value = "状态", name = "status", dataType = "string", allowableValues = "0:停用，1:正常"),
            @ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
            @ApiImplicitParam(value = "一页行数", name = Constant.LIMIT, dataType = "string")
    })
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = enterpriseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 企业信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("sys:enterprise")
    @ApiOperation("根据Id查询企业")
    public R info(@PathVariable("id") Long id){
        SysEnterpriseEntity enterprise = enterpriseService.getById(id);
        return R.ok().put("entity", enterprise);
    }

    /**
     * 企业员工信息
     */
    @GetMapping("/worker_info")
    @RequiresPermissions("sys:enterprise:workerInfo")
    @ApiOperation("根据企业Id查询员工")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "企业id", name = "id", dataType = "string"),
            @ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
            @ApiImplicitParam(value = "当前页", name = Constant.LIMIT, dataType = "string")
    })
    public R workerInfo(@ApiIgnore @RequestParam Map<String, Object> params){
        R r = enterpriseService.queryByEnterpriseID (params);
        return r;
    }
    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("sys:enterprise")
    @ApiOperation("新增企业")
    public R save( @Valid @RequestBody SysEnterpriseEntity enterprise){
        ValidatorUtils.validateEntity(enterprise, AddGroup.class);

		enterpriseService.saveEnterprise(enterprise);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("sys:enterprise")
    @ApiOperation("修改企业租用信息")
    public R update(@Valid @RequestBody SysEnterpriseEntity enterprise){
        ValidatorUtils.validateEntity(enterprise, UpdateGroup.class);

        enterpriseService.updateEnterprise(enterprise,getUserId ());
        return R.ok();
    }

    @PostMapping("/update/info")
    @RequiresPermissions("sys:enterprise")
    @ApiOperation("修改企业基本信息")
    public R updateInfo(@Valid @RequestBody SysEnterpriseEntity enterprise){
        ValidatorUtils.validateEntity(enterprise, AddGroup.class);
        enterprise.setUpdateTime (new Date ());
        enterprise.setUpdaterId (getUserId ());
        enterpriseService.updateById(enterprise);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("sys:enterprise")
    @ApiOperation("删除企业")
    public R delete(@RequestBody Long[] ids){
		enterpriseService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }


}
