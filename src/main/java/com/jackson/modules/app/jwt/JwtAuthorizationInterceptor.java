package com.jackson.modules.app.jwt;

import com.jackson.common.exception.RRException;
import com.jackson.modules.sys.service.SysUserService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 权限(Token)验证
 */
@Component
public class JwtAuthorizationInterceptor implements HandlerInterceptor {
    public static final String USER_KEY = "userId";
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private SysUserService sysUserService ;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        JwtLogin annotation;
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(JwtLogin.class);
        } else {
            return true;
        }

        if (annotation == null) {
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(jwtUtils.getHeader());
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(jwtUtils.getHeader());
        }

        //凭证为空
        if (StringUtils.isBlank(token)) {
            throw new RRException(jwtUtils.getHeader() + "不能为空", HttpStatus.UNAUTHORIZED.value());
        }

        Claims claims = jwtUtils.getClaimByToken(token);
        if (claims == null || jwtUtils.isTokenExpired(claims.getExpiration())) {
            throw new RRException(jwtUtils.getHeader() + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
        }
        long userId = Long.parseLong(claims.getSubject());

        String[] values = annotation.value();
        //login注解里的内容不为null; 检查权限
        if(values.length>0&&(!values[0].equals(""))){
            //System.err.println("需要的权限"+values[0]);
            //1 去数据库里查询 用户的所有权限；

            List<String> perms = sysUserService.queryAllPerms(userId);
            //System.err.println("拥有的权限"+perms);

            //检查权限
            if(!checkPerms(perms,values)) throw new RRException("权限不足",400);

        }
        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(USER_KEY, userId);

        return true;
    }

    private boolean checkPerms(List<String> hadPerms, String[] needPerms){
        for (String perm : hadPerms) {
            if(perm==null||perm.equals("")) continue;
            String[] split = perm.split(",");
            for (int i = 0; i <split.length ; i++) {
                //这里进行权限检查
                String str = split[i].replace("*",".*");
                if(needPerms[0].matches(str)){
                    return true;
                }
            }
        }
        return false;
    }
}
