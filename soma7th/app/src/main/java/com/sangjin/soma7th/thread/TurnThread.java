package com.sangjin.soma7th.thread;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.sangjin.soma7th.activity.GameActivity;

public class TurnThread extends Thread {
    public boolean isBlackTurn = false; // 첫 턴은 하얀색으로 잡는다.
    private final int INIT_SECOND = 10; // 10초 안에 수를 놓도록 한다.
    public int second = 10;
    Context context;
    private boolean interruption = false;

    public TurnThread(Context context) {
        this.context = context;
    }

    public boolean isBlack() {
        return isBlackTurn;
    } // 검정색 차례이면 검정색 차례라고 넘겨준다.

    public void initSecond() {
        second = INIT_SECOND;
    }

    public void turnChange() {
        isBlackTurn = !isBlackTurn;
    } // 턴 전환

    public void run() {
        while (!interruption) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (second == 0) {
                            turnChange();
                            initSecond();

                            ((GameActivity)context).getBack().clear();
                            ((GameActivity)context).getForward().clear();

                            Toast.makeText(context,
                                    isBlackTurn ? "Black Turn" : "White Turn",
                                    Toast.LENGTH_SHORT).show();
                            ((GameActivity)context).ov.control.computeProcedure();
                            ((GameActivity)context).ov.invalidate();
                        } else {
                            second--;
                            ((GameActivity)context).tTime.setText("남은 시간 : "+Integer.toString(second));
                        } // 정해진 시간(10초)안에 바둑돌을 놓도록 하는데, 10초가 지났을 경우 다른 색 차례라는 메세지를 띄운다.

                    } catch (NullPointerException e) {
                    }
                }
            });

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopThread() {
        interruption = true;
    } // 오류가나면 멈춘다.

    public void restartThread() {
        interruption = false;
    } // 아니면 다시 시작한다.
}
