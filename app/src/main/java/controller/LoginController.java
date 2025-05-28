package controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quizapplication.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import bussiness.Buss_Account;
import model.Account;

public class LoginController extends AppCompatActivity {

    private static final String TAG = "LoginController";
    TextView txterr, tvTitle;
    EditText username, password, rePassword;
    Button login, register;
    Buss_Account ba = new Buss_Account();

    private boolean hasLoginOnce = true;
    private boolean hasRegisterOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        bindView();
        txterr.setText("");
        signListenerEvent();
        registerListenerEvent();
    }

    public void signListenerEvent() {
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasLoginOnce) {
                    tvTitle.setText("Login");
                    txterr.setText("");
                    rePassword.setVisibility(View.GONE);
                    hasLoginOnce = true;
                    hasRegisterOnce = false;
                } else {
                    String strusername = username.getText().toString();
                    String strpassword = password.getText().toString();

                    if (strusername.isBlank()) {
                        txterr.setText("Username is empty, pls input username");
                        username.requestFocus();
                    } else if (strpassword.isBlank()) {
                        txterr.setText("password is empty, pls input password");
                        password.requestFocus();
                    } else {
                        Account account = ba.getAcount(getApplicationContext(), strusername, strpassword, R.raw.account);
                        if (account != null) {
                            Intent intent = new Intent(LoginController.this, ExamController.class);
                            intent.putExtra("username", account.getUsername());
                            startActivity(intent);
                        } else
                            txterr.setText("username or password incorrect!");
                    }
                }
            }
        });
    }

    public void registerListenerEvent() {
        register.setOnClickListener(v -> {
            if (!hasRegisterOnce) {
                tvTitle.setText("Register");
                txterr.setText("");
                rePassword.setVisibility(View.VISIBLE);
                hasRegisterOnce = true;
                hasLoginOnce = false;
            } else {
                String strusername = username.getText().toString().trim();
                String strpassword = password.getText().toString().trim();
                String strRePassword = rePassword.getText().toString().trim();

                Log.d(TAG, "Register clicked - Username: " + strusername + ", Password length: " + strpassword.length());

                if (strusername.isBlank()) {
                    txterr.setText("Username is empty, pls input username");
                    username.requestFocus();
                } else if (strpassword.isBlank()) {
                    txterr.setText("password is empty, pls input password");
                    password.requestFocus();
                } else if(ba.isExisted(LoginController.this, strusername, R.raw.account)){
                    txterr.setText("Username is existed, pls input username");
                    username.requestFocus();
                } else if(!strpassword.equals(strRePassword)){
                    txterr.setText("password and confirm password doesn't match");
                } else{
                    Log.d(TAG, "Attempting to register user: " + strusername);
                    boolean result = ba.addAccount(getApplicationContext(), strusername, strpassword);
                    if (result) {
                        Toast.makeText(LoginController.this, "Account registered successfully!", Toast.LENGTH_LONG).show();
                        tvTitle.setText("Login");
                        username.setText("");
                        password.setText("");
                        rePassword.setText("");
                        rePassword.setVisibility(View.GONE);
                        txterr.setText("");
                        hasLoginOnce = true;
                        hasRegisterOnce = false;
                    } else {
                        txterr.setText("Username already exists!");
                    }
                }
            }
        });
    }

    private void bindView() {
        txterr = findViewById(R.id.errorText);
        username = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        login = findViewById(R.id.loginButton);
        register = findViewById(R.id.registerButton);
        tvTitle = findViewById(R.id.title);
        rePassword = findViewById(R.id.rePasswordInput);
    }

}