package com.catplanet.module.family.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.entity.Family;
import com.catplanet.module.family.entity.FamilyMember;
import com.catplanet.module.family.mapper.FamilyMapper;
import com.catplanet.module.family.mapper.FamilyMemberMapper;
import com.catplanet.module.family.service.FamilyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FamilyServiceImpl implements FamilyService {

    private final FamilyMapper familyMapper;
    private final FamilyMemberMapper familyMemberMapper;

    @Override
    @Transactional
    public Family create(CreateFamilyRequest request, Long userId) {
        Family family = new Family();
        family.setFamilyId(IdWorker.getId());
        family.setName(request.getName());
        family.setCoverEmoji(request.getCoverEmoji() != null ? request.getCoverEmoji() : "🏠");
        family.setCreatorId(userId);
        family.setInviteCode(generateInviteCode());
        familyMapper.insert(family);

        // 创建者自动成为 owner
        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getFamilyId());
        member.setUserId(userId);
        member.setRole("owner");
        member.setJoinedAt(LocalDateTime.now());
        familyMemberMapper.insert(member);

        return family;
    }

    @Override
    @Transactional
    public Family joinByInviteCode(String inviteCode, Long userId) {
        Family family = familyMapper.selectOne(
                new LambdaQueryWrapper<Family>().eq(Family::getInviteCode, inviteCode));
        if (family == null) {
            throw new BizException(ResultCode.INVITE_CODE_INVALID);
        }

        // 检查是否已是成员
        Long count = familyMemberMapper.selectCount(
                new LambdaQueryWrapper<FamilyMember>()
                        .eq(FamilyMember::getFamilyId, family.getFamilyId())
                        .eq(FamilyMember::getUserId, userId));
        if (count > 0) {
            throw new BizException(ResultCode.FAMILY_MEMBER_EXISTS);
        }

        FamilyMember member = new FamilyMember();
        member.setFamilyId(family.getFamilyId());
        member.setUserId(userId);
        member.setRole("member");
        member.setJoinedAt(LocalDateTime.now());
        familyMemberMapper.insert(member);

        return family;
    }

    @Override
    @Transactional
    public void leave(Long familyId, Long userId) {
        familyMemberMapper.delete(
                new LambdaQueryWrapper<FamilyMember>()
                        .eq(FamilyMember::getFamilyId, familyId)
                        .eq(FamilyMember::getUserId, userId));
    }

    @Override
    public List<Family> listByUserId(Long userId) {
        List<FamilyMember> members = familyMemberMapper.selectList(
                new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId));
        List<Long> familyIds = members.stream()
                .map(FamilyMember::getFamilyId)
                .collect(Collectors.toList());
        if (familyIds.isEmpty()) {
            return List.of();
        }
        return familyMapper.selectBatchIds(familyIds);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
