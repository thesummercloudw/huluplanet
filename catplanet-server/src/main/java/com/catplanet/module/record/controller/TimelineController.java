package com.catplanet.module.record.controller;

import com.catplanet.common.context.UserContext;
import com.catplanet.common.result.Result;
import com.catplanet.module.cat.entity.Cat;
import com.catplanet.module.cat.service.CatService;
import com.catplanet.module.record.dto.TimelineItem;
import com.catplanet.module.record.entity.CareRecord;
import com.catplanet.module.record.entity.FeedingRecord;
import com.catplanet.module.record.entity.HealthRecord;
import com.catplanet.module.record.service.CareRecordService;
import com.catplanet.module.record.service.FeedingRecordService;
import com.catplanet.module.record.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/timeline")
@RequiredArgsConstructor
public class TimelineController {

    private final FeedingRecordService feedingRecordService;
    private final CareRecordService careRecordService;
    private final HealthRecordService healthRecordService;
    private final CatService catService;

    private static final Map<String, String> CARE_TYPE_NAMES = Map.of(
            "litter", "清理猫砂",
            "bath", "洗澡",
            "grooming", "梳毛",
            "nail", "剪指甲",
            "play", "陪玩",
            "other", "其他养护"
    );

    private static final Map<String, String> HEALTH_TYPE_NAMES = Map.of(
            "vaccine", "疫苗",
            "deworm", "驱虫",
            "checkup", "体检",
            "medicine", "用药",
            "weight", "称重"
    );

    @GetMapping
    public Result<List<TimelineItem>> timeline(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long catId) {
        Long familyId = UserContext.getFamilyId();
        if (familyId == null) {
            return Result.ok(List.of());
        }

        // 获取猫咪名称映射
        List<Cat> cats = catService.listByFamilyId(familyId);
        Map<Long, String> catNameMap = cats.stream()
                .collect(Collectors.toMap(Cat::getCatId, Cat::getName, (a, b) -> a));

        List<TimelineItem> items = new ArrayList<>();

        // 聚合喂食记录
        List<FeedingRecord> feedings = catId != null
                ? feedingRecordService.listByCat(catId, familyId)
                : feedingRecordService.listByFamily(familyId, null, limit);
        for (FeedingRecord r : feedings) {
            TimelineItem item = new TimelineItem();
            item.setType("feeding");
            item.setRecordId(r.getRecordId());
            item.setCatId(r.getCatId());
            item.setCatName(catNameMap.getOrDefault(r.getCatId(), "未知"));
            item.setSummary(buildFeedingSummary(r));
            item.setTime(r.getFedAt());
            item.setOperatorUserId(r.getOperatorUserId());
            items.add(item);
        }

        // 聚合养护记录
        List<CareRecord> cares = catId != null
                ? careRecordService.listByCat(catId, familyId)
                : careRecordService.listByFamily(familyId, null, limit);
        for (CareRecord r : cares) {
            TimelineItem item = new TimelineItem();
            item.setType("care");
            item.setRecordId(r.getRecordId());
            item.setCatId(r.getCatId());
            item.setCatName(catNameMap.getOrDefault(r.getCatId(), "未知"));
            item.setSummary(CARE_TYPE_NAMES.getOrDefault(r.getCareType(), r.getCareType()));
            item.setTime(r.getDoneAt());
            item.setOperatorUserId(r.getOperatorUserId());
            items.add(item);
        }

        // 聚合健康记录
        List<HealthRecord> healths = catId != null
                ? healthRecordService.listByCat(catId, familyId)
                : healthRecordService.listByFamily(familyId, null, limit);
        for (HealthRecord r : healths) {
            TimelineItem item = new TimelineItem();
            item.setType("health");
            item.setRecordId(r.getRecordId());
            item.setCatId(r.getCatId());
            item.setCatName(catNameMap.getOrDefault(r.getCatId(), "未知"));
            item.setSummary(buildHealthSummary(r));
            item.setTime(r.getRecordDate().atStartOfDay());
            item.setOperatorUserId(r.getOperatorUserId());
            items.add(item);
        }

        // 按时间倒序排序，取 limit 条
        items.sort(Comparator.comparing(TimelineItem::getTime).reversed());
        if (items.size() > limit) {
            items = items.subList(0, limit);
        }

        return Result.ok(items);
    }

    private String buildFeedingSummary(FeedingRecord r) {
        StringBuilder sb = new StringBuilder("喂了");
        sb.append(r.getFoodName());
        if (r.getAmountG() != null) {
            sb.append(" ").append(r.getAmountG()).append("g");
        }
        return sb.toString();
    }

    private String buildHealthSummary(HealthRecord r) {
        String typeName = HEALTH_TYPE_NAMES.getOrDefault(r.getHealthType(), r.getHealthType());
        if (r.getSubtype() != null) {
            return typeName + "·" + r.getSubtype();
        }
        if ("weight".equals(r.getHealthType()) && r.getValueNumeric() != null) {
            return "体重 " + r.getValueNumeric() + "kg";
        }
        return typeName;
    }
}
