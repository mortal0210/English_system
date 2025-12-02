package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 首页学习概览数据 DTO，对应接口文档中的 /dashboard/overview。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewDTO {

    /** 今日学习总时长（分钟） */
    private int todayDurationMinutes;

    /** 连续打卡天数 */
    private int streakDays;

    /** 已掌握词汇量（这里按照累计学习词汇量统计） */
    private int masteredWords;

    /** 本月目标完成率（0-100） */
    private int monthGoalCompletion;
}


