package com.yyquan.jzh.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yyquan.jzh.R;
import com.yyquan.jzh.entity.Ip;
import com.yyquan.jzh.entity.User;
import com.yyquan.jzh.util.Base64Coder;
import com.yyquan.jzh.util.ImageCompressUtils;
import com.yyquan.jzh.util.PhotoSelectedHelper;
import com.yyquan.jzh.view.DialogView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.adapter.MainGridAdapter;
import me.nereo.multi_image_selector.bean.Image;
import uk.co.senab.photoview.PhotoViewAttacher;

public class LuntanToStateActivity extends Activity implements View.OnClickListener, PopupWindow.OnDismissListener, MainGridAdapter.Callback {
    private static final int REQUEST_IMAGE = 2;
    public static final int RESULT_OK = -1;

    LinearLayout ll_back;
    LinearLayout ll_enter;
    RelativeLayout rl_location;
    boolean cb_bool = false;
    EditText et_content;
    PhotoViewAttacher mAttacher;


    GridView mGridView;
    MainGridAdapter mainGridAdapter;
    public ArrayList<String> mSelectPath = new ArrayList<>();
    public boolean isYuantu;
    PopupWindow popupWindow;
    ImageView popImageView;

    String str_content;
    String location;
    PhotoSelectedHelper mPhotoSelectedHelper;
    Intent intent;
    public User user;
    TextView tv_location;

