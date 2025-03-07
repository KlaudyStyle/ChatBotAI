package ru.BotAI.Klaudy.TelegramManager;

import java.io.IOException;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.BotAI.Klaudy.ILogger;
import ru.BotAI.Klaudy.TextAnalyzer.LogicManager;

public class TelegramBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return TelegramData.USERNAME;  // Замените на имя вашего бота
    }

    @Override
    public String getBotToken() {
        return TelegramData.TOKEN;     // Замените на токен вашего бота
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            if(messageText.equalsIgnoreCase("stop")) System.exit(0);
    		ILogger.log(messageText);
            User user = update.getMessage().getFrom();
            String userName = user.getFirstName() + (user.getLastName() != null ? " " + user.getLastName() : "");
            
            ILogger.log(chatId + "/" + userName + ": " + messageText);
            if(chatId != Long.parseLong("5704635291")) {
            	this.sendMessage(chatId, userName + ", Диана находится на стадии разработки, поэтому не может ответить на ваше сообщение. 🙃");
            	return;
            }
            
            TelegramMessageData.saveData(chatId, userName);
            String response;
			try {
				response = LogicManager.process(messageText);
	            this.sendMessage(chatId, response);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    public void sendMessage(long chatId, String messageText) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(messageText);

        try {
            execute(message); 
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}