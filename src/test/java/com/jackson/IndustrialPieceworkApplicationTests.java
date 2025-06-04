package com.jackson;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.jackson.common.utils.DateUtils;
import com.jackson.common.validator.Assert;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.app.service.AIService;
import com.jackson.modules.enterprise.entity.BussImageEntity;
import com.jackson.modules.enterprise.entity.SteelTypeEntity;
import com.jackson.modules.enterprise.service.BussImageService;
import com.jackson.modules.enterprise.service.SteelTypeService;
import com.jackson.modules.enterprise.vo.ImageUserVO;
import com.jackson.modules.sys.entity.SysEnterpriseEntity;
import com.jackson.modules.sys.entity.SysUserEntity;
import com.jackson.modules.sys.service.EnterpriseService;
import com.jackson.modules.sys.service.SysRoleService;
import com.jackson.modules.sys.service.SysUserRoleService;
import com.jackson.modules.sys.service.SysUserService;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SpringBootTest
class IndustrialPieceworkApplicationTests {

	@Autowired
	private AIService aiService;
	@Autowired
	private BussImageService imageService;
	@Autowired
	SysUserService sysUserService;
	@Autowired
	EnterpriseService enterpriseService;
	@Autowired
	SteelTypeService steelTypeService;
	@Autowired
	RedisTemplate redisTemplate;
	private SysUserRoleService sysUserRoleService;
	private SysRoleService sysRoleService;
	@Autowired
	public void setSysRoleService(SysRoleService sysRoleService) {
		this.sysRoleService = sysRoleService;
	}
	@Autowired
	public void setSysUserRoleService(SysUserRoleService sysUserRoleService) {
		this.sysUserRoleService = sysUserRoleService;
	}
	@Test
	public void testImg2() throws IOException {
		SysUserEntity user = sysUserService.getOne(new QueryWrapper<SysUserEntity>()
				.select("enterprise_id")
				.eq("id", "1505594403031465985"));
		Assert.isNull(user.getEnterpriseId(),"员工无归属企业");
		//查找员工对应公司
		SysEnterpriseEntity enterpriseEntity = enterpriseService.getById(user.getEnterpriseId());


		//此时图片容量存储是四舍五入状态  1.4==> 1.0   1.5==>2.0
		enterpriseService.update(new UpdateWrapper<SysEnterpriseEntity>()
				.set("storage_used",enterpriseEntity.getStorageUsed() + Float.parseFloat(String.format("%.2f", 1.2323 )))
				.eq("id",user.getEnterpriseId()));

	}
	@Test
	public void testImg3() throws IOException {
		//redisTemplate
	}
	@Test
	public void test3() {
		//System.out.println (DateUtils.format (new Date ()));
		List<ImageUserVO> imageUserVOS = new ArrayList<> ();
		List<BussImageEntity> list =  imageService.list ();
		for (BussImageEntity bussImage: list) {
			SysUserEntity operator = sysUserService.getById (bussImage.getUserId());

			SteelTypeEntity steel = steelTypeService.getById(bussImage.getSteelId());
			bussImage.setCreateUserName (operator.getName ());
			bussImage.setSteelTypeName(steel.getName());
		}
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
				for (Long userId:userIdList
					 ) {
					//根据工件号,时间,账号id分组
					List<BussImageEntity> filterList2 = filterList1.stream().filter(a -> a.getUserId ().equals (userId)).collect(Collectors.toList());
					ImageUserVO imageUserVO = new ImageUserVO ();
					int num = 0;
					for (BussImageEntity image: filterList2
						 ) {
						imageUserVO.setUserId (image.getUserId ());
						imageUserVO.setCreateTime (image.getCreateTime ());
						num += image.getNum ();
					}
					imageUserVO.setCreateTimeStr (createTimeStr);
					imageUserVO.setCreateUserName (filterList2.get (0).getCreateUserName ());
					imageUserVO.setSteelTypeName (steelType);
					imageUserVO.setImageNum (filterList2.size ());
					imageUserVO.setNum (num);
					imageUserVOS.add (imageUserVO);
					System.err.println(imageUserVO);

				}
			}
		}
	}
	@Test
	public void test4() throws IOException {
		System.out.println (DateUtil.endOfDay(DateUtil.parseDate("2022-04-22")));
		System.out.println(DateUtil.parseDate("2022-04-22"));
		System.out.println(DateUtil.parse("2022-04-22", "yyyy-MM-dd HH:mm:ss"));

	}
	@Test
	public void test5() throws IOException {
		ImageEntity one = aiService.getOne (new QueryWrapper<ImageEntity> ().eq ("`name`", "8b6e9252cbbe42a3b4ac7651a657e0b3.jpg"));

		List labelStringList = JSON.parseArray (one.getLabelString ());
		System.out.println (labelStringList);
	}
}
