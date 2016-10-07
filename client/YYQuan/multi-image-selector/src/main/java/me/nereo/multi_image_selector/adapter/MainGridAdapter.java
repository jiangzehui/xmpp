package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by 晗 on 2015/5/4.
 */
public class MainGridAdapter extends BaseAdapter {
    private List<Image> mImages = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private Callback mCallback;
    private int mItemSize;
    private GridView.LayoutParams mItemLayoutParams;
    private int number;

    public MainGridAdapter(Context context, Callback callback,int n) {
        mCallback = callback;
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItemLayoutParams = new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT);
        number=n;

    }

    public void setData(List<Image> images) {

        if (images != null && images.size() > 0) {
            mImages = images;
        } else {
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 重置每个Column的Size
     *
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if (mItemSize == columnWidth) {
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new GridView.LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mImages.size() == number) {
            return mImages.size();
        }
        return mImages.size() + 1;
    }

    @Override
    public Image getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (mImages.size() != number && position >= mImages.size()) {
            convertView = mInflater.inflate(R.layout.grid_item_add, parent, false);
            convertView.setTag(null);
        } else {
            Viewholder viewholder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.grid_item_image, parent, false);
                viewholder = new Viewholder(convertView);
            } else {
                viewholder = (Viewholder) convertView.getTag();
                if (viewholder == null) {
                    convertView = mInflater.inflate(R.layout.grid_item_image, parent, false);
                    viewholder = new Viewholder(convertView);
                }
            }
            if (viewholder != null) {
                viewholder.bindData(getItem(position));
            }
        }
        GridView.LayoutParams lp = (GridView.LayoutParams) convertView.getLayoutParams();
        if (lp.height != mItemSize) {
            convertView.setLayoutParams(mItemLayoutParams);
        }

        return convertView;


    }


    class Viewholder {
        ImageView image;
        ImageView indicator;

        Viewholder(View view) {
            image = (ImageView) view.findViewById(R.id.grid_item_image);
            indicator = (ImageView) view.findViewById(R.id.grid_item_delete);
            view.setTag(this);
        }

        void bindData(final Image data) {
            indicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.callbackDelete(data.path);
                }
            });
            if (data == null) {
                return;
            }
            File imageFile = new File(data.path);

            Picasso.with(mContext)
                    .load(imageFile)
                    .placeholder(R.drawable.default_error)
                            //.error(R.drawable.default_error)
                    .resize(mItemSize, mItemSize)
                    .centerCrop()
                    .into(image);


        }
    }

    public interface Callback {
        public void callbackDelete(String str);
    }
}
