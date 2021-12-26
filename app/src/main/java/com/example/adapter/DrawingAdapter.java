package com.example.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androiddrawing.ActivityDrawingDetails;
import com.example.item.DrawingListItem;
import com.example.util.Constant;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ixidev.gdpr.GDPRChecker;
import com.squareup.picasso.Picasso;
import com.viaviapp.androiddrawingdemo.R;

import java.util.ArrayList;
import java.util.List;


public class DrawingAdapter extends RecyclerView.Adapter {

    private Activity mContext;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_Ad = 0;
    private ArrayList<DrawingListItem> dataList;
    private ProgressDialog pDialog;

    public DrawingAdapter(Activity context, ArrayList<DrawingListItem> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_drawing_item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_Ad) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.admob_adapter, parent, false);
            return new AdOption(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;
            final DrawingListItem singleItem = dataList.get(position);

            Typeface tf = Typeface.createFromAsset(this.mContext.getAssets(), "fonts/Montserrat-SemiBold_0.otf");
            viewHolder.text.setTypeface(tf);
            viewHolder.txt_step.setTypeface(tf);
            viewHolder.item_step2.setTypeface(tf);

            viewHolder.text.setText(singleItem.getName());
            viewHolder.txt_step.setText("(" + singleItem.getDescription() + ")");
            Picasso.get().load(singleItem.getIcon()).placeholder(R.mipmap.app_icon).into(viewHolder.image);

            viewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mContext.getString(R.string.interstitial_ads_on_off).equals("true")) {
                        if (mContext.getString(R.string.interstitial_ads_type).equals("admob")) {
                            Constant.AD_COUNT++;
                            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                                Constant.AD_COUNT = 0;
                                Loading();
                                GDPRChecker.Request request = GDPRChecker.getRequest();
                                AdRequest.Builder builder = new AdRequest.Builder();
                                if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                                    Bundle extras = new Bundle();
                                    extras.putString("npa", "1");
                                    builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                                }
                                InterstitialAd.load(mContext, mContext.getString(R.string.admob_interstitial_id), builder.build(), new InterstitialAdLoadCallback() {
                                    @Override
                                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                                        super.onAdLoaded(interstitialAd);
                                        interstitialAd.show((Activity) mContext);
                                        pDialog.dismiss();
                                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                            @Override
                                            public void onAdDismissedFullScreenContent() {
                                                super.onAdDismissedFullScreenContent();
                                                Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                                image_princess.putExtra("NAME", singleItem.getName());
                                                mContext.startActivity(image_princess);
                                            }

                                            @Override
                                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                                super.onAdFailedToShowFullScreenContent(adError);
                                                pDialog.dismiss();
                                                Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                                image_princess.putExtra("NAME", singleItem.getName());
                                                mContext.startActivity(image_princess);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                        super.onAdFailedToLoad(loadAdError);
                                        pDialog.dismiss();
                                        Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                        image_princess.putExtra("NAME", singleItem.getName());
                                        mContext.startActivity(image_princess);
                                    }
                                });
                            } else {
                                Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                image_princess.putExtra("NAME", singleItem.getName());
                                mContext.startActivity(image_princess);
                            }
                        } else {
                            Constant.AD_COUNT++;
                            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                                Constant.AD_COUNT = 0;
                                Loading();
                                final com.facebook.ads.InterstitialAd mInterstitialfb = new com.facebook.ads.InterstitialAd(mContext, mContext.getString(R.string.facebook_interstitial_id));
                                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                                    @Override
                                    public void onInterstitialDisplayed(Ad ad) {
                                    }

                                    @Override
                                    public void onInterstitialDismissed(Ad ad) {
                                        Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                        image_princess.putExtra("NAME", singleItem.getName());
                                        mContext.startActivity(image_princess);
                                    }

                                    @Override
                                    public void onError(Ad ad, AdError adError) {
                                        pDialog.dismiss();
                                        Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                        image_princess.putExtra("NAME", singleItem.getName());
                                        mContext.startActivity(image_princess);
                                    }

                                    @Override
                                    public void onAdLoaded(Ad ad) {
                                        pDialog.dismiss();
                                        mInterstitialfb.show();
                                    }

                                    @Override
                                    public void onAdClicked(Ad ad) {
                                    }

                                    @Override
                                    public void onLoggingImpression(Ad ad) {
                                    }
                                };
                                com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = mInterstitialfb.buildLoadAdConfig().withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
                                mInterstitialfb.loadAd(loadAdConfig);

                            } else {
                                Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                                image_princess.putExtra("NAME", singleItem.getName());
                                mContext.startActivity(image_princess);
                            }
                        }
                    } else {
                        Intent image_princess = new Intent(mContext, ActivityDrawingDetails.class);
                        image_princess.putExtra("NAME", singleItem.getName());
                        mContext.startActivity(image_princess);
                    }
                }
            });
        } else if (holder.getItemViewType() == VIEW_TYPE_Ad) {

            final AdOption adOption = (AdOption) holder;
            if (mContext.getString(R.string.native_ads_on_off).equals("true")) {
                if (mContext.getString(R.string.native_ads_type).equals("admob")) {

                    if (adOption.linearLayout.getChildCount() == 0) {

                        View view = mContext.getLayoutInflater().inflate(R.layout.admob_ad, null, true);

                        final TemplateView templateView = view.findViewById(R.id.my_template);
                        if (templateView.getParent() != null) {
                            ((ViewGroup) templateView.getParent()).removeView(templateView);
                        }
                        adOption.linearLayout.addView(templateView);
                        AdLoader adLoader = new AdLoader.Builder(mContext, mContext.getString(R.string.admob_native_id))
                                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                                    @Override
                                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                                        NativeTemplateStyle styles = new
                                                NativeTemplateStyle.Builder()
                                                .build();

                                        templateView.setStyles(styles);
                                        templateView.setNativeAd(unifiedNativeAd);

                                    }
                                })
                                .build();

                        GDPRChecker.Request request = GDPRChecker.getRequest();
                        AdRequest.Builder builder = new AdRequest.Builder();
                        if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                            Bundle extras = new Bundle();
                            extras.putString("npa", "1");
                            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                        }
                        adLoader.loadAd(builder.build());
                    }

                } else {
                    if (adOption.linearLayout.getChildCount() == 0) {

                        LayoutInflater inflater = LayoutInflater.from(mContext);
                        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, adOption.linearLayout, false);

                        adOption.linearLayout.addView(adView);

                        // Add the AdOptionsView
                        final LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);

                        // Create native UI using the ad metadata.
                        final TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                        final MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                        final TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                        final TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                        final TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                        final Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                        final NativeAd nativeAd = new NativeAd(mContext, mContext.getString(R.string.facebook_native_id));
                        NativeAdListener nativeAdListener = new NativeAdListener() {
                            @Override
                            public void onMediaDownloaded(Ad ad) {
                                Log.d("status_data", "MediaDownloaded" + " " + ad.toString());
                            }

                            @Override
                            public void onError(Ad ad, AdError adError) {
                                Toast.makeText(mContext, adError.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("status_data", "error" + " " + adError.toString());
                            }

                            @Override
                            public void onAdLoaded(Ad ad) {
                                // Race condition, load() called again before last ad was displayed
                                if (nativeAd == null || nativeAd != ad) {
                                    return;
                                }
                                // Inflate Native Ad into Container
                                Log.d("status_data", "on load" + " " + ad.toString());

                                NativeAdLayout nativeAdLayout = new NativeAdLayout(mContext);
                                AdOptionsView adOptionsView = new AdOptionsView(mContext, nativeAd, nativeAdLayout);
                                adChoicesContainer.removeAllViews();
                                adChoicesContainer.addView(adOptionsView, 0);

                                // Set the Text.
                                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                                nativeAdBody.setText(nativeAd.getAdBodyText());
                                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                                // Create a list of clickable views
                                List<View> clickableViews = new ArrayList<>();
                                clickableViews.add(nativeAdTitle);
                                clickableViews.add(nativeAdCallToAction);

                                // Register the Title and CTA button to listen for clicks.
                                nativeAd.registerViewForInteraction(
                                        adOption.linearLayout,
                                        nativeAdMedia,
                                        clickableViews);
                            }

                            @Override
                            public void onAdClicked(Ad ad) {
                                Log.d("status_data", "AdClicked" + " " + ad.toString());
                            }

                            @Override
                            public void onLoggingImpression(Ad ad) {
                                Log.d("status_data", "Impression" + " " + ad.toString());
                            }

                        };
                        // Request an ad
                        nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) != null ? VIEW_TYPE_ITEM : VIEW_TYPE_Ad;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        private TextView text, txt_step, item_step2;
        private LinearLayout lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            text = itemView.findViewById(R.id.text);
            lyt_parent = itemView.findViewById(R.id.rootLayout);
            txt_step = itemView.findViewById(R.id.item_step);
            item_step2 = itemView.findViewById(R.id.item_step2);
        }
    }

    public class AdOption extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;

        public AdOption(View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.adView_admob);
        }
    }

    private void Loading() {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getResources().getString(R.string.loading_msg));
        pDialog.setCancelable(false);
        pDialog.show();
    }
}