package com.austain.domain.dto;

import lombok.Data;

/**
 * 今日任务进度 DTO，对应 /dashboard/tasks/today。
 */
@Data
public class DashboardTasksDTO {

    private TaskProgress wordStudy;          // 智能背词系统
    private TaskProgress gameCenter;         // 词汇游戏中心
    private TaskProgress sentenceDictation;  // 句子默写训练
    private TaskProgress wordDictation;      // 单词默写训练
    private SimpleCounter jotting;           // 知识积累本：今日记录数
    private SimpleCounter article;           // AI 文章生成：今日文章数

    @Data
    public static class TaskProgress {
        private int done;
        private int total;
    }

    @Data
    public static class SimpleCounter {
        private int done;
    }
}


