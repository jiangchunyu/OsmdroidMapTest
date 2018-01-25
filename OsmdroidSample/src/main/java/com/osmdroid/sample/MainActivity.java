package com.osmdroid.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.osmdroid.sample.file.FilePathManage;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RxPermissions mRxPermissions ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxPermissions=new RxPermissions(this);
        mRxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {

                        }else {
                            finish();
                        }
                    }
                });
        FilePathManage.GetInstance();
        initView();
    }

    private void initView() {
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button1).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        findViewById(R.id.button3).setOnClickListener(this);
        findViewById(R.id.button4).setOnClickListener(this);
        findViewById(R.id.button5).setOnClickListener(this);
        findViewById(R.id.button6).setOnClickListener(this);
        findViewById(R.id.button7).setOnClickListener(this);
        findViewById(R.id.button8).setOnClickListener(this);
        findViewById(R.id.button9).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                startActivity(new Intent(this,GridFragmentActivity.class));
                break;
            case R.id.button1:
                startActivity(new Intent(this,DirectGridActivity.class));
                break;
            case R.id.button2:
                startActivity(new Intent(this,OffLineMapActivity.class));
                break;
            case R.id.button3:
                startActivity(new Intent(this,PointFragmentActivity.class));
                break;
            case R.id.button4:
                startActivity(new Intent(this,PointActivity.class));
                break;
            case R.id.button5:
                startActivity(new Intent(this,LineActivity.class));
                break;
            case R.id.button6:
                startActivity(new Intent(this,PolygonActivity.class));
                break;
            case R.id.button7:
                startActivity(new Intent(this,BasicMapTestActivity.class));
                break;
            case R.id.button8:
                startActivity(new Intent(this,BasicMapTestActivity2.class));
                break;
            case R.id.button9:
                startActivity(new Intent(this,BasicMapTestActivity3.class));
                break;
        }
    }
}
