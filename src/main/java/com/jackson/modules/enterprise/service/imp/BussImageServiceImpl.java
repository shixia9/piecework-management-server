package com.jackson.modules.enterprise.service.imp;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jackson.common.utils.DateUtils;
import com.jackson.common.utils.PageUtils;
import com.jackson.common.utils.Query;
import com.jackson.common.utils.ShiroUtils;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.enterprise.dao.BussImageMapper;
import com.jackson.modules.enterprise.entity.BussImageEntity;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.service.BussImageService;
import com.jackson.modules.enterprise.service.SteelTypeService;
import com.jackson.modules.enterprise.vo.ImageUserVO;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("bussImageService")
@RequiredArgsConstructor
public class BussImageServiceImpl extends ServiceImpl<BussImageMapper, BussImageEntity> implements BussImageService {


    private final SysUserService sysUserService;
    private final SteelTypeService steelTypeService;

    @Override
    public List<ImageUserVO> steelTotal(SysUserEntity user, Map<String,Object> params) {

        String steelId = (String)params.get("steelId");
        String userId = (String)params.get("userId");
        String dateStart = (String)params.get("dateStart");
        String dateEnd = (String)params.get("dateEnd");

        QueryWrapper<BussImageEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("enterprise_id",user.getEnterpriseId())
                .eq (StringUtils.isNotBlank(userId),"user_id",userId)
                .eq(StringUtils.isNotBlank(steelId),"steel_id",steelId)
                .ge(StringUtils.isNotBlank(dateStart),"create_time",params.get("dateStart"))
                .le(StringUtils.isNotBlank(dateEnd),"create_time",params.get("dateEnd"))
                .select("user_id","create_time","steel_id","num")
                .orderByDesc("create_time");

        List<BussImageEntity> list = this.list (wrapper);
        for (BussImageEntity bussImage: list) {
            SysUserEntity operator = sysUserService.getById (bussImage.getUserId());

            SteelTypeEntity steel = steelTypeService.getById(bussImage.getSteelId());
            bussImage.setCreateUserName (operator.getName ());
            bussImage.setSteelTypeName(steel.getName());
        }
        List<ImageUserVO> imageUserVOS = new ArrayList<>();

        //增加String createTimeStr存储CreateTime转换的字符串 方便判断是否处于同一天
        list.forEach (x->x.setCreateTimeStr (DateUtils.format (x.getCreateTime ())));//2022-04-13
        //根据工件号去重
        List<String> steelTypeList = list.stream().map(BussImageEntity::getSteelTypeName).distinct().collect(Collectors.toList());
        //循环每一个工件号
        for (String steelType:steelTypeList
        ) {
            //根据工件号分组
            List<BussImageEntity> filterList = list.stream().filter(a -> a.getSteelTypeName ().equals(steelType)).collect(Collectors.toList());
            //根据时间去重
            List<String> createTimeStrList = filterList.stream().map(BussImageEntity::getCreateTimeStr).distinct().collect(Collectors.toList());
            //循环时间字符串的种类 例如2022-04-09
            for (String createTimeStr: createTimeStrList
            ) {
                //根据工件号,时间分组
                List<BussImageEntity> filterList1 = filterList.stream().filter(a -> a.getCreateTimeStr ().equals (createTimeStr)).collect(Collectors.toList());
                //根据账号Id去重
                List<Long> userIdList = filterList1.stream().map(BussImageEntity::getUserId).distinct().collect(Collectors.toList());
                //循环去重后的用户
                for (Long id:userIdList
                ) {
                    //根据工件号,时间,账号id分组
                    List<BussImageEntity> filterList2 = filterList1.stream().filter(a -> a.getUserId ().equals (id)).collect(Collectors.toList());
                    ImageUserVO imageUserVO = new ImageUserVO ();
                    int num = 0;
                    for (BussImageEntity image: filterList2
                    ) {
                        imageUserVO.setUserId (image.getUserId ());
                        imageUserVO.setSteelId(image.getSteelId());
                        imageUserVO.setCreateTime (image.getCreateTime ());
                        num += image.getNum ();
                    }
                    imageUserVO.setCreateTimeStr (createTimeStr);
                    imageUserVO.setCreateUserName (filterList2.get (0).getCreateUserName ());
                    imageUserVO.setSteelTypeName (steelType);
                    imageUserVO.setImageNum (filterList2.size ());
                    imageUserVO.setNum (num);
                    imageUserVOS.add (imageUserVO);


                }
            }
        }
        return  imageUserVOS;
    }

    @Override
    public PageUtils imageDetail(Map<String, Object> params) {

        String date = (String)params.get("date"); //date为YYYY-MM-dd格式的字符串

        QueryWrapper<BussImageEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("enterprise_id",ShiroUtils.getUserEntity().getEnterpriseId())
                .eq ("user_id",params.get("userId"))
                .eq("steel_id",params.get("steelId"))
                .ge("create_time",DateUtil.parseDate(date))
                .le("create_time",DateUtil.endOfDay(DateUtil.parseDate(date)))
                .select("user_id","create_time","steel_id","name","num")
                .orderByDesc("create_time");
        IPage<BussImageEntity> page = this.page(
                new Query<BussImageEntity>().getPage(params,"create_time",true),
                wrapper
        );
        return new PageUtils(page);
    }

}
