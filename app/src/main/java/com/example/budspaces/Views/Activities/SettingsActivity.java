package com.example.budspaces.Views.Activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.budspaces.Adapters.SettingsAdapter;
import com.example.budspaces.Listeners.OnSettingChecked;
import com.example.budspaces.Models.Setting;
import com.example.budspaces.R;
import com.example.budspaces.Utils.SimpleDividerItemDecoration;
import com.example.budspaces.ViewModels.Main.MessagesViewModel;
import com.example.budspaces.ViewModels.SettingViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import carbon.widget.RecyclerView;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.notifications)
    RecyclerView notifications;
    @BindView(R.id.confidentialities)
    RecyclerView confidentialities;
    @BindView(R.id.previous)
    ImageView previous;
    @BindView(R.id.page_title)
    TextView pageTitle;

    private Unbinder unbinder;
    private SettingViewModel mViewModel;

    private SettingsAdapter notifAdapter, profilAdapter;
    private String[] pref_notif, pref_profile;
    private Setting setting;

    private OnSettingChecked onNotificationSettingsChecked = new OnSettingChecked() {
        @Override
        public void isChecked(int pos, boolean value) {
            switch (pos) {
                case 0:
                    setting.setNotification(value);
                    break;
                case 1:
                    setting.setEventModified(value);
                    break;
                case 2:
                    setting.setMessage(value);
                    break;
                case 3:
                    setting.setEventReminder(value);
                    break;
                case 4:
                    setting.setGroupNotifications(value);
                    break;
            }
        }
    };

    private OnSettingChecked onProfileSettingsChecked = new OnSettingChecked() {
        @Override
        public void isChecked(int pos, boolean value) {
            switch (pos) {
                case 0:
                    setting.setShowGroups(value);
                    break;
                case 1:
                    setting.setShowInterests(value);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        unbinder = ButterKnife.bind(this);
        makeStatusBarWhite(this);
        mViewModel = new ViewModelProvider(this).get(SettingViewModel.class);

        pref_notif = getResources().getStringArray(R.array.pref_notif);
        pref_profile = getResources().getStringArray(R.array.pref_profile);

        notifAdapter = new SettingsAdapter(onNotificationSettingsChecked);
        profilAdapter = new SettingsAdapter(onProfileSettingsChecked);

        initRecycler(notifications, notifAdapter);
        initRecycler(confidentialities, profilAdapter);

        pageTitle.setText(getText(R.string.app_settings));
        previous.setVisibility(View.VISIBLE);

        mViewModel.loadSettings();
        mViewModel.getErrorMessage().observe(this, errMsg -> {
            Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
        });

        mViewModel.getSettings().observe(this, setting -> {
            if (setting != null) {
                this.setting = setting;

                notifAdapter.add(pref_notif[0], setting.getNotification());
                notifAdapter.add(pref_notif[1], setting.getEventModified());
                notifAdapter.add(pref_notif[2], setting.getMessage());
                notifAdapter.add(pref_notif[3], setting.getEventReminder());
                notifAdapter.add(pref_notif[4], setting.getGroupNotifications());

                profilAdapter.add(pref_profile[0], setting.getShowGroups());
                profilAdapter.add(pref_profile[1], setting.getShowInterests());
            }
        });
    }

    private void initRecycler(RecyclerView recyclerView, SettingsAdapter adapter) {
        LinearLayoutManager wallManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(wallManager);
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, androidx.recyclerview.widget.RecyclerView parent, androidx.recyclerview.widget.RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                outRect.top = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
//                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
                outRect.left = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
            }
        });

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(Objects.requireNonNull(this), R.drawable.divider_settings, getResources().getDimension(R.dimen.activity_vertical_margin)));
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.previous)
    public void onViewClicked() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        mViewModel.saveSettings(setting);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}