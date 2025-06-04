package com.jackson.modules.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSort;
import com.jackson.common.exception.RRException;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.R;
import com.jackson.common.validator.Assert;
import com.jackson.common.validator.ValidatorUtils;
import com.jackson.common.validator.group.UpdateGroup;
import com.jackson.modules.app.dto.HistoryDTO;
import com.jackson.modules.app.dto.ImageDTO;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.jwt.JwtLogin;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.app.vo.ImageVO;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.service.SteelTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/steel")
@Api(tags="app-钢材AI计数模块")
public class AIController {

    private final AIService aiService;
    private final SteelTypeService steelTypeService;
    @PostMapping("/doAi")
    @ApiOperation("执行钢材计数功能")
    @JwtLogin()
    public R  doAI( MultipartFile file,String picSize ,Long steelId ,@ApiIgnore @RequestAttribute("userId") Long userId) throws IOException {
        Assert.isNull(file,"上传files文件不能为空");

        //保存到数据库和本地
        Long id = aiService.save(file,userId,Constant.AlgorithmType.STEEL.getValue(),picSize,steelId);

        return R.ok().put("id", id);
    }

    @PostMapping("/redoAi")
    @ApiOperation("重新执行钢材计数功能")
    @JwtLogin()
    public R  redoAi(@RequestParam Long imageId,@ApiIgnore @RequestAttribute("userId") Long userId) throws IOException {
        aiService.reSave(imageId,userId);
        return R.ok();
    }
//
//    @GetMapping("/getImage/{name}")
//    @ApiOperation("获取图片")
//    @JwtLogin()
//    public byte[] getImage(@PathVariable String name,HttpServletResponse response, HttpServletRequest request,@ApiIgnore Long userId){
//
//        aiService.getImage(name,response,request,userId);
//
//        return new byte[1];
//    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @ApiOperation("删除图片")
    @JwtLogin()
    public R delete(@RequestBody Long[] ids){
        aiService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    @PostMapping("/queryById")
    @ApiOperation("根据Id查图片记录")
    @JwtLogin("steel:select")
    public R  queryById( @RequestParam Long id, @ApiIgnore @RequestAttribute Long userId){
        ImageEntity ImageEntity = aiService.queryById(id,userId,Constant.AlgorithmType.STEEL.getValue());
        return R.ok().put("entity", ImageEntity);

    }

    @PostMapping("/queryHistory")
    @ApiOperation("查询历史记录")
    @JwtLogin("steel:select")
    public R  queryHistory(HistoryDTO historyDTO, @ApiIgnore @RequestAttribute Long userId){

        List<ImageEntity> list = aiService.queryHistory(historyDTO, userId,Constant.AlgorithmType.STEEL.getValue());
        return R.ok().put("list", list);
    }

    @PostMapping("/update")
    @ApiOperation("更新标注框")
    @JwtLogin("steel:update")
    @ApiOperationSupport(order = 6)
    public R  update(@RequestBody ImageDTO imageDTO, @ApiIgnore @RequestAttribute Long userId){
        ValidatorUtils.validateEntity(imageDTO, UpdateGroup.class);
        ImageEntity imageEntity = aiService.getById (imageDTO.getId ());
        JSONArray array = JSON.parseArray(imageDTO.getLabelString());//拿到数组
        Integer baseNum = steelTypeService.getById(imageEntity.getSteelId ()).getBaseNum();
        aiService.update(
                new UpdateWrapper<ImageEntity>().set("label_string",imageDTO.getLabelString())
                .set("num",array.size() * baseNum)
                .eq("id",imageDTO.getId())
                .eq("user_Id",userId)
        );
        return R .ok();
    }

    @PostMapping("/updateTypeAndDate")
    @ApiOperation("更新图片工件号与日期")
    @JwtLogin("steel:update")
    @ApiOperationSupport(order = 7)
    public R  updateTypeAndDate (@RequestBody ImageDTO imageDTO, @ApiIgnore @RequestAttribute Long userId){
        Assert.isNull(imageDTO.getId(),"图片Id不能为null");
//        if (StringUtils.isBlank(imageDTO.getSteelTypeName()) && imageDTO.getCreateTime() == null){
//            throw new RRException("图片更新内容不能为空");
//        }
        aiService.update(
                new UpdateWrapper<ImageEntity>()
                        .set(imageDTO.getCreateTime() != null,"create_time",imageDTO.getCreateTime())
                        .set(imageDTO.getSteelId() != null ,"steel_id",imageDTO.getSteelId())
                        .set (imageDTO.getList () !=null,"manual_num_list",JSON.toJSONString (imageDTO.getList ()))
                        .eq("id",imageDTO.getId())
                        .eq("user_Id",userId)
        );
        return R .ok();
    }


    @ApiOperation("查询工件号")
    @PostMapping("/steelTypeList")
    @JwtLogin("steel:update")
    public R list(Long steelId,@ApiIgnore @RequestAttribute Long userId){

        R r = steelTypeService.steelTypeList(steelId,userId);
        return r;
    }


    @ApiOperation("更新工件号基数")
    @PostMapping("/updateBaseNum")
    @JwtLogin("steel:update")
    public R updateBaseNum(@RequestBody SteelTypeEntity steelType, @ApiIgnore @RequestAttribute Long userId){
        Assert.isNull(steelType.getSteelId(),"工件Id不能为空");
        Assert.isNull(steelType.getBaseNum(),"工件基数不能为空");

        steelTypeService.updateBaseNum(steelType,userId);
        return R.ok();
    }
}
