/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.*;
import java.io.File;
import java.io.PrintWriter;
/**
 *
 * @author beia
 */
public class NLP {

    public static ArrayList<Batch> batches;
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        batches = new ArrayList<>();
        File testFile, resultFile, excelResultFile;
        testFile = new File("C:\\Users\\beia\\Documents\\NetBeansProjects\\NLP\\src\\nlp\\test.txt");
        resultFile = new File("C:\\Users\\beia\\Documents\\NetBeansProjects\\NLP\\src\\nlp\\results.txt");
        excelResultFile = new File("C:\\Users\\beia\\Documents\\NetBeansProjects\\NLP\\src\\nlp\\excelResults.txt");
        Scanner scan = new Scanner(testFile);
        
        while(scan.hasNextLine())
        {
            
            String line = scan.nextLine();
        
            // Add in sentiment
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,parse,sentiment");

            StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
    
            // Initialize an Annotation with some text to be annotated. The text is the argument to the constructor.
            Annotation annotation = new Annotation(line);

            // run all the selected Annotators on this text
            pipeline.annotate(annotation);
    
            // create a array of batches - a batch has multiple sentences, every sentence has a label, every batch has a score based on the sentiment
            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
            String[] sentencesStrings = new String[sentences.size()];
            String[] sentencesLabels = new String[sentences.size()];
            double batchMeanScore = 0;
            for (int i = 0; i < sentences.size(); i++) {
        
                String sentenceLabel = sentences.get(i).get(SentimentCoreAnnotations.SentimentClass.class);
                sentencesStrings[i] = sentences.get(i).toString();
                sentencesLabels[i] = sentenceLabel;
                //zero at 'Very negative'
                if(sentenceLabel.trim().equals("Negative"))
                {
                    batchMeanScore += 0.25;
                    continue;
                }
                if(sentenceLabel.trim().equals("Neutral"))
                {
                    batchMeanScore += 0.5;
                    continue;
                }
                if(sentenceLabel.trim().equals("Positive"))
                {
                    batchMeanScore += 0.75;
                    continue;
                }
                if(sentenceLabel.trim().equals("Very positive"))
                {
                    batchMeanScore += (double)1;
                }
            }
            batchMeanScore /= (double)sentences.size();
            batches.add(new Batch(sentencesStrings, sentencesLabels, line, batchMeanScore, sentences.size()));
        }
        
        //write the results in the files
        PrintWriter writer = new PrintWriter("C:\\Users\\beia\\Documents\\NetBeansProjects\\NLP\\src\\nlp\\results.txt", "UTF-8");
        for(int i = 0; i < batches.size(); i++)
        {
            String batchScore = String.valueOf(batches.get(i).batchMeanScore);
            writer.print(batches.get(i).toString());
            System.out.println(batchScore);
        }
    }
}