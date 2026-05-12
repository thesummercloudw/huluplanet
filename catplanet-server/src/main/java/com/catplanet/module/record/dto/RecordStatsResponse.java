package com.catplanet.module.record.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class RecordStatsResponse {

    /** 近N天每日统计 */
    private List<DailyStat> dailyStats;

    /** 类型分布 {类型: 次数} */
    private Map<String, Integer> typeDistribution;

    /** 总记录数 */
    private int totalCount;

    /** 数值汇总（如总喂食量、总花费等） */
    private BigDecimal totalValue;

    @Data
    public static class DailyStat {
        private String date;       // yyyy-MM-dd
        private int count;         // 当日次数
        private BigDecimal value;  // 当日数值（如喂食克数、花费等）

        public DailyStat() {}

        public DailyStat(String date, int count, BigDecimal value) {
            this.date = date;
            this.count = count;
            this.value = value;
        }
    }
}
