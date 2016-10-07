
import java.io.*;
import java.util.*;

import com.google.android.gcm.server.*;
import com.google.android.gcm.server.Message.Builder;


public class SendMessage {
	Sender sender = null;
	Builder builder = null;
	
	public SendMessage() {
		sender = new Sender("AIzaSyBYKtWjLVbKSaji-XgKXoGyD4r5HJK3NzI");
		builder = new Message.Builder();
	}
	public void addHeader(String pkt) {
		builder.addData("header", pkt);
	}
	public void addPkt(String key, String pkt) {
		builder.addData(key, pkt);
	}
	
	public void sendOneClient(String id) {
		List<String> list = new ArrayList<String>();
		list.add(id);
		sendToGcmServer(list);
	}
	
	public void sendToGcmServer(List<String> members) {
		try { 
            MulticastResult result = sender.send(builder.build(), members, 10);
            List<Result> debug = result.getResults();
            
            for(int i=0; i<debug.size(); i++) {
            	Result res = debug.get(i);
            	
            	/* 차후 재전송 메커니즘 구현... */
            	if (res.getMessageId() != null) { 
                    // 푸쉬 전송 성공 
                    System.out.println(i+"번푸쉬 전송 성공"); 
                } else { 
                    String error = res.getErrorCodeName(); 
                    if (Constants.ERROR_INTERNAL_SERVER_ERROR.equals(error)) { 
                        // 구글 푸시 서버 에러 
                        System.out.println("구글 푸시 서버 에러"); 
                    }
                    System.out.println(error); 
                } 
            }
        } catch (IOException e) { 
            // TODO Auto-generated catch block 
            e.printStackTrace(); 
            System.out.println("e:" + e.toString()); 
        } 
	}
	// 메시지 전송 실패 결과 리턴한 클라이언트에 대해 재전송
	private void retransmission(String regID) {
		
	}
}
