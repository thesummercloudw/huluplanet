package com.catplanet.module.adoption.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.adoption.dto.AdoptionApplyRequest;
import com.catplanet.module.adoption.dto.AdoptionPublishRequest;
import com.catplanet.module.adoption.entity.AdoptionApplication;
import com.catplanet.module.adoption.entity.AdoptionCat;
import com.catplanet.module.adoption.service.AdoptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adoption")
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;

    @GetMapping("/cats")
    public Result<List<AdoptionCat>> listCats(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.ok(adoptionService.listAvailable(city, page, size));
    }

    @GetMapping("/cats/{adoptId}")
    public Result<AdoptionCat> catDetail(@PathVariable Long adoptId) {
        return Result.ok(adoptionService.getById(adoptId));
    }

    @PostMapping("/cats")
    public Result<AdoptionCat> publishCat(@Valid @RequestBody AdoptionPublishRequest request) {
        Long userId = UserContext.getUserId();
        return Result.ok(adoptionService.publish(request, userId));
    }

    @PostMapping("/apply")
    public Result<AdoptionApplication> apply(@Valid @RequestBody AdoptionApplyRequest request) {
        Long userId = UserContext.getUserId();
        return Result.ok(adoptionService.apply(request, userId));
    }

    @GetMapping("/applications")
    public Result<List<AdoptionApplication>> myApplications() {
        Long userId = UserContext.getUserId();
        return Result.ok(adoptionService.myApplications(userId));
    }
}
