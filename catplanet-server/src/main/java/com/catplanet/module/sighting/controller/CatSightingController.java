package com.catplanet.module.sighting.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.sighting.dto.SightingRequest;
import com.catplanet.module.sighting.entity.CatSighting;
import com.catplanet.module.sighting.service.CatSightingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sightings")
@RequiredArgsConstructor
public class CatSightingController {

    private final CatSightingService sightingService;

    @PostMapping
    public Result<CatSighting> create(@Valid @RequestBody SightingRequest request) {
        Long userId = UserContext.getUserId();
        CatSighting sighting = sightingService.create(request, userId);
        return Result.ok(sighting);
    }

    @GetMapping
    public Result<List<CatSighting>> listRecent(@RequestParam(defaultValue = "20") int limit) {
        List<CatSighting> list = sightingService.listRecent(limit);
        return Result.ok(list);
    }

    @DeleteMapping("/{sightingId}")
    public Result<Void> delete(@PathVariable Long sightingId) {
        Long userId = UserContext.getUserId();
        sightingService.delete(sightingId, userId);
        return Result.ok();
    }
}
