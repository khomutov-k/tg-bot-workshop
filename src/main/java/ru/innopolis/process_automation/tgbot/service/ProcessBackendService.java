package ru.innopolis.process_automation.tgbot.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.innopolis.process_automation.tgbot.dto.StudentDto;

@Service
public class ProcessBackendService {
    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    public ProcessBackendService(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    public boolean createCorrelationForStudent(Long chatId, String studentId) {
        StudentDto dto = StudentDto.builder()
                .studentCardId(studentId)
                .chatId(chatId)
                .build();

        HttpStatus statusCode = restTemplate.postForEntity("http://localhost:8081/api/v1/student/subscribe", dto, null)
                .getStatusCode();
        if (statusCode.equals(HttpStatus.OK)) {
            cacheService.addToCache(chatId, studentId);
            return true;
        }
        return false;
    }

    public void unsubscribe(Long chatId) {
        StudentDto dto = StudentDto.builder()
                .chatId(chatId)
                .build();

        restTemplate.postForEntity("http://localhost:8081/api/v1/student/unsubscribe", dto, null);
    }

}
