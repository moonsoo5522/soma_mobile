
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class MemberDAO {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	Member member;
	Rank rank;
	
	public MemberDAO(Member member) {
		dbConnect();
		this.member = member;
	}
	public MemberDAO(Rank rank) {
		dbConnect();
		this.rank = rank;
	}
	public MemberDAO() {
		dbConnect();
	}
	
	private void dbConnect() {
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context)initContext.lookup("java:comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/mysql");
			conn = ds.getConnection();
			
		} catch (NamingException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean dupCheck() {
		String sql = "select id from member where id='"+member.getId()+"'";
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				if(rs.getString("id").equals(member.getId())) return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean join() {
		
		if(dupCheck()) {
			String sql = "insert into member(id, password, name) values(?,?,?)";
			try {
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, member.getId());
				pstmt.setString(2, member.getPassword());
				pstmt.setString(3, member.getName());
				pstmt.executeUpdate();
				
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public int idCheck() {
		String sql = "select id, password from member where id='"+member.getId()+"'"
				+" and mode=0";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				if(member.getPassword().equals(rs.getString("password")))
					return 0; // 로그인 성공시
				else
					return 1; // 패스워드 불일치시
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 2; // 아이디가 없을 시
	}
	// 페북아이디가 등록되어있지 않을경우만 등록
	public void facebookIdRegister() {
		String sql = "insert into member(id, password, name, mode) "
				+"select '"+member.getId()+"', 'null', '"+member.getName()+"', 1 from dual "
				+"where not exists (select * from member where mode=1 and id='"+member.getId()+"')";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getMyInfo() {
		String sql = "select * from member where id='"+member.getId()+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next())
				member.setDol(rs.getInt("dol"));
				member.setWin(rs.getInt("win"));
				member.setLose(rs.getInt("lose"));
				member.setPoint(rs.getInt("point"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<Rank> getEntireRecord() {
		String sql = "select id, win, lose from member";
		ArrayList<Rank> list = new ArrayList<Rank>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Rank rank = new Rank();
				rank.setId(rs.getString("id"));
				rank.setWin(rs.getInt("win"));
				rank.setLose(rs.getInt("lose"));
				
				list.add(rank);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	public ArrayList<Member> getMembersInfo() {
		String sql = "select * from member";
		ArrayList<Member> list = new ArrayList<Member>();
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				Member mem = new Member();
				mem.setId(rs.getString("id"));
				mem.setPassword(rs.getString("password"));
				mem.setName(rs.getString("name"));
				mem.setWin(rs.getInt("win"));
				mem.setLose(rs.getInt("lose"));
				mem.setPoint(rs.getInt("point"));
				mem.setDol(rs.getInt("dol"));
				
				list.add(mem);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public void resultUpdate(String id, String result) {
		String sql = "update member set "+result+"="+result+"+1"
				+ " where id='"+id+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void increasePoint(String id, int point) {
		String sql = "update member set point=point+"+point+
				" where id='"+id+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean pointCheck(String id, int amount) {
		String sql = "select mileage from member where id='"+id+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			System.out.println(id);
			
			if(rs.next()) {
				System.out.println(rs.getInt("mileage") + " / " + amount);
				int mileage = rs.getInt("mileage");
				if(mileage - amount < 0) 
					return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	public void decreasePoint(String id, int amount) {
		String sql = "update member set point=point-"+amount+
				" where id='"+id+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void regIdInsert() {
		String sql = "update member set regid='"+member.getRegid()+"'"
				+ " where id='"+member.getId()+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void buyDol(int dol) {
		System.out.println(member.getId());
		String sql = "update member set dol=dol+"+(1<<(dol-1))+" where id='"+member.getId()+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void close() {
		try {
			if(rs != null)
				rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getRegId() {
		String sql = "select regid from member where id='"+member.getId()+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return rs.getString("regid");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public void deleteMember() {
		String sql = "delete from member where id='"+member.getId()+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void setPoint(int point) {
		String sql = "update member set point="+point+" where id='"+member.getId()+"'";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
