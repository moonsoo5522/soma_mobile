

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class ManageServlet
 */
@WebServlet("/ManageServlet")
public class ManageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ManageServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		request.setCharacterEncoding("UTF-8");
		
		String param = request.getParameter("header");
		System.out.println(param);
		
		
		switch(param)
		{
		
		case "banlist":
			banlist(request, response);
			break;
		case "ban":
			ban(request, response);
			break;
		case "pointlist":
			pointlist(request, response);
			break;
		case "pointset":
			pointset(request, response);
			break;
		case "dollist":
			dollist(request, response);
			break;
		case "dolset":
			dolset(request, response);
			break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	protected void banlist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDAO dao = new MemberDAO();
		ArrayList<Member> list = dao.getMembersInfo();
		
		request.setAttribute("member", list);
		System.out.println(new Gson().toJson(list));
		RequestDispatcher dis = request.getRequestDispatcher("memberlist.jsp");
		dis.forward(request, response);
	}

	protected void ban(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		
		Member member = new Member();
		member.setId(id);
		MemberDAO dao = new MemberDAO(member);
		dao.deleteMember();
		
		response.sendRedirect("/soma7th/ban.html");
	}
	
	protected void pointlist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDAO dao = new MemberDAO();
		ArrayList<Member> list = dao.getMembersInfo();
		
		request.setAttribute("member", list);
		System.out.println(new Gson().toJson(list));
		RequestDispatcher dis = request.getRequestDispatcher("point.jsp");
		dis.forward(request, response);
	}
	
	protected void pointset(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		int point = Integer.parseInt(request.getParameter("point"));
		
		Member mem = new Member();
		mem.setId(id);
		MemberDAO dao = new MemberDAO(mem);
		dao.setPoint(point);
		
		response.sendRedirect("/soma7th/pointconfirm.html");
	}
	
	protected void dollist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDAO dao = new MemberDAO();
		ArrayList<Member> list = dao.getMembersInfo();
		ArrayList<DolInfo> dolList = new ArrayList<DolInfo>();
		
		for(int i=0; i<list.size(); i++) {
			Member mem = list.get(i);
			DolInfo info = new DolInfo();
			String dol="";
			
			for(int j=0; j<8; j++) {
				if(((mem.getDol() >> j) & 1) == 1) {
					dol+=(j+1+" ");
				}
			}
			info.setId(mem.getId());
			info.setDol(dol);
			dolList.add(info);
		}
		
		request.setAttribute("member", dolList);
		RequestDispatcher dis = request.getRequestDispatcher("dollist.jsp");
		dis.forward(request, response);
	}
	
	protected void dolset(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		int dol = Integer.parseInt(request.getParameter("dol"));
		
		Member mem = new Member();
		mem.setId(id);
		MemberDAO dao = new MemberDAO(mem);
		dao.buyDol(dol);
		
		response.sendRedirect("/soma7th/dolconfirm.html");
	}
}
