package com.clopez.LopezGram;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class CreateEvent
 */
@WebServlet("/CreateEvent")
public class CreateEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Date d = new Date();
		Gson gson = new Gson();
		HashMap<String, String> mapa = new HashMap<>();
		String mail = request.getParameter("mail");
		String token = request.getParameter("token");
		if (!DataStore.validToken(mail, token)) {
			System.out.println("Invalid User");
			mapa.put("status", "Invalid User");
		} else { //Let's do the task
			
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("cache-control", "no-cache");
		response.getWriter().write(gson.toJson(mapa));
		response.flushBuffer();
		
	}

}
