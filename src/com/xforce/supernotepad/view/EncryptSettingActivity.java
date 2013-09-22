package com.xforce.supernotepad.view;


import com.xforce.supernotepad.util.Utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EncryptSettingActivity extends Activity implements
		OnClickListener, OnFocusChangeListener {
	TextView backButton, commitButton;
	EditText oldPasswdEditText, newPasswdEditText, confirmPasswdEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.encryptsetting_layout);

		backButton = (TextView) findViewById(R.id.backbtn);
		commitButton = (TextView) findViewById(R.id.commit_passwd_btn);
		oldPasswdEditText = (EditText) findViewById(R.id.oldpasswd_edit);
		newPasswdEditText = (EditText) findViewById(R.id.newpasswd_edit);
		confirmPasswdEditText = (EditText) findViewById(R.id.confirmpasswd_edit);

		backButton.setOnClickListener(this);
		commitButton.setOnClickListener(this);

		oldPasswdEditText.setOnFocusChangeListener(this);
		newPasswdEditText.setOnFocusChangeListener(this);
		confirmPasswdEditText.setOnFocusChangeListener(this);
	}

	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.backbtn:
			EncryptSettingActivity.this.finish();
			break;

		case R.id.commit_passwd_btn:
			if (checkOldPasswd()&&checkNewPasswd()&&checkConfirmPasswd()) {
				SharedPreferences sp=getSharedPreferences("setting", MODE_PRIVATE);
				//保存新密码
				Utils.savePasswd(sp, confirmPasswdEditText.getText().toString());
				EncryptSettingActivity.this.finish();
				Toast.makeText(EncryptSettingActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
			}

			break;
		}
	}

	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.oldpasswd_edit:
			if (!hasFocus) {
				checkOldPasswd();
			}

			break;

		case R.id.newpasswd_edit:
			if (!hasFocus) {
				checkNewPasswd();
			}

			break;

		case R.id.confirmpasswd_edit:
			if (!hasFocus) {
				checkConfirmPasswd();
			}
			break;
		}
	}

	/**
	 * 检查旧密码是否匹配
	 * 
	 * @return 匹配结果
	 */
	public boolean checkOldPasswd() {
		SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
		String oldString = oldPasswdEditText.getText().toString();
		if (!Utils.getPasswd(sp).equals(oldString)) {
			oldPasswdEditText.setError("请输入正确的密码");
			return false;
		}
		return true;
	}

	/**
	 * 检查新密码长度是否符合
	 * 
	 * @return 是否符合
	 */
	public boolean checkNewPasswd() {
		String newString = newPasswdEditText.getText().toString();
		if (newString.length() < 5) {
			newPasswdEditText.setError("请输入5位以上的密码");
			return false;
		}
		return true;
	}
	
	/**
	 * 检查两次密码是否相同
	 * @return
	 */
	public boolean checkConfirmPasswd(){
		String confirmString = confirmPasswdEditText.getText()
				.toString();
		if (!confirmString.equals(newPasswdEditText.getText()
				.toString())) {
			confirmPasswdEditText.setError("两次密码不一致");
			return false;
		}
		return true;
	}

}
