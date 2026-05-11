package com.catplanet.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.mapper.UserMapper;
import com.catplanet.module.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User findByOpenid(String openid) {
        return getOne(new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));
    }

    @Override
    public User findOrCreate(String openid, String unionid) {
        User user = findByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setUnionid(unionid);
            user.setNickname("猫奴" + System.currentTimeMillis() % 10000);
            save(user);
        }
        return user;
    }
}
