package com.example.budspaces.Views.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.budspaces.Adapters.GridPagerAdapter;
import com.example.budspaces.Listeners.OnCategorySelectedListener;
import com.example.budspaces.Models.Theme;
import com.example.budspaces.Network.AppRetrofit;
import com.example.budspaces.R;
import com.example.budspaces.Utils.Utils;
import com.example.budspaces.ViewModels.Themes.CategoryChooserViewModel;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.relex.circleindicator.CircleIndicator3;

import static com.example.budspaces.Kotlin.ViewExtensionKt.makeStatusBarWhite;

public class CategoryChooserActivity extends AppCompatActivity {

    @BindView(R.id.pageIndicator)
    CircleIndicator3 pageIndicator;
    @BindView(R.id.categories)
    ViewPager2 categoriesVP;
    @BindView(R.id.left)
    ImageView left;
    @BindView(R.id.right)
    ImageView right;
    @BindView(R.id.skip)
    TextView skip;
    @BindView(R.id.title)
    TextView title;

    private int page = 0;
    private boolean canAddCat = true;
    private boolean verified = true;
    private GridPagerAdapter gridPagerAdapter;
    private List<Theme> choosenCategories = new ArrayList<>();

    private CategoryChooserViewModel categoryChooserViewModel;
    private Disposable internetDisposable;

    private OnCategorySelectedListener listener = new OnCategorySelectedListener() {
        @Override
        public boolean selectCat(Theme cat) {
            if (!canAddCat)
                return false;

            choosenCategories.add(cat);
            canAddCat = choosenCategories.size() < 5;

            if (choosenCategories.size() > 0)
                skip.setText(getString(R.string.validate));

            return true;
        }

        @Override
        public void delCat(Theme cat) {
            choosenCategories.remove(cat);
            canAddCat = choosenCategories.size() < 5;

            if (choosenCategories.size() <= 0)
                skip.setText(getString(R.string.skip));
        }

        @Override
        public boolean isSelected(String catName) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_interests_fragment);
        ButterKnife.bind(this);
        makeStatusBarWhite(this);

        if (getIntent().hasExtra("verified"))
            verified = getIntent().getBooleanExtra("verified", true);

        categoryChooserViewModel = new ViewModelProvider(this).get(CategoryChooserViewModel.class);
    }

    private void initGridAdapter(Integer count) {
        gridPagerAdapter = new GridPagerAdapter(this, listener, count);
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
                categoryChooserViewModel.setCategories(choosenCategories);

                Intent intent = new Intent(CategoryChooserActivity.this, MainActivity.class);
                intent.putExtra("verified", verified);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    @Override protected void onResume() {
        super.onResume();

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> {
                    AppRetrofit.setIsConnected(isConnected);
                    categoryChooserViewModel.getCount();
                });

        categoryChooserViewModel.getErrorMessage().observe(this, errorMsg -> {
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
        });

        categoryChooserViewModel.getThemesCount().observe(this, count -> {
            if (count > 0) {
                initGridAdapter(count);
            }
        });

        skip.setVisibility(View.VISIBLE);
        title.setText(getText(R.string.choose_categories));
    }

    @Override protected void onPause() {
        super.onPause();
        Utils.safelyDispose(internetDisposable);
    }
}