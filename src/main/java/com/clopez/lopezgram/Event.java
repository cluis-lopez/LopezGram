package com.clopez.lopezgram;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class Event {
	public String creator;
	public String name;
	public String  id;
	public Date createOn;
	public String text;
	public List<String> picture;
	public List<String> likes;
	public List<String> hates;
	public List<String> comments;
	
	public Event() {
		creator = "";
		name = "";
		createOn = new Date();
		id = (UUID.randomUUID()).toString();
		text="";
		picture = new ArrayList<>();
		likes = new ArrayList<>();
		hates = new ArrayList<>();
		comments = new ArrayList<>();
	}
}
