package com.austain.controller;

import com.austain.domain.dto.DashboardOverviewDTO;
import com.austain.domain.dto.DashboardTasksDTO;
import com.austain.domain.dto.DashboardTrendPointDTO;
import com.austain.domain.dto.Result;
import com.austain.srevice.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private StudySessionService studySessionService;

    /**
     * 首页学习概览统计。
     */
    @GetMapping("/overview")
    public Result getOverview(@RequestParam(required = false) String userId) {
        DashboardOverviewDTO overview = studySessionService.getOverview(userId);
        return Result.success(overview);
    }

    /**
     * 今日任务进度。
     */
    @GetMapping("/tasks/today")
    public Result getTodayTasks(@RequestParam(required = false) String userId) {
        DashboardTasksDTO tasks = studySessionService.getTodayTasks(userId);
        return Result.success(tasks);
    }

    /**
     * 最近 N 天学习趋势。
     */
    @GetMapping("/trend")
    public Result getTrend(@RequestParam(required = false) String userId,
                           @RequestParam(required = false, defaultValue = "7") Integer days) {
        List<DashboardTrendPointDTO> trend = studySessionService.getTrend(userId, days);
        return Result.success(trend);
    }
}


