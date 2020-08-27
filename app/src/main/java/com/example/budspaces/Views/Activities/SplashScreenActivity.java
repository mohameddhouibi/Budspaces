package com.example.budspaces.Views.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.Cache;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.R;
import com.example.budspaces.Samples.CategoriesSamples;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Auth.SplashScreenViewModel;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.wang.avi.AVLoadingIndicatorView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarTransparent;

public class SplashScreenActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    private boolean isClickable = false;
    private Disposable internetDisposable;
    private SplashScreenViewModel viewModel;

    @BindView(R.id.avi)
    AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        makeStatusBarTransparent(this);

        Utils.setGso(this);
    }

    @OnClick({R.id.discover, R.id.signup, R.id.signin})
    public void onViewClicked(View view) {
        if (!isClickable)
            return;

        switch (view.getId()) {
            case R.id.discover:
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                intent.putExtra("discover", true);
                startActivity(intent);
                break;
            case R.id.signup:
                startActivity(new Intent(SplashScreenActivity.this, SignUpActivity.class));
//                finish();
                break;
            case R.id.signin:
                startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
//                finish();
                break;
        }
    }

    @Override protected void onStart() {
        super.onStart();

        viewModel = new ViewModelProvider(this).get(SplashScreenViewModel.class);
        Utils.verifyStoragePermissions(this);

        CategoriesSamples.getInstance();
        Cache.getInstance(this);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MotionLayout) findViewById(R.id.motionLayout)).transitionToEnd();
                isClickable = true;
                handler.removeCallbacks(this);
                avi.hide();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override protected void onResume() {
        super.onResume();

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(isConnected -> {
                AppRetrofit.setIsConnected(isConnected);
                viewModel.checkIfLoggedIn();
            });

        viewModel.getIsLoggedIn().observe(this, isLoggedIn -> {
            if (isLoggedIn) {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                intent.putExtra("verified", viewModel.isVerified());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override protected void onPause() {
        super.onPause();
        Utils.safelyDispose(internetDisposable);
    }
}