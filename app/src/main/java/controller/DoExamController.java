package controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.Question;

public class DoExamController extends AppCompatActivity {

    private TextView tvQuestionNumber, tvQuestionText, tvScore, tvMark;
    private RadioGroup rgOptions;
    private RadioButton[] optionButtons = new RadioButton[4];
    private Button btnNext, btnPrevious, btnSubmit, btnExit;

    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private boolean isReviewing = false;
    private boolean hasSubmittedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_exam);

        int numQuestions = getIntent().getIntExtra("numQuestions", 5);

        bindViews();
        loadQuestions(numQuestions);
        showQuestion(currentIndex);

        btnNext.setOnClickListener(v -> {
            if (!isReviewing)
                saveAnswer();
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                showQuestion(currentIndex);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (!isReviewing)
                saveAnswer();
            if (currentIndex > 0) {
                currentIndex--;
                showQuestion(currentIndex);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            saveAnswer();

            if (!hasSubmittedOnce) {
                // First click: Calculate and show score & mark
                int score = 0;
                for (Question q : questions) {
                    if (q.isCorrect()) score++;
                }

                double mark = Math.round((((double) score / questions.size()) * 10) * 100.0) / 100.0;
                tvScore.setText("Score: " + score + "/" + questions.size());
                tvScore.setVisibility(View.VISIBLE);
                tvMark.setText("Mark: " + mark);
                tvMark.setVisibility(View.VISIBLE);

                btnSubmit.setText("Review");
                hasSubmittedOnce = true;

                // Hide question views
                tvQuestionNumber.setVisibility(View.GONE);
                tvQuestionText.setVisibility(View.GONE);
                rgOptions.setVisibility(View.GONE);
            } else {
                // Second click: Switch to review mode

                tvScore.setVisibility(View.GONE);
                tvMark.setVisibility(View.GONE);

                // Display question views
                tvQuestionNumber.setVisibility(View.VISIBLE);
                tvQuestionText.setVisibility(View.VISIBLE);
                rgOptions.setVisibility(View.VISIBLE);

                isReviewing = true;
                currentIndex = 0;
                showQuestion(currentIndex);
                btnSubmit.setVisibility(View.GONE);
            }
        });


        btnExit.setOnClickListener(v -> {
            startActivity(new Intent(this, ExamController.class));
            finish();
        });
    }

    private void bindViews() {
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        tvScore = findViewById(R.id.tvScore);
        tvMark = findViewById(R.id.tvMark);
        rgOptions = findViewById(R.id.rgOptions);
        optionButtons[0] = findViewById(R.id.rbOptionA);
        optionButtons[1] = findViewById(R.id.rbOptionB);
        optionButtons[2] = findViewById(R.id.rbOptionC);
        optionButtons[3] = findViewById(R.id.rbOptionD);
        btnNext = findViewById(R.id.btnNext);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnExit = findViewById(R.id.btnExit);
    }

    private void showQuestion(int index) {
        Question q = questions.get(index);
        tvQuestionNumber.setText("Question " + (index + 1) + "/" + questions.size());
        tvQuestionText.setText(q.getTitle());

        List<String> options = q.getOptions();
        for (int i = 0; i < optionButtons.length; i++) {
            if (i < options.size()) {
                RadioButton rb = optionButtons[i];
                rb.setText(options.get(i));
                rb.setVisibility(View.VISIBLE);
                rb.setEnabled(!isReviewing); // Disable during review

                // Reset background
                rb.setTextColor(Color.BLACK);

                if (q.getIndexAnswer() == i) rb.setChecked(true);
                else rb.setChecked(false);

                if (isReviewing) {
                    if (i == q.getIndexCorrect()) {
                        rb.setTextColor(Color.GREEN); // Green for correct
                    } else if (i == q.getIndexAnswer() && !q.isCorrect()) {
                        rb.setTextColor(Color.RED); // Red for wrong
                    }
                }
            } else {
                optionButtons[i].setVisibility(View.GONE);
            }
        }
    }

    private void saveAnswer() {
        Question q = questions.get(currentIndex);
        int selectedId = rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) {
            q.setAnswer("");
            return;
        }

        for (RadioButton rb : optionButtons) {
            if (rb.getId() == selectedId) {
                q.setAnswer(rb.getText().toString());
                break;
            }
        }
    }

    private void loadQuestions(int limit) {
        try {
            InputStream is = getResources().openRawResource(R.raw.questions);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            NodeList nodeList = doc.getElementsByTagName("question");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element e = (Element) nodeList.item(i);
                String title = e.getElementsByTagName("title").item(0).getTextContent();
                String a = e.getElementsByTagName("a").item(0).getTextContent();
                String b = e.getElementsByTagName("b").item(0).getTextContent();
                String c = e.getElementsByTagName("c").item(0).getTextContent();
                String d = e.getElementsByTagName("d").item(0).getTextContent();
                String correct = e.getElementsByTagName("answer").item(0).getTextContent();

                Question q = new Question(title, correct, a, b, c, d);
                questions.add(q);
            }

            Collections.shuffle(questions);
            if (questions.size() > limit) {
                questions = questions.subList(0, limit);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading questions: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
