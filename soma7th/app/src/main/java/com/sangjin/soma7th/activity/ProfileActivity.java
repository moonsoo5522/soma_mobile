package com.sangjin.soma7th.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sangjin.soma7th.Music;
import com.sangjin.soma7th.PreferenceData;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;
import com.sangjin.soma7th.beans.Member;

import java.util.ArrayList;

public class ProfileActivity extends Activity {
    final int[] resources = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8};

    final int[] resimg = {R.drawable.etc3328_n, R.drawable.etc3329_n, R.drawable.etc3338_n,
            R.drawable.etc3339_n, R.drawable.etc3341_n, R.drawable.etc3342_n, R.drawable.etc3343_n,
            R.drawable.etc3344_n};

    final int[] resimg_pick = {R.drawable.etc3328_p, R.drawable.etc3329_p, R.drawable.etc3338_p,
            R.drawable.etc3339_p, R.drawable.etc3341_p, R.drawable.etc3342_p, R.drawable.etc3343_p,
            R.drawable.etc3344_p};

    final int[] resimg_gray = {R.drawable.etc3328_gray, R.drawable.etc3329_gray, R.drawable.etc3338_gray,
            R.drawable.etc3339_gray, R.drawable.etc3341_gray, R.drawable.etc3342_gray, R.drawable.etc3343_gray,
            R.drawable.etc3344_gray};

    Button back;
    PreferenceData pref;

    TextView name, point, twin;
    TextView setup, connect, disconnect, quit;
    ArrayList<String> array;

    Button[] buttonSet = new Button[8];
    Member member;

    public static final String TITLE_USERNAME = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        for(int i=0; i<8; i++) {
            buttonSet[i] = (Button) findViewById(resources[i]);
            buttonSet[i].setOnClickListener(mClickListener);
        }

        twin = (TextView) findViewById(R.id.win);
        /*
        승패, 현재 포인트, 사용중인 오목알을 받아온다.
         */
        pref = new PreferenceData(this);

        SendPost send = new SendPost();
        try {
            send.addHeader("getMyInfo");
            send.addThePkt("id", MainActivity.id);
            String packet = send.execute().get();

            member = new Gson().fromJson(packet, Member.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        array = new ArrayList<String>();
        checkItem();

        name = (TextView) findViewById(R.id.name);
        name.setText(MainActivity.id);

        point = (TextView) findViewById(R.id.point);
        point.setText("포인트: " + String.valueOf(member.getPoint()));

        int win = member.getWin();
        int lose = member.getLose();

        float temp = ((float)win / (float)(win + lose));
        temp*=100;
        int per = (int)temp;
        twin.setText(String.valueOf(win) + "승 " + String.valueOf(lose) + "패 승률:" + String.valueOf(per) + "%");
        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setup = (TextView) findViewById(R.id.setup);
        connect = (TextView) findViewById(R.id.connect);
        disconnect = (TextView) findViewById(R.id.disconnect);
        quit = (TextView) findViewById(R.id.quit);

        setup.setText("설치 시간 : "+pref.getValue("setup", ""));
        connect.setText("접속 시간 : "+pref.getValue("connect", ""));
        disconnect.setText("종료 시간 : "+pref.getValue("disconnect", ""));
        quit.setText("마지막 플레이 : "+pref.getValue("lastplay", "아직 플레이하지 않았습니다."));

        checkItem();
    }

    public void checkItem() {
        for(int i=0; i<8; i++) {
            if((member.getDol() & (1 << i)) == 0) {
                buttonSet[i].setBackgroundResource(resimg_gray[i]);
            }
            else {
                buttonSet[i].setBackgroundResource(resimg[i]);
            }
        }

        String item = pref.getValue("item", "invalid");
        if(!item.equals("invalid")) {
            int idx = Integer.parseInt(item)-1;
            buttonSet[idx].setBackgroundResource(resimg_pick[idx]);
        }
    }

    void alertDialog(final int contents, final int point) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ProfileActivity.this);
        alert_confirm.setMessage("구매하시겠습니까?\n" + point + " 포인트가 소모됩니다.").setCancelable(false).setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (member.getPoint() >= 1000) {
                            SendPost send = new SendPost();
                            try {
                                send.addHeader("buy");
                                send.addThePkt("id", MainActivity.id);
                                send.addThePkt("dol", contents);
                                String retValue = send.execute().get();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            dialog.dismiss();
                            checkItem();
                            onResume();
                            Toast.makeText(ProfileActivity.this, "구매되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "포인트가 부족합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.music_info);

        SendPost send = new SendPost();
        try {
            send.addHeader("getMyInfo");
            send.addThePkt("id", MainActivity.id);
            String packet = send.execute().get();

            member = new Gson().fromJson(packet, Member.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        point.setText("포인트: " + String.valueOf(member.getPoint()));
        checkItem();
    } // 게임 실행시 음악 재생

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int dol = member.getDol();
            for(int i=0; i<8; i++) {
                if(resources[i] != v.getId()) continue;
                if(resources[0] == v.getId()) {
                    Toast.makeText(getApplicationContext(),
                            "컴퓨터의 돌이므로 구매하실 수 없습니다...ㅠㅠ", Toast.LENGTH_SHORT).show();
                    continue;
                }

                if((dol & (1 << i)) == 0) {
                    alertDialog(i+1, 1000);
                }
                else {
                    // 장착
                    Toast.makeText(getApplicationContext(),
                            (i+1)+"번 돌이 선택되었습니다.", Toast.LENGTH_SHORT).show();
                    pref.put("item", String.valueOf(i + 1));

                    for(int j=0; j<8; j++) {
                        if((dol & (1 << j)) == 0)
                            buttonSet[j].setBackgroundResource(resimg_gray[j]);
                        else
                            buttonSet[j].setBackgroundResource(resimg[j]);
                    }

                    buttonSet[i].setBackgroundResource(resimg_pick[i]);
                }
            }
        }
    };
}