package com.sangjin.soma7th;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.sangjin.soma7th.activity.GameActivity;
import com.sangjin.soma7th.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by user on 2016. 7. 21..
 */
public class OmokControl {
    public final int PANE = GameActivity.LINE_NUM;
    public static final int PLAYER = 8; // 돌의 가중치. 플레이어는 8
    public static final int COMPUTER = 9; // 컴퓨터는 9.
    public static int gameLevel = 3;
    public Dol mainLocation = new Dol();
    public int[][] mainBoard;

    GameActivity thisGame;
    Context context;

    public OmokControl(Context context) {
        this.context = context;
        thisGame = (GameActivity)context;
        mainBoard = new int[PANE+1][PANE+1];
        if(thisGame.mode.equals("multi")) {
            context.registerReceiver(br, new IntentFilter("dol"));
        }
    }

    public boolean checkVictory(Dol dol) {

        if (countStone(dol, 1, 0) + countStone(dol, -1, 0) == 6) {
            return true;
        } else if (countStone(dol, 0, 1) + countStone(dol, 0, -1) == 6) {
            return true;
        } else if (countStone(dol, 1, 1) + countStone(dol, -1, -1) == 6) {
            return true;
        } else if (countStone(dol, 1, -1) + countStone(dol, -1, 1) == 6) {
            return true;
        }
        return false;
    } // 가로, 세로, 대각선까지 모두 고려하여 연속 5개가 같은 색일 경우 그 승패를 넘겨준다.

    int countStone(Dol dol, int dx, int dy) {
        int i = dol.x;
        int j = dol.y;
        int playerOrComputer = dol.degree; // x,y의 좌표와 놓을 바둑돌의 색을 가져온다.

        int count = 0;

        while ((i >= 0 && i <= GameActivity.LINE_NUM - 1)
                && (j >= 0 && j <= GameActivity.LINE_NUM - 1)) {


            if (thisGame.dolArr[i][j].degree == playerOrComputer && thisGame.dolArr[i][j].isPlace)
                count++; // 연속된 x,y 좌표에 대하여 같은 색이 몇개인지 센다.
            else
                break;
            i += dx;
            j += dy;
        }
        return count; //그리고 몇개의 색이 같은지 넘겨준다.
    }

