package com.sangjin.soma7th.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sangjin.soma7th.MemberAdapter;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;
import com.sangjin.soma7th.beans.Member;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class FriendActivity extends AppCompatActivity implements View.OnClickListener{
    EditText eFriend;
    Button bSubmit;
    ListView listView;

    final int[] resources = {R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8};
    Button[] buttonSet = new Button[8];

    View presentDialogView;
    MemberAdapter adapter;

    ArrayList<Member> list;
    int i;
    String friendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        eFriend = (EditText) findViewById(R.id.friend_name);
        bSubmit = (Button) findViewById(R.id.submit);
        listView = (ListView) findViewById(R.id.listview);

        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String friendId = eFriend.getText().toString();

                SendPost send = new SendPost();
                try {
                    send.addHeader("registerFriend"); // 친구 추가
                    send.addThePkt("id", MainActivity.id);
                    send.addThePkt("friendId", friendId);
                    String retStr = send.execute().get();

                    Toast.makeText(getApplicationContext(), retStr, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        SendPost send = new SendPost();
        try {
            send.addHeader("requestFriendList"); // 친구목록 받아오기
            send.addThePkt("id", MainActivity.id);
            String packet = send.execute().get();

            Type listType = new TypeToken<ArrayList<Member>>() {
            }.getType();
            list = new Gson().fromJson(packet, listType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("리스트", Integer.toString(list.size()));

        adapter = new MemberAdapter(this);
        adapter.clear();
        adapter.addOrderList(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                present(list.get(position).getId());
            }
        });
    }

    public void present(String friendName) {
        this.friendName = friendName;

        presentDialogView = (View) View.inflate(FriendActivity.this, R.layout.dialog_present, null);
        TextView tName = (TextView) presentDialogView.findViewById(R.id.friend_name);
        tName.setText(friendName+"님에게 선물");

        for(int i=0; i<8; i++) {
            buttonSet[i] = (Button) presentDialogView.findViewById(resources[i]);
            buttonSet[i].setOnClickListener(this);
        }

        AlertDialog.Builder dlg = new AlertDialog.Builder(FriendActivity.this);
        dlg.setView(presentDialogView);
        dlg.setPositiveButton("확인", null);
        dlg.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder ab = new AlertDialog.Builder(this);
        ab.setTitle("Title");
        ab.setMessage("정말 이 아이템을 선물하시겠습니까?");

        // 몇번째 돌인지 확인
        i = 0;
        for(; i<8; i++) {
            if(v.getId() == resources[i]) break;
        }
        if(i == 0) {
            Toast.makeText(getApplicationContext(), "컴퓨터 전용 돌은 선물하실 수 없습니다."
            , Toast.LENGTH_SHORT).show();
            return;
        }

        ab.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

                // 친구에게 돌을 선물하는 부분
                SendPost send = new SendPost();
                try {
                    send.addHeader("present");
                    send.addThePkt("id", MainActivity.id);
                    send.addThePkt("friendId", friendName);
                    send.addThePkt("dol", i+1);

                    String retStr = send.execute().get();
                    Toast.makeText(getApplicationContext(), retStr, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ab.setNegativeButton("취소", null);
        ab.show();
    }
}
