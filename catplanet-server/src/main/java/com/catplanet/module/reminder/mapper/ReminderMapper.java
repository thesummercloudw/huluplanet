package com.catplanet.module.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.catplanet.module.reminder.entity.Reminder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReminderMapper extends BaseMapper<Reminder> {
}
