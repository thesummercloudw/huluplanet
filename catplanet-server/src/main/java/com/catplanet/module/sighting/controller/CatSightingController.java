package com.catplanet.module.sighting.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.sighting.dto.SightingCommentRequest;
import com.catplanet.module.sighting.dto.SightingRequest;
import com.catplanet.module.sighting.entity.CatSighting;
import com.catplanet.module.sighting.entity.SightingComment;
import com.catplanet.module.sighting.service.CatSightingService;
import com.catplanet.module.sighting.service.SightingCommentService;
import com.catplanet.module.sighting.vo.SightingCommentVO;
import com.catplanet.module.sighting.vo.SightingListVO;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sightings")
@RequiredArgsConstructor
public class CatSightingController {

    private final CatSightingService sightingService;
    private final SightingCommentService commentService;
    private final UserService userService;

    @PostMapping
    public Result<CatSighting> create(@Valid @RequestBody SightingRequest request) {
        Long userId = UserContext.getUserId();
        CatSighting sighting = sightingService.create(request, userId);
        return Result.ok(sighting);
    }

    @GetMapping
    public Result<List<SightingListVO>> listRecent(@RequestParam(defaultValue = "20") int limit) {
        List<CatSighting> list = sightingService.listRecent(limit);

        // 批量查询发布者信息
        List<Long> userIds = list.stream()
                .map(CatSighting::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of() :
                userService.listByIds(userIds).stream()
                        .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));

        List<SightingListVO> result = list.stream().map(s -> {
            SightingListVO vo = new SightingListVO();
            vo.setSightingId(s.getSightingId());
            vo.setUserId(s.getUserId());
            vo.setImage(s.getImage());
            vo.setContent(s.getContent());
            vo.setLat(s.getLat());
            vo.setLng(s.getLng());
            vo.setAddress(s.getAddress());
            vo.setLikeCount(s.getLikeCount());
            vo.setCommentCount(s.getCommentCount());
            vo.setCreatedAt(s.getCreatedAt());
            User user = userMap.get(s.getUserId());
            vo.setNickname(user != null ? user.getNickname() : null);
            vo.setUserAvatar(user != null ? user.getAvatar() : null);
            return vo;
        }).collect(Collectors.toList());

        return Result.ok(result);
    }

    @GetMapping("/{sightingId}")
    public Result<Map<String, Object>> detail(@PathVariable Long sightingId) {
        CatSighting sighting = sightingService.getById(sightingId);
        List<SightingCommentVO> comments = commentService.listBySighting(sightingId, 1, 20);

        // 查询发布者信息
        User publisher = userService.getById(sighting.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("sighting", sighting);
        result.put("comments", comments);
        result.put("publisherNickname", publisher != null ? publisher.getNickname() : null);
        result.put("publisherAvatar", publisher != null ? publisher.getAvatar() : null);
        return Result.ok(result);
    }

    @DeleteMapping("/{sightingId}")
    public Result<Void> delete(@PathVariable Long sightingId) {
        Long userId = UserContext.getUserId();
        sightingService.delete(sightingId, userId);
        return Result.ok();
    }

    // === 评论相关接口 ===

    @PostMapping("/comments")
    public Result<SightingComment> createComment(@Valid @RequestBody SightingCommentRequest request) {
        Long userId = UserContext.getUserId();
        return Result.ok(commentService.create(request, userId));
    }

    @GetMapping("/{sightingId}/comments")
    public Result<List<SightingCommentVO>> listComments(
            @PathVariable Long sightingId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(commentService.listBySighting(sightingId, page, size));
    }

    @DeleteMapping("/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        Long userId = UserContext.getUserId();
        commentService.delete(commentId, userId);
        return Result.ok();
    }
}
