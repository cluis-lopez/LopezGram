package com.clopez.LopezGram;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetEvent
 */
@WebServlet("/GetEvent")
public class GetEvent extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String mail = request.getParameter("mail");
		String token = request.getParameter("token");
		int numevents = Integer.parseInt(request.getParameter("numevents"));
		Map<String, Object> mapa = new HashMap<>();
		Gson gson = new Gson();
		
		if (!DataStore.validToken(mail, token)) {
			mapa.put("status", "UNAUTHORIZED");
		} else { // Do the job
			mapa.put("status", "OK");
			Event[] evs = DataStore.GetEvents(numevents);
			mapa.put("NumberOfEvents", evs.length);
			mapa.put("Events", evs);
		}
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("cache-control", "no-cache");
		response.getWriter().write(gson.toJson(mapa));
		response.flushBuffer();
		
	}

}
