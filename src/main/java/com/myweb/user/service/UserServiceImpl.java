package com.myweb.user.service;

import java.io.IOException;

import com.myweb.user.model.UserDAO;
import com.myweb.user.model.UserDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class UserServiceImpl implements UserService {

	@Override
	public void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//값을 대신 받을 수 있음
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String gender = request.getParameter("gender");
		
		//중복되는 회원이 있는지 확인
		//중복이 없는 경우 회원가입 처리
		UserDAO dao = UserDAO.getInstance();
		int cnt = dao.findUser(id);
		if(cnt == 1) { //아이디 중복
			request.setAttribute("msg", "이미 존재하는 회원입니다.");
			request.getRequestDispatcher("join.jsp").forward(request, response);
		} else { //중복x - 회원가입
			dao.insertUser(id, pw, name, email, gender);
			response.sendRedirect("login.user"); //MVC방식에서 리다이렉트는 다시 컨트롤러로 태울 때 사용합니다.
		}
	}

	@Override
	public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		
		UserDAO dao = UserDAO.getInstance();
		UserDTO dto = dao.login(id, pw);
		if(dto == null) { //아이디 or 비밀번호가 틀렸음
			request.setAttribute("msg", "아이디 또는 비밀번호를 확인하세요.");
			request.getRequestDispatcher("login.jsp").forward(request, response);
		} else { //로그인 성공
			//세션에 로그인 성공에 대한 내용을 저장
			HttpSession session = request.getSession(); //리퀘스트에서 현재 세션을 얻음
			session.setAttribute("user_id", dto.getId());
			session.setAttribute("user_name", dto.getName());
			response.sendRedirect("mypage.user"); //다시 컨트롤러를 태워 나간다.
		}
	}

	@Override
	public void getInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//아이디 기반으로 회원정보를 조회해서 데이터를 가지고, 수정페이지로 이동
		//실습...
		/*
			1.아이디는 세션에 있습니다.
			2.아이디 기반으로 회원정보를 조회하는 getInfo()를 DAO에 생성합니다.
			3.서비스에서는 getInfo()를 호출하고, 조회한 데이터를 request에 저장합니다.
			4.forward를 이용해서 modify.jsp로 이동합니다.
			5.회원정보를 input태그에 미리 출력해주면 됩니다.
			
		*/
		HttpSession session = request.getSession();
		String id = (String)session.getAttribute("user_id");
		
		UserDAO dao = UserDAO.getInstance();
		UserDTO dto = dao.getInfo(id);
		request.setAttribute("user_id", dto.getId());
		request.setAttribute("user_pw", dto.getPw());
		request.setAttribute("user_name", dto.getName());
		request.setAttribute("user_email", dto.getEmail());
		request.setAttribute("user_gender", dto.getGender());
		request.getRequestDispatcher("modify.jsp").forward(request, response);
		//response.sendRedirect("modify.jsp");
	}
}
