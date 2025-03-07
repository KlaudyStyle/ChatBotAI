package ru.BotAI.Klaudy.TelegramManager;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import ru.BotAI.Klaudy.ILogger;

public class TelegramManager {
	
	public static void init() {
		ILogger.log("Инициализация Telegram бота...");
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new TelegramBot());
        } catch (TelegramApiException e) {
        	ILogger.log("Бот не был инициализирован!");
            e.printStackTrace();
        }
	}
}