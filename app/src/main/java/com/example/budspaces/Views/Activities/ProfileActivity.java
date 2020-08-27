package com.example.budspaces.Views.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.bumptech.glide.Glide;
import com.example.budspaces.Adapters.ProfileChipsAdapter;
import com.example.budspaces.Adapters.ProfileGroupsAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Models.User;
import com.example.budspaces.R;
import com.example.budspaces.ViewModels.Main.ProfileViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.groups)
    RecyclerView groups;
    @BindView(R.id.fullName)
    TextView fullName;
    @BindView(R.id.genre)
    TextView genre;
    @BindView(R.id.birthdate)
    TextView birthdate;
    @BindView(R.id.country)
    TextView country;
    @BindView(R.id.motionLayout)
    MotionLayout motionLayout;
    @BindView(R.id.chips)
    RecyclerView chips;

    private ProfileViewModel mViewModel;
    private Unbinder unbinder;
    private boolean start = true;
    private boolean mainUser = false;
    private User user;
    private String userID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_fragment);
        unbinder = ButterKnife.bind(this);
        makeStatusBarWhite(this);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        userID  = getIntent().getStringExtra("userId");

        mViewModel.loadProfile(userID);
        if (!mainUser) {
            mViewModel.loadMemberGroups(userID);
        } else {
            mViewModel.loadHostGroups(userID);
        }
        mViewModel.getUser().observe(ProfileActivity.this, user -> {
            if (user != null) {
                this.user = user;
                initUser();
            }
        });

        mViewModel.getGroups().observe(ProfileActivity.this, groupList -> {
            if (groupList != null && groupList.size() > 0) {
                groups.setAdapter(new ProfileGroupsAdapter(groupList, mainUser));
            }
        });

        mViewModel.getErrorMessage().observe(ProfileActivity.this, errorMsg -> {
            Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
        });

        mViewModel.getIsLoggedout().observe(ProfileActivity.this, logout -> {
            if (logout) {
                Cache.deleteCache(ProfileActivity.this);
                startActivity(new Intent(ProfileActivity.this, SignInActivity.class));
                ProfileActivity.this.finish();
            }
        });
        initGroups();
        initChips();

    }
    private void initUser()
    {
        Glide.with(this).load(user.getPicture()).error(R.drawable.com_facebook_profile_picture_blank_square).into(picture);
        name.setText(user.getName().split(" ")[0]);

        if (user.getCity() != null && !user.getCity().isEmpty())
            location.setText(user.getCity());
        else
            location.setText(user.getCountry());

        fullName.setText(user.getName());
        genre.setText(user.getGender());
        birthdate.setText(user.getBirthdate());

        country.setText(user.getCountry());
        chips.setAdapter(new ProfileChipsAdapter(user.getInterests()));
    }

    private void initGroups() {
        LinearLayoutManager groupsManager = new LinearLayoutManager(ProfileActivity.this, RecyclerView.HORIZONTAL, false);
        groups.setLayoutManager(groupsManager);
        groups.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
                outRect.top = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.appbar_padding_top);
            }
        });

        groups.setAdapter(new ProfileGroupsAdapter(new ArrayList<>(), mainUser));
    }
    private void initChips() {
        ChipsLayoutManager chipsLayoutManager = ChipsLayoutManager.newBuilder(ProfileActivity.this)
                .setChildGravity(Gravity.TOP)
                .setScrollingEnabled(true)
                .setGravityResolver(position -> Gravity.CENTER)
                .setOrientation(ChipsLayoutManager.HORIZONTAL)
                .setRowStrategy(ChipsLayoutManager.STRATEGY_FILL_SPACE)
                .build();
        chips.setLayoutManager(chipsLayoutManager);
        chips.setNestedScrollingEnabled(false);
    }
    @OnClick(R.id.expandedArrow)
    public void onViewClicked() {
        if (start)
            motionLayout.transitionToEnd();
        else
            motionLayout.transitionToStart();
        start = !start;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}