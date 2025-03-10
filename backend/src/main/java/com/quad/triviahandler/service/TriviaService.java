package com.quad.triviahandler.service;

import com.quad.triviahandler.model.Question;
import com.quad.triviahandler.model.TriviaResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpSession;
import java.util.*;

@Service
public class TriviaService {

    private static final String TRIVIA_API_BASE_URL = "https://opentdb.com/api.php?amount=1&type=multiple&token=";
    private static final String TOKEN_REQUEST_URL = "https://opentdb.com/api_token.php?command=request";
    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, HttpSession> activeSessions = new HashMap<>();
    private final Map<String, Long> sessionTimeStamps = new HashMap<>();

    public String initializeToken(HttpSession session) {
        String sessionToken = (String) session.getAttribute("opentdbToken");
        if (sessionToken == null) {
            Map<String, String> response = restTemplate.getForObject(TOKEN_REQUEST_URL, Map.class);
            if (response != null && response.containsKey("token")) {
                sessionToken = response.get("token");
                session.setAttribute("opentdbToken", sessionToken);
                activeSessions.put(session.getId(), session);
                sessionTimeStamps.put(session.getId(), System.currentTimeMillis());
            } else {
                throw new RuntimeException("Failed to obtain session token from OpenTDB");
            }
        } else {
            sessionTimeStamps.put(session.getId(), System.currentTimeMillis());
        }
        return sessionToken;
    }


    public Question getQuestion(HttpSession session) {
        String token = initializeToken(session);
        TriviaResponse response = restTemplate.getForObject(TRIVIA_API_BASE_URL + token, TriviaResponse.class);
        if (response == null || response.results.isEmpty()) {
            throw new RuntimeException("No questions available");
        }
        Question question = response.results.get(0);
        session.setAttribute("currentQuestion", question);
        return question;
    }

    public boolean checkAnswer(HttpSession session, String userAnswer) {
        Question question = (Question) session.getAttribute("currentQuestion");
        return question != null && question.getCorrectAnswer().equals(userAnswer);
    }

    @Scheduled(fixedRate = (1 * 60 * 60 * 1000))
    public void cleanupOldSessions() {
        long expirationTime = System.currentTimeMillis() - (3 * 60 * 60 * 1000);
        Iterator<Map.Entry<String, Long>> iterator = sessionTimeStamps.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Long> entry = iterator.next();
            if (entry.getValue() < expirationTime) {
                HttpSession session = activeSessions.get(entry.getKey());
                if (session != null) {
                    session.invalidate();
                    activeSessions.remove(entry.getKey());
                }
                iterator.remove();
            }
        }
    }

}
