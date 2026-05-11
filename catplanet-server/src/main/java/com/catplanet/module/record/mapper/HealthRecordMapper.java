package com.catplanet.module.record.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.record.entity.HealthRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HealthRecordMapper extends BaseMapper<HealthRecord> {
}
