package com.austain.controller;

import com.austain.domain.dto.BookStatsDTO;
import com.austain.domain.dto.Result;
import com.austain.srevice.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private StudySessionService studySessionService;

    @GetMapping("/books/{bookName}")
    public Result getBookStats(@PathVariable String bookName,
                               @RequestParam(required = false) String userId) {
        BookStatsDTO dto = studySessionService.getBookStats(userId, bookName);
        return Result.success(dto);
    }
}


