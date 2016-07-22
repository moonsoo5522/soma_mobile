package com.sangjin.soma7th;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.sangjin.soma7th.activity.GameActivity;

public class OmokView extends View {
    private Context context;
    private int LINE_WIDTH = 0;
    private int X0;
    private int Y0;
    private GameActivity thisGame;
    public static final int PANE = 13;
    // 돌의 가중치는 4를 넘지 않으므로 편의상 플레이어 돌을 8, 컴퓨터를 9로 세팅해서
    public int[][] mainBoard;

    public OmokControl control;

    public void init() {
        Display display = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();

        LINE_WIDTH = width / GameActivity.LINE_NUM;

        mainBoard = new int[PANE + 1][PANE + 1];

    } // 이는 화면의 너비를 구하여 그를 그려줄 줄 수로 나눠서 한 칸의 간격을 정하고, 놓을 바둑돌의 크기도 정하는 것이다.

    public OmokView(Context context) {
        super(context);
        this.context = context;
        thisGame = (GameActivity) context;
        control = new OmokControl(context);

        init();
    } // 새로운 게임을 시작할 때 마다 init 메소드를 실행시킨다.

    public void onDraw(Canvas canvas) {
        X0 = getWidth() / 2 - GameActivity.LINE_NUM / 2 * LINE_WIDTH; //(0,0)의 x좌표
        Y0 = getWidth() / 2 - GameActivity.LINE_NUM / 2 * LINE_WIDTH; //(0,0)의 y좌표

        Paint background = new Paint();
        background.setAlpha(0);
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

        for (int i = 0; i < PANE; i++) {
            canvas.drawLine(X0, i * LINE_WIDTH + Y0, (GameActivity.LINE_NUM - 1)
                    * LINE_WIDTH + X0, i * LINE_WIDTH + Y0, new Paint());
            canvas.drawLine(i * LINE_WIDTH + X0, Y0, i * LINE_WIDTH + X0,
                    (GameActivity.LINE_NUM - 1) * LINE_WIDTH + Y0, new Paint());
        } // 바둑판을 그린다.

        PreferenceData pref = new PreferenceData(getContext());

        Bitmap black = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece01);
        Bitmap white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece02);
        String item = pref.getValue("item", "invalid");
        if (!item.equals("invalid"))
            if (item.equals("2")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece02);
            } else if (item.equals("3")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece03);
            } else if (item.equals("4")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece04);
            } else if (item.equals("5")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece05);
            } else if (item.equals("6")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece06);
            } else if (item.equals("7")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece07);
            } else if (item.equals("8")) {
                white = BitmapFactory.decodeResource(getResources(), R.drawable.img_piece08);
            }
        Paint selected = new Paint();

        for (int i = 0; i < ((GameActivity) context).getBack().size(); i++) {
            if (thisGame.getBack().get(i).isBlack)
                canvas.drawBitmap(black, thisGame.getBack().get(i).x * LINE_WIDTH + (X0 / 2),
                        thisGame.getBack().get(i).y * LINE_WIDTH + (Y0 / 2), null);
            else
                canvas.drawBitmap(white, thisGame.getBack().get(i).x * LINE_WIDTH + (X0 / 2),
                        thisGame.getBack().get(i).y * LINE_WIDTH + (Y0 / 2), null);
            selected.setColor(Color.WHITE); // 검은색 차례가 맞으면 검은색을 그리고, 아니면 흰색으로 설정

        }
        selected.setColor(Color.RED);

        if (!thisGame.getBack().isEmpty())
            canvas.drawCircle(thisGame.getLastDol().x * LINE_WIDTH + X0,
                    thisGame.getLastDol().y * LINE_WIDTH + Y0, 5, selected);
    }

    public boolean onTouchEvent(MotionEvent e) {
        if(thisGame.mode.equals("multi")) {
            if(thisGame.thread.isBlack())
                return true;
        }

        int x = (int) e.getX(); // 화면을 터치했을 때 터치한 점의 x 좌표를 가져온다.
        int y = (int) e.getY(); // 화면을 터치했을 때 터치한 점의 y 좌표를 가져온다.

        if (x < X0 - LINE_WIDTH / 2
                || x > X0 + (GameActivity.LINE_NUM - 1) * LINE_WIDTH + LINE_WIDTH
                / 2)
            return false;
        if (y < Y0 - LINE_WIDTH / 2
                || y > Y0 + (GameActivity.LINE_NUM - 1) * LINE_WIDTH + LINE_WIDTH
                / 2)
            return false; // 그린 바둑판의 밖을 터치했을 경우 false를 넘겨준다.

        x = (x - X0 + LINE_WIDTH / 2) / LINE_WIDTH * LINE_WIDTH + X0;
        y = (y - Y0 + LINE_WIDTH / 2) / LINE_WIDTH * LINE_WIDTH + Y0;

        int ArrX = (x - X0) / LINE_WIDTH;
        int ArrY = (y - Y0) / LINE_WIDTH; // 배열로 표현하기 위해 화면의 좌표값을 바둑판의 좌표값으로 바꾼다.

        if(control.otherManPlay(ArrX, ArrY)) {
            return true;
        }

        return super.onTouchEvent(e);
    }
}
