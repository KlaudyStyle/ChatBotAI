package ru.BotAI.Klaudy.TextAnalyzer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.telegram.telegrambots.meta.api.objects.File;

public class TextAnalyzer {
	
    public static HashMap<String, String> responses = new HashMap<>();

    static {
        responses.put("Привет", "Привет");
        responses.put("добрый день", "Добрый день");
        responses.put("добрый вечер", "Добрый вечер");
        responses.put("тебя зовут", "Меня зовут БОТ");
        responses.put("сколько тебе лет", "мне 17 лет");
        responses.put("где ты живешь", "Я живу в России");
        responses.put("что ты делаешь", "я существую|я мыслю|я сознаю себя");
        responses.put("чем занимаешься", "я существую|я мыслю|я сознаю себя");
        responses.put("как дела", "Нормально|Пойдет|Могло быть и лучше...");
        responses.put("Что ты делала на выходных", "На выходных я делала дома приборку | На выходных я читала книгу");
        responses.put("Какие у тебя увлечения", "Я интересуюсь компьютерными технологиями");
        responses.put("Во сколько ты ложишься спать", "я обычно ложусь спать в 9 часов вечера | я обычно ложусь спать в 10 часов вечера | я обычно ложусь спать в 11 часов вечера");
    }

    public static List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        Matcher matcher = Pattern.compile("[^.!?\\s][^.!?]*[.!?]?").matcher(text);
        while (matcher.find()) {
            sentences.add(matcher.group());
        }
        return sentences;
    }

    public static String removeAllExceptRussianLetters(String text) {
        return text.replaceAll("[^а-яА-Я ]", "").trim();
    }

    public static String getRandomResponse(String value) {
        String[] options = value.split("\\|");
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    public static String generateResponse(String text) {
        StringBuilder response = new StringBuilder();
        List<String> offers = splitIntoSentences(text);

        for (String offer : offers) {
            offer = removeAllExceptRussianLetters(offer).toLowerCase();

            for (Map.Entry<String, String> entry : responses.entrySet()) {
                String key = entry.getKey().toLowerCase();
                String value = entry.getValue();

                if (offer.contains(key)) {
                    if (response.length() > 0) {
                        response.append(", ");
                    }
                    response.append(getRandomResponse(value));
                }
            }
        }

        return response.length() > 0 ? response.toString() : "Извини, я тебя не понимаю.";
    }
	

	public static String getRandomString(List<String> list) {
		if (list == null || list.isEmpty()) {
			return "none";
		}
		Random random = new Random();
		int randomIndex = random.nextInt(list.size());
		return list.get(randomIndex);
	}
}
