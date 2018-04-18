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
 * Servlet implementation class CheckUser
 */
@WebServlet("/TokenCheck")
public class TokenCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

			String mail = request.getParameter("mail");
			String token = request.getParameter("token");
			Gson gson = new Gson();
			HashMap<String, String> mapa = new HashMap<>();
			
			if (DataStore.validToken(mail, token)) {
				mapa.put("mail", mail);
				mapa.put("token", token);
			} else {
				mapa.put("mail", mail);
				mapa.put("token", "INVALID");
			}
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("cache-control", "no-cache");
			response.getWriter().write(gson.toJson(mapa));
			response.flushBuffer();

	}
}
