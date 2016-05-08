package edu.brown.cs.suggest;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import edu.brown.cs.suggest.ORM.*;

public class MyPLSA {

    private int topicNum;

    private int docSize;

    private int vocabularySize;

    private int[][] docTermMatrix;

    //p(z|d)
    private double[][] docTopicPros;

    //p(w|z)
    private double[][] topicTermPros;

    //p(z|d,w)
    private double[][][] docTermTopicPros;

    private List<Word> allWords;

    public MyPLSA(int numOfTopic) {
        topicNum = numOfTopic;
        docSize = 0;
    }

    /**
     * 
     * train plsa
     * 
     * @param docs all documents
     */
    public void train(Set<Tweet> docs, int maxIter) {
        if (docs == null) {
            throw new IllegalArgumentException("The documents set must be not null!");
        }

        //statistics vocabularies
        allWords = statisticsVocabularies(docs);

        //element represent times the word appear in this document 
        docTermMatrix = new int[docSize][vocabularySize];
        //init docTermMatrix
        int docIndex = -1;
        //for (int docIndex = 0; docIndex < docSize; docIndex++) {
        for (Tweet doc : docs){
            docIndex++;
            for (Word word : doc.words()) {
                if (allWords.contains(word)) {
                    int wordIndex = allWords.indexOf(word);
                    docTermMatrix[docIndex][wordIndex] += 1;
                }
            }
            
            //free memory
            //doc.setWords(null);
        }
        

        docTopicPros = new double[docSize][topicNum];
        topicTermPros = new double[topicNum][vocabularySize];
        docTermTopicPros = new double[docSize][vocabularySize][topicNum];

        //init p(z|d),for each document the constraint is sum(p(z|d))=1.0
        for (int i = 0; i < docSize; i++) {
            double[] pros = randomProbilities(topicNum);
            for (int j = 0; j < topicNum; j++) {
                docTopicPros[i][j] = pros[j];
            }
        }
        //init p(w|z),for each topic the constraint is sum(p(w|z))=1.0
        for (int i = 0; i < topicNum; i++) {
            double[] pros = randomProbilities(vocabularySize);
            for (int j = 0; j < vocabularySize; j++) {
                topicTermPros[i][j] = pros[j];
            }
        }

        //use em to estimate params
        for (int i = 0; i < maxIter; i++) {
            em();
            System.out.print(i+"\n");
        }
        System.out.println("done");
    }

    /**
     * 
     * EM algorithm
     * 
     */
    private void em() {
        /*
         * E-step,calculate posterior probability p(z|d,w,&),& is
         * model params(p(z|d),p(w|z))
         * 
         * p(z|d,w,&)=p(z|d)*p(w|z)/sum(p(z'|d)*p(w|z'))
         * z' represent all posible topic
         * 
         */
        for (int docIndex = 0; docIndex < docSize; docIndex++) {
            for (int wordIndex = 0; wordIndex < vocabularySize; wordIndex++) {
                double total = 0.0;
                double[] perTopicPro = new double[topicNum];
                for (int topicIndex = 0; topicIndex < topicNum; topicIndex++) {
                    double numerator = docTopicPros[docIndex][topicIndex]
                            * topicTermPros[topicIndex][wordIndex];
                    total += numerator;
                    perTopicPro[topicIndex] = numerator;
                }

                if (total == 0.0) {
                    total = avoidZero(total);
                }

                for (int topicIndex = 0; topicIndex < topicNum; topicIndex++) {
                    docTermTopicPros[docIndex][wordIndex][topicIndex] = perTopicPro[topicIndex]
                            / total;
                }
            }
        }

        //M-step
        /*
         * update p(w|z),p(w|z)=sum(n(d',w)*p(z|d',w,&))/sum(sum(n(d',w')*p(z|d',w',&)))
         * 
         * d' represent all documents
         * w' represent all vocabularies
         * 
         * 
         */
        for (int topicIndex = 0; topicIndex < topicNum; topicIndex++) {
            double totalDenominator = 0.0;
            for (int wordIndex = 0; wordIndex < vocabularySize; wordIndex++) {
                double numerator = 0.0;
                for (int docIndex = 0; docIndex < docSize; docIndex++) {
                    numerator += docTermMatrix[docIndex][wordIndex]
                            * docTermTopicPros[docIndex][wordIndex][topicIndex];
                }

                topicTermPros[topicIndex][wordIndex] = numerator;

                totalDenominator += numerator;
            }

            if (totalDenominator == 0.0) {
                totalDenominator = avoidZero(totalDenominator);
            }

            for (int wordIndex = 0; wordIndex < vocabularySize; wordIndex++) {
                topicTermPros[topicIndex][wordIndex] = topicTermPros[topicIndex][wordIndex]
                        / totalDenominator;
            }
        }
        /*
         * update p(z|d),p(z|d)=sum(n(d,w')*p(z|d,w'&))/sum(sum(n(d,w')*p(z'|d,w',&)))
         * 
         * w' represent all vocabularies
         * z' represnet all topics
         * 
         */
        for (int docIndex = 0; docIndex < docSize; docIndex++) {
            //actually equal sum(w) of this doc
            double totalDenominator = 0.0;
            for (int topicIndex = 0; topicIndex < topicNum; topicIndex++) {
                double numerator = 0.0;
                for (int wordIndex = 0; wordIndex < vocabularySize; wordIndex++) {
                    numerator += docTermMatrix[docIndex][wordIndex]
                            * docTermTopicPros[docIndex][wordIndex][topicIndex];
                }
                docTopicPros[docIndex][topicIndex] = numerator;
                totalDenominator += numerator;
            }

            if (totalDenominator == 0.0) {
                totalDenominator = avoidZero(totalDenominator);
            }

            for (int topicIndex = 0; topicIndex < topicNum; topicIndex++) {
                docTopicPros[docIndex][topicIndex] = docTopicPros[docIndex][topicIndex]
                        / totalDenominator;
            }
        }
    }

