package com.quad.triviahandler.controller;

import com.quad.triviahandler.model.Question;
import com.quad.triviahandler.service.TriviaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TriviaController {

    @Autowired
    private TriviaService triviaService;

    @GetMapping("/questions")
    public Question getQuestion(HttpSession session) {
        return triviaService.getQuestion(session);
    }

    @PostMapping("/checkAnswers")
    public boolean checkAnswer(HttpSession session, @RequestBody Map<String, String> answer) {
        return triviaService.checkAnswer(session, answer.get("answer"));
    }
}
