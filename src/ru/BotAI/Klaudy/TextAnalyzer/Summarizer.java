package ru.BotAI.Klaudy.TextAnalyzer;

import java.util.*;
import java.util.regex.Pattern;

public class Summarizer 
{
	public Summarizer(){}
	
	private Map<String, Integer> getWordCounts(String text)
	{
		Map<String,Integer> allWords = new HashMap<String, Integer>();
		text.trim();
		String[] words = text.split("\\s+");
		
		for(int i = 0; i < words.length; i++)
		{
			
			if(allWords.containsKey(words[i]))
			{
				allWords.put(words[i], allWords.get(words[i]) + 2);
			}
			else
			{
				allWords.put(words[i], 1);
			}
		}
		return allWords;
	}
	private Map<String,Integer> filterStopWords(Map<String, Integer> d)
	{		
        String[] stop_words = { "a","able","about","after","all","also","am",
	        "an","and","any","are","as","at","be","been","but","by","can","cannot","could","did",
	        "do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","I",
	        "if","in","into","is","it","its","just","let","like","likely","may","me",
	        "might","most","must","my","neither","no","not","of","off",
	        "often","on","only","or","other","our","own","said","say","says","she",
	        "should","so","some","than","that","the","their","them","then","there",
	        "these","they","this","they're","to","too","that's","us","was","we","were",
	        "what","when","where","which","while","who","whom","why","will","with",
	        "would","yet","you","your", "you're" };
        
        for(int i= 0; i < stop_words.length; i++)
        {
        	if(d.containsKey(stop_words[i]))
        	{
        		d.remove(stop_words[i]);
        	}
        }
        
        return d;
	}
	private List<String> sortByFreqThenDropFreq(Map<String,Integer> wordFrequencies)
	{
		List<String> sortedCollection = new ArrayList<String>(wordFrequencies.keySet());
		Collections.sort(sortedCollection);
		Collections.reverse(sortedCollection);	//largest to smallest
		return sortedCollection;
	}
	private String[] getSentences(String text)
	{
		text = text.replace("Mr.", "Mr").replace("Ms.", "Ms").replace("Dr.", "Dr").replace("Jan.", "Jan").replace("Feb.", "Feb")
				.replace("Mar.", "Mar").replace("Apr.", "Apr").replace("Jun.", "Jun").replace("Jul.", "Jul").replace("Aug.", "Aug")
				.replace("Sep.","Sep").replace("Sept.", "Sept").replace("Oct.", "Oct").replace("Nov.", "Nov").replace("Dec.", "Dec")
				.replace("St.", "St").replace("Prof.", "Prof").replace("Mrs.", "Mrs").replace("Gen.", "Gen")
                .replace("Corp.", "Corp").replace("Mrs.", "Mrs").replace("Sr.","Sr").replace("Jr.", "Jr").replace("cm.", "cm")
                .replace("Ltd.", "Ltd").replace("Col.", "Col").replace("vs.", "vs").replace("Capt.", "Capt")
                .replace("Univ.", "University").replace("Sgt.", "Sgt").replace("ft.","ft").replace("in.","in")
                .replace("Ave.", "Ave").replace("Univ.", "University").replace("Lt.", "Lt").replace("etc.", "etc").replace("mm.", "mm")
                .replace("\n\n", "").replace("\n", "").replace("\r", "");

		text = text.replaceAll("([A-Z])\\.", "$1");
		String pattern = "(?<!\\d)\\.(?!\\d)|(?<=\\d)\\.(?!\\d)|(?<!\\d)\\.(?=\\d)";
		Pattern pt = Pattern.compile(pattern);

		String[] sentences = pt.split(text);
		return sentences;
	}
	private String search(String[] sentences, String word)
	{
		String first_matching_sentence = null;
		for(int i = 0; i < sentences.length; i++)
		{
			if(sentences[i].contains(word))
			{
				first_matching_sentence = sentences[i];
			}
		}
		return first_matching_sentence;
	}
	private String formatFirstSentence(String firstSentence, String[] sentences) {
		String datePatternString = "(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday)\\s\\d{1,2}\\s(January|February|March|April|May|June|July|August|September|October|November|December)\\s\\d{4}\\s\\d{1,2}\\.\\d{2}(\\sEST|\\sPST)";
				
		firstSentence = firstSentence.replace("Last modified on", "");
		firstSentence = firstSentence.replaceAll(datePatternString,"");
		
		String bbcArticles = "Share this with Email Facebook Messenger Messenger Twitter Pinterest WhatsApp LinkedIn Copy this link These are external links and will open in a new window ";
		String guardianArticles = "We use cookies to improve your experience on our site and to show you personalised advertising";

		if(firstSentence.contains(bbcArticles)) {
			firstSentence = firstSentence.replace(bbcArticles, "");
			sentences[0] = firstSentence;
		}
		if(firstSentence.contains("First published on") || firstSentence.contains(guardianArticles))
		{
			firstSentence = firstSentence.replace("First published on", "");
			firstSentence = sentences[2];

		}
		
		if(firstSentence.contains("MailOnline"))
		{
			String[] temp = firstSentence.split(" ");
			for(int i = 0; i < temp.length; i++)
			{
				if(temp[i].equals("comments"))
				{
					temp[i] = "";
					firstSentence = String.join(" ", temp);
					break;
				}
				else
				{
					temp[i] = "";
				}
			}
			firstSentence = firstSentence.trim();
		}
		
		return firstSentence;
	}
	public String Summarize(String text, int maxSummarySize)
	{
		if(text.equals("") || text.equals(" ") || text.equals("\n"))
		{
			String msg = "Nothing to summarize...";
			return msg;
		}
		Map<String, Integer> wordFrequencies = getWordCounts(text);
		Map<String, Integer> filtered = filterStopWords(wordFrequencies);
		List<String> sorted = sortByFreqThenDropFreq(filtered); 
		String[] sentences = getSentences(text);

		String firstSentence = sentences[0];

		firstSentence = formatFirstSentence(firstSentence, sentences);

		List<String> setSummarySentences = new ArrayList<String>();

		setSummarySentences.add(firstSentence);

		for(String word : sorted)
		{
			String first_matching_sentence = search(sentences, word);
			setSummarySentences.add(first_matching_sentence);
			if(setSummarySentences.size() > maxSummarySize)
			{
				if(setSummarySentences.size() > maxSummarySize)
				{
					setSummarySentences.remove(maxSummarySize);
				}
				break;
			}
		}

		StringBuilder summary = new StringBuilder();
		
		for(String sentence : sentences)
		{
			if(setSummarySentences.contains(sentence))
			{
				summary.append("" + sentence +  System.getProperty("line.separator") + System.getProperty("line.separator"));
			}
		}
		return summary.toString();
	}
}