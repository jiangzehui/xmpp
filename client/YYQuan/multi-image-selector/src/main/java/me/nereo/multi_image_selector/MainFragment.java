package me.nereo.multi_image_selector;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.adapter.MainGridAdapter;
import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by 晗 on 2015/5/4.
 */
public class MainFragment extends Fragment implements MainGridAdapter.Callback {
    private static final int REQUEST_IMAGE = 2;
    public static final int RESULT_OK = -1;

    GridView mGridView;
    MainGridAdapter mainGridAdapter;
    public ArrayList<String> mSelectPath = new ArrayList<>();
    public boolean isYuantu;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGridView = (GridView) view.findViewById(R.id.fragment_main_gridview);
        mainGridAdapter = new MainGridAdapter(getActivity(), this,3);
        mGridView.setAdapter(mainGridAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = mGridView.getWidth();
                final int height = mGridView.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
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
                    Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
                    // 是否显示拍摄图片
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    // 最大可选择图片数量
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 3);
                    // 选择模式
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 1);
                    // 默认选择
                    if (mSelectPath != null && mSelectPath.size() > 0) {
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                    }
                    startActivityForResult(intent, REQUEST_IMAGE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                isYuantu=data.getBooleanExtra("YUANTU",false);
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                mainGridAdapter.setData(toImages(mSelectPath));
            }
        }
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
}
