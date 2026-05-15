package com.catplanet.module.sighting.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.sighting.dto.SightingCommentRequest;
import com.catplanet.module.sighting.entity.CatSighting;
import com.catplanet.module.sighting.entity.SightingComment;
import com.catplanet.module.sighting.mapper.CatSightingMapper;
import com.catplanet.module.sighting.mapper.SightingCommentMapper;
import com.catplanet.module.sighting.service.SightingCommentService;
import com.catplanet.module.sighting.vo.SightingCommentVO;
import com.catplanet.module.user.entity.User;
import com.catplanet.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SightingCommentServiceImpl implements SightingCommentService {

    private final SightingCommentMapper commentMapper;
    private final CatSightingMapper sightingMapper;
    private final UserService userService;

    @Override
    public SightingComment create(SightingCommentRequest request, Long userId) {
        // 校验出没记录是否存在
        CatSighting sighting = sightingMapper.selectById(request.getSightingId());
        if (sighting == null) {
            throw new BizException(ResultCode.NOT_FOUND);
        }

        SightingComment comment = new SightingComment();
        comment.setSightingId(request.getSightingId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setImages(request.getImages());
        commentMapper.insert(comment);

        // 更新评论数
        sighting.setCommentCount(sighting.getCommentCount() == null ? 1 : sighting.getCommentCount() + 1);
        sightingMapper.updateById(sighting);

        return comment;
    }

    @Override
    public List<SightingCommentVO> listBySighting(Long sightingId, int page, int size) {
        int offset = (page - 1) * size;
        List<SightingComment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<SightingComment>()
                        .eq(SightingComment::getSightingId, sightingId)
                        .orderByDesc(SightingComment::getCreatedAt)
                        .last("LIMIT " + offset + "," + size));

        if (comments.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询用户信息
        List<Long> userIds = comments.stream()
                .map(SightingComment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, User> userMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (a, b) -> a));

        // 转换为VO
        return comments.stream().map(c -> {
            SightingCommentVO vo = new SightingCommentVO();
            vo.setCommentId(c.getCommentId());
            vo.setSightingId(c.getSightingId());
            vo.setUserId(c.getUserId());
            vo.setContent(c.getContent());
            vo.setImages(c.getImages());
            vo.setCreatedAt(c.getCreatedAt());

            User user = userMap.get(c.getUserId());
            if (user != null) {
                vo.setNickname(user.getNickname());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public void delete(Long commentId, Long userId) {
        SightingComment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new BizException(ResultCode.NOT_FOUND);
        }
        commentMapper.deleteById(commentId);

        // 更新评论数
        CatSighting sighting = sightingMapper.selectById(comment.getSightingId());
        if (sighting != null && sighting.getCommentCount() != null && sighting.getCommentCount() > 0) {
            sighting.setCommentCount(sighting.getCommentCount() - 1);
            sightingMapper.updateById(sighting);
        }
    }
}
