package ru.innopolis.process_automation.tgbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.innopolis.process_automation.tgbot.config.BotConfig;
import ru.innopolis.process_automation.tgbot.dto.ReminderDto;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String SUBSCRIBE = "/subscribe";
    private static final String UNSUBSCRIBE = "/unsubscribe";
    private final BotConfig config;
    private final CacheService cacheService;
    private String previousCommand;
    private final ProcessBackendService backendService;

    public TelegramBot(BotConfig config, CacheService cacheService, ProcessBackendService backendService) {
        super(config.getToken());
        this.config = config;
        this.cacheService = cacheService;
        this.backendService = backendService;
    }

    public void sendReminder(ReminderDto dto) throws TelegramApiException {

        SendMessage reminderMessage = getSendMessage(dto.getChatId(), dto.getMessage());

        execute(reminderMessage);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            String text = update.getMessage().getText();
            switch (text) {
                case SUBSCRIBE -> executeSubscribeCommand(update, text);
                case UNSUBSCRIBE -> executeUnsubscribeCommand(update, text);
                default -> {
                    log.info("Not command entered");
                    executeAfterSubscribeLogic(update, text);

                }
            }
        }
    }

    private void executeSubscribeCommand(Update update, String text) {
        previousCommand = SUBSCRIBE;
        Long chatId = update.getMessage().getChatId();
        SendMessage responseMessage;
        if (cacheService.hasStudentAlreadySubscribedSuccessfully(chatId)) {
            responseMessage = getSendMessage(chatId, "You have already been subscribed. If you willing to change the student id, please enter new id");
        } else {
            responseMessage = getSendMessage(chatId, "Please enter your student card id");
        }

        sendMessage(responseMessage, "Error during Subscribe Command");
    }

    private void sendMessage(SendMessage responseMessage) {
        sendMessage(responseMessage, "Can not correctly send message back");
    }

    private void sendMessage(SendMessage responseMessage, String errorMessage) {
        try {
            execute(responseMessage);
        } catch (TelegramApiException e) {
            log.error(errorMessage + e.getMessage());
        }
    }

    private SendMessage getSendMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    private void executeUnsubscribeCommand(Update update, String text) {
        Long chatId = update.getMessage().getChatId();
        if (cacheService.hasStudentAlreadySubscribedSuccessfully(chatId)) {
            backendService.unsubscribe(chatId);
            cacheService.removeFromCache(chatId);
            SendMessage responseMessage = getSendMessage(chatId, "Success, now you don't receive any reminders");
            sendMessage(responseMessage, "Error while Unsubscribe command:");
        } else {
            SendMessage responseMessage = getSendMessage(chatId, "You have not subscribed yet");
            sendMessage(responseMessage, "Error while Unsubscribe command:");
        }
    }

    private void executeAfterSubscribeLogic(Update update, String text) {
        if (previousCommand != null && previousCommand.equals(SUBSCRIBE) && !text.isEmpty()) {
            Long chatId = update.getMessage().getChatId();
            //validate student id via regex
            boolean wasAlreadyCreated = cacheService.hasStudentAlreadySubscribedSuccessfully(chatId);
            boolean success = backendService.createCorrelationForStudent(chatId, text);
            if (success) {
                sendSuccessAnswer(text, chatId, wasAlreadyCreated);
            } else {
                log.error("[{}] Cannot be added to subscribers [{}]", chatId, text);
                sendMessage(getSendMessage(chatId, "Some internal error has happened, please try command again"));
            }
            previousCommand = null;
        }
    }

    private void sendSuccessAnswer(String text, Long chatId, boolean wasAlreadyCreated) {
        log.info("Successfully added to subscribers [{}]", text);
        if (wasAlreadyCreated) {
            sendMessage(getSendMessage(chatId, "You has successfully changed student id"));
        } else {
            sendMessage(getSendMessage(chatId, "You have been successfully added to subscribers"));
        }
    }

    @Override
    public String getBotUsername() {
        return config.getName();
    }
}
