package com.clopez.lopezgram;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class LoginUser
 */
@WebServlet("/LoginUser")
public class LoginUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String mail = request.getParameter("mail");
		String passwd = request.getParameter("password");
		Gson gson = new Gson();
		HashMap<String, String> mapa = new HashMap<String, String>();
		
		System.out.println("Mail: "+mail+"   Password: "+passwd);
		
		String[] res = new String[2];
		
		if (mail != null && passwd != null) {
			res = DataStore.loginUser(mail, passwd);
			if (res[0].equals("INVALID")) {
				mapa.put("mail", mail);
				mapa.put("token", "INVALID");
				mapa.put("user", "INVALID");
			} else {
				mapa.put("mail", mail);
				mapa.put("token", res[0]);
				mapa.put("user", res[1]);
			}
		} else {
			mapa.put("mail", "INVALID");
			mapa.put("token", "INVALID");
			mapa.put("user", "INVALID");
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("cache-control", "no-cache");
		response.getWriter().write(gson.toJson(mapa));
		response.flushBuffer();
		System.out.println(gson.toJson(mapa));
	}

}
