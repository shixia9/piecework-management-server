package com.jackson.modules.enterprise.controller;

import com.jackson.common.annotation.SysLog;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.R;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.enterprise.entity.BussImageEntity;
import com.jackson.modules.enterprise.service.BussImageService;
import com.jackson.modules.enterprise.vo.ImageUserVO;
import com.jackson.modules.sys.controller.AbstractController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/enterprise")
@Api(tags = "企业管理-计数统计")
public class BussImageController extends AbstractController {
    @Autowired
    private BussImageService imageService;

    @PostMapping("/imageTotal")
    @ApiOperation("统计")
    @RequiresPermissions("enterprise:imageTotal")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "开始时间", name = "dateStart", dataType = "date"),
            @ApiImplicitParam(value = "结束时间", name = "dateEnd", dataType = "date"),
            @ApiImplicitParam(value = "工件号", name = "steelId", dataType = "String"),
            @ApiImplicitParam(value = "账号ID", name = "userId", dataType = "string")
    })
    @SysLog("查看本公司计数统计")
    public R imageTotal(@ApiIgnore @RequestParam Map<String,Object> params) {
        List<ImageUserVO> list =  imageService.steelTotal(getUser(),params);
        return R.ok().put("list",list);
    }

    @PostMapping("/imageDetail")
    @ApiOperation("详情")
    @RequiresPermissions("enterprise:imageTotal")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "时间", name = "date", dataType = "date",required = true),
            @ApiImplicitParam(value = "工件号", name = "steelId", dataType = "String",required = true),
            @ApiImplicitParam(value = "用户ID", name = "userId", dataType = "string",required = true),
            @ApiImplicitParam(value = "页码", name = Constant.PAGE, dataType = "string"),
            @ApiImplicitParam(value = "当前页", name = Constant.LIMIT, dataType = "string")
    })
    @SysLog("查看本公司计数统计--详情")
    public R imageDetail(@ApiIgnore @RequestParam Map<String,Object> params) {
        PageUtils page =  imageService.imageDetail(params);
        return R.ok().put("page",page);
    }
}
