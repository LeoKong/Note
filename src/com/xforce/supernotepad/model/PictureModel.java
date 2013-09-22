package com.xforce.supernotepad.model;

import java.io.Serializable;

public class PictureModel implements Serializable{
	private int p_id;//图片id
	private int n_id;//所属记事本id
	private String pName;//图片文件名
	private String illustration;//图片文字说明
	
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
