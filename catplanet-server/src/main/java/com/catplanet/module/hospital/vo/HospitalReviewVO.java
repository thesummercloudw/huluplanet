package com.catplanet.module.hospital.vo;

import com.catplanet.common.serializer.ImageUrlListSerializer;
import com.catplanet.common.serializer.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class HospitalReviewVO {

    private Long reviewId;
    private Long hospitalId;
    private Long userId;
    private Integer score;
    private String content;

    private List<String> serviceTags;

    @JsonSerialize(using = ImageUrlListSerializer.class)
    private List<String> images;

    private LocalDateTime createdAt;

    // 用户信息
    private String nickname;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String avatar;
}
