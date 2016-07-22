

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class MainServlet
 */
@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MainServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		
		String param = request.getParameter("header");
		System.out.println(param);
		
		response.setCharacterEncoding("UTF-8");
		switch(param)
		{
		case "join" :
			join(request, response);
			break;	
		case "login" :
			login(request, response);
			break;
		case "regid" :
			regid(request, response);
			break;
		case "getMyInfo" :
			getMyInfo(request, response);
			break;
		case "facebook" :
			facebook(request, response);
			break;
		case "buy" :
			buy(request, response);
			break;
		case "resultUpdate" :
			resultUpdate(request, response);
			break;
		case "getRanking" :
			getRanking(request, response);
			break;
		case "registerFriend" :
			registerFriend(request, response);
			break;
		case "requestFriendList" :
			requestFriendList(request, response);
			break;
		case "present" :
			present(request, response);
			break;
			
		}
	}
	protected void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		String name = request.getParameter("name");
		
		Member member = new Member();
		member.setId(id);
		member.setPassword(password);
		member.setName(name);
		
		MemberDAO dao = new MemberDAO(member);
		
		String retValue = null;
		if(dao.join())
			retValue = "ok";
		else
			retValue = "no";
		dao.close();
		
		PrintWriter out = response.getWriter();
		out.println(retValue);
		out.flush();
	}

	protected void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		
		PrintWriter out = response.getWriter();
		
		Member member = new Member();
		member.setId(id);
		member.setPassword(password);
		
		MemberDAO dao = new MemberDAO(member);
		
		String resValue = null;
		int    retCode = dao.idCheck();
		if(retCode == 0) {
			resValue = "ok";
		}
		else if(retCode == 1)
			resValue = "password";
		else
			resValue = "no";
		
		dao.close();
		System.out.println(resValue);
		out.println(resValue);
		out.flush();
	}
	
	protected void regid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String regId = request.getParameter("regid");
		
		Member mem = new Member();
		mem.setId(id);
		mem.setRegid(regId);
		MemberDAO dao = new MemberDAO(mem);
		dao.regIdInsert();
	}
	
	protected void getMyInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		
		Member member = new Member();
		member.setId(id);
		
		MemberDAO dao = new MemberDAO(member);
		dao.getMyInfo();
		dao.close();
		
		String packet = new Gson().toJson(member);
		PrintWriter out = response.getWriter();
		out.write(packet);
		out.flush();
		System.out.println(packet);
	}

	protected void facebook(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String regId = request.getParameter("regid");
		String name = request.getParameter("name");
		
		Member member = new Member();
		member.setId(id);
		member.setRegid(regId);
		member.setName(name);
		
		MemberDAO dao = new MemberDAO(member);
		dao.facebookIdRegister();
		dao.regIdInsert();
		dao.close();
	}
	protected void resultUpdate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 게임 결과 갱신
		String id = request.getParameter("id");
		String result = request.getParameter("result");
		
		MemberDAO dao = new MemberDAO();
		dao.resultUpdate(id, result);
		dao.increasePoint(id, result.equals("win") ? 500 : 0);
		dao.close();
	}
	protected void buy(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		int dol = Integer.parseInt(request.getParameter("dol"));
		
		Member mem = new Member();
		mem.setId(id);
		MemberDAO dao = new MemberDAO(mem);
		dao.buyDol(dol);
		dao.decreasePoint(id, 1000);
	}
	protected void getRanking(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDAO dao = new MemberDAO();
		ArrayList<Rank> list = dao.getEntireRecord();
		
		String packet = new Gson().toJson(list);
		PrintWriter out = response.getWriter();
		out.write(packet);
		out.flush();
	}
	protected void registerFriend(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String friendId = request.getParameter("friendId");
		
		String msg = id;
		
		Member mem = new Member();
		mem.setId(friendId);
		MemberDAO mDao = new MemberDAO(mem);
		Member mem2 = new Member();
		mem2.setId(id);
		FriendDAO dao = new FriendDAO(mem2);
		
		System.out.println(id+", "+friendId);
		
		PrintWriter out = response.getWriter();
		
		if(!dao.dupCheck(friendId)) {
			out.write("이미 등록된 친구입니다.");
			out.flush();
			return;
		} else if(mDao.dupCheck()) {
			out.write("존재하지 않는 사용자입니다.");
			out.flush();
			return;
		}
		
		dao.registerFriend(id, friendId);
		dao.registerFriend(friendId, id);
		
		SendMessage send = new SendMessage();
		send.addHeader("addFriend");
		send.addPkt("id", msg);
		
		List<String> list = new ArrayList<String>();
		list.add(mDao.getRegId());
		send.sendToGcmServer(list);
		
		out.write("친구로 추가되었습니다.");
		
		mDao.close();
		dao.close();
	}
	protected void requestFriendList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		
		Member mem = new Member();
		mem.setId(id);
		
		FriendDAO dao = new FriendDAO(mem);
		ArrayList<Member> list = dao.getMyFriendList();
		
		String packet = new Gson().toJson(list);
		PrintWriter out = response.getWriter();
		out.write(packet);
		out.flush();
		dao.close();
	}
	protected void present(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String friendId = request.getParameter("friendId");
		int dol = Integer.parseInt(request.getParameter("dol"));
		
		Member mem = new Member();
		mem.setId(id);
		MemberDAO mDao = new MemberDAO(mem);
		mDao.getMyInfo();
		
		PrintWriter out = response.getWriter();
		
		if(mem.getPoint() < 1000) {
			out.write("돈이 모자랍니다.");
			out.flush();
			return;
		}
		
		mem = new Member();
		mem.setDol(dol);
		
		FriendDAO dao = new FriendDAO(mem);
		dao.presentDol(friendId);
		mDao.decreasePoint(id, 1000);
		mDao.close();
		
		SendMessage send = new SendMessage();
		send.addHeader("present");
		send.addPkt("id", id);
		mem.setId(friendId);
		mDao = new MemberDAO(mem);
		System.out.println(mem.getId()+", "+mDao.getRegId());
		send.sendOneClient(mDao.getRegId());
		
		out.write("성공적으로 선물되었습니다.");
		out.flush();
		
		dao.close();
		mDao.close();
	}
}
