package com.austain.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每本书的统计信息，对应 /stats/books/{bookName}。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookStatsDTO {

    private String bookName;

    private int totalWords;

    private int learnedWords;

    private int reviewedWords;

    private int masteredWords;

    private int completionRate;
}


