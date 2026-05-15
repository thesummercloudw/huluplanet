package com.catplanet.module.sighting.vo;

import com.catplanet.common.serializer.ImageUrlListSerializer;
import com.catplanet.common.serializer.ImageUrlSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SightingCommentVO {

    private Long commentId;
    private Long sightingId;
    private Long userId;
    private String content;

    @JsonSerialize(using = ImageUrlListSerializer.class)
    private List<String> images;

    private LocalDateTime createdAt;

    // 用户信息
    private String nickname;

    @JsonSerialize(using = ImageUrlSerializer.class)
    private String avatar;
}
