package controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.quizapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ExamController extends AppCompatActivity {

    EditText edtNumQuestions;
    Button btnStartQuiz;
    TextView err;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exam);

        edtNumQuestions = findViewById(R.id.edtNumQuestions);
        btnStartQuiz = findViewById(R.id.btnStartQuiz);
        err = findViewById(R.id.errorText);
        err.setText("");

        btnStartQuiz.setOnClickListener(v -> {
            String input = edtNumQuestions.getText().toString().trim();

            if (input.isEmpty()) {
                Toast.makeText(ExamController.this, "Vui lòng nhập số câu hỏi!", Toast.LENGTH_SHORT).show();
                err.setText("Vui lòng nhập số câu hỏi!");
                return;
            }

            int numQuestions = Integer.parseInt(input);

            if (numQuestions <= 0) {
                Toast.makeText(ExamController.this, "Số câu hỏi phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                err.setText("Số câu hỏi phải lớn hơn 0");
                return;
            }

            try {
                InputStream is = getResources().openRawResource(R.raw.questions);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(is);
                NodeList nodes = doc.getElementsByTagName("question");

                if (numQuestions > nodes.getLength()) {
                    Toast.makeText(ExamController.this, "Vượt quá số lượng câu hỏi!", Toast.LENGTH_SHORT).show();
                    err.setText("Vượt quá số lượng câu hỏi!");
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Chuyển sang TestActivity và truyền số câu hỏi
            Intent intent = new Intent(ExamController.this, DoExamController.class);
            intent.putExtra("numQuestions", numQuestions);
            startActivity(intent);
        });
    }
}