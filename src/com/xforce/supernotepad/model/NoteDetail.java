package com.xforce.supernotepad.model;

public class NoteDetail {
	
	private int n_id;//���±���(Ψһ��ʶ��)
	private int t_id;//�����
	private String Ctime;//����ʱ��
	private String Rtime;//����ʱ��
	private String content;//���±�������
	private int locked;//���±��Ƿ����
	
	
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
