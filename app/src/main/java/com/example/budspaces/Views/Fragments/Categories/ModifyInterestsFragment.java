package com.example.budspaces.Views.Fragments.Categories;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.budspaces.Adapters.GridPagerAdapter;
import com.example.budspaces.Enums.InterestType;
import com.example.budspaces.Listeners.OnCategorySelectedListener;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.Navigation.Core.BaseFragment;
import com.example.budspaces.Navigation.Keys.ProfileKey;
import com.example.budspaces.R;
import com.example.budspaces.Samples.CategoriesSamples;
import com.example.budspaces.ViewModels.Themes.ModifyInterestsViewModel;
import com.example.budspaces.Views.Fragments.Groups.GroupFragment;
import com.zhuinden.simplestack.Backstack;
import com.zhuinden.simplestack.History;
import com.zhuinden.simplestack.StateChange;
import com.zhuinden.simplestack.navigator.Navigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.relex.circleindicator.CircleIndicator3;

public class ModifyInterestsFragment extends BaseFragment {
    @BindView(R.id.pageIndicator)
    CircleIndicator3 pageIndicator;
    @BindView(R.id.categories)
    ViewPager2 categoriesVP;
    @BindView(R.id.left)
    ImageView left;
    @BindView(R.id.right)
    ImageView right;
    @BindView(R.id.page_title)
    TextView pageTitle;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.skip)
    TextView skip;

    private ModifyInterestsViewModel mViewModel;
    private Unbinder unbinder;

    private int page = 0;
    private List<Theme> choosenCategories = new ArrayList<>();
    private GridPagerAdapter gridPagerAdapter;
    private InterestType type;
    private String groupID;
    private List<String> previousInterests;
    private Backstack backstack;

    private OnCategorySelectedListener listener = new OnCategorySelectedListener() {
        @Override
        public boolean selectCat(Theme cat) {
            choosenCategories.add(cat);
            return true;
        }

        @Override
        public void delCat(Theme cat) {
            choosenCategories.remove(cat);
        }

        @Override
        public boolean isSelected(String catName) {
            return previousInterests.contains(catName);
        }
    };

    public ModifyInterestsFragment() {
        // Required empty public constructor
    }

    public ModifyInterestsFragment(InterestType type, String id, List<String> previousInterests) {
        this.type = type;
        this.groupID = id;

        if (previousInterests == null)
            this.previousInterests = new ArrayList<>();
        else
            this.previousInterests = previousInterests;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.modify_interests_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        backstack = Navigator.getBackstack(requireContext());

        switch (type) {
            case profile:
                break;
            case group_new:
                pageTitle.setText(getText(R.string.new_group_2));
                title.setText(getText(R.string.group_interests));
                break;
            case group_modify:
                pageTitle.setText(getText(R.string.modif_group_2));
                title.setText(getText(R.string.group_interests));
                break;
        }

        skip.setVisibility(View.VISIBLE);
        skip.setText(getText(R.string.validate));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ModifyInterestsViewModel.class);

        mViewModel.getThemesCount().observe(getViewLifecycleOwner(), count -> {
            if (count > 0)
                initGridAdapter(count);
        });

        mViewModel.getIsValid().observe(getViewLifecycleOwner(), isValid -> {
            if (isValid) {
                switch (type) {
                    case group_modify:
                        backstack.setHistory(GroupFragment.getHistory(), StateChange.REPLACE);
                        break;
                    case profile:
                    case group_new:
                        backstack.setHistory(History.of(ProfileKey.create("")), StateChange.REPLACE);
                        break;
                }
            }
        });

        mViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
        });
    }

    private void initGridAdapter(Integer count) {
        gridPagerAdapter = new GridPagerAdapter(requireActivity(), listener, count);
        page = count / 2;

        categoriesVP.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (page - 1 >= 0)
                    left.setVisibility(View.VISIBLE);
                else
                    left.setVisibility(View.GONE);
                if (page + 1 < gridPagerAdapter.getItemCount())
                    right.setVisibility(View.VISIBLE);
                else
                    right.setVisibility(View.GONE);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        categoriesVP.setAdapter(gridPagerAdapter);
        pageIndicator.setViewPager(categoriesVP);
        categoriesVP.setCurrentItem(page);
    }

    @OnClick({R.id.left, R.id.right, R.id.skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.left:
                if (page - 1 >= 0)
                    categoriesVP.setCurrentItem(--page);
                break;
            case R.id.right:
                if (page + 1 < gridPagerAdapter.getItemCount())
                    categoriesVP.setCurrentItem(++page);
                break;
            case R.id.skip:
                switch (type) {
                    case profile:
                        mViewModel.updateInterests(choosenCategories);
                        break;
                    case group_modify:
                    case group_new:
                        if (choosenCategories.size() <= 0) {
                            Toast.makeText(requireContext(), "You have to choose at least 1 theme", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mViewModel.updateInterests(groupID, choosenCategories);
                        break;
//                        backstack.setHistory(GroupFragment.getHistory(), StateChange.REPLACE);
//                        backstack.setHistory(History.of(ProfileKey.create()), StateChange.REPLACE);
//                        break;
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}