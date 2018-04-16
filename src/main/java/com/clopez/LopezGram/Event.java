package com.clopez.LopezGram;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Event {
	public String creator;
	public String  id;
	public Date createOn;
	public String text;
	public List<String> picture;
	public List<String> likes;
	public List<String> hates;
	public List<String> comments;
	
	public Event(String user, String text, String picture) {
		creator = user;
		createOn = new Date();
		id = (UUID.randomUUID()).toString();
		this.text = text;
		this.picture = new ArrayList<>();
		this.picture.add(picture);
		likes = new ArrayList<>();
		hates = new ArrayList<>();
		comments = new ArrayList<>();
	}
}
