package com.yyquan.jzh.view.LockView;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.yyquan.jzh.R;


/**
 * 图形加锁类
 * 
 * @author jiangzehui
 * 
 */

public class LockSetupActivity extends Activity implements
		LockPatternView.OnPatternListener, OnClickListener {

	private static final String TAG = "LockSetupActivity";
	private LockPatternView lockPatternView;
	private Button leftButton;
	private Button rightButton;

	private static final int STEP_1 = 1; // 初始化
	private static final int STEP_2 = 2; // 第一次设置手势
	private static final int STEP_3 = 3; // 按下继续按钮
	private static final int STEP_4 = 4; // 第二次设置手势

	private int step;

	private List<LockPatternView.Cell> choosePattern;

	private boolean confirm = false;

	SharedPreferences preferences;
	SharedPreferences.Editor editor;

	String password = "";
	private List<LockPatternView.Cell> lockPattern;
	boolean bool = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lock_setup);
		lockPatternView = (LockPatternView) findViewById(R.id.lock_pattern);
		lockPatternView.setOnPatternListener(this);
		leftButton = (Button) findViewById(R.id.left_btn);
		rightButton = (Button) findViewById(R.id.right_btn);
		preferences = getSharedPreferences("user_message",
				LockSetupActivity.MODE_PRIVATE);
		editor = preferences.edit();

		bool = preferences.getBoolean("lock", false);
		if (bool) {
			password = preferences.getString("lock_password", "no");
			lockPattern = LockPatternView.stringToPattern(password);
		}
		step = STEP_1;
		updateView();
	}

	private void updateView() {
		switch (step) {
		case STEP_1:
			leftButton.setText(R.string.cancel);
			rightButton.setText("");
			rightButton.setEnabled(false);
			choosePattern = null;
			confirm = false;
			lockPatternView.clearPattern();
			lockPatternView.enableInput();
			break;
		case STEP_2:
			leftButton.setText(R.string.try_again);
			rightButton.setText(R.string.goon);
			rightButton.setEnabled(true);
			lockPatternView.disableInput();
			break;
		case STEP_3:
			leftButton.setText(R.string.cancel);
			rightButton.setText("");
			rightButton.setEnabled(false);
			lockPatternView.clearPattern();
			lockPatternView.enableInput();
			break;
		case STEP_4:
			leftButton.setText(R.string.cancel);
			if (confirm) {
				rightButton.setText(R.string.confirm);
				rightButton.setEnabled(true);
				lockPatternView.disableInput();

			} else {
				rightButton.setText("");
				lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
				lockPatternView.enableInput();
				rightButton.setEnabled(false);
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.left_btn:
			if (step == STEP_1 || step == STEP_3 || step == STEP_4) {
				finish();
			} else if (step == STEP_2) {
				step = STEP_1;
				updateView();
			}
			break;

		case R.id.right_btn:
			if (step == STEP_2) {
				step = STEP_3;
				updateView();
			} else if (step == STEP_4) {

				if (bool) {

					editor.putString("lock_password", "no");
					editor.putBoolean("lock", false);
					editor.commit();
					Toast.makeText(LockSetupActivity.this, "手势密码解除成功",
							Toast.LENGTH_SHORT).show();
				} else {
					editor.putString("lock_password",
							LockPatternView.patternToString(choosePattern));
					editor.putBoolean("lock", true);
					editor.commit();
					Toast.makeText(LockSetupActivity.this, "手势密码设置成功",
							Toast.LENGTH_SHORT).show();
				}

				finish();
			}

			break;

		}

	}

	@Override
	public void onPatternStart() {
		Log.d(TAG, "onPatternStart");
	}

	@Override
	public void onPatternCleared() {
		Log.d(TAG, "onPatternCleared");
	}

	@Override
	public void onPatternCellAdded(List<LockPatternView.Cell> pattern) {
		Log.d(TAG, "onPatternCellAdded");
	}

	@Override
	public void onPatternDetected(List<LockPatternView.Cell> pattern) {
		Log.d(TAG, "onPatternDetected");

		if (pattern.size() < LockPatternView.MIN_LOCK_PATTERN_SIZE) {
			Toast.makeText(this,
					R.string.lockpattern_recording_incorrect_too_short,
					Toast.LENGTH_LONG).show();
			lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
			return;
		}

		if (choosePattern == null) {
			if (bool) {
				if (lockPattern.equals(pattern)) {
					choosePattern = new ArrayList<LockPatternView.Cell>(pattern);
					step = STEP_2;
					updateView();
				} else {
					Toast.makeText(this, "手势密码输入错误", Toast.LENGTH_LONG).show();
					lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);
					return;
				}
			} else {
				choosePattern = new ArrayList<LockPatternView.Cell>(pattern);
				step = STEP_2;
				updateView();
			}

			return;
		}

		if (choosePattern.equals(pattern)) {

			confirm = true;
		} else {
			confirm = false;
		}

		step = STEP_4;
		updateView();

	}

}
