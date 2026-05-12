package com.catplanet.module.sighting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.sighting.entity.CatSighting;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CatSightingMapper extends BaseMapper<CatSighting> {
}
