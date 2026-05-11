package com.catplanet.module.record.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimelineItem {

    private String type;       // feeding / care / health
    private Long recordId;
    private Long catId;
    private String catName;
    private String summary;    // 简短描述
    private LocalDateTime time;
    private Long operatorUserId;
    private String operatorName;
}
