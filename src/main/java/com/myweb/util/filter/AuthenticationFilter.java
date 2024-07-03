package com.myweb.util.filter;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter({"/board/write.board", "/board/registForm.board", "/user/mypage.user", "/user/modify.user", "/user/update.user", "/user/delete.user", "/user/deleteForm.user"}) //글 작성화면, 글 등록...
public class AuthenticationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		//세션이 있는지 확인해서 -> 세션이 있으면 통과, 세션이 없으면 로그인 페이지로
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		//세션은 리퀘스트에서 얻음
		HttpSession session = request.getSession();
		String id = (String)session.getAttribute("user_id");
		if(id == null) { //로그인이 안됨
			response.setContentType("text/html; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			out.println("alert('로그인이 필요한 서비스입니다.');");
			out.println("location.href='" + request.getContextPath() + "/user/login.user';");
			out.println("</script>");
			return; //함수 종료 -> 컨트롤러를 실행하지 않음
		}
		
		chain.doFilter(request, response); //다음 필터로 연결
	}
}
