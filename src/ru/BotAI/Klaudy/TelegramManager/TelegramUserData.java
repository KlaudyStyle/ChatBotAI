package ru.BotAI.Klaudy.TelegramManager;

import java.util.HashMap;

public class TelegramUserData {

	public String name;
	public long userId;
	
	public HashMap<Integer, String> messages;

	public TelegramUserData(long id, String name) {
		this.userId = id;
		this.name = name;
		this.messages = new HashMap<>();
	}
}
