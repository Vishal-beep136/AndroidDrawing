package com.example.androiddrawing;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.adapter.CustomViewPagerAdapter;
import com.example.adapter.DrawingAdapter;
import com.example.item.DrawingListItem;
import com.example.item.SliderItem;
import com.example.util.Drawing_Image_Arrays;
import com.example.util.EnchantedViewPager;
import com.example.util.ItemOffsetDecoration;
import com.example.util.JsonUtils;
import com.viaviapp.androiddrawingdemo.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;


public class FragmentDrawingList extends Fragment {

    ArrayList<DrawingListItem> mListItem;
    RecyclerView recyclerView;
    DrawingAdapter adapter;
    String[] title, description;

    List<SliderItem> sliderItems;
    EnchantedViewPager enchantedViewPager;
    CustomViewPagerAdapter customViewPagerAdapter;
    CircleIndicator circleIndicator;
    int currentCount = 0;
    int columnWidth;
    JsonUtils jsonUtils;
    int j = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_drawinglist, container, false);
        mListItem = new ArrayList<>();
        title = getResources().getStringArray(R.array.names);
        description = getResources().getStringArray(R.array.descriptions);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);

        jsonUtils = new JsonUtils(requireActivity());
        columnWidth = (jsonUtils.getScreenWidth());

        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setFocusable(false);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == 0) {
                    return 2;
                }
                return 1;
            }
        });
        for (int i = 0; i < title.length; i++) {
            DrawingListItem item = new DrawingListItem(description[i], title[i], Drawing_Image_Arrays.icons[i]);
            if (requireActivity().getString(R.string.native_ads_on_off).equals("true")) {
                if (j % Integer.parseInt(getString(R.string.native_ads_position)) == 0) {
                    mListItem.add(null);
                    j++;
                }
            }
            mListItem.add(item);
            j++;
        }

        adapter = new DrawingAdapter(getActivity(), mListItem);
        recyclerView.setAdapter(adapter);

        sliderItems = new ArrayList<>();
        circleIndicator = rootView.findViewById(R.id.indicator_unselected_background);
        sliderItems.add(new SliderItem(R.drawable.instagram_banner, getString(R.string.instagram_url)));
        sliderItems.add(new SliderItem(R.drawable.facebook, getString(R.string.facebook_url)));
        sliderItems.add(new SliderItem(R.drawable.youtube, getString(R.string.youtube_url)));

        enchantedViewPager = rootView.findViewById(R.id.viewPager);
        enchantedViewPager.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, columnWidth / 2));
        enchantedViewPager.useScale();
        enchantedViewPager.removeAlpha();

        customViewPagerAdapter = new CustomViewPagerAdapter(requireActivity(), sliderItems);
        enchantedViewPager.setAdapter(customViewPagerAdapter);
        enchantedViewPager.setCurrentItem(1);
        circleIndicator.setViewPager(enchantedViewPager);
        autoPlay(enchantedViewPager);

        return rootView;
    }

    private void autoPlay(final ViewPager viewPager) {

        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (customViewPagerAdapter != null && viewPager.getAdapter().getCount() > 0) {
                        int position = currentCount % customViewPagerAdapter.getCount();
                        currentCount++;
                        viewPager.setCurrentItem(position);
                        autoPlay(viewPager);
                    }
                } catch (Exception e) {
                    Log.e("TAG", "auto scroll pager error..", e);
                }
            }
        }, 5000);
    }
}
