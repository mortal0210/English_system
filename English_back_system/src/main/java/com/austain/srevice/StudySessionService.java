package com.austain.srevice;

import com.austain.domain.dto.BookStatsDTO;
import com.austain.domain.dto.DashboardOverviewDTO;
import com.austain.domain.dto.DashboardTasksDTO;
import com.austain.domain.dto.DashboardTrendPointDTO;
import com.austain.domain.po.StudySession;

import java.util.List;

public interface StudySessionService {

    void recordSession(StudySession session);

    DashboardOverviewDTO getOverview(String userId);

    DashboardTasksDTO getTodayTasks(String userId);

    List<DashboardTrendPointDTO> getTrend(String userId, int days);

    BookStatsDTO getBookStats(String userId, String bookName);
}