    private List<Word> statisticsVocabularies(Set<Tweet> docs) {
        System.out.println("docSize:"+docs.size());
        Set<Word> uniqWords = new HashSet<>();
        for (Tweet doc : docs) {
            for (Word word : doc.words()) {
                if (!uniqWords.contains(word)) {
                    uniqWords.add(word);
                }
            }
            docSize++;
        }
        vocabularySize = uniqWords.size();

        return new LinkedList<Word>(uniqWords);
    }

    /**
     * 
     * 
     * Get a normalize array
     * 
     * @param size
     * @return
     */
    public double[] randomProbilities(int size) {
        if (size < 1) {
            throw new IllegalArgumentException("The size param must be greate than zero");
        }
        double[] pros = new double[size];

        int total = 0;
        Random r = new Random();
        for (int i = 0; i < pros.length; i++) {
            //avoid zero
            pros[i] = r.nextInt(size) + 1;

            total += pros[i];
        }

        //normalize
        for (int i = 0; i < pros.length; i++) {
            pros[i] = pros[i] / total;
        }

        return pros;
    }

    /**
     * 
     * @return
     */
    public double[][] getDocTopics() {
        return docTopicPros;
    }

    /**
     * 
     * @return
     */
    public double[][] getTopicWordPros() {
        return topicTermPros;
    }

    /**
     * 
     * @return
     */
    public List<Word> getAllWords() {
        return allWords;
    }

    /**
     * 
     * Get topic number
     * 
     * 
     * @return
     */
    public Integer getTopicNum() {
        return topicNum;
    }

    /**
     * 
     * Get p(w|z)
     * 
     * @param word
     * @return
     */
    public double[] getTopicWordPros(String word) {
        int index = allWords.indexOf(word);
        if (index != -1) {
            double[] topicWordPros = new double[topicNum];
            for (int i = 0; i < topicNum; i++) {
                topicWordPros[i] = topicTermPros[i][index];
            }
            return topicWordPros;
        }

        return null;
    }

    /**
     * 
     * avoid zero number.if input number is zero, we will return a magic
     * number.
     * 
     * 
     */
    private final static double MAGICNUM = 0.0000000000000001;

    public double avoidZero(double num) {
        if (num == 0.0) {
            return MAGICNUM;
        }

        return num;
    }
    public static List<Integer> getTop(double[] array, int i) {
        int index = 0;
        List<Integer> rankList = new ArrayList<Integer>();
        HashSet<Integer> scanned = new HashSet<Integer>();
        double max = Double.MIN_VALUE;
        for (int m = 0; m < i && m < array.length; m++) {
            max = Double.MIN_VALUE;
            for (int no = 0; no < array.length; no++) {
                if (!scanned.contains(no)) {
                    if (array[no] > max) {
                        index = no;
                        max = array[no];
                    } else if (array[no] == max && Math.random() > 0.5) {
                        index = no;
                        max = array[no];
                    }
                }
            }
            if (!scanned.contains(index)) {
                scanned.add(index);
                rankList.add(index);
            }
            //System.out.println(m + "\t" + index);
        }
        return rankList;
    }
    public void printFB(int topTopics, int topWords) {
            //System.out.println("User"+uIndex+" handle: "+users.get(uIndex).getHandle()+":");
            for (int tIndex = 0; tIndex < topicNum; tIndex++) {
            int t = 0;
            // List<Integer> topics = getTop(topicTermPros[tIndex],numTopics+1);
            // for (Integer tIndex : topics) {
                if (t > topTopics) {
                    break;
                }
                t++;
                System.out.println("Topic"+tIndex);
                List<Integer> rankList = getTop(topicTermPros[tIndex],100000);
                int i = 0;
                for (Integer rnk : rankList) {
                    if (i >= topWords) {
                        continue;
                    }
                    i++;
                    System.out.println("Word:{"+allWords.get(rnk)+" , "+topicTermPros[tIndex][rnk]+"}");
                }
            }
        }
}