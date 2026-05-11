package com.catplanet.module.cat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.catplanet.common.exception.BizException;
import com.catplanet.common.result.ResultCode;
import com.catplanet.module.cat.dto.CatRequest;
import com.catplanet.module.cat.entity.Cat;
import com.catplanet.module.cat.mapper.CatMapper;
import com.catplanet.module.cat.service.CatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatServiceImpl implements CatService {

    private final CatMapper catMapper;

    @Override
    public Cat create(CatRequest request, Long familyId) {
        Cat cat = new Cat();
        copyProperties(request, cat);
        cat.setFamilyId(familyId);
        catMapper.insert(cat);
        return cat;
    }

    @Override
    public Cat update(Long catId, CatRequest request, Long familyId) {
        Cat cat = getById(catId, familyId);
        copyProperties(request, cat);
        catMapper.updateById(cat);
        return cat;
    }

    @Override
    public void delete(Long catId, Long familyId) {
        Cat cat = getById(catId, familyId);
        catMapper.deleteById(cat.getCatId());
    }

    @Override
    public Cat getById(Long catId, Long familyId) {
        Cat cat = catMapper.selectById(catId);
        if (cat == null || !cat.getFamilyId().equals(familyId)) {
            throw new BizException(ResultCode.CAT_NOT_FOUND);
        }
        return cat;
    }

    @Override
    public List<Cat> listByFamilyId(Long familyId) {
        return catMapper.selectList(
                new LambdaQueryWrapper<Cat>().eq(Cat::getFamilyId, familyId)
                        .orderByDesc(Cat::getCreatedAt));
    }

    private void copyProperties(CatRequest request, Cat cat) {
        cat.setName(request.getName());
        cat.setAvatar(request.getAvatar());
        cat.setBreed(request.getBreed());
        cat.setGender(request.getGender());
        cat.setBirthday(request.getBirthday());
        cat.setIsNeutered(request.getIsNeutered());
        cat.setWeightKg(request.getWeightKg());
        cat.setPersonalityTags(request.getPersonalityTags());
        cat.setAdoptionDate(request.getAdoptionDate());
    }
}
