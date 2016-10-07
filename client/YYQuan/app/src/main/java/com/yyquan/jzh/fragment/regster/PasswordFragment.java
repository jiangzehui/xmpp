package com.yyquan.jzh.fragment.regster;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
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
import com.yyquan.jzh.location.Location;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.smssdk.SMSSDK;
import cz.msebera.android.httpclient.Header;

/**
 * Created by jzh on 2015/9/29.
 */
public class PasswordFragment extends Fragment implements View.OnClickListener {

    View view;
    LinearLayout ll_back;
    TextView tv_enter;
    EditText et_password;
    EditText et_password_again;

    String password;
    String password_again;

    boolean bool_password;
    boolean bool_password_again;
    private String url = Ip.ip + "/YfriendService/DoGetUser";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            DialogView.Initial(getActivity(),"正在重置密码......");

            view = inflater.inflate(R.layout.fragment_password, container, false);

            tv_enter = (TextView) view.findViewById(R.id.password_textview_enter);
            tv_enter.setEnabled(false);
            ll_back = (LinearLayout) view.findViewById(R.id.password_layout__back);
            et_password = (EditText) view.findViewById(R.id.password_editText_password);
            et_password.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0) {
                        bool_password = true;
                    } else {
                        bool_password = false;
                    }

                    if (bool_password && bool_password_again) {
                        tv_enter.setEnabled(true);
                        tv_enter.setBackgroundResource(R.drawable.shape_login_textview_focused);
                    } else {
                        tv_enter.setEnabled(false);
                        tv_enter.setBackgroundResource(R.drawable.shape_login_textview_nomal);
                    }

                }
            });
            et_password_again = (EditText) view.findViewById(R.id.password_editText_password_again);
            et_password_again.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().length() > 0) {
                        bool_password_again = true;
                    } else {
                        bool_password_again = false;
                    }

                    if (bool_password && bool_password_again) {
                        tv_enter.setEnabled(true);
                        tv_enter.setBackgroundResource(R.drawable.shape_login_textview_focused);
                    } else {
                        tv_enter.setEnabled(false);
                        tv_enter.setBackgroundResource(R.drawable.shape_login_textview_nomal);
                    }

                }
            });

            ll_back.setOnClickListener(this);
            tv_enter.setOnClickListener(this);

        }

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (view != null) {
            ViewGroup vg = (ViewGroup) view.getParent();
            vg.removeView(view);



        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.password_layout__back:
                getActivity().finish();
                break;

            case R.id.password_textview_enter:
                changePassword();
                break;
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {

        password = et_password.getText().toString();
        password_again = et_password_again.getText().toString();
        if (!password.equals(password_again)) {
            Toast.makeText(getActivity(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getActivity(), "密码的长度为6~16之间", Toast.LENGTH_SHORT).show();
            return;
        }

        DialogView.show();

        tv_enter.setEnabled(false);
        RequestParams params = new RequestParams();
        params.put("user", ((PhoneRegsterActivity) this.getActivity()).phone);
        params.put("password", password);
        params.put("action", "update_password");
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = new String(responseBody);
                if (str != null) {
                    try {
                        JSONObject object = new JSONObject(str);
                        if (object.getString("code").equals("success")) {
                            Toast.makeText(getActivity(), "密码重置成功", Toast.LENGTH_SHORT).show();
                            DialogView.dismiss();
                            getActivity().finish();
                        } else {
                            Toast.makeText(getActivity(), "密码重置失败，请重试", Toast.LENGTH_SHORT).show();
                            DialogView.dismiss();
                            tv_enter.setEnabled(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        DialogView.dismiss();
                        tv_enter.setEnabled(true);
                    }
                }else{
                    DialogView.dismiss();
                    tv_enter.setEnabled(true);
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                tv_enter.setEnabled(true);
                DialogView.dismiss();
            }
        });
    }




}
