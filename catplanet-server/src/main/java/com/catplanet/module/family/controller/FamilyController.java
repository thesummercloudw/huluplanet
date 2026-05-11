package com.catplanet.module.family.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.family.dto.CreateFamilyRequest;
import com.catplanet.module.family.dto.JoinFamilyRequest;
import com.catplanet.module.family.entity.Family;
import com.catplanet.module.family.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    public Result<Family> create(@Valid @RequestBody CreateFamilyRequest request) {
        Family family = familyService.create(request, UserContext.getUserId());
        return Result.ok(family);
    }

    @PostMapping("/join")
    public Result<Family> join(@Valid @RequestBody JoinFamilyRequest request) {
        Family family = familyService.joinByInviteCode(request.getInviteCode(), UserContext.getUserId());
        return Result.ok(family);
    }

    @DeleteMapping("/{familyId}/leave")
    public Result<Void> leave(@PathVariable Long familyId) {
        familyService.leave(familyId, UserContext.getUserId());
        return Result.ok();
    }

    @GetMapping("/my")
    public Result<List<Family>> myFamilies() {
        List<Family> families = familyService.listByUserId(UserContext.getUserId());
        return Result.ok(families);
    }
}
