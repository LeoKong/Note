package com.xforce.supernotepad.model;

public class NoteDetail {
	
	private int n_id;//记事本号(唯一标识符)
	private int t_id;//分组号
	private String Ctime;//创建时间
	private String Rtime;//提醒时间
	private String content;//记事本的内容
	private int locked;//记事本是否加密
	
	
	public int getN_id() {
		return n_id;
	}
	public void setN_id(int n_id) {
		this.n_id = n_id;
	}
	
	public int getT_id() {
		return t_id;
	}
	public void setT_id(int t_id) {
		this.t_id = t_id;
	}
	public String getCtime() {
		return Ctime;
	}
	public void setCtime(String ctime) {
		Ctime = ctime;
	}
	public String getRtime() {
		return Rtime;
	}
	public void setRtime(String rtime) {
		Rtime = rtime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLocked() {
		return locked;
	}
	public void setLocked(int locked) {
		this.locked = locked;
	}
	
	

}
