package com.austain.controller;

import com.austain.domain.dto.Result;
import com.austain.domain.po.StudySession;
import com.austain.srevice.StudySessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 学习会话上报接口，对应 /study/session。
 */
@RestController
@RequestMapping("/study")
public class StudySessionController {

    @Autowired
    private StudySessionService studySessionService;

    @PostMapping("/session")
    public Result recordSession(@RequestBody StudySession session) {
        studySessionService.recordSession(session);
        return Result.success();
    }
}


