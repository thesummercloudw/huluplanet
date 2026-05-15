package com.catplanet.module.sighting.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.sighting.entity.SightingComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SightingCommentMapper extends BaseMapper<SightingComment> {
}
