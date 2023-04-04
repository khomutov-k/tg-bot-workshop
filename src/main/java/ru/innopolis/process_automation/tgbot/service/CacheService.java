package ru.innopolis.process_automation.tgbot.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CacheService {
    private Map<Long, String> studentCache = new HashMap<>();

    public boolean hasStudentAlreadySubscribedSuccessfully(Long chatId) {
        return studentCache.entrySet().stream()
                .anyMatch(entry -> entry.getKey().equals(chatId));
    }

    public String addToCache(Long chatId, String studentId) {
       return studentCache.putIfAbsent(chatId, studentId);
    }

    public void removeFromCache(Long chatId) {
        studentCache.remove(chatId);
    }

}
