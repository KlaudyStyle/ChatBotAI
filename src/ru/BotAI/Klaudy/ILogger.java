package ru.BotAI.Klaudy;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class ILogger {

	public static void log(String text) {
		if(!System.getProperty("user.dir").contains("workds")) {
			appendLogFile(text);
		}
		System.out.println(text);
	}
	
	public static void appendLogFile(String text) {
		Path Location = new File(System.getProperty("user.dir") + File.separator + "log.txt").toPath();
		try {
		    Files.write(Location, Arrays.asList(text), StandardCharsets.UTF_8,
		            Files.exists(Location) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
