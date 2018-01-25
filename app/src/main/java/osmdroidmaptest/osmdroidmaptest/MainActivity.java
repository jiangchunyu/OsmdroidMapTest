package osmdroidmaptest.osmdroidmaptest;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity  {


    private RxPermissions mRxPermissions;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxPermissions = new RxPermissions(this);
        initView();
    }

    private void initView() {
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
    }


 public  void toMapActvity(View v ){
     Intent intent = new Intent(this,MapActivity.class);
     startActivity(intent);
     finish();
 }


    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}