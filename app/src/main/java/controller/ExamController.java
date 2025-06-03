package controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.Subject;

public class ExamController extends AppCompatActivity {

    EditText edtNumQuestions;
    Button btnStartQuiz;
    TextView err;
    Subject selectedSubject = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam);

        edtNumQuestions = findViewById(R.id.edtNumQuestions);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        err = findViewById(R.id.errorText);
        err.setText("");

        // RecyclerView setup
        RecyclerView rvSubjects = findViewById(R.id.rvSubjects);
        rvSubjects.setLayoutManager(new LinearLayoutManager(this));

        List<Subject> subjectList = new ArrayList<>();
        subjectList.add(new Subject("Vietnamese", R.raw.questions));
        subjectList.add(new Subject("Math", R.raw.math_questions));
        subjectList.add(new Subject("English", R.raw.english_questions));
        subjectList.add(new Subject("Science", R.raw.science_questions));

        SubjectAdapter adapter = new SubjectAdapter(subjectList, subject -> {
            selectedSubject = subject;
            Toast.makeText(this, "Đã chọn: " + subject.getName(), Toast.LENGTH_SHORT).show();
        });

        rvSubjects.setAdapter(adapter);

        // Start quiz button click
        btnStartQuiz.setOnClickListener(v -> {
            String input = edtNumQuestions.getText().toString().trim();

            if (input.isEmpty()) {
                err.setText("Vui lòng nhập số câu hỏi!");
                Toast.makeText(this, "Vui lòng nhập số câu hỏi!", Toast.LENGTH_SHORT).show();
                return;
            }

            int numQuestions;
            try {
                numQuestions = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                err.setText("Số không hợp lệ!");
                return;
            }

            if (numQuestions <= 0) {
                err.setText("Số câu hỏi phải lớn hơn 0");
                Toast.makeText(this, "Số câu hỏi phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSubject == null) {
                err.setText("Vui lòng chọn môn học!");
                Toast.makeText(this, "Chưa chọn môn học!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                InputStream is = getResources().openRawResource(selectedSubject.getXmlResId());
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(is);
                NodeList nodes = doc.getElementsByTagName("question");

                if (numQuestions > nodes.getLength()) {
                    err.setText("Vượt quá số lượng câu hỏi!");
                    Toast.makeText(this, "Vượt quá số lượng câu hỏi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Start DoExamController and pass both subject and number of questions
                Intent intent = new Intent(this, DoExamController.class);
                intent.putExtra("numQuestions", numQuestions);
                intent.putExtra("subjectName", selectedSubject.getName());
                intent.putExtra("xmlResId", selectedSubject.getXmlResId());
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi đọc dữ liệu!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
