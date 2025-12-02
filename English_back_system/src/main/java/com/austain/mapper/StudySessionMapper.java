package com.austain.mapper;

import com.austain.domain.po.StudySession;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StudySessionMapper {

    @Insert("INSERT INTO study_session(user_id, module, book_name, duration_minutes, words_learned, create_time) " +
            "VALUES(#{userId}, #{module}, #{bookName}, #{durationMinutes}, #{wordsLearned}, NOW())")
    int insert(StudySession session);

    @Select("SELECT IFNULL(SUM(duration_minutes),0) FROM study_session " +
            "WHERE user_id = #{userId} AND DATE(create_time) = CURRENT_DATE()")
    int getTodayDurationMinutes(@Param("userId") String userId);

    @Select("SELECT IFNULL(SUM(words_learned),0) FROM study_session WHERE user_id = #{userId}")
    int getTotalWordsLearned(@Param("userId") String userId);

    @Select("SELECT IFNULL(SUM(duration_minutes),0) FROM study_session " +
            "WHERE user_id = #{userId} AND DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(CURRENT_DATE(), '%Y-%m')")
    int getThisMonthDurationMinutes(@Param("userId") String userId);

    /**
     * 获取最近有学习记录的日期列表，用于计算连续打卡天数。
     */
    @Select("SELECT DISTINCT DATE(create_time) AS day " +
            "FROM study_session WHERE user_id = #{userId} AND create_time >= DATE_SUB(CURRENT_DATE(), INTERVAL 60 DAY) " +
            "ORDER BY day DESC")
    List<LocalDate> getActiveDays(@Param("userId") String userId);

    /**
     * 获取最近 N 天每天的学习统计，用于趋势图。
     */
    @Select("SELECT DATE(create_time) AS day, " +
            "COUNT(DISTINCT id) AS tasksCompleted, " +
            "SUM(duration_minutes) AS durationMinutes " +
            "FROM study_session " +
            "WHERE user_id = #{userId} AND create_time >= DATE_SUB(CURRENT_DATE(), INTERVAL #{days} DAY) " +
            "GROUP BY DATE(create_time) " +
            "ORDER BY day ASC")
    List<TrendRow> getTrend(@Param("userId") String userId, @Param("days") int days);

    /**
     * 统计某本书相关的词汇学习数据。
     */
    @Select("SELECT IFNULL(SUM(words_learned),0) FROM study_session " +
            "WHERE user_id = #{userId} AND book_name = #{bookName}")
    int getBookLearnedWords(@Param("userId") String userId, @Param("bookName") String bookName);

    /**
     * 简单的投影接口，对应 getTrend 的结果行。
     */
    interface TrendRow {
        LocalDate getDay();

        int getTasksCompleted();

        int getDurationMinutes();
    }
}


