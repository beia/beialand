/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp;

/**
 *
 * @author beia
 */
public class Batch {
    private final String[] sentences;
    private final int sentencesNumber;
    private final String[] sentencesLabels;
    public final String batchText;
    public final double batchMeanScore;
    public Batch(String[] sentences, String[] sentencesLabels, String batchText, double batchMeanScore, int sentencesNumber)
    {
        this.sentences = sentences;
        this.sentencesLabels = sentencesLabels;
        this.batchText = batchText;
        this.batchMeanScore = batchMeanScore;
        this.sentencesNumber = sentencesNumber;
    }
    @Override
    public String toString()
    {
        String string = "";
        
        string += this.batchText + "\n";
        for(int i = 0; i < sentences.length; i++)
        {
            string += this.sentences[i] + "\n";
            string += "Label: " + this.sentencesLabels[i] + "\n\n";
        }
        string += "Batch mean score: " + String.valueOf(batchMeanScore) + "\n\n\n";
        return string;
    }
}
