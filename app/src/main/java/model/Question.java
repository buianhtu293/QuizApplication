package model;

import java.util.ArrayList;
import java.util.Collections;

public class Question {
    private int id;
    private static int id_auto=1;
    private String title, correct, answer;
    private ArrayList<String> options;

    public Question(String title, String correct, String ... options) throws Exception {
        this.title = title;
        String answers ="abcdefgh";
        this.options=new ArrayList<String>();
        if(options.length <2) throw new Exception("Please provide at least 2 options for the question.");
        for(String option : options)
            this.options.add(option);
        this.correct = this.options.get(answers.indexOf(correct.toLowerCase()));
        answer ="";
        id = id_auto++;
    }

    public String getTitle() {
        return title;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public ArrayList<String> getOptions() {
        return options;
    }


    public void randomOptions(){
        Collections.shuffle(options);
    }
    public boolean isCorrect(){
        return correct.equals(answer);
    }
    public int getIndexAnswer(){
        return options.indexOf(answer);
    }
    public int getIndexCorrect(){
        return options.indexOf(correct);
    }
}
