package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 最近 N 天学习趋势点，对应 /dashboard/trend。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardTrendPointDTO {

    private LocalDate date;

    /** 当天完成任务数量（可按学习会话或打卡记录统计） */
    private int tasksCompleted;

    /** 当天学习总时长（分钟） */
    private int durationMinutes;
}


