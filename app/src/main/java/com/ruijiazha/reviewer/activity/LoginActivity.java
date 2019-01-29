package com.ruijiazha.reviewer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ruijiazha.reviewer.database.DBHelper;
import com.ruijiazha.reviewer.R;
import com.ruijiazha.reviewer.data.User;

import java.sql.Connection;

public class LoginActivity extends Activity {

    private String username, password, gender, age, years;

    Button registerButton;
    EditText emailInput, passwordInput, ageInput, yearsInput;
    RadioButton maleButton, femaleButton;
    RadioGroup genderGroup;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(LoginActivity.this, "Registration Complete!", Toast.LENGTH_LONG).show();
                SharedPreferences sp = getSharedPreferences("token", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                String token = username;
                editor.putString("token", token);
                editor.commit();
                finish();
            } else if (msg.what == 0) {
                Toast.makeText(LoginActivity.this, "Registration Failed!", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerButton = (Button) findViewById(R.id.registerButton);
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        ageInput = (EditText) findViewById(R.id.ageInput);
        yearsInput = (EditText) findViewById(R.id.yearsInput);
        genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        maleButton = (RadioButton) findViewById(R.id.maleButton);
        femaleButton = (RadioButton) findViewById(R.id.femaleButton);

        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == maleButton.getId()) {
                    gender = "male";
                } else if (checkedId == femaleButton.getId()) {
                    gender = "female";
                } else {
                    gender = null;
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final User u = new User();
                username = emailInput.getText().toString().trim();
                password = passwordInput.getText().toString();

                age = ageInput.getText().toString().trim();
                if (age.equals("")) {
                    age = null;
                }
                years = yearsInput.getText().toString().trim();
                if (years.equals("")) {
                    years = null;
                }

                u.setUsername(username);
                u.setPassword(password);
                u.setGender(gender);
                u.setAge(age);
                u.setYears(years);

                if (username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "Please input valid email address or password!", Toast.LENGTH_LONG).show();
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            Connection con = DBHelper.setConnection();
                            int res = DBHelper.register(con, u);
                            if (res == 1) {
                                mHandler.sendEmptyMessage(1);
                            } else {
                                mHandler.sendEmptyMessage(0);
                            }
                        }
                    }.start();
                }
            }
        });
    }
}
