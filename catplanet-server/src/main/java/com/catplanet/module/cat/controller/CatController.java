package com.catplanet.module.cat.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.cat.dto.CatRequest;
import com.catplanet.module.cat.entity.Cat;
import com.catplanet.module.cat.service.CatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

    private final CatService catService;

    @PostMapping
    public Result<Cat> create(@Valid @RequestBody CatRequest request) {
        Cat cat = catService.create(request, UserContext.getFamilyId());
        return Result.ok(cat);
    }

    @PutMapping("/{catId}")
    public Result<Cat> update(@PathVariable Long catId, @Valid @RequestBody CatRequest request) {
        Cat cat = catService.update(catId, request, UserContext.getFamilyId());
        return Result.ok(cat);
    }

    @DeleteMapping("/{catId}")
    public Result<Void> delete(@PathVariable Long catId) {
        catService.delete(catId, UserContext.getFamilyId());
        return Result.ok();
    }

    @GetMapping("/{catId}")
    public Result<Cat> detail(@PathVariable Long catId) {
        Cat cat = catService.getById(catId, UserContext.getFamilyId());
        return Result.ok(cat);
    }

    @GetMapping
    public Result<List<Cat>> list() {
        List<Cat> cats = catService.listByFamilyId(UserContext.getFamilyId());
        return Result.ok(cats);
    }
}
