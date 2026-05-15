package com.catplanet.module.sighting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class SightingCommentRequest {

    @NotNull(message = "出没记录ID不能为空")
    private Long sightingId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    private String content;

    private List<String> images;
}
