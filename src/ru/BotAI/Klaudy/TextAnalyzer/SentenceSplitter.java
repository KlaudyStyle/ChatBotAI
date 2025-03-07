package ru.BotAI.Klaudy.TextAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class SentenceSplitter {
	
	private static final char[] CHARS = new char[] {'.', '!', '?'};

    public static List<String> splitIntoSentences(String text) {
        List<String> sentences = new ArrayList<>();
        StringBuilder currentSentence = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            currentSentence.append(c);

            for(char ch : CHARS) {
                if (c == ch) {
                    if (i == text.length() - 1 || Character.isWhitespace(text.charAt(i + 1))) {
                        sentences.add(currentSentence.toString().trim());
                        currentSentence.setLength(0);
                    }
                    continue;
                }
            }
        }
        if (currentSentence.length() > 0) {
            sentences.add(currentSentence.toString().trim());
        }

        return sentences;
    }
}
