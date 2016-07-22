

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class MultiServlet
 */
@WebServlet("/MultiServlet")
public class MultiServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MultiServlet() {
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
		case "play":
			play(request, response);
			break;
		case "dol":
			dol(request, response);
			break;
		case "gamePlay":
			gamePlay(request, response);
			break;
		case "entireMembers":
			entireMembers(request, response);
			break;
		}
	}
	
	protected void play(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String p2 = request.getParameter("p2");
		
		Member mem = new Member();
		mem.setId(p2);
		MemberDAO dao = new MemberDAO(mem);
		String regid = dao.getRegId();
		
		SendMessage send = new SendMessage();
		send.addHeader("play");
		send.addPkt("id", p2);
		send.addPkt("targetId", id);
		send.sendOneClient(regid);
		
		PrintWriter out = response.getWriter();
		out.write("play");
		out.flush();
	}
	
	protected void dol(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String targetId = request.getParameter("targetId");
		int x = Integer.parseInt(request.getParameter("x"));
		int y = Integer.parseInt(request.getParameter("y"));
		
		DolPacket dol = new DolPacket();
		dol.x = x;
		dol.y = y;
		
		SendMessage send = new SendMessage();
		send.addHeader("dol");
		String packet = new Gson().toJson(dol);
		send.addPkt("where", packet);
		System.out.println(targetId);
		
		Member mem = new Member();
		mem.setId(targetId);
		MemberDAO dao = new MemberDAO(mem);
		send.sendOneClient(dao.getRegId());
	}
	protected void gamePlay(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		
		Member mem = new Member();
		mem.setId(id);
		MemberDAO dao = new MemberDAO(mem);
		
		SendMessage send = new SendMessage();
		send.addHeader("gamePlay");
		send.sendOneClient(dao.getRegId());
	}
	protected void entireMembers(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		MemberDAO dao = new MemberDAO();
		ArrayList<Member> list = dao.getMembersInfo();
		
		PrintWriter out = response.getWriter();
		String packet = new Gson().toJson(list);
		out.write(packet);
		out.flush();
	}
}
