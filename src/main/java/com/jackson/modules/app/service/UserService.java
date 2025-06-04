package com.jackson.modules.app.service;

import com.google.common.util.concurrent.AbstractListeningExecutorService;
import com.jackson.common.utils.R;
import com.jackson.modules.app.entity.ImageEntity;
import com.jackson.modules.sys.entity.SysUserEntity;

import java.util.List;

public interface UserService {
    /**
     * APP登录
     * @param username
     * @param password
     * @return
     */
    R login(String username, String password);


}
