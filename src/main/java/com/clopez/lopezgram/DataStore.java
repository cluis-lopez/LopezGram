package com.clopez.lopezgram;

import java.util.Date;
import java.util.List;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.UUID;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;

public class DataStore {

	static DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

	public static boolean validToken(String mail, String token) {
		Key k = KeyFactory.createKey("User", mail);
		Filter propertyFilter = new FilterPredicate("__key__", FilterOperator.EQUAL, k);
		Query q = new Query("User").setFilter(propertyFilter);
		List<Entity> ents = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1));
		
		if (ents.size() != 1) {
			System.out.println("No se encuentra al usuario");
			return false;
		} else {
			Entity e = ents.get(0);
			// First check if the toke is still valid
			Date d = new Date();
			Date dt = (Date) e.getProperty("TokenValidUpTo");
			if (d.getTime() > dt.getTime()) {
				System.out.println("Token expired");
				return false;
			}
			// CHeck is the token matches with the stored token in the Datastore
			if (String.valueOf(e.getProperty("Token")).equals(token)) {
				return true;
			}
			return false;
		}
	}
	
	public static String createUser(String mail, String name, String password) {
		Key k = KeyFactory.createKey("User", mail);
		Filter propertyFilter = new FilterPredicate("__key__", FilterOperator.EQUAL, k);
		Query q = new Query("User").setFilter(propertyFilter);
		List<Entity> ents = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1));

		if (ents.size() >= 1) { // One user with this email already exists in the databse. Return.
			System.out.println("Usuario ya registrado");
			return "INVALID";
		}
		// No users with this email, we may create one
		
		Entity e = new Entity("User", mail);
		Date d = new Date();
		Date old = new Date(0);
		String[] pass = new String[2];
		try {
			pass = Encrypt.hashPasswd(password);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		e.setProperty("Name", name);
		e.setProperty("Password", pass[0]);
		e.setProperty("Salt", pass[1]);
		e.setProperty("UserSince", d);
		e.setProperty("LastLogin", null);
		e.setProperty("TokenValidUpTo", old);
		ds.put(e);
		
		return mail;
	}
	
	public static String[] loginUser(String mail, String passwd) {
		Key k = KeyFactory.createKey("User", mail);
		Filter propertyFilter = new FilterPredicate("__key__", FilterOperator.EQUAL, k);
		Query q = new Query("User").setFilter(propertyFilter);
		List<Entity> ents = ds.prepare(q).asList(FetchOptions.Builder.withLimit(1));
		
		String[] ret = new String[2];
		if (ents.size() == 1) { // The user exists, then check the password
			Entity e = ents.get(0);
			if (Encrypt.checkPasswd(passwd, (String) e.getProperty("Password"), (String) e.getProperty("Salt"))){ // Las passwords coinciden !!
				ret[1] = (String) e.getProperty("Name");
				UUID uuid = UUID.randomUUID();
				Date d = new Date();
				long t = d.getTime();
				t = t + 24 * 60 * 60 * 1000; // tiempo de validez: 1 d�a
				Date valid = new Date(t);
				e.setProperty("Token", uuid.toString());
				e.setProperty("TokenValidUpTo", valid);
				e.setProperty("LastLogin", d);
				ds.put(e);
				ret[0] = uuid.toString();
				return ret;
			}
		}
		ret[0] = ret[1] = "INVALID";
		return ret;
	}
	
	public static void SaveEvent(Event e) {
		Entity ent = new Entity("Event", e.id);
		ent.setProperty("Creator", e.creator);
		ent.setProperty("User", e.name);
		ent.setProperty("CreateOn", e.createOn);
		ent.setProperty("Text", e.text);
		ent.setProperty("Pictures", e.picture);
		ent.setProperty("Likes", e.likes);
		ent.setProperty("Hates", e.hates);
		ent.setProperty("Comments", e.comments);
		ds.put(ent);
	}
	
	public static Event[] GetEvents(int numevents) {
		Query q = new Query("Event");
		q.addSort("CreateOn", SortDirection.DESCENDING);
		List<Entity> ents = ds.prepare(q).asList(FetchOptions.Builder.withLimit(numevents));
		
		Event[] evs = new Event[ents.size()];
		
		System.out.println("Numero de eventos: " + numevents);
		System.out.println("Numero de entodades encontradas: " + ents.size());
		
		for (int i=0; i<ents.size(); i++) {
			Entity e;
			try {
				e = ds.get((ents.get(i)).getKey());
				evs[i] = new Event();
				evs[i].id = (String) e.getKey().getName();
				evs[i].createOn = (Date) e.getProperty("CreateOn");
				evs[i].creator = (String) e.getProperty("Creator");
				evs[i].name = (String) e.getProperty("User");
				evs[i].text = (String) e.getProperty("Text");
				evs[i].picture = (ArrayList<String>) e.getProperty("Pictures");
				//TODO faltan los Likes, Hates, etc.
			} catch (EntityNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		return evs;
	}
}
