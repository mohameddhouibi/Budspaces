package com.example.budspaces.Views.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.Cache;
import com.example.budspaces.Enums.NavBarType;
import com.example.budspaces.Navigation.Core.BaseKey;
import com.example.budspaces.Navigation.Core.FragmentStateChanger;
import com.example.budspaces.Navigation.Keys.DiscoverFilterKey;
import com.example.budspaces.Navigation.Keys.DiscoverKey;
import com.example.budspaces.Navigation.Keys.DiscussionKey;
import com.example.budspaces.Navigation.Keys.HomeKey;
import com.example.budspaces.Navigation.Keys.ProfileKey;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.R;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.MainViewModel;
import com.example.budspaces.Views.Dialog.AccountConfirmationDialogFragment;
import com.example.budspaces.Views.Dialog.UserChatProfileDialogFragment;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.StateChanger;
import com.zhuinden.simplestack.navigator.Navigator;

import javax.annotation.Nonnull;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;

public class MainActivity extends AppCompatActivity implements StateChanger, DialogInterface.OnDismissListener {
    @BindView(R.id.pager)
    FrameLayout pager;
    @BindView(R.id.bnve)
    BottomNavigationView bnve;

    private Backstack backstack;
    private FragmentStateChanger fragmentStateChanger;
    private Disposable internetDisposable;
    private MainViewModel mViewModel;

    private boolean discover = false;
    private boolean verified = true; // TODO: false

    private AccountConfirmationDialogFragment dialogFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item .getItemId()) {
                case R.id.action_home:
                    backstack.setHistory(History.of(HomeKey.create()), StateChange.REPLACE);
                    break;
                case R.id.action_discover:
                    backstack.setHistory(History.of(DiscoverKey.create()), StateChange.REPLACE);
                    break;
                case R.id.action_chat:
                    backstack.setHistory(History.of(DiscussionKey.create()), StateChange.REPLACE);
                    break;
                case R.id.action_profile:
                    backstack.setHistory(History.of(ProfileKey.create("")), StateChange.REPLACE);
                    break;
            }

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        makeStatusBarWhite(this);
        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Handler handler = new Handler();

        dialogFragment =
                AccountConfirmationDialogFragment.newInstance(8, 3.5f, true, false, mViewModel);

//        if (getIntent().hasExtra("verified"))
//            verified = getIntent().getBooleanExtra("verified", false);

        mViewModel.getErrorMessage().observe(this, msgErr -> {
            Toast.makeText(this, msgErr, Toast.LENGTH_SHORT).show();
        });

        mViewModel.getIsMailSent().observe(this, isMailSent -> {
            if (isMailSent) {
                Toast.makeText(this, getText(R.string.verify_mail_sent), Toast.LENGTH_SHORT).show();
            }
        });

        mViewModel.getIsVerified().observe(this, isVerified -> {
            if (!isVerified && !verified) {
                showSnackBar();
            } else if (isVerified) {
                verified = true;

                bnve.getMenu().findItem(R.id.action_home).setEnabled(true);
                bnve.getMenu().findItem(R.id.action_chat).setEnabled(true);

                dialogFragment.dismiss();
            }
        });
    }

    private void showPinDialog() {
        dialogFragment.show(getFragmentManager(), "dialog");
    }

    private void showSnackBar() {
        Snackbar.make(findViewById(R.id.root), R.string.verify_mail, Snackbar.LENGTH_INDEFINITE)
            .setAnchorView(bnve)
            .setAction(R.string.verify, v -> showPinDialog())
            .show();
    }

    @Override
    public void handleStateChange(@NonNull StateChange stateChange, @NonNull Callback completionCallback) {
        if (stateChange.isTopNewKeyEqualToPrevious()) {
            completionCallback.stateChangeComplete();
            return;
        }
        fragmentStateChanger.handleStateChange(stateChange);
        completionCallback.stateChangeComplete();
    }

    @Override
    public void onBackPressed() {
        if(!Navigator.onBackPressed(this)) {
            if (backstack.getHistory().size() == 1
                    && !backstack.getHistory().equals(History.of(HomeKey.create()))) {

                bnve.setSelectedItemId(R.id.action_home);
            } else {
                super.onBackPressed();
            }
        }
    }

    public void goTo(NavBarType type) {
        switch (type) {
            case home:
                bnve.setSelectedItemId(R.id.action_home);
                break;
            case discover:
                bnve.setSelectedItemId(R.id.action_discover);
                break;
            case discussion:
                bnve.setSelectedItemId(R.id.action_chat);
                break;
            case profile:
                bnve.setSelectedItemId(R.id.action_profile);
                break;
        }
    }

    @Override protected void onStart() {
        super.onStart();

        // Initialize the SDK
        Places.initialize(this, getString(R.string.places_api));
        fragmentStateChanger = new FragmentStateChanger(getSupportFragmentManager(), R.id.pager);

        bnve.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (getIntent().hasExtra("discover") && getIntent().getBooleanExtra("discover", false)) {
            discover = true;
            setBackstack(DiscoverKey.create());

            bnve.getMenu().findItem(R.id.action_home).setEnabled(false);
            bnve.getMenu().findItem(R.id.action_chat).setEnabled(false);
            bnve.getMenu().findItem(R.id.action_profile).setEnabled(false);
            bnve.setSelectedItemId(R.id.action_discover);
        } else if (!verified) {
            setBackstack(DiscoverKey.create());

            bnve.getMenu().findItem(R.id.action_home).setEnabled(false);
            bnve.getMenu().findItem(R.id.action_chat).setEnabled(false);
            bnve.setSelectedItemId(R.id.action_discover);
        } else {
            setBackstack(HomeKey.create());
        }
    }

    public void setBackstack(BaseKey key) {
        Navigator.configure()
            .setStateChanger(this)
            .install(this, pager, History.single(key));

        backstack = Navigator.getBackstack(this);
    }

    @Override protected void onResume() {
        super.onResume();

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(AppRetrofit::setIsConnected);
    }

    @Override protected void onPause() {
        super.onPause();
        Utils.safelyDispose(internetDisposable);
    }

    public boolean isDiscover() {
        return discover;
    }
    public boolean isVerified() {
        return verified;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (!verified)
            showSnackBar();
    }
}