package com.catplanet.module.sighting.vo;

import com.catplanet.common.serializer.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SightingListVO {

    private Long sightingId;
    private Long userId;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String image;

    private String content;
    private BigDecimal lat;
    private BigDecimal lng;
    private String address;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;

    // 发布者信息
    private String nickname;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String userAvatar;
}
