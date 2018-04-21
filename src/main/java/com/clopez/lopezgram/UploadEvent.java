package com.clopez.lopezgram;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.google.common.io.ByteStreams;

@WebServlet("/UploadEvent")
public class UploadEvent extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		
		// For multipart support
		
		ServletFileUpload upload = new ServletFileUpload();

		String filename = "", bucket = "", ret = "";
		byte[] bytes = new byte[0];
		Map<String, String> mapa = new HashMap<>();

		try {

			FileItemIterator iterator = upload.getItemIterator(req);
			
			while (iterator.hasNext()) {
				FileItemStream item = iterator.next();
			    InputStream stream = item.openStream();
				if (item.isFormField()) {
					String key = item.getFieldName();
					String value = Streams.asString(stream);
					System.out.println(key + ","+ value);
					mapa.put(key, value);
				} else {
					filename = item.getName();
					bucket = item.getFieldName();
					bytes = ByteStreams.toByteArray(stream); // De momento solo se puede subir una única foto
					stream.close();
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
			ret = "FAILED";
		}
		
		if (DataStore.validToken(mapa.get("mail"), mapa.get("token"))) { // Token is valid
			Event e = new Event();
			e.text = mapa.get("text");
			e.creator = mapa.get("mail");
			e.name = mapa.get("user");
			Foto foto = new Foto(filename, bucket, bytes);
			if (foto.upload()) {
				e.picture.add(filename);
			} else {
				e.picture.add("NOPICT");
			}
			DataStore.SaveEvent(e);
			ret = "UPLOADED";
		} else {
			ret = "UNAUTHORIZED";
		}


		res.setStatus(HttpServletResponse.SC_CREATED);
		res.setContentType("text/plain");
		PrintWriter out = res.getWriter();
		out.write(ret);
		out.flush();
		out.close();

	}
}