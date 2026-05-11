package com.catplanet.module.cat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.cat.entity.Cat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CatMapper extends BaseMapper<Cat> {
}
