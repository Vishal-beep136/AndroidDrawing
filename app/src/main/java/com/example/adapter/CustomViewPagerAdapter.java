package com.example.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.example.item.SliderItem;
import com.example.util.EnchantedViewPager;
import com.squareup.picasso.Picasso;
import com.viaviapp.androiddrawingdemo.R;


import java.util.List;

public class CustomViewPagerAdapter extends PagerAdapter {

     private Activity activity;
    private List<SliderItem> sliderItems;
    private LayoutInflater inflater;

    public CustomViewPagerAdapter(Activity activity, List<SliderItem> sliderItems) {
        this.activity = activity;
        this.sliderItems = sliderItems;
        // TODO Auto-generated constructor stub
        inflater = activity.getLayoutInflater();
    }


    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = inflater.inflate(R.layout.slider_adapter, container, false);
        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById(R.id.imageView_slider_adapter);

        imageLayout.setTag(EnchantedViewPager.ENCHANTED_VIEWPAGER_POSITION + position);

        Picasso.get().load(sliderItems.get(position).getInteger()).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sliderItems.get(position).getUrl()));
                activity.startActivity(browserIntent);
            }
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public int getCount() {
        return sliderItems.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        (container).removeView((View) object);
    }
}

