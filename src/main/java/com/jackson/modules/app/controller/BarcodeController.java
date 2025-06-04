package com.jackson.modules.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jackson.common.utils.Constant;
import com.jackson.common.utils.R;
import com.jackson.modules.app.dto.HistoryDTO;
import com.jackson.modules.app.dto.ImageDTO;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.app.vo.ImageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/app/barcode")
@Api(tags="app-条码扫描模块")
public class BarcodeController {
    @Autowired
    private AIService aiService;

//    @PostMapping("/doAi")
//    @ApiOperation("执行条码扫描")
////    @Login("barcode:do")
//    public R   doAI(MultipartFile[] files, String picSize, String steelTypeName,@ApiIgnore @RequestAttribute("userId") Long userId) throws IOException {
//        List<ImageVO> barcode = aiService.save(files, userId, Constant.AlgorithmType.BARCODE.getValue(),picSize ,steelTypeName);
//        return R.ok().put("list", barcode);
//    }

    @PostMapping("/queryHistory")
    @ApiOperation("查询历史记录")
//    @Login("barcode:select")
    public R test1(HistoryDTO historyDTO, @ApiIgnore @RequestAttribute Long userId){
        List<ImageEntity> list = aiService.queryHistory(historyDTO, userId, Constant.AlgorithmType.BARCODE.getValue());
        List<ImageVO> imageVOs = new ArrayList<>();

        for (ImageEntity entity : list) {
            ImageVO imageVO = new ImageVO();
            imageVO.setId(entity.getId());
            //TODO labelString后面根据需求换成String或List
    //        imageVO.setLabelString(entity.getLabelString());
            imageVO.setName(entity.getName());
            imageVOs.add(imageVO);
        }
        return R.ok().put("list", imageVOs);

    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除记录")
//    @Login("barcode:delete")
    public R delete( @PathVariable Long id,@ApiIgnore @RequestAttribute Long userId){
        ImageEntity entity = aiService.getById(id);
        if(entity==null)return R .error(400,"查无此ID");
        File file = new File(entity.getUrl());
        boolean deleted = false;
        if(file.exists()){deleted=file.delete();}
        aiService.remove(new QueryWrapper<ImageEntity>()
                .eq("id",id)
                .eq("user_id",userId));
        if(!deleted){return R .error(500,"删除失败");}
        return R .ok();
    }
    @PostMapping("/update")
    @ApiOperation("更新标签记录")
//    @Login("barcode:update")
    public R update(@Validated ImageDTO image, @ApiIgnore @RequestAttribute Long userId){
        aiService.update(new UpdateWrapper<ImageEntity>()
                .set("label_string",image.getLabelString())
                .eq("id",image.getId())
                .eq("user_id",userId));
        return R.ok();
    }
}
