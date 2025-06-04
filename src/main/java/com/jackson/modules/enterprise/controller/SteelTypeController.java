package com.jackson.modules.enterprise.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.jackson.common.annotation.SysLog;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;

import com.jackson.common.validator.Assert;
import com.jackson.modules.enterprise.entity.BussImageEntity;
import com.jackson.modules.enterprise.service.BussImageService;
import com.jackson.common.validator.ValidatorUtils;
import com.jackson.common.validator.group.AddGroup;
import com.jackson.common.validator.group.UpdateGroup;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.service.SteelTypeService;
import com.jackson.modules.enterprise.vo.SteelTypeVO;
import com.jackson.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/enterprise/steelType")
@Api(tags="企业管理-工件号管理")
@ApiSort(100)
public class SteelTypeController extends AbstractController {
    @Autowired
    private SteelTypeService steelTypeService;

    /**
     * 分页
     */
    @RequestMapping("/page")
    @RequiresPermissions("enterprise:steelType:page")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = steelTypeService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 分页
     */

    @ApiOperation("查询工件号")
    @PostMapping("/list")
    @SysLog("查询工件号")
    @RequiresPermissions("enterprise:steelType:usedList")
    public R usedList(){
        QueryWrapper<SteelTypeEntity> wrapper = new QueryWrapper<SteelTypeEntity>().eq("enterprise_id",getUser().getEnterpriseId());
        List<SteelTypeEntity> list = steelTypeService.list(wrapper);
        return R.ok().put("list", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{steelId}")
    @RequiresPermissions("enterprise:steelType:info")
    public R info(@PathVariable("steelId") Long steelId){
		SteelTypeEntity steelType = steelTypeService.getById(steelId);

        return R.ok().put("steelType", steelType);
    }

    /**
     * 保存
     */
    @SysLog("保存新的工件号")
    @PostMapping("/saveSteelType")
    @ApiOperation("保存新的工件号")
    @RequiresPermissions("enterprise:steelType:save")
    public R saveNewSteelType(@Valid @RequestBody SteelTypeEntity steelType) {
        ValidatorUtils.validateEntity(steelType, AddGroup.class);

        steelTypeService.saveNewSteelType(steelType,getUser());

        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("修改工件号")
    @PostMapping("/update")
    @ApiOperation("修改工件号")
    @RequiresPermissions("enterprise:steelType:update")
    public R update(@Valid @RequestBody SteelTypeEntity steelType){
        ValidatorUtils.validateEntity(steelType, UpdateGroup.class);
        Assert.isTheOne(steelType.getBaseNum(),0,"工件号基数不能为零");
        steelTypeService.updateSteelType(steelType,getUser());

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("删除工件号")
    @PostMapping("/delete")
    @ApiOperation("删除工件号")
    @RequiresPermissions("enterprise:steelType:delete")
    public R delete(@RequestBody Long[] steelIds){

		steelTypeService.delete(Arrays.asList(steelIds));


        return R.ok();
    }


   @SysLog("展示工件号")
   @PostMapping("/showSteelType")
   @ApiOperation("展示工件号")
   @ApiImplicitParams({
           @ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
           @ApiImplicitParam(value = "一页大小", name = Constant.LIMIT, dataType = "string")
   })
   @RequiresPermissions("enterprise:steelType")
   public R showSteelType(@ApiIgnore @RequestParam Map<String, Object> params) {
       PageUtils page = steelTypeService.showSteelType(getUser(),params);
       return R.ok().put("page",page);
   }

}
