package com.example.budspaces.Views.Fragments.Groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.example.budspaces.Adapters.EventMembersAdapter;
import com.example.budspaces.Adapters.PopupAdapter;
import com.example.budspaces.Cache;
import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Enums.PopupActions;
import com.example.budspaces.Enums.WarnType;
import com.example.budspaces.Listeners.OnPopupActionSelectedListener;
import com.example.budspaces.Models.Event;
import com.example.budspaces.Models.Member;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.CreateModifEventKey;
import com.example.budspaces.R;
import com.example.budspaces.Utils.SimpleDividerItemDecoration;
import com.example.budspaces.Utils.Tuple3;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.EventViewModel;
import com.example.budspaces.Views.Activities.MainActivity;
import com.example.budspaces.Views.Activities.WarnActivity;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class EventFragment extends BaseFragment {
    private static final int DELETE_REQUEST = 3215;
    private static final int LEAVE_REQUEST = 3125;

    @BindView(R.id.picture)
    ImageView picture;
    @BindView(R.id.eventName)
    TextView eventName;
    @BindView(R.id.hostName)
    TextView hostName;
    @BindView(R.id.txt)
    TextView txt;
    @BindView(R.id.locationCity)
    TextView locationCity;
    @BindView(R.id.locationAddress)
    TextView locationAddress;
    @BindView(R.id.participantNb)
    TextView participantNb;
    @BindView(R.id.members)
    RecyclerView members;
    @BindView(R.id.description)
    TextView description;
    @BindView(R.id.eventDate)
    TextView eventDate;
    @BindView(R.id.eventTime)
    TextView eventTime;
    @BindView(R.id.settings)
    ImageView settings;
    @BindView(R.id.action)
    ConstraintLayout action;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private boolean joined = false;
    private boolean isHost = false;
    private boolean discover = false;

    private EventMembersAdapter membersAdapter;
    private Member user;

    private EventViewModel mViewModel;
    private Unbinder unbinder;
    private Event event;
    private PopupWindow popupWindow;
    private PopupAdapter adapter;
    private List<Tuple3<String, Drawable, PopupActions>> tupleList = new ArrayList<>();
    private int[] buttonPos = new int[2];
    private String eventID;

    private OnPopupActionSelectedListener actionSelectedListener = new OnPopupActionSelectedListener() {
        @Override
        public void actionSelected(PopupActions action) {
            popupWindow.dismiss();
            switch (action) {
                case modify:
                    Navigator.getBackstack(requireContext()).goTo(CreateModifEventKey.create(InterestType.event_modify, event.getId(), event.getHost()));
                    break;
                case delete:
                    setWarning(WarnType.delete, DELETE_REQUEST);
                    break;
            }
        }
    };

    public EventFragment(String eventID) {
        this.eventID = eventID;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        discover = ((MainActivity) requireActivity()).isDiscover();

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

    private void initTuples() {
        adapter.removeAll();
        tupleList.clear();

        if (isHost) {
            tupleList.add(new Tuple3<>(getString(R.string.modify_event), getResources().getDrawable(R.drawable.ic_settings), PopupActions.modify));
            tupleList.add(new Tuple3<>(getString(R.string.delete_event), getResources().getDrawable(R.drawable.ic_trash), PopupActions.delete));
        } else if (event.getMembers().contains(user.getId()))
            tupleList.add(new Tuple3<>(getString(R.string.leave), getResources().getDrawable(R.drawable.ic_logout), PopupActions.leave));
        else
            tupleList.add(new Tuple3<>(getString(R.string.participate), getResources().getDrawable(R.drawable.ic_add_attachment), PopupActions.join));

        adapter.addAll(tupleList);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        String userId = Cache.getInstance().getSession("userId");

        mViewModel.loadEvent(eventID);
        mViewModel.loadMyMember();
        mViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null)
                this.user = user;
        });

        mViewModel.getevent().observe(getViewLifecycleOwner(), event -> {
            if (event != null) {
                initEvent(event);
                mViewModel.loadMembers(eventID, 7);

                if (!discover) {
                    if (event.getHost().equals(userId)) {
                        isHost = true;
                        action.setVisibility(View.GONE);
                    }

                    if (event.getMembers().contains(userId))
                        joined = true;

                    if (joined) {
                        txt.setText(getResources().getText(R.string.leave));
                    } else {
                        txt.setText(getResources().getText(R.string.participate));
                    }

                    initTuples();
                }
            }
        });

        mViewModel.getMembers().observe(getViewLifecycleOwner(), membersLst -> {
            if (membersLst.size() > 0) {
                initMembers(membersLst);
            }
        });

        mViewModel.getIsJoined().observe(getViewLifecycleOwner(), isJoined -> {
            if (isJoined) {
                joined = true;
                membersAdapter.add(user);
                participantNb.setText(membersAdapter.getItemCount() + "");
            }
        });

        mViewModel.getIsLeft().observe(getViewLifecycleOwner(), isLeft -> {
            if (isLeft) {
                joined = false;
                membersAdapter.remove(user);
                participantNb.setText(membersAdapter.getItemCount() + "");
            }
        });

        mViewModel.getIsDeleted().observe(getViewLifecycleOwner(), isDeleted -> {
            if (isDeleted) {
                Navigator.getBackstack(requireContext()).goBack();
            }
        });
    }

    private void initEvent(Event event) {
        this.event = event;
        Glide.with(this).load(event.getPicture()).dontAnimate().into(picture);
        eventName.setText(event.getName());
        hostName.setText(event.getgroupName());
        String startDay = (String) DateFormat.format("dd", event.getStartDate());
        String endDay = (String) DateFormat.format("dd MMMM ", event.getEndDate());
        String startTime = (String) DateFormat.format("hh.mm", event.getStartDate());
        String endTime = (String) DateFormat.format("hh.mm ", event.getEndDate());
        eventDate.setText(startDay + " - " + endDay);
        eventTime.setText("De " + startTime + " à " + endTime);
        locationCity.setText(event.getAddress());
        locationAddress.setText(event.getAddress());
        description.setText(event.getDescription());
        participantNb.setText(event.getMembers().size() + "");
    }

    private void initMembers(List<Member> memberList) {
        LinearLayoutManager eventManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        members.setLayoutManager(eventManager);
        members.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = getResources().getDimensionPixelSize(R.dimen.elevation);
            }
        });

        membersAdapter = new EventMembersAdapter(memberList, event.getMembers().size(), 6, event.getName(), eventID);
        members.setAdapter(membersAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.action, R.id.settings})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.action:
                if (!joined) {
                    mViewModel.joinEvent(eventID);
                    txt.setText(getResources().getText(R.string.leave));
                } else
                    setWarning(WarnType.leave, LEAVE_REQUEST);
                break;
            case R.id.settings:
                int pos = (int) Utils.convertDpToPixel(95.0f, requireContext()) - buttonPos[1];
                popupWindow.showAsDropDown(settings, pos * -1, 0); //showAsDropDown(below which view you want to show as dropdown,horizontal position, vertical position)
                break;
        }
    }

    private void setWarning(WarnType action, int request) {
        Intent intent = new Intent(getContext(), WarnActivity.class);

        intent.putExtra("name", event.getName());
        intent.putExtra("action", action.name());
        intent.putExtra("type", WarnType.event.name());

        startActivityForResult(intent, request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && Objects.requireNonNull(data.getExtras()).getBoolean("choice")) {
            if (requestCode == DELETE_REQUEST) {
                if (isHost) {
                    mViewModel.deleteEvent(eventID);
                } else
                    Toast.makeText(requireContext(), "only the host can delete the event", Toast.LENGTH_SHORT).show();
            } else if (requestCode == LEAVE_REQUEST) {
                mViewModel.leaveEvent(eventID);
                joined = false;
                txt.setText(getResources().getText(R.string.participate));
            }
        }
    }
}