package com.lxy.wifistore.act;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.lib.app.BaseActivity;
import com.lxy.wifistore.R;


/**
 * Depiction: 登录
 * <p>
 * Author: Kevin Lynn
 * <p>
 * Create Date：2014年7月21日 下午3:56:05
 * <p>
 * Modify:
 * <p>
 * 
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends BaseActivity {
	private Button       loginBtn;
	private Button       resBtn;
	private LinearLayout loginLayout;
	private LinearLayout resLayout;
	
	private EditText     mailInput;
	private EditText     nameInput;
	private EditText     passwdInput1;
	private EditText     passwdInput2;
	private EditText     codeInput;
	private ImageView    codeView;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_login);
		loginLayout = (LinearLayout) findViewById(R.id.login_layout);
		resLayout = (LinearLayout) findViewById(R.id.res_layout);
		loginBtn = (Button) findViewById(R.id.login_tab);
		resBtn = (Button) findViewById(R.id.res_tab);
		loginBtn.setSelected(true);
		
		final String[] array = getResources().getStringArray(R.array.login_res_array);
		LinearLayout item1 = (LinearLayout) findViewById(R.id.login_res_item1);
		TextView tip1 = (TextView) item1.findViewById(R.id.login_res_tip_tv);
		tip1.setText(array[0]);
		mailInput = (EditText) item1.findViewById(R.id.login_res_input_tv);
		
		LinearLayout item2 = (LinearLayout) findViewById(R.id.login_res_item2);
		TextView tip2 = (TextView) item2.findViewById(R.id.login_res_tip_tv);
		tip2.setText(array[1]);
		nameInput = (EditText) item2.findViewById(R.id.login_res_input_tv);
		
		LinearLayout item3 = (LinearLayout) findViewById(R.id.login_res_item3);
		TextView tip3 = (TextView) item3.findViewById(R.id.login_res_tip_tv);
		tip3.setText(array[2]);
		passwdInput1 = (EditText) item3.findViewById(R.id.login_res_input_tv);
		passwdInput1.setInputType(0x81);
		
		LinearLayout item4 = (LinearLayout) findViewById(R.id.login_res_item4);
		TextView tip4 = (TextView) item4.findViewById(R.id.login_res_tip_tv);
		tip4.setText(array[3]);
		passwdInput2 = (EditText) item4.findViewById(R.id.login_res_input_tv);
		passwdInput2.setInputType(0x81);
		
		codeInput = (EditText) findViewById(R.id.login_res_input_code_tv);
		codeView = (ImageView) findViewById(R.id.login_res_code_iv);
	}
	
	public void onLoginTabAction(View v) {
		loginBtn.setSelected(true);
		resBtn.setSelected(false);
		loginLayout.setVisibility(View.VISIBLE);
		resLayout.setVisibility(View.GONE);
	}
	
	public void onResTabAction(View v) {
		loginBtn.setSelected(false);
		resBtn.setSelected(true);
		loginLayout.setVisibility(View.GONE);
		resLayout.setVisibility(View.VISIBLE);
	}
	
	public void onLoginAction(View v) {
		openActivity(MainActivity.class, null);
		finish();
		overridePendingTransition(R.anim.from_right_to_left, R.anim.from_left_to_right);
	}
	
	public void onForgetPasswdAction(View v) {
	}
	
	public void onResAction(View v) {
	}
}
