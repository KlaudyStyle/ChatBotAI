package ru.BotAI.Klaudy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import ru.BotAI.Klaudy.BotProfile.ProfileFactsLoader;
import ru.BotAI.Klaudy.TelegramManager.TelegramManager;
import ru.BotAI.Klaudy.TextAnalyzer.SentenceSplitter;
import ru.BotAI.Klaudy.TextAnalyzer.Summarizer;

public class Main {
	public static int i = 0;

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\Klaudy\\Desktop\\AI\\faq-categorizer.txt");
		//processFile(file.getPath());
		TelegramManager.init();
	}

	public static void processFile(String filePath) throws IOException {
		// ������ ���� ����� �� �����
		List<String> lines = Files.readAllLines(Paths.get(filePath));

		// ��������� ������ ������
		List<String> processedLines = lines.stream().map(Main::processLine).collect(Collectors.toList());

		// ������ ������������ ����� ������� � ����
		Files.write(Paths.get(filePath), processedLines);
	}

	// ������ ��������� ������
	public static String processLine(String line) {
		if(!line.isEmpty()) line = i++ + "X@LINE@    " + line;
		return line;
	}
}
