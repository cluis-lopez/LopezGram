package com.clopez.lopezgram;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class Foto {
	private String name;
	private String bucket;
	private byte[] bytes;
	
	public Foto(String name, String bucket, byte[] bytes) {
		this.name = name;
		this.bucket = bucket;
		this.bytes = bytes;
	}
	
	public boolean upload() {
		System.out.println("Tamaño foto: " + bytes.length);
		GcsService gcsService = GcsServiceFactory.createGcsService();
		GcsFileOptions options = new GcsFileOptions.Builder().mimeType("image/jpg").acl("public-read").build();
		GcsFilename filename = new GcsFilename(bucket, name);
		GcsOutputChannel writeChannel;
		try {
			writeChannel = gcsService.createOrReplace(filename, options);
			writeChannel.write(ByteBuffer.wrap(bytes));
			writeChannel.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
