package bussiness;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

import model.Account;
import model.Question;

public class Buss_Question {
    private ArrayList<Question> questionsBank;
    ArrayList<Integer> ids;

    public Buss_Question(Context context, int reid) {
        questionsBank = XmlParser.parseQuestion(context,reid);
        ids = new ArrayList<>();
        for (int i = 0; i < questionsBank.size(); i++) {
            ids.add(i);
        }
    }

  // Tạo đề
    public ArrayList<Question> createExam(int n){
        // Trộn ngân hàng câu hỏi
        Collections.shuffle(ids);
        //Update lại so luong câu hỏi nếu vượt quá questionsBank
        if(n>questionsBank.size())
            n=questionsBank.size();
        //Lấy câu hỏi tạo đề
        ArrayList<Question> exam = new ArrayList<>();
        for(int i=0; i<n; i++){
            Question question =questionsBank.get(ids.get(i));
//            question.randomOptions();
            exam.add(question);
        }
        // tạo đề xong
        return exam;
    }
}
