package com.yyquan.jzh.fragment.regster;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yyquan.jzh.R;
import com.yyquan.jzh.activity.PhoneRegsterActivity;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cz.msebera.android.httpclient.Header;

/**
 * Created by jzh on 2015/9/29.
 */
public class PhoneRegsterFragment extends Fragment implements View.OnClickListener {

    View view;
    TextView tv_getcode;
    TextView tv_enter;
    TextView tv_title;
    EditText et_phone;
    EditText et_code;
    boolean bool_phone = false;
    boolean bool_code = false;
    String phone;//手机号码
    int i = 60;//发送短信倒计时
    String url = Ip.ip + "/YfriendService/DoGetUser";

    LinearLayout ll_back;
    String type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            Intent intent=getActivity().getIntent();
            type=intent.getStringExtra("type");
            view = inflater.inflate(R.layout.fragment_phone_regster, container, false);
            initialView();
        }
        return view;
    }


    /**
     * 初始化数据
     */
    private void initialView() {
        iniSMSSDK();
        tv_title = (TextView) view.findViewById(R.id.phone_regster_textview_title);
        if(type.equals("regster")){
            tv_title.setText("注  册");
        }else if(type.equals("password")){
            tv_title.setText("找回密码");
        }
        DialogView.Initial(getActivity(),"正在验证");
        tv_getcode = (TextView) view.findViewById(R.id.phone_regster_textview_getcode);
        tv_enter = (TextView) view.findViewById(R.id.phone_regster_textview_enter);
        tv_enter.setEnabled(false);
        et_phone = (EditText) view.findViewById(R.id.phone_regster_editText_phone);
        et_code = (EditText) view.findViewById(R.id.phone_regster_editText_code);
        ll_back = (LinearLayout) view.findViewById(R.id.phone_regster_layout_back);
        ll_back.setOnClickListener(this);
        tv_getcode.setOnClickListener(this);
        tv_enter.setOnClickListener(this);
        /**
         * 判断手机号码是否够11位
         */
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 11) {
                    bool_phone = true;
                } else {
                    bool_phone = false;
                }

                if (bool_code && bool_phone) {

                    tv_enter.setEnabled(true);
                    tv_enter.setBackgroundResource(R.drawable.shape_login_textview_focused);
                } else {

                    tv_enter.setEnabled(false);
                    tv_enter.setBackgroundResource(R.drawable.shape_login_textview_nomal);
                }
            }
        });

        /**
         * 判断验证码是否够4位
         */
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 4) {
                    bool_code = true;
                } else {
                    bool_code = false;
                }

                if (bool_code && bool_phone) {

                    tv_enter.setEnabled(true);
                    tv_enter.setBackgroundResource(R.drawable.shape_login_textview_focused);
                } else {

                    tv_enter.setEnabled(false);
                    tv_enter.setBackgroundResource(R.drawable.shape_login_textview_nomal);
                }
            }
        });


    }

    /**
     * 初始化短信sdk
     */
    private void iniSMSSDK() {

        SMSSDK.initSDK(getActivity(), "add91b1ca379", "9c4250259e8bdabb1e52bf867ab17781", true);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message m = new Message();
                m.what = 2;
                m.arg1 = event;
                m.arg2 = result;
                m.obj = data;
                h.sendMessage(m);


            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
        SMSSDK.getSupportedCountries();
    }


    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            if (msg.what == 1) {

                tv_getcode.setText("重新发送(" + i + ")");
                if (i == 0) {
                    i = 60;
                    tv_getcode.setText("获取验证码");
                    tv_getcode.setEnabled(true);

                }


            } else if (msg.what == 2) {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;


                if (result == SMSSDK.RESULT_COMPLETE) {

                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getActivity(), "验证码已经发送", Toast.LENGTH_SHORT).show();

                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        DialogView.dismiss();
                        if(type.equals("regster")){
                            ((PhoneRegsterActivity) getActivity()).gotoPhoneRegsterToMessgae();
                        }else if(type.equals("password")){
                            ((PhoneRegsterActivity) getActivity()).gotoGetPassword();
                        }
                        // Toast.makeText(getActivity(), "验证成功", Toast.LENGTH_SHORT).show();

                    }


                } else if (result == SMSSDK.RESULT_ERROR) {
                    DialogView.dismiss();
                    Toast.makeText(getActivity(), "验证失败,请重试", Toast.LENGTH_SHORT).show();
                }
            }


        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);
            SMSSDK.unregisterAllEventHandler();

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.phone_regster_textview_getcode:
                tv_getcode.setEnabled(false);
                phone = et_phone.getText().toString().trim();
                if(type.equals("regster")){
                    isRegster();
                }else if(type.equals("password")){
                    getCode();
                }


                break;
            case R.id.phone_regster_textview_enter:
                enterCode();
                break;
            case R.id.phone_regster_layout_back:
                getActivity().finish();
                break;
        }
    }

    /**
     * 判断该账号是否已注册
     */
    private void isRegster() {

        RequestParams params = new RequestParams();
        params.put("user", phone);
        params.put("action", "search");
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if (str != null) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {
                            getCode();
                        } else if (object.getString("code").equals("failure")) {
                            Toast.makeText(getActivity(), "该账号已注册", Toast.LENGTH_SHORT).show();

                            tv_getcode.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tv_getcode.setEnabled(true);
                    }
                } else {
                    tv_getcode.setEnabled(true);
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                tv_getcode.setEnabled(true);
            }
        });


    }


    /**
     * 获取短信验证码
     */
    private void getCode() {
        //  通过规则判断手机号

        if (!judgePhoneNums(phone)) {
            return;
        }

        SMSSDK.getVerificationCode("86", phone);

        tv_getcode.setText("重新发送(" + i + ")");
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (; i >= 0; i--) {
                    h.sendEmptyMessage(1);
                    if (i == 0) {
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    /**
     * 验证短信验证码
     */
    private void enterCode() {
        DialogView.show();
        String code = et_code.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        SMSSDK.submitVerificationCode("86", phone, code);

        ((PhoneRegsterActivity) getActivity()).phone = phone;

    }


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        tv_getcode.setEnabled(true);
        Toast.makeText(getActivity(), "手机号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }


}
