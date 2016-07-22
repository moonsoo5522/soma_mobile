package com.sangjin.soma7th.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sangjin.soma7th.DateManage;
import com.sangjin.soma7th.Dol;
import com.sangjin.soma7th.GCMIntentService;
import com.sangjin.soma7th.Music;
import com.sangjin.soma7th.OmokView;
import com.sangjin.soma7th.PreferenceData;
import com.sangjin.soma7th.R;
import com.sangjin.soma7th.SendPost;
import com.sangjin.soma7th.thread.TurnThread;

import java.io.UnsupportedEncodingException;
import java.util.Stack;

public class GameActivity extends Activity implements View.OnClickListener {
    public static String menu = "menu";

    public static final int LINE_NUM = 13;
    public Dol[][] dolArr;
    public TurnThread thread;

    private Stack<Dol> back;
    private Stack<Dol> forward;
    private Dol lastDol;
    public OmokView ov;

    public TextView tTime;

    private Button btnMoveFirst;
    private Button btnUndo;
    private Button btnRedo;
    private Button btnMoveLast;
    private Button btnNewGame;
    private Button btnExit;
    private Button btnMultiExit;

    TextView second;
    public static Context context;
    Button enter;

    PreferenceData pref;
    DateManage man;
    public String mode;
    public int player;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        pref = new PreferenceData(this);
        mode = getIntent().getStringExtra("mode");
        player = getIntent().getIntExtra("player", 0);
        String flag = getIntent().getStringExtra("gamePlay");
        if(flag != null && flag.equals("ok")) {

            SendPost send = new SendPost("MultiServlet");
            try {
                send.addHeader("gamePlay");// 기다리고 있는 상대방이 게임을 시작할 수 있게 푸시 날림
                send.addThePkt("id", GCMIntentService.targetId);
                send.execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        if(mode.equals("single")) {

            btnUndo = (Button) findViewById(R.id.undo);
            btnRedo = (Button) findViewById(R.id.redo);
            btnNewGame = (Button) findViewById(R.id.new_game);
            btnMoveFirst = (Button) findViewById(R.id.first);
            btnMoveLast = (Button) findViewById(R.id.last);
            btnExit = (Button) findViewById(R.id.exit);
            btnMultiExit = (Button) findViewById(R.id.multi_exit);

            btnUndo.setOnClickListener(this);
            btnRedo.setOnClickListener(this);
            btnMoveFirst.setOnClickListener(this);
            btnMoveLast.setOnClickListener(this);
            btnNewGame.setOnClickListener(this);
            btnExit.setOnClickListener(this);
            btnMultiExit.setOnClickListener(this);

            second = (TextView) findViewById(R.id.second);

            //갱신

            LinearLayout timeLayout = (LinearLayout) findViewById(R.id.time_layout);
            timeLayout.setVisibility(View.VISIBLE);
            LinearLayout layout1 = (LinearLayout) findViewById(R.id.single1);
            layout1.setVisibility(View.VISIBLE);
            LinearLayout layout2 = (LinearLayout) findViewById(R.id.single2);
            layout2.setVisibility(View.VISIBLE);
        }

        dolArr = new Dol[LINE_NUM][LINE_NUM];
        LinearLayout layout = (LinearLayout) findViewById(R.id.game);
        tTime = (TextView) findViewById(R.id.time);

        for (int i = 0; i < dolArr.length; i++) {
            for (int j = 0; j < dolArr[0].length; j++) {
                dolArr[i][j] = new Dol();
            }
        }


        back = new Stack<Dol>();
        forward = new Stack<Dol>();
        lastDol = new Dol();
        thread = new TurnThread(this);
        if(mode.equals("single")) {
            thread.start();
        } else {
            LinearLayout layout2 = (LinearLayout) findViewById(R.id.multi);
            layout2.setVisibility(View.VISIBLE);
        }

        if(player == 2) {
            thread.turnChange();

        }
        ov = new OmokView(this); //게임 화면을 부른다.
        layout.addView(ov, 0);
        GCMIntentService.gameActivity = (GameActivity)context;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.stopThread();
        man = new DateManage(this);
        pref.put("lastplay", man.getCurrentDate());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.undo) {
            goBack();
            goBack();
        } else if (v.getId() == R.id.redo) {
            goForward();
            goForward();
        } else if (v.getId() == R.id.new_game) {
            newGame();
        } else if (v.getId() == R.id.first) {
            goFirst();
        } else if (v.getId() == R.id.last) {
            goLast();
        } else if (v.getId() == R.id.exit || v.getId() == R.id.multi_exit) {
            finish();
        }
        ov.invalidate();
    }
    public void newGame() {
        while (!getBack().isEmpty()) {
            getBack().pop().isPlace = false;
            dolArr = new Dol[LINE_NUM][LINE_NUM];
            for (int i = 0; i < dolArr.length; i++) {
                for (int j = 0; j < dolArr[0].length; j++) {
                    dolArr[i][j] = new Dol();
                }
            }
            ov.mainBoard = new int[LINE_NUM + 1][LINE_NUM + 1];
        }


        forward.clear();
        thread.initSecond();
        if (!thread.isBlackTurn) {
            thread.turnChange();
        }
     //   thread = new TurnThread(this);
     //   thread.start();
    }

    public void goFirst() {
        Dol tmp = new Dol();
        for (int i = getBack().size(); i > 0; i--) {
            if (!getBack().empty()) {
                tmp = getBack().pop();
                tmp.isPlace = false;
                tmp.degree = 0;
                forward.push(tmp);
            }
        }
    }

    public void goLast() {

        Dol tmp = new Dol();

        while (!forward.empty()) {
            tmp = forward.pop();
            tmp.isPlace = true;
            getBack().push(tmp);
        }
        if (!getBack().empty()) {
            getLastDol().x = getBack().peek().x;
            getLastDol().y = getBack().peek().y;
            getLastDol().isPlace = getBack().peek().isPlace;
            getLastDol().setDegree(getBack().peek().savedDegree);
        }
    }

    public void goForward() {
        if (!getForward().empty()) {
            Dol tmp;
            tmp = getForward().pop();
            tmp.isPlace = true;
            getBack().push(tmp);
            if (!getBack().empty()) {
                getLastDol().x = getBack().peek().x;
                getLastDol().y = getBack().peek().y;
                getLastDol().isPlace = getBack().peek().isPlace;
                getLastDol().setDegree(getBack().peek().savedDegree);
            }
        }
    }

    public void goBack() {
        if (!getBack().empty()) {
            Dol tmp = getBack().pop();
            tmp.isPlace = false;
            tmp.degree = 0;
            forward.push(tmp);
            if (!getBack().empty()) {
                getLastDol().x = getBack().peek().x;
                getLastDol().y = getBack().peek().y;

            }
        }
    }

    public void putDol(Dol dol) {
        getBack().push(dol);
        dol.savedDegree = dol.degree;

        if (!forward.empty()) {
            forward.clear();
        } // 스택에 돌 위치 저장
//        second.setText(String.valueOf(thread.second));
    }

    public Stack<Dol> getBack() {
        return back;
    }

    public Stack<Dol> getForward() { return forward; }

    public Dol getLastDol() {
        return lastDol;
    } // 전단계의 좌표를 가져오는 메소드

    @Override
    protected void onResume() {
        super.onResume();
        Music.play(this, R.raw.music_game);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Music.stop(this);
    }
}