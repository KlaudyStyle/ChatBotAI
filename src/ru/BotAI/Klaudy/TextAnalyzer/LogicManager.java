package ru.BotAI.Klaudy.TextAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import opennlp.tools.doccat.BagOfWordsFeatureGenerator;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.doccat.FeatureGenerator;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.model.ModelUtil;
import ru.BotAI.Klaudy.ILogger;

public class LogicManager {

	private static Map<String, String> questionAnswer = new HashMap<>();
	private static DoccatModel model;

	static {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Klaudy\\Desktop\\AI\\faq-categorizer.txt"), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("@LINE@");
                if (parts.length > 1) {
                	String[] parts1 = parts[1].split("->");
                	if(parts1.length > 1) {
                		questionAnswer.put(parts[0]+"@LINE@", parts1[1]);
                	} else questionAnswer.put(parts[0]+"@LINE@", parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Map.Entry<String, String> entry : questionAnswer.entrySet()) {
            System.out.println("questionAnswer.put(\"" + entry.getKey() + "\", \"" + entry.getValue() + ".\");");
        }

		try {
			model = trainCategorizerModel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    public static String capitalizeSentences(String text) {
        // Используем регулярное выражение для нахождения начала предложений
        Pattern pattern = Pattern.compile("(^|[.!?]\\s*)([a-zа-я])");
        Matcher matcher = pattern.matcher(text);

        StringBuffer result = new StringBuffer();

        // Пока есть совпадения
        while (matcher.find()) {
            // Заменяем первую букву предложения на заглавную
            matcher.appendReplacement(result, matcher.group(1) + matcher.group(2).toUpperCase());
        }
        matcher.appendTail(result);

        return result.toString();
    }

	public static String process(String prompt) throws IOException {
		ILogger.log("");
		String answer = "";
		String userInput = prompt;

		String[] sentences = breakSentences(userInput);
		for (String sentence : sentences) {

			String[] tokens = tokenizeSentence(sentence);

			String[] posTags = detectPOSTags(tokens);

			String[] lemmas = lemmatizeTokens(tokens, posTags);

			String category = detectCategory(model, lemmas);

			answer = answer + " " + capitalizeSentences(getRandomAnswer(questionAnswer.get(category)).replaceFirst("^\\s*", "") + ".");
		}
		ILogger.log(answer);
		return answer;
	}
	
    public static String getRandomAnswer(String questionAnswer) {
        String[] answers = questionAnswer.split("\\|");
        Random random = new Random();
        int randomIndex = random.nextInt(answers.length);
        return answers[randomIndex];
    }

	private static DoccatModel trainCategorizerModel() throws FileNotFoundException, IOException {
		InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(
				new File("C:\\Users\\Klaudy\\Desktop\\AI\\faq-categorizer.txt"));
		ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
		ObjectStream<DocumentSample> sampleStream = new DocumentSampleStream(lineStream);

		DoccatFactory factory = new DoccatFactory(new FeatureGenerator[] { new BagOfWordsFeatureGenerator() });

		TrainingParameters params = ModelUtil.createDefaultTrainingParameters();
		params.put(TrainingParameters.ALGORITHM_PARAM, "NAIVEBAYES");
		// Число итераций обучения
		params.put(TrainingParameters.ITERATIONS_PARAM, 1000);
		// Порог частоты характеристик
		params.put(TrainingParameters.CUTOFF_PARAM, 1);
		// Число потоков для параллельного обучения
		params.put(TrainingParameters.THREADS_PARAM, 4);

		DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, factory);
		return model;
	}

	private static String detectCategory(DoccatModel model, String[] finalTokens) throws IOException {
		DocumentCategorizerME myCategorizer = new DocumentCategorizerME(model);

		double[] probabilitiesOfOutcomes = myCategorizer.categorize(finalTokens);
		String category = myCategorizer.getBestCategory(probabilitiesOfOutcomes);
		System.out.println("Category: " + category);

		return category;

	}

	private static String[] breakSentences(String data) throws FileNotFoundException, IOException {

		try (InputStream modelIn = new FileInputStream("C:\\Users\\Klaudy\\Desktop\\AI\\en-sent.bin")) {

			SentenceDetectorME myCategorizer = new SentenceDetectorME(new SentenceModel(modelIn));
			String[] sentences = myCategorizer.sentDetect(data);
			System.out.println("Sentence Detection: " + Arrays.stream(sentences).collect(Collectors.joining(" | ")));

			return sentences;
		}
	}

	private static String[] tokenizeSentence(String sentence) throws FileNotFoundException, IOException {
		try (InputStream modelIn = new FileInputStream("C:\\Users\\Klaudy\\Desktop\\AI\\en-token.bin")) {

			TokenizerME myCategorizer = new TokenizerME(new TokenizerModel(modelIn));
			String[] tokens = myCategorizer.tokenize(sentence);
			System.out.println("Tokenizer : " + Arrays.stream(tokens).collect(Collectors.joining(" | ")));

			return tokens;

		}
	}

	private static String[] detectPOSTags(String[] tokens) throws IOException {
		try (InputStream modelIn = new FileInputStream("C:\\Users\\Klaudy\\Desktop\\AI\\en-pos-maxent.bin")) {
			POSTaggerME myCategorizer = new POSTaggerME(new POSModel(modelIn));
			String[] posTokens = myCategorizer.tag(tokens);
			System.out.println("POS Tags : " + Arrays.stream(posTokens).collect(Collectors.joining(" | ")));

			return posTokens;

		}

	}

	private static String[] lemmatizeTokens(String[] tokens, String[] posTags)
			throws InvalidFormatException, IOException {
		try (InputStream modelIn = new FileInputStream("C:\\Users\\Klaudy\\Desktop\\AI\\en-lemmatizer.bin")) {
			LemmatizerME myCategorizer = new LemmatizerME(new LemmatizerModel(modelIn));
			String[] lemmaTokens = myCategorizer.lemmatize(tokens, posTags);
			System.out.println("Lemmatizer : " + Arrays.stream(lemmaTokens).collect(Collectors.joining(" | ")));

			return lemmaTokens;

		}
	}
}
