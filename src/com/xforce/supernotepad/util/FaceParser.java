package com.xforce.supernotepad.util;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.xforce.supernotepad.view.R;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class FaceParser {
	private Context mContext;
	private String[] mSmileyTexts;
	private Pattern mPattern;
	private HashMap<String, Integer> mSmileyToRes;
	public static final int[] DEFAULT_SMILEY_RES_IDS = { R.drawable.face01,
			R.drawable.face02, R.drawable.face03, R.drawable.face04,
			R.drawable.face05, R.drawable.face06, R.drawable.face07,
			R.drawable.face08, R.drawable.face09, R.drawable.face10,
			R.drawable.face11, R.drawable.face12, R.drawable.face13,
			R.drawable.face14, R.drawable.face15, R.drawable.face16,
			R.drawable.face17, R.drawable.face18, R.drawable.face19,
			R.drawable.face20, R.drawable.face21, R.drawable.face22,
			R.drawable.face23, R.drawable.face24, R.drawable.face25,
			R.drawable.face26, R.drawable.face27, R.drawable.face28,
			R.drawable.face29, R.drawable.face30, R.drawable.face31,
			R.drawable.face32, R.drawable.face33, R.drawable.face34,
			R.drawable.circle, R.drawable.square, R.drawable.rectangle,
			R.drawable.love };

	public FaceParser(Context context) {
		mContext = context;
		mSmileyTexts = mContext.getResources().getStringArray(
				R.array.face_textstyle);
		mSmileyToRes = buildSmileyToRes();
		mPattern = buildPattern();
	}

	private HashMap<String, Integer> buildSmileyToRes() {
		if (DEFAULT_SMILEY_RES_IDS.length != mSmileyTexts.length) {
			// Log.w("SmileyParser", "Smiley resource ID/text mismatch");
			// 表情的数量需要和数组定义的长度一致！
			throw new IllegalStateException("Smiley resource ID/text mismatch");
		}

		HashMap<String, Integer> smileyToRes = new HashMap<String, Integer>(
				mSmileyTexts.length);
		for (int i = 0; i < mSmileyTexts.length; i++) {
			smileyToRes.put(mSmileyTexts[i], DEFAULT_SMILEY_RES_IDS[i]);
		}

		return smileyToRes;
	}

	// 构建正则表达式
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(mSmileyTexts.length * 3);
		patternString.append('(');
		for (String s : mSmileyTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");

		return Pattern.compile(patternString.toString());
	}

	// 根据文本替换成图片
	public CharSequence replace(CharSequence text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		while (matcher.find()) {
			int resId = mSmileyToRes.get(matcher.group());
			builder.setSpan(new ImageSpan(mContext, resId), matcher.start(),
					matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		return builder;
	}

}
