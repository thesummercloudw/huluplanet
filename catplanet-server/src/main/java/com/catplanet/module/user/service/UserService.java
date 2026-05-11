package com.catplanet.module.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.catplanet.module.user.entity.User;

public interface UserService extends IService<User> {

    User findByOpenid(String openid);

    User findOrCreate(String openid, String unionid);
}
