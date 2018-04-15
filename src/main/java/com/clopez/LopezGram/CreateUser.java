package com.clopez.LopezGram;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class CreateUser
 */
@WebServlet("/CreateUser")
public class CreateUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("username");
		String pass = request.getParameter("password");
		String mail = request.getParameter("mail");
		Gson gson = new Gson();
		String result;
		
		if ((DataStore.createUser(mail, name, pass)).equals(mail)){
			result = mail; // Success creating the user
		} else {
			result = "INVALID";
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("cache-control", "no-cache");
		String json = gson.toJson("{'result' : " + result + "}");
		response.getWriter().write(json);
		response.flushBuffer();
	}

}
