package com.sangjin.soma7th.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;

import java.util.regex.Pattern;

public class JoinActivity extends Activity {
    EditText e_email, e_passwd, e_passwd2, e_name;
    Button btn_join, btn_join_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy); // 강제로 네트워크 연결

        btn_join = (Button) findViewById(R.id.btn_join);
        e_email = (EditText) findViewById(R.id.e_email);
        e_passwd = (EditText) findViewById(R.id.e_passwd1);
        e_passwd2 = (EditText) findViewById(R.id.e_passwd2);
        e_name = (EditText) findViewById(R.id.e_name);

        e_email.setFilters(new InputFilter[]{filterAlphaNum});

        btn_join_cancel = (Button) findViewById(R.id.btn_join_cancel);
        btn_join_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        btn_join.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (e_email.getText().toString().equals("")
                        || e_passwd.getText().toString().equals("")
                        || e_name.getText().toString().equals("")) {
                    Toast.makeText(JoinActivity.this, "빈 칸이 있습니다.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        // TODO Auto-generated method stub
                        String email = e_email.getText().toString();
                        String passwd = e_passwd.getText().toString();
                        String name = e_name.getText().toString();
                        try {
                            SendPost post = new SendPost();

                            post.addHeader("join");
                            post.addThePkt("id", email);
                            post.addThePkt("password", passwd);
                            post.addThePkt("name", name);

                            String result = post.execute().get();

                            if (result.equals("ok")) { // result 태그값이 1일때 성공
                                Toast.makeText(JoinActivity.this, "회원가입 성공",
                                        Toast.LENGTH_SHORT).show();

                                e_email.setText("");
                                e_passwd.setText("");
                                e_name.setText("");
                                startActivity(new Intent(JoinActivity.this, LoginActivity.class));
                                finish();

                            } else
                                // result 태그값이 1이 아닐때 실패
                                Toast.makeText(JoinActivity.this, "회원가입 실패",
                                        Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                });
            }

        });
    }

    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

}
