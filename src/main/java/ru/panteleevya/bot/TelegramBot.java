package ru.panteleevya.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private static final Logger log = LogManager.getLogger();
    private final String botUsername;
    private final String botToken;

    public TelegramBot(
            @Value("${telegram.bot.name}") String botUsername,
            @Value("${telegram.bot.token}") String botToken
    ) {
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Skip all updates, that doesn't have the message and text inside
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        // Collect information for the reply
        Message message = update.getMessage();
        String text = message.getText();
        long chatId = message.getChatId();
        String username = message.getChat().getUserName();

        sendMessage(chatId, text); // echo bot
        log.info("Replied to the user - " + username);
    }

    private void sendMessage(long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(text);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("Sending message error: " + e.getMessage());
        }
    }
}
