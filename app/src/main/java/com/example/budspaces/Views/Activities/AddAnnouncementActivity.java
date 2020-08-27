package com.example.budspaces.Views.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.budspaces.R;
import com.example.budspaces.ViewModels.AddAnnouncementViewModel;
import com.example.budspaces.ViewModels.Main.MessagesViewModel;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;
import static com.example.budspaces.Utils.LoginChecker.validateEmail;
import static com.example.budspaces.Utils.LoginChecker.validateName;
import static com.example.budspaces.Utils.LoginChecker.validatePassword;

public class AddAnnouncementActivity extends AppCompatActivity {
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.previous)
    ImageView previous;
    @BindView(R.id.name)
    TextInputLayout name;
    @BindView(R.id.content)
    TextInputLayout content;

    private AddAnnouncementViewModel mViewModel;
    private Unbinder unbinder;
    private String groupId, groupName, groupPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_announcement);
        unbinder = ButterKnife.bind(this);
        makeStatusBarWhite(this);
        mViewModel = new ViewModelProvider(this).get(AddAnnouncementViewModel.class);

        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        groupPicture = getIntent().getStringExtra("groupPicture");

        pageTitle.setText(getText(R.string.add_announcement));
        previous.setVisibility(View.VISIBLE);

        mViewModel.getErrorMessage().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        });

        mViewModel.getIsAdded().observe(this, isAdded -> {
            if (isAdded) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("title", Objects.requireNonNull(name.getEditText()).getText().toString());
                returnIntent.putExtra("content", Objects.requireNonNull(content.getEditText()).getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.previous, R.id.validate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.previous:
                onBackPressed();
                break;
            case R.id.validate:
                if (validateName(this, name) && validateName(this, content)) {
                    Objects.requireNonNull(name.getEditText()).setEnabled(false);
                    Objects.requireNonNull(content.getEditText()).setEnabled(false);

                    mViewModel.addAnnouncement(Objects.requireNonNull(name.getEditText()).getText().toString(),
                            Objects.requireNonNull(content.getEditText()).getText().toString(),
                            groupId, groupName, groupPicture);
                }
                break;
        }
    }
}