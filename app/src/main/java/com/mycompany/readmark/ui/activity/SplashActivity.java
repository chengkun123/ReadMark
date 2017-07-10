package com.mycompany.readmark.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.mycompany.readmark.R;
import com.mycompany.readmark.ui.widget.customloadinganimview.LoadingAnimView;

/**
 * Created by Lenovo.
 */

public class SplashActivity extends AppCompatActivity{

    private LoadingAnimView mLoadingAnimView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mLoadingAnimView = (LoadingAnimView) findViewById(R.id.loading_anim_view);
        mLoadingAnimView.setOnViewAnimEndListener(new LoadingAnimView.OnViewAnimEndListener() {
            @Override
            public void onViewAnimEnd() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
            }
        });
        //mLoadingAnimView.startAnim();
    }
}
