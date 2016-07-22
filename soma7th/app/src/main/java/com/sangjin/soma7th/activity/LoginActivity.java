package com.sangjin.soma7th.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.sangjin.soma7th.PreferenceData;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    EditText l_email, l_passwd;
    Button btn_login, btn_gojoin;
    String email, passwd;
    final PreferenceData pref = new PreferenceData(this);

    private CallbackManager callbackManager;
    private LoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        loginButton = (LoginButton)findViewById(R.id.login_button);

        callbackManager = CallbackManager.Factory.create();

        // 페이스북 로그인 로
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    String email = null;
                                    String name = null;
                                    try {
                                        email = me.getString("email");
                                        name = me.getString("name");

                                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if(email == null) {
                                        String t = "페이스북 이메일 접근 권한을 허가해주세요.";
                                        Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        pref.put("email", email);
                                        pref.put("password", "facebook");
                                        pref.put("mode", "facebook");
                                        Log.d("로그인", "좆북");

                                        MainActivity.id = email;
                                        MainActivity.name = name;

                                        Intent intent = new Intent(
                                                getApplicationContext(),
                                                MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAndWait();

            }

            @Override
            public void onCancel() {
                Log.d("실패", "실패");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("에러", "에러");
            }
        });

        // 페이스북 관련 로직 끝
        /////////////////////////////////////////////////////////////

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy); // 강제로 네트워크 연결

        btn_gojoin = (Button) findViewById(R.id.btn_gojoin);
        btn_login = (Button) findViewById(R.id.btn_login);
        l_email = (EditText) findViewById(R.id.l_email);
        l_passwd = (EditText) findViewById(R.id.l_passwd);

        l_email.setFilters(new InputFilter[]{filterAlphaNum});

        btn_gojoin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, JoinActivity.class));
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (l_email.getText().toString().equals("")
                        || l_passwd.getText().toString().equals("")) {
                    Toast.makeText(LoginActivity.this, "빈 칸이 있습니다.", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                runOnUiThread(new Runnable() {

                    public void run() {
                        email = l_email.getText().toString();
                        passwd = l_passwd.getText().toString();
                        try {
                            SendPost post = new SendPost();

                            post.addHeader("login");
                            post.addThePkt("id", email);
                            post.addThePkt("password", passwd);

                            String result = post.execute().get();
                            // 로그인 성공여부
                            if (result.equals("ok")) {
                                Toast.makeText(LoginActivity.this, "로그인 성공",
                                        Toast.LENGTH_SHORT).show();

                                l_email.setText("");
                                l_passwd.setText("");

                                pref.put("email", email);
                                pref.put("password", passwd);
                                pref.put("mode", "normal");
                                Log.d("로그인", "로그인");

                                MainActivity.id = email;

                                Intent intent = new Intent(
                                        getApplicationContext(),
                                        MainActivity.class);
                                startActivity(intent);
                                finish();

                            } else
                                    // result 태그값이 1이 아닐때 실패
                                Toast.makeText(LoginActivity.this, "로그인 실패",
                                            Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                    }
                });
            }

        });

    }

    // sql 인젝션 방지
    public InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]*$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
