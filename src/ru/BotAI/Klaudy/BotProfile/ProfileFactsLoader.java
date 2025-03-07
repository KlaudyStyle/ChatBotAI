package ru.BotAI.Klaudy.BotProfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ru.BotAI.Klaudy.ILogger;

public class ProfileFactsLoader {
	
    public static void loadProfile() throws IOException {
        try (InputStream is = ProfileFactsLoader.class.getResourceAsStream("/assets//profile_facts_1.txt"); 
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            ILogger.log("Loading profile facts from resource file");

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    ProfileFactsManager.FACTS.add(line);
                }
            }
        } catch (IOException exception) {
        	ILogger.log("Failed to load profile facts: " + exception.getMessage());
            throw exception;
        }
    }
}
