package com.xforce.supernotepad.model;

import java.io.Serializable;

public class PictureModel implements Serializable{
	private int p_id;//ͼƬid
	private int n_id;//�������±�id
	private String pName;//ͼƬ�ļ���
	private String illustration;//ͼƬ����˵��
	
	public int getP_id() {
		return p_id;
	}
	public void setP_id(int p_id) {
		this.p_id = p_id;
	}
	public int getN_id() {
		return n_id;
	}
	public void setN_id(int n_id) {
		this.n_id = n_id;
	}
	public String getpName() {
		return pName;
	}
	public void setpName(String pName) {
		this.pName = pName;
	}
	public String getIllustration() {
		return illustration;
	}
	public void setIllustration(String illustration) {
		this.illustration = illustration;
	}
	

}
