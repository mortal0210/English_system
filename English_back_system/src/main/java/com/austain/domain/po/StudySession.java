package com.austain.domain.po;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 学习会话实体，与 study_session 表对应。
 */
@Data
public class StudySession {

    private Long id;

    private String userId;

    private String module;

    private String bookName;

    private Integer durationMinutes;

    private Integer wordsLearned;

    private LocalDateTime createTime;
}


