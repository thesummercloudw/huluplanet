package com.catplanet.module.cat.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.Result;
import com.catplanet.common.result.ResultCode;
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
        Long familyId = requireFamilyId();
        Cat cat = catService.create(request, familyId);
        return Result.ok(cat);
    }

    @PutMapping("/{catId}")
    public Result<Cat> update(@PathVariable Long catId, @Valid @RequestBody CatRequest request) {
        Long familyId = requireFamilyId();
        Cat cat = catService.update(catId, request, familyId);
        return Result.ok(cat);
    }

    @DeleteMapping("/{catId}")
    public Result<Void> delete(@PathVariable Long catId) {
        Long familyId = requireFamilyId();
        catService.delete(catId, familyId);
        return Result.ok();
    }

    @GetMapping("/{catId}")
    public Result<Cat> detail(@PathVariable Long catId) {
        Long familyId = requireFamilyId();
        Cat cat = catService.getById(catId, familyId);
        return Result.ok(cat);
    }

    @GetMapping
    public Result<List<Cat>> list() {
            Long familyId = UserContext.getFamilyId();
        if (familyId == null) {
            return Result.ok(List.of());
        }
        List<Cat> cats = catService.listByFamilyId(familyId);
        return Result.ok(cats);
    }

    private Long requireFamilyId() {
        Long familyId = UserContext.getFamilyId();
        if (familyId == null) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "请先创建或加入一个家庭");
        }
        return familyId;
    }
}
