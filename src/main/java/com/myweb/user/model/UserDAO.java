package com.myweb.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.myweb.util.JDBCUtil;

public class UserDAO {

	//DAO는 불필요하게 여러개 만들 필요가 없기 때문에 객체가 한개만 생성되도록
	//singleton형식으로 설계
	
	//1.나 자신의 객체를 1개 생성하고, private을 붙임
	private static UserDAO instance = new UserDAO();
	
	//2.직접 객체를 생성할 수 없도록 생성자에도 private을 붙임
	private UserDAO() {
		//커넥션풀에 사용할 객체를 얻어옴
		try {
			InitialContext init = new InitialContext();
			ds = (DataSource)init.lookup("java:comp/env/jdbc/oracle");
		} catch(Exception e) {
			System.out.println("커넥션풀 에러");
		} //시작설정객체
	}
	
	//3.객체 생성을 요구할 때 getter메서드를 이용해서 1번의 객체를 반환
	public static UserDAO getInstance() {
		return instance;
	}
	
	//----------------------------------------------------------------------------------------------------
	//필요한 메서드를 생성
	
	//데이터베이스 연결
//	public String url = "jdbc:oracle:thin:@localhost:1521:xe";
//	public String uid = "HR";
//	public String upw = "HR";
	
	//커넥션풀 객체정보
	private DataSource ds;
	
	//아이디 중복검사
	public int findUser(String id) {
		int result = 0;

		String sql = "SELECT * FROM USERS WHERE ID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection(); //conn
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) { //다음 행이 있다는 것은 유저가 있다는 의미
				result = 1; //유저 있다는 뜻
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return result;
	}
	
	//회원가입
	public void insertUser(String id, String pw, String name, String email, String gender) {
		String sql = "INSERT INTO USERS(ID, PW, NAME, EMAIL, GENDER) VALUES (?, ?, ?, ?, ?)";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			pstmt.setString(3, name);
			pstmt.setString(4, email);
			pstmt.setString(5, gender);
			pstmt.executeUpdate(); //i, u, d구문은 executeUpdate()
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt, null);
		}
	}
	
	public UserDTO login(String id, String pw) {
		UserDTO dto = null;
		
		String sql = "SELECT * FROM USERS WHERE ID = ? AND PW = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, pw);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String gender = rs.getString("gender");
				
				dto = new UserDTO(id, null, name, email, gender, null);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return dto;
	}

	public UserDTO getInfo(String id) {
		UserDTO dto = null;
		
		String sql = "SELECT * FROM USERS WHERE ID = ?";
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = ds.getConnection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				String pw = rs.getString("pw");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String gender = rs.getString("gender");
				
				dto = new UserDTO(id, pw, name, email, gender, null);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.close(conn, pstmt, rs);
		}
		return dto;
	}
}
