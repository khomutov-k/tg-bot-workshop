package ru.innopolis.process_automation.tgbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.innopolis.process_automation.tgbot.dto.ReminderDto;
import ru.innopolis.process_automation.tgbot.service.TelegramBot;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class ReminderController {
    @Autowired
    TelegramBot bot;

    @PostMapping("/api/v1/reminder/send")
    @ResponseStatus(OK)
    public void subscribeToService(@RequestBody ReminderDto dto) throws TelegramApiException {
        bot.sendReminder(dto);
    }
}
