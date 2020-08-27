package com.example.budspaces.Views.Fragments.Groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.budspaces.Adapters.GroupEventAdapter;
import com.example.budspaces.Adapters.GroupMembersAdapter;
import com.example.budspaces.Adapters.PopupAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Enums.PopupActions;
import com.example.budspaces.Enums.WarnType;
import com.example.budspaces.Factory.GroupFactory;
import com.example.budspaces.Listeners.OnPopupActionSelectedListener;
import com.example.budspaces.Models.Group;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.CreateModifEventKey;
import com.example.budspaces.Navigation.Keys.CreateModifGroupKey;
import com.example.budspaces.Navigation.Keys.EventListKey;
import com.example.budspaces.R;
import com.example.budspaces.Samples.SearchSamples;
import com.example.budspaces.Utils.SimpleDividerItemDecoration;
import com.example.budspaces.Utils.Tuple3;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.GroupViewModel;
import com.example.budspaces.Views.Activities.MainActivity;
import com.example.budspaces.Views.Activities.MessagesActivity;
import com.example.budspaces.Views.Activities.WarnActivity;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.navigator.Navigator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class GroupFragment extends BaseFragment {
    private static final int DELETE_REQUEST = 3215;
    private static final int LEAVE_REQUEST = 3125;

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.interests)
    TextView interests;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.members)
    RecyclerView members;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.events)
    RecyclerView events;
    @BindView(R.id.activities)
    RecyclerView activities;
    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.txt)
    TextView txt;
    @BindView(R.id.icon)
    ImageView icon;
    @BindView(R.id.settings)
    ImageView settings;

    private static History history;
    @BindView(R.id.createEvent)
    FloatingActionButton createEvent;
    @BindView(R.id.action)
    carbon.widget.ConstraintLayout action;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.shimmerContainer)
    ShimmerFrameLayout shimmerContainer;

    private boolean joined = false;
    private boolean isHost = false;
    private boolean discover = false;
    private boolean verified = false;

    private String groupID;
    private String userId;

    private GroupMembersAdapter membersAdapter;
    private Member user;

    private Unbinder unbinder;
    private Group group;
    private GroupViewModel mViewModel;
    private PopupWindow popupWindow;
    private PopupAdapter adapter;

    private List<Tuple3<String, Drawable, PopupActions>> tupleList = new ArrayList<>();
    private int[] buttonPos = new int[2];

    private OnPopupActionSelectedListener actionSelectedListener = new OnPopupActionSelectedListener() {
        @Override
        public void actionSelected(PopupActions action) {
            popupWindow.dismiss();
            switch (action) {
                case modify:
                    Navigator.getBackstack(requireContext()).goTo(CreateModifGroupKey.create(InterestType.group_modify, group.getId()));
                    break;
                case delete:
                    setWarning(WarnType.delete, DELETE_REQUEST);
                    break;
                case leave:
                    setWarning(WarnType.leave, LEAVE_REQUEST);
                    break;
                case join:
                    mViewModel.joinGroup();
                    break;
            }

            initTuples(group);
        }
    };

    public GroupFragment(String groupID) {
        this.groupID = groupID;
    }

    public static History getHistory() {
        return history;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        history = Navigator.getBackstack(requireContext()).getHistory();

        discover = ((MainActivity) requireActivity()).isDiscover();
        verified = ((MainActivity) requireActivity()).isVerified();

        if (discover) {
            action.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        } else {
            setPopUpWindow();
            settings.getLocationOnScreen(buttonPos);
        }

        return view;
    }

    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.popup_menu, null);

        RecyclerView menuItems = view.findViewById(R.id.menu);
        popupWindow = new PopupWindow(view, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT, true);

        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        menuItems.setItemAnimator(new DefaultItemAnimator());
        menuItems.setLayoutManager(wallManager);
        menuItems.addItemDecoration(new SimpleDividerItemDecoration(requireContext(), R.drawable.popup_devider, getResources().getDimension(R.dimen.activity_vertical_margin)));
        adapter = new PopupAdapter(new ArrayList<>(), actionSelectedListener);
        adapter.showHideLast(false);
        menuItems.setAdapter(adapter);
    }

    private void initTuples(Group group) {
        adapter.removeAll();
        tupleList.clear();

        if (isHost) {
            tupleList.add(new Tuple3<>(getString(R.string.modify_group), getResources().getDrawable(R.drawable.ic_settings), PopupActions.modify));
            tupleList.add(new Tuple3<>(getString(R.string.delete_group), getResources().getDrawable(R.drawable.ic_trash), PopupActions.delete));
        } else if (group.getMembers().contains(userId))
            tupleList.add(new Tuple3<>(getString(R.string.leave), getResources().getDrawable(R.drawable.ic_logout), PopupActions.leave));
        else
            tupleList.add(new Tuple3<>(getString(R.string.join), getResources().getDrawable(R.drawable.ic_add_attachment), PopupActions.join));

        adapter.addAll(tupleList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this, new GroupFactory(groupID)).get(GroupViewModel.class);

        mViewModel.loadGroup();
        mViewModel.loadMyMember();
        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null)
                this.user = user;
        });

        mViewModel.getGroup().observe(getViewLifecycleOwner(), group -> {
            if (group != null) {
                initGroup(group);
                shimmerContainer.stopShimmer();
                shimmerContainer.hideShimmer();

                if (!discover)
                    initTuples(group);

                mViewModel.loadMembers(7);
            }
        });

        mViewModel.getMembers().observe(getViewLifecycleOwner(), membersLst -> {
            if (membersLst.size() > 0) {
                initMembers(membersLst);
            }
        });

        mViewModel.getIsJoined().observe(getViewLifecycleOwner(), isJoined -> {
            if (isJoined) {
                leaveOrJoinGroup(View.VISIBLE, R.string.discussion, true);
                membersAdapter.add(user);
            }
        });

        mViewModel.getIsLeft().observe(getViewLifecycleOwner(), isLeft -> {
            if (isLeft) {
                leaveOrJoinGroup(View.GONE, R.string.join, false);
                membersAdapter.remove(user);
            }
        });

        mViewModel.getIsDeleted().observe(getViewLifecycleOwner(), isDeleted -> {
            if (isDeleted)
                Navigator.getBackstack(requireContext()).goBack();
        });

        mViewModel.loadEvents(groupID);
        mViewModel.getEvents().observe(getViewLifecycleOwner(), groupList -> {
            if (groupList != null && groupList.size() > 0) {
                events.setAdapter(new GroupEventAdapter(groupList));
            }
        });

        initEvents();
        initActivities();
    }

    private void leaveOrJoinGroup(int gone, int p, boolean b) {
        icon.setVisibility(gone);
        txt.setText(requireContext().getString(p));
        joined = b;

        initTuples(group);
    }

    private void initActivities() {
        LinearLayoutManager wallManager = new LinearLayoutManager(getContext());
        activities.setLayoutManager(wallManager);
        activities.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
            }
        });

        mViewModel.setRecyclerView(activities, wallManager);
    }

    private void initEvents() {
        LinearLayoutManager groupsManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        events.setLayoutManager(groupsManager);
        events.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.activity_horizontal_margin);
            }
        });

        events.setAdapter(new GroupEventAdapter(SearchSamples.getInstance().getEvents()));
    }

    private void initMembers(List<Member> memberList) {
        LinearLayoutManager groupsManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        members.setLayoutManager(groupsManager);
        members.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.low_gap);
            }
        });

        membersAdapter = new GroupMembersAdapter(memberList, group.getMembers().size(), 6, group.getName(), groupID);
        members.setAdapter(membersAdapter);
    }

    private void initGroup(Group group) {
        this.group = group;
        Glide.with(this).load(group.getPicture()).dontAnimate().into(picture);
        name.setText(group.getName());
        interests.setText(TextUtils.join(", ", group.getInterests()));
        location.setText(group.getAddress());
        description.setText(group.getDescription());
        userId = Cache.getInstance().getSession("userId");

        if (group.getMembers().contains(userId)) {
            leaveOrJoinGroup(View.VISIBLE, R.string.discussion, true);
        }

        if (group.getHost().equals(userId)) {
            isHost = true;
            createEvent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.action, R.id.createEvent, R.id.moreEvents, R.id.settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action:
                if (!joined) {
                    mViewModel.joinGroup();
                    return;
                }

                if (verified) {
                    Intent intent = new Intent(requireContext(), MessagesActivity.class);
                    intent.putExtra("roomID", group.getId());
                    intent.putExtra("roomName", group.getName());

                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), getText(R.string.verify_account_messages), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.createEvent:
                if (isHost) {
                    Navigator.getBackstack(requireContext()).goTo(CreateModifEventKey.create(InterestType.event_new, group.getId(), group.getName()));
                } else
                    Toast.makeText(requireContext(), "you need to be the host to create new Events", Toast.LENGTH_SHORT).show();
                break;
            case R.id.moreEvents:
                Navigator.getBackstack(requireContext()).goTo(EventListKey.create(groupID));
                break;
            case R.id.settings:
                int pos = (int) Utils.convertDpToPixel(70.0f, requireContext()) - buttonPos[1];
                popupWindow.showAsDropDown(settings, pos * -1, 0); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
        }
    }

    private void setWarning(WarnType action, int request) {
        Intent intent = new Intent(getContext(), WarnActivity.class);
        intent.putExtra("name", group.getName());
        intent.putExtra("action", action.name());
        intent.putExtra("type", WarnType.group.name());
        startActivityForResult(intent, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && Objects.requireNonNull(data.getExtras()).getBoolean("choice")) {
            if (requestCode == DELETE_REQUEST) {
                mViewModel.deleteGroup();
            } else if (requestCode == LEAVE_REQUEST) {
                mViewModel.leaveGroup();
                txt.setText(requireContext().getString(R.string.join));
            }
        }
    }
}