    public boolean otherManPlay(int ArrX, int ArrY) {
        if (!thisGame.dolArr[ArrX][ArrY].isPlace) {
            placeDol(ArrX, ArrY, PLAYER);

            if(gameResult(ArrX, ArrY) && thisGame.mode.equals("multi")) {
                SendPost send = new SendPost("MultiServlet");
                try {
                    send.addHeader("dol");
                    send.addThePkt("targetId", GCMIntentService.targetId);
                    send.addThePkt("x", ArrX);
                    send.addThePkt("y", ArrY);
                    send.execute();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                return true;
            }

            thisGame.ov.invalidate();

            // 컴퓨터 턴이기 때문에, 2인플에서는 미가동

            if(thisGame.mode.equals("single")) {
                computeProcedure();
                thisGame.ov.invalidate();
            } else {
                SendPost send = new SendPost("MultiServlet");
                try {
                    send.addHeader("dol");
                    send.addThePkt("targetId", GCMIntentService.targetId);
                    send.addThePkt("x", ArrX);
                    send.addThePkt("y", ArrY);
                    send.execute();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    public BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("dol")) {
                int x = intent.getIntExtra("x", 0);
                int y = intent.getIntExtra("y", 0);
                placeDol(x, y, COMPUTER);
                gameResult(x, y);
            }
        }
    };

    public void placeDol(int ArrX, int ArrY, int playerORcomputer) {
        thisGame.ov.invalidate();

        thisGame.dolArr[ArrX][ArrY].x = ArrX;
        thisGame.dolArr[ArrX][ArrY].y = ArrY;

        if (((GameActivity) context).thread.isBlack())
            thisGame.dolArr[ArrX][ArrY].isBlack = true;

        else
            ((GameActivity) context).dolArr[ArrX][ArrY].isBlack = false; // 화면에 그릴 바둑돌의 색이 검은색차례인지 아닌지 넘겨준다.

        ((GameActivity) context).thread.turnChange(); // 색을 바꾼다.
        ((GameActivity) context).thread.initSecond(); // 다음 차례를 위해 시간도 다시 세팅한다.

        thisGame.dolArr[ArrX][ArrY].isPlace = true; // 놓은 자리에 바둑돌이 놓였다고 표시한다.

        int tempDegree = thisGame.dolArr[ArrX][ArrY].degree;
        thisGame.dolArr[ArrX][ArrY].setDegree(playerORcomputer);
        for (int i = 0; i < PANE; i++) {
            for (int j = 0; j < PANE; j++) {
                mainBoard[i][j] = thisGame.dolArr[i][j].degree;
            }
        }

        thisGame.putDol(thisGame.dolArr[ArrX][ArrY]);

        thisGame.getLastDol().x = ArrX;
        thisGame.getLastDol().y = ArrY; // 가장 최근에 놓았던 자리의 좌표를 저장한다(Undo 버튼 이용하기 위해)
    }

    public boolean gameResult(int ArrX, int ArrY) {

        boolean isVictory = checkVictory(thisGame.dolArr[ArrX][ArrY]);

        if (isVictory) {
            boolean winFlag = ((GameActivity) context).getBack().peek().isBlack;
            Toast.makeText(context,
                    winFlag ? "패배하였습니다." : "승리하였습니다.",
                    Toast.LENGTH_SHORT).show();

            SendPost send = new SendPost();
            try {
                send.addHeader("resultUpdate");
                send.addThePkt("id", MainActivity.id);
                send.addThePkt("result", winFlag ? "lose" : "win");
                send.execute();

            } catch (Exception e1) {
                e1.printStackTrace();
            }

            if(thisGame.mode.equals("multi")) {
                context.unregisterReceiver(br);
            }
            ((GameActivity) context).finish();
            return true;
        }
        return false;
    }

    public void computeProcedure() {

        for (int i = 0; i < PANE; i++) {
            for (int j = 0; j < PANE; j++) {
                mainBoard[i][j] = thisGame.dolArr[i][j].degree;
            }
        }
        computePoint(mainBoard); // 돌들의 가중치를 계산한다.

        mainLocation = new Dol(); // 컴퓨터가 놓을 위치를 담아놓을 객체
        getAttackPos(mainBoard, 1); // 컴퓨터가 놓을 자리를 정한다.

        int arrX = mainLocation.x;
        int arrY = mainLocation.y;
        Log.d("x : ", Integer.toString(arrX));
        Log.d("y : ", Integer.toString(arrY));

        mainBoard[arrX][arrY] = COMPUTER; // 컴퓨터의 한수!
        thisGame.putDol(thisGame.dolArr[arrX][arrY]);

        thisGame.getLastDol().x = arrX;
        thisGame.getLastDol().y = arrY;

        for (int i = 0; i < PANE; i++) {
            for (int j = 0; j < PANE; j++) {
                if (mainBoard[i][j] == COMPUTER || mainBoard[i][j] == PLAYER) {
                    thisGame.dolArr[i][j].x = i;
                    thisGame.dolArr[i][j].y = j;
                    thisGame.dolArr[i][j].isPlace = true;
                }
                thisGame.dolArr[i][j].setDegree(mainBoard[i][j]); // 가중치 계산 결과를 다시 대입
            }
        }
        if (((GameActivity) context).thread.isBlack())
            thisGame.dolArr[arrX][arrY].isBlack = true;

        else
            ((GameActivity) context).dolArr[arrX][arrY].isBlack = false;

        ((GameActivity) context).thread.turnChange(); // 색을 바꾼다.
        Boolean isVictory = checkVictory(thisGame.dolArr[arrX][arrY]); // 이겼으면 어느 색이 이겼는지 넘긴다.

        if (isVictory) {
            boolean winFlag = ((GameActivity) context).getBack().peek().isBlack;
            Toast.makeText(context,
                    winFlag ? "패배하였습니다." : "승리하였습니다.",
                    Toast.LENGTH_SHORT).show();

            SendPost send = new SendPost();
            try {
                send.addHeader("resultUpdate");
                send.addThePkt("id", MainActivity.id);
                send.addThePkt("result", winFlag ? "lose" : "win");
                send.execute();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            ((GameActivity) context).finish();
        }
    }

    public int computePoint(int[][] board) {

        int row, col;
        int i = 0;
        int count = 1;
        int tempPlayer = 0;
        int sum = 0;

        //--------------- 가중치 1 세팅 -----------------------------
        for (row = 0; row < PANE; row++) {
            for (col = 0; col < PANE; col++) {
                // LEFT →
                if (col < PANE - 1 && board[row][col + 1] == PLAYER && board[row][col] < 1)
                    board[row][col] = 1;
                else if (col > 0 && board[row][col - 1] == PLAYER && board[row][col] < 1) // right
                    board[row][col] = 1;
                else if (row < PANE - 1 && board[row + 1][col] == PLAYER && board[row][col] < 1) // down
                    board[row][col] = 1;
                else if (row > 0 && board[row - 1][col] == PLAYER && board[row][col] < 1) // up
                    board[row][col] = 1;
                else if ((row < PANE - 1 && col < PANE - 1) && board[row + 1][col + 1] == PLAYER && board[row][col] < 1) // ↘
                    board[row][col] = 1;
                else if ((row > 0 && col > 0) && board[row - 1][col - 1] == PLAYER && board[row][col] < 1) // ↖
                    board[row][col] = 1;
                else if ((row > 0 && col < PANE - 1) && board[row - 1][col + 1] == PLAYER && board[row][col] < 1) // ↗
                    board[row][col] = 1;
                else if ((row < PANE - 1 && col > 0) && board[row + 1][col - 1] == PLAYER && board[row][col] < 1)  // ↙
                    board[row][col] = 1;
            }
        }


        //-------------- 돌의 개수에 의한 일반 가중치 -------------------
        for (row = 0; row < PANE; row++) {
            for (col = 0; col < PANE; col++) {
                // RIGHT →
                if (col < PANE
                        && board[row][col] < PLAYER       //현재 가중치가 설정된 위치
                        && board[row][col + 1] >= PLAYER) {   //right에 돌이 있는 경우

                    count = 0;
                    tempPlayer = board[row][col + 1];

                    for (i = col + 1; i < PANE; i++) {
                        if (board[row][i] == tempPlayer)
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }

                // LEFT ←
                if (col > 0
                        && board[row][col] < PLAYER       //현재 가중치가 설정된 위치
                        && board[row][col - 1] >= PLAYER) {   //left에 돌이 있는 경우

                    count = 0;
                    tempPlayer = board[row][col - 1];

                    for (i = col - 1; i >= 0; i--) {
                        if (board[row][i] == tempPlayer)
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                // DOWN ↓
                if (row < PANE
                        && board[row][col] < PLAYER          //현재 가중치가 설정된 위치
                        && board[row + 1][col] >= PLAYER) {      //down에 돌이 있는 경우

                    count = 0;
                    tempPlayer = board[row + 1][col];

                    for (i = row + 1; i < PANE; i++) {
                        if (board[i][col] == tempPlayer)
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                // UP ↑
                if (row > 0
                        && board[row][col] < PLAYER       //현재 가중치가 설정된 위치
                        && board[row - 1][col] >= PLAYER) {   //up에 돌이 있는 경우

                    count = 0;
                    tempPlayer = board[row - 1][col];

                    for (i = row - 1; i >= 0; i--) {
                        if (board[i][col] == tempPlayer)
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                // ↘ right down
                if ((row < PANE && col < PANE)
                        && board[row][col] < PLAYER       // 현재 가중치가 설정된 위치
                        && board[row + 1][col + 1] >= PLAYER)   // right-down에 돌이 있는 경우
                {
                    count = 0;
                    tempPlayer = board[row + 1][col + 1];

                    for (i = 1; i < 5; i++) {
                        if ((row + i < PANE && col + i < PANE) && (board[row + i][col + i] == tempPlayer))
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                // ↖ left up
                if ((row > 0 && col > 0)
                        && board[row][col] < PLAYER       // 현재 가중치가 설정된 위치
                        && board[row - 1][col - 1] >= PLAYER) { // left-up에 돌이 있는 경우

                    count = 0;
                    tempPlayer = board[row - 1][col - 1];

                    for (i = 1; i < 5; i++) {
                        if ((row - i >= 0 && col - i >= 0) && (board[row - i][col - i] == tempPlayer))
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                // ↗ right up
                if ((row > 0 && col < PANE)
                        && board[row][col] < PLAYER          // 현재 가중치가 설정된 위치
                        && board[row - 1][col + 1] >= PLAYER)      // right-up에 돌이 있는 경우
                {
                    count = 0;
                    tempPlayer = board[row - 1][col + 1];

                    for (i = 1; i < 5; i++) {
                        if ((row - i >= 0 && col + i < PANE) && (board[row - i][col + i] == tempPlayer))
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                // ↙ left down
                if ((row < PANE && col > 0)
                        && board[row][col] < PLAYER         // 현재 가중치가 설정된 위치
                        && board[row + 1][col - 1] >= PLAYER)  // left-down에 돌이 있을 경우
                {
                    count = 0;
                    tempPlayer = board[row + 1][col - 1];

                    for (i = 1; i < 5; i++) {
                        if ((row + i < PANE && col - i > 0) && (board[row + i][col - i] == tempPlayer))
                            count++;
                        else
                            break;
                    }

                    if (board[row][col] < count) {   // 해당 위치 가중치가 count 보다 작다면
                        board[row][col] = count;
                    }
                }
                sum = board[row][col] > sum ? board[row][col] : sum;
            }
        }

        return sum;
    }

    public int getAttackPos(int[][] board, int level) {
        int Max = 0;
        int[][] tempBoard = new int[16][16];
        ArrayList<Dol> list = new ArrayList<Dol>();

        for (int i = 0; i < PANE; i++)
            for (int j = 0; j < PANE; j++)
                tempBoard[i][j] = board[i][j];

        for (int i = 0; i < PANE; i++) {
            for (int j = 0; j < PANE; j++) {
                if (tempBoard[i][j] > Max && tempBoard[i][j] < PLAYER)
                    Max = tempBoard[i][j];
            }
        }

        for (int i = 0; i < 15; i++)
            for (int j = 0; j < 15; j++)
                if (tempBoard[i][j] == Max)
                    list.add(new Dol(i, j));

        if (gameLevel == 1) {
            mainLocation = list.get(0);
            return 0;
        }

        int max_int = 0;
        int midValue = 0, result = 0;
        int size = list.size();

        for (int i = 0; i < size; i++) {
            Dol info = list.get(i);
            int tempIndexValue = tempBoard[info.x][info.y];
            tempBoard[info.x][info.y] = level % 2 == 1 ? COMPUTER : PLAYER;
            int retValue = computePoint(tempBoard);
            max_int = max_int > retValue ? max_int : retValue;
            if (level < 2) {
                midValue = getAttackPos(tempBoard, level + 1);
                if (midValue > result) {
                    result = midValue;
                    mainLocation = info;
                }
            }
            tempBoard[info.x][info.y] = tempIndexValue;
        }
        return level == 2 ? max_int : result;
    }
}
