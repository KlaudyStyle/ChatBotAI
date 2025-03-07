package ru.BotAI.Klaudy.TelegramManager;

import java.util.HashMap;

import ru.BotAI.Klaudy.ILogger;

public class TelegramMessageData {
	private static HashMap<Long, TelegramUserData> users = new HashMap<>();
	
	public static boolean hasUserData(long chatid) {
		return users.containsKey(chatid);
	}
	
	public static void createUserData(long chatid, String name) {
		TelegramUserData user = new TelegramUserData(chatid, name);
		users.putIfAbsent(chatid, user);
	}
	
	public static void addMessageData(long chatid, String text) {
		if(users.containsKey(chatid)) {
			TelegramUserData data = users.get(chatid);
			int countMessages = data.messages.size();
			data.messages.putIfAbsent(countMessages++, text);
		}
	}
	
	public static void saveData(long chatid, String name) {
        if(!TelegramMessageData.hasUserData(chatid)) {
        	createUserData(chatid, name);
        }
        addMessageData(chatid, name);
	}
}
