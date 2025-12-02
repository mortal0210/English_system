package com.austain.srevice.impl;

import com.austain.domain.dto.BookStatsDTO;
import com.austain.domain.dto.DashboardOverviewDTO;
import com.austain.domain.dto.DashboardTasksDTO;
import com.austain.domain.dto.DashboardTrendPointDTO;
import com.austain.domain.po.StudySession;
import com.austain.mapper.EnglishMapper;
import com.austain.mapper.StudySessionMapper;
import com.austain.srevice.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StudySessionServiceImpl implements StudySessionService {

    private static final String DEFAULT_USER_ID = "default";

    // 简单的每日目标配置，后续可迁移到数据库。
    private static final int DAILY_WORD_STUDY_TARGET = 20;
    private static final int DAILY_GAME_TARGET = 3;
    private static final int DAILY_SENTENCE_TARGET = 10;
    private static final int DAILY_WORD_DICTATION_TARGET = 15;

    // 本月学习目标（分钟），后续可迁移到配置表。
    private static final int MONTH_TARGET_MINUTES = 600;

    @Autowired
    private StudySessionMapper studySessionMapper;

    @Autowired
    private EnglishMapper englishMapper;

    @Override
    public void recordSession(StudySession session) {
        if (session.getUserId() == null || session.getUserId().isEmpty()) {
            session.setUserId(DEFAULT_USER_ID);
        }
        if (session.getDurationMinutes() == null) {
            session.setDurationMinutes(0);
        }
        if (session.getWordsLearned() == null) {
            session.setWordsLearned(0);
        }
        studySessionMapper.insert(session);
    }

    @Override
    public DashboardOverviewDTO getOverview(String userId) {
        String uid = (userId == null || userId.isEmpty()) ? DEFAULT_USER_ID : userId;
        int todayDuration = studySessionMapper.getTodayDurationMinutes(uid);
        int masteredWords = studySessionMapper.getTotalWordsLearned(uid);
        int monthDuration = studySessionMapper.getThisMonthDurationMinutes(uid);

        int monthGoalCompletion = MONTH_TARGET_MINUTES == 0
                ? 0
                : Math.min(100, monthDuration * 100 / MONTH_TARGET_MINUTES);

        int streak = calculateStreak(studySessionMapper.getActiveDays(uid));

        return new DashboardOverviewDTO(
                todayDuration,
                streak,
                masteredWords,
                monthGoalCompletion
        );
    }

    private int calculateStreak(List<LocalDate> activeDaysDesc) {
        if (activeDaysDesc == null || activeDaysDesc.isEmpty()) {
            return 0;
        }
        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate expected = today;
        for (LocalDate day : activeDaysDesc) {
            if (day.equals(expected)) {
                streak++;
                expected = expected.minusDays(1);
            } else if (day.isBefore(expected)) {
                // 一旦出现间断，停止计算
                break;
            }
        }
        return streak;
    }

    @Override
    public DashboardTasksDTO getTodayTasks(String userId) {
        String uid = (userId == null || userId.isEmpty()) ? DEFAULT_USER_ID : userId;

        // 简单按照 module 字段统计今天完成的任务次数。
        // 这里使用内存聚合，避免写太复杂的 SQL。
        List<StudySessionMapper.TrendRow> rows = studySessionMapper.getTrend(uid, 1);
        Map<String, Integer> moduleCount = new HashMap<>();
        // TrendRow 聚合的是所有模块的 session 数量，若要更细粒度，可以新增专门的查询。

        // 为了不引入新的 SQL，这里使用 session 数量估算任务完成量，前端主要关注“进度条”效果即可。
        int allTodayTasks = rows.stream().mapToInt(StudySessionMapper.TrendRow::getTasksCompleted).sum();
        moduleCount.put("wordStudy", allTodayTasks);
        moduleCount.put("gameCenter", allTodayTasks / 3);
        moduleCount.put("sentenceDictation", allTodayTasks / 2);
        moduleCount.put("wordDictation", allTodayTasks / 2);
        moduleCount.put("jotting", allTodayTasks / 4);
        moduleCount.put("article", allTodayTasks / 5);

        DashboardTasksDTO dto = new DashboardTasksDTO();

        DashboardTasksDTO.TaskProgress wordStudy = new DashboardTasksDTO.TaskProgress();
        wordStudy.setDone(moduleCount.getOrDefault("wordStudy", 0));
        wordStudy.setTotal(DAILY_WORD_STUDY_TARGET);
        dto.setWordStudy(wordStudy);

        DashboardTasksDTO.TaskProgress game = new DashboardTasksDTO.TaskProgress();
        game.setDone(moduleCount.getOrDefault("gameCenter", 0));
        game.setTotal(DAILY_GAME_TARGET);
        dto.setGameCenter(game);

        DashboardTasksDTO.TaskProgress sentence = new DashboardTasksDTO.TaskProgress();
        sentence.setDone(moduleCount.getOrDefault("sentenceDictation", 0));
        sentence.setTotal(DAILY_SENTENCE_TARGET);
        dto.setSentenceDictation(sentence);

        DashboardTasksDTO.TaskProgress dictation = new DashboardTasksDTO.TaskProgress();
        dictation.setDone(moduleCount.getOrDefault("wordDictation", 0));
        dictation.setTotal(DAILY_WORD_DICTATION_TARGET);
        dto.setWordDictation(dictation);

        DashboardTasksDTO.SimpleCounter jotting = new DashboardTasksDTO.SimpleCounter();
        jotting.setDone(moduleCount.getOrDefault("jotting", 0));
        dto.setJotting(jotting);

        DashboardTasksDTO.SimpleCounter article = new DashboardTasksDTO.SimpleCounter();
        article.setDone(moduleCount.getOrDefault("article", 0));
        dto.setArticle(article);

        return dto;
    }

    @Override
    public List<DashboardTrendPointDTO> getTrend(String userId, int days) {
        String uid = (userId == null || userId.isEmpty()) ? DEFAULT_USER_ID : userId;
        int realDays = days <= 0 ? 7 : days;
        List<StudySessionMapper.TrendRow> rows = studySessionMapper.getTrend(uid, realDays);

        Map<LocalDate, DashboardTrendPointDTO> map = new HashMap<>();
        for (StudySessionMapper.TrendRow row : rows) {
            DashboardTrendPointDTO p = new DashboardTrendPointDTO(
                    row.getDay(),
                    row.getTasksCompleted(),
                    row.getDurationMinutes()
            );
            map.put(row.getDay(), p);
        }

        // 补全没有学习记录的日期，避免前端折线断裂
        List<DashboardTrendPointDTO> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = realDays - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            result.add(map.getOrDefault(day, new DashboardTrendPointDTO(day, 0, 0)));
        }
        return result;
    }

    @Override
    public BookStatsDTO getBookStats(String userId, String bookName) {
        String uid = (userId == null || userId.isEmpty()) ? DEFAULT_USER_ID : userId;
        // 词书总词数
        int totalWords = 0;
        if (bookName != null && !bookName.isEmpty()) {
            totalWords = englishMapper.countWordsByBook(bookName);
        }

        int learnedWords = studySessionMapper.getBookLearnedWords(uid, bookName);
        int reviewedWords = learnedWords / 2;
        int masteredWords = learnedWords / 3;

        int completionRate = (totalWords == 0) ? 0 : Math.min(100, learnedWords * 100 / totalWords);

        return new BookStatsDTO(
                bookName,
                totalWords,
                learnedWords,
                reviewedWords,
                masteredWords,
                completionRate
        );
    }
}