    String url = Ip.ip + "/YfriendService/DoGetLunTan?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_luntan_to_state);
        mPhotoSelectedHelper = new PhotoSelectedHelper(this);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        initialView();
        initialPopups();
        initialDialog();
    }

    /**
     * 初始化状态框
     */
    private void initialDialog() {


        DialogView.Initial(this, "正在发表......");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initialView() {
        ll_back = (LinearLayout) findViewById(R.id.luntan_state_layout_back);
        ll_enter = (LinearLayout) findViewById(R.id.luntan_state_layout_enter);
        rl_location = (RelativeLayout) findViewById(R.id.luntan_state_layout_location);
        tv_location = (TextView) findViewById(R.id.luntan_state_textview_location);

        et_content = (EditText) findViewById(R.id.luntan_state_edittext_content);
        mGridView = (GridView) findViewById(R.id.luntan_state_gridview);
        mainGridAdapter = new MainGridAdapter(LuntanToStateActivity.this, this, 9);
        mGridView.setAdapter(mainGridAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = mGridView.getWidth();
                final int height = mGridView.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(me.nereo.multi_image_selector.R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(me.nereo.multi_image_selector.R.dimen.space_size);
                int columnWidth = (width - columnSpace * (numCount - 1)) / numCount;
                mainGridAdapter.setItemSize(columnWidth);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mSelectPath.size()) {
                    Intent intent = new Intent(LuntanToStateActivity.this, MultiImageSelectorActivity.class);
                    // 是否显示拍摄图片
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    // 最大可选择图片数量
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                    // 选择模式
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 1);
                    // 默认选择
                    if (mSelectPath != null && mSelectPath.size() > 0) {
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                    }
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    Picasso.with(LuntanToStateActivity.this).load(new File(mSelectPath.get(position))).into(popImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mAttacher = new PhotoViewAttacher(popImageView);
                            popImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (popupWindow != null && popupWindow.isShowing()) {
                                        popupWindow.dismiss();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });
                    popupWindow.showAtLocation(LayoutInflater.from(LuntanToStateActivity.this).inflate(R.layout.activity_luntan_to_state, null)
                            , Gravity.CENTER, 0, 0);
                }
            }
        });
        ll_back.setOnClickListener(this);
        ll_enter.setOnClickListener(this);
        rl_location.setOnClickListener(this);

    }

    /**
     * 初始化popupwindow
     */
    private void initialPopups() {
        popImageView = new ImageView(this);
        // popImageView.setPadding(50, 50, 50, 50);
        popupWindow = new PopupWindow(popImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.luntan_state_layout_back:
                finish();
                break;
            case R.id.luntan_state_layout_enter:
                upData();
                break;
            case R.id.luntan_state_layout_location:
                Intent it = new Intent(this, LocationActivity.class);
                startActivityForResult(it, 99);

                break;
        }

    }

    private void upData() {

        str_content = et_content.getText().toString();
        if (str_content.length() < 5) {
            Toast.makeText(LuntanToStateActivity.this, "内容长度必须大于5", Toast.LENGTH_SHORT).show();
            return;

        }
        Message m = h.obtainMessage(1);
        h.sendMessage(m);

    }

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                DialogView.show();
                ll_enter.setEnabled(false);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());

                RequestParams params = new RequestParams();
                params.put("action", "save");
                params.put("user", user.getUser());
                params.put("time", time);
                params.put("content", str_content);

                if (cb_bool) {
                    params.put("location", location);
                } else {
                    params.put("location", "");
                }
                if (mSelectPath != null && mSelectPath.size() > 0) {
                    params.put("image_size", mSelectPath.size());
                    for (int i = 0; i < mSelectPath.size(); i++) {

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        ImageCompressUtils.getimage(mSelectPath.get(i)).compress(Bitmap.CompressFormat.JPEG,
                                80, stream);
                        byte[] b = stream.toByteArray();
                        // 将图片流以字符串形式存储下来
                        String file = new String(Base64Coder.encodeLines(b));
                        params.put("file" + i, file);
                        String filename = user.getUser() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
                        params.put("filename" + i, i + filename);
                    }
                } else {
                    params.put("image_size", "0");
                }


                // Toast.makeText(LuntanToStateActivity.this, params.toString(), Toast.LENGTH_SHORT).show();


                AsyncHttpClient client = new AsyncHttpClient();
                client.post(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        ll_enter.setEnabled(true);
                        DialogView.dismiss();
                        String str = new String(responseBody);
                        if (str != null) {
                            try {
                                JSONObject object = new JSONObject(str);
                                if (object.getString("code").equals("success")) {

                                    Toast.makeText(LuntanToStateActivity.this, "发表成功", Toast.LENGTH_SHORT).show();
                                    Intent intents = new Intent(LuntanToStateActivity.this, MainActivity.class);
                                    setResult(200, intents);
                                    finish();

                                    //更新评论内容

                                } else {
                                    Toast.makeText(LuntanToStateActivity.this, "发表失败，请重试", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                ll_enter.setEnabled(true);
                                DialogView.dismiss();
                                e.printStackTrace();
                            }
                        }
                    }


                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        ll_enter.setEnabled(true);
                        DialogView.dismiss();
                        Toast.makeText(LuntanToStateActivity.this, "网络连接失败，请查看网络设置", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 99) {
            if (data == null) {
                return;
            } else {
                location = data.getStringExtra("location");
                if (location.equals("地点")) {
                    cb_bool = false;
                    tv_location.setText(location);
                } else {
                    cb_bool = true;
                    tv_location.setText(location);
                }

            }
        }

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                isYuantu = data.getBooleanExtra("YUANTU", false);
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                mainGridAdapter.setData(toImages(mSelectPath));
            }
        }


    }


    @Override
    public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }

    private List<Image> toImages(ArrayList<String> mmSelectPath) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < mmSelectPath.size(); i++) {
            Image image = new Image();
            image.path = mmSelectPath.get(i);
            images.add(image);
        }
        return images;
    }

    public void dataDelete(String str) {
        if (str == null) {
            return;
        } else {
            if (mSelectPath.contains(str)) {
                mSelectPath.remove(str);
                mainGridAdapter.setData(toImages(mSelectPath));
            }
        }
    }

    @Override
    public void callbackDelete(String str) {
        dataDelete(str);
    }

    @Override
    public void onBackPressed() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            finish();
        }
    }

}
