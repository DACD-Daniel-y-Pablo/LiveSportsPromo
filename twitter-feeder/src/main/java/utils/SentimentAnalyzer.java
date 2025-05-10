package utils;

import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentClass;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;

public class SentimentAnalyzer {
    private static final StanfordCoreNLP pipeline;
    static {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
        pipeline = new StanfordCoreNLP(props);
    }


    public static int score(String text) {
        var annotation = new Annotation(text);
        pipeline.annotate(annotation);
        List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
        if (sentences.isEmpty()) return 5;

        int sum = 0;
        for (var sentence : sentences) {
            String sentiment = sentence.get(SentimentClass.class);
            int val = switch (sentiment) {
                case "Very negative" -> 0;
                case "Negative"      -> 1;
                case "Neutral"       -> 2;
                case "Positive"      -> 3;
                case "Very positive"-> 4;
                default              -> 2;
            };
            sum += val;
        }
        double avg = (double) sum / sentences.size();
        return (int) Math.round((avg / 4.0) * 10.0);
    }
}
