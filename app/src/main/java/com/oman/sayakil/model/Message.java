package com.oman.sayakil.model;

import java.io.Serializable;

public class Message implements Serializable {

	private String date;
	private String content;
	private boolean fromMe;
	private boolean showTime = true;

	public Message() {
	}

	public Message(String content, boolean fromMe, String date) {

		this.date = date;
		this.content = content;
		this.fromMe = fromMe;
	}




	public String getDate() {
		return date;
	}

	public String getContent() {
		return content;
	}

	public boolean isFromMe() {
		return fromMe;
	}

}