package com.z.multiimage;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1001;
    private RecyclerView mRvRecyclerView;
    private List<String> datas;
    private MyAdapter adapter;
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRvRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRvRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 4));
        datas = new ArrayList<>();
        datas.add(null);
        adapter = new MyAdapter(getBaseContext(), R.layout.image, datas);
        mRvRecyclerView.setAdapter(adapter);
        activity = this;

    }

    class MyAdapter extends CommonAdapter<String> {

        public MyAdapter(Context context, int layoutId, List<String> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final String s, final int position) {
            ImageView image = holder.getView(R.id.iv);
            Glide.with(getBaseContext()).load(s).error(R.mipmap.ic_launcher).into(image);

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getBaseContext(), s + position, Toast.LENGTH_SHORT).show();
                    if (s == null) {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED
                                && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {//申请权限
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                            Manifest.permission.CAMERA}, 300);
                        } else {
                            MultiImageSelector.create()
                                    .count(8 - datas.size() + 1)
                                    .start(activity, REQUEST_IMAGE);
                        }
                    } else {
                        datas.remove(s);
                        if (datas.get(datas.size() - 1) != null)//删除之后如果最后一条不是空的那就再加一条空的,
                            datas.add(null);
                        adapter.notifyDataSetChanged();
                    }

                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (datas.get(datas.size() - 1) == null) {//添加前去空
                    datas.remove(datas.size() - 1);
                }
                datas.addAll(data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT));
                if (datas.size() < 8) {//添加后加空
                    datas.add(null);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

}

