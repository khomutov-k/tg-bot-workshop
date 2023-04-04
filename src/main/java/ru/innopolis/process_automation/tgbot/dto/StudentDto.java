package ru.innopolis.process_automation.tgbot.dto;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentDto {
    private  String studentCardId;
    private  Long chatId;
    private  boolean unsubscribed;
}
