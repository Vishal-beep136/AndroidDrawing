package com.example.androiddrawing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.example.util.BannerAds;
import com.example.util.ColorPickerDialog;
import com.example.util.Drawing_Image_Arrays;
import com.example.util.JsonUtils;
import com.viaviapp.androiddrawingdemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ActivityDrawingDetails extends AppCompatActivity {

    ViewPager viewpager;
    ImageView ivShow;
    RelativeLayout lytDraw, lytDrawSave;
    ImageView ivColorPick, ivPencil, ivStroke, ivErasure, ivClear, ivSave;
    private static int TOTAL_IMAGES;
    private int currentPosition = 0;
    protected Dialog dialog;
    protected View lytView;
    protected int progress;
    protected float stroke = 6;
    PaintView pv;
    String  strTitleName;
    int pos;
    TextView tvTitle, tvStep, tvBrush, tvColor, tvClear, tvStroke, tvSave, tvErasure, tvDevBy;
    ImageView ivBackPress, ivPrevious, ivForward, ivShare;
    LinearLayout lytAds;
    ProgressDialog progressDialog;
    JsonUtils jsonUtils;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawingdetails_activity);

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        progressDialog = new ProgressDialog(ActivityDrawingDetails.this);

        lytAds = findViewById(R.id.adView);
        BannerAds.ShowBannerAds(ActivityDrawingDetails.this, lytAds);

        lytDraw = findViewById(R.id.rell2);
        lytDrawSave = findViewById(R.id.rell);
        viewpager = findViewById(R.id.view_pagercake);
        ivBackPress = findViewById(R.id.image_backress);
        ivPrevious = findViewById(R.id.image_back);
        ivForward = findViewById(R.id.image_next);
        ivShare = findViewById(R.id.image_share);
        tvStep = findViewById(R.id.toolbar_step);
        tvTitle = findViewById(R.id.toolbar_title);
        tvBrush = findViewById(R.id.textView_brush);
        tvStroke = findViewById(R.id.textView_stroke);
        tvSave = findViewById(R.id.textView_save);
        tvErasure = findViewById(R.id.textView_erase);
        tvColor = findViewById(R.id.textView_color);
        tvClear = findViewById(R.id.textView_clear);
        tvDevBy = findViewById(R.id.textView);

        if (getResources().getString(R.string.isRTL).equals("true")) {
            ivBackPress.setImageResource(R.drawable.img_backpress_rtl);
        } else {
            ivBackPress.setImageResource(R.drawable.img_backpress);
        }

        Intent i = getIntent();
        strTitleName = i.getStringExtra("NAME");

        tvTitle.setText(strTitleName);
        ivColorPick = findViewById(R.id.imageView2_color);
        ivStroke = findViewById(R.id.imageView4_stroke);
        ivClear = findViewById(R.id.imageView5_clear);
        ivErasure = findViewById(R.id.imageView3_eraser);
        ivPencil = findViewById(R.id.imageView1_pencil);
        ivSave = findViewById(R.id.imageView1_save);

        tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_checked));
        tvClear.setTextColor(getResources().getColor(R.color.textdrawing_default));
        tvColor.setTextColor(getResources().getColor(R.color.textdrawing_default));
        tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_default));
        tvSave.setTextColor(getResources().getColor(R.color.textdrawing_default));
        tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_default));

        Typeface tf = Typeface.createFromAsset(this.getAssets(), "fonts/Montserrat-SemiBold_0.otf");
        tvBrush.setTypeface(tf);
        tvClear.setTypeface(tf);
        tvColor.setTypeface(tf);
        tvErasure.setTypeface(tf);
        tvSave.setTypeface(tf);
        tvStroke.setTypeface(tf);

        ivPencil.setImageResource(R.drawable.brush_active_btn); //click of pencil

        this.pv = new PaintView(this);
        this.pv.togglePencil(true);
        lytDraw.addView(pv);
        pos = viewpager.getCurrentItem();

        ivColorPick.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorPickerDialogDemo();
                tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvClear.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvColor.setTextColor(getResources().getColor(R.color.textdrawing_checked));
                tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvSave.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_default));
            }
        });

        ivStroke.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                strokeDialog();
                tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvClear.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvColor.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvSave.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_checked));
            }
        });

        ivClear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDrawingDetails.this.pv.clear();
                tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvClear.setTextColor(getResources().getColor(R.color.textdrawing_checked));
                tvColor.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvSave.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_default));
            }
        });

        ivErasure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDrawingDetails.this.pv.togglePencil(false);
                ivErasure.setImageResource(R.drawable.erase_active_btn);
                ivPencil.setImageResource(R.drawable.brush_btn);
                tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvClear.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvColor.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_checked));
                tvSave.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_default));
            }
        });

        ivPencil.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityDrawingDetails.this.pv.togglePencil(true);
                ivPencil.setImageResource(R.drawable.brush_active_btn);
                ivErasure.setImageResource(R.drawable.erase_btn);
                tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_checked));
                tvClear.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvColor.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvSave.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_default));
            }
        });

        ivSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvBrush.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvClear.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvColor.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvErasure.setTextColor(getResources().getColor(R.color.textdrawing_default));
                tvSave.setTextColor(getResources().getColor(R.color.textdrawing_checked));
                tvStroke.setTextColor(getResources().getColor(R.color.textdrawing_default));
                pos = viewpager.getCurrentItem();
                tvDevBy.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pos == TOTAL_IMAGES) {
                            progressDialog.setMessage(getString(R.string.loading_msg));
                            progressDialog.show();

                            pv.setDrawingCacheEnabled(true);
                            pv.invalidate();
                            File file = saveBitMap(ActivityDrawingDetails.this, lytDrawSave);
                            progressDialog.cancel();
                            if (file != null) {
                                Toast.makeText(ActivityDrawingDetails.this, getString(R.string.image_save), Toast.LENGTH_SHORT).show();

                            } else {
                                Log.i("TAG", "Oops! Image could not be saved.");
                            }

                        } else {
                            Toast.makeText(ActivityDrawingDetails.this, getString(R.string.complete_drawing_msg), Toast.LENGTH_SHORT).show();
                            tvDevBy.setVisibility(View.INVISIBLE);
                        }
                    }
                }, 500);


            }
        });

        viewpager.addOnPageChangeListener(new OnPageChangeListener() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onPageSelected(int arg0) {
                tvStep.setText(arg0 + "/" + TOTAL_IMAGES);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewpager.setAdapter(adapter);
        switch (strTitleName) {
            case "Car":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_car.length - 1);
                break;
            case "House":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_house.length - 1);
                break;
            case "Spider":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_spider.length - 1);
                break;
            case "Gun":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_gun.length - 1);
                break;
            case "Boat":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_boat.length - 1);
                break;
            case "Bird":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_bird.length - 1);
                break;
            case "Elephant":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_elephant.length - 1);
                break;
            case "Flower":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_flower.length - 1);
                break;
            case "Frog":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_frog.length - 1);
                break;
            case "Horse":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_horse.length - 1);
                break;
            case "Lion":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_lion.length - 1);
                break;
            case "Micky Mouse":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_mickey_mouse.length - 1);
                break;
            case "Dog":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_dog.length - 1);
                break;
            case "Turtle":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_turtle.length - 1);
                break;
            case "Princess":
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_princess.length - 1);
                break;
            default:
                TOTAL_IMAGES = (Drawing_Image_Arrays.image_car.length - 1);
                break;
        }
        tvStep.setText(String.valueOf(0) + "/" + TOTAL_IMAGES);
        ivPrevious.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                currentPosition = viewpager.getCurrentItem();

                int positionToMoveTo = currentPosition;
                positionToMoveTo--;
                if (positionToMoveTo < 0) {
                    positionToMoveTo = TOTAL_IMAGES;
                }
                viewpager.setCurrentItem(positionToMoveTo);
            }
        });
        ivForward.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                currentPosition = viewpager.getCurrentItem();

                int positionToMoveTo = currentPosition;
                positionToMoveTo++;
                if (currentPosition == TOTAL_IMAGES) {
                    positionToMoveTo = 0;
                }
                viewpager.setCurrentItem(positionToMoveTo);

            }
        });

        ivShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDevBy.setVisibility(View.VISIBLE);
                pos = viewpager.getCurrentItem();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pos == TOTAL_IMAGES) {

                            progressDialog.setMessage(getString(R.string.loading_msg));
                            progressDialog.show();

                            pv.setDrawingCacheEnabled(true);
                            pv.invalidate();
                            File file = saveBitMapShare(ActivityDrawingDetails.this, lytDrawSave);
                            if (file != null) {
                                progressDialog.cancel();
                                Log.i("TAG", "Drawing saved to the gallery!");
                            } else {
                                progressDialog.cancel();
                                Log.i("TAG", "Oops! Image could not be saved.");
                            }
                            String appLink = "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName();
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("image/png");
                            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                            share.putExtra(Intent.EXTRA_TEXT, appLink);
                            startActivity(Intent.createChooser(share, "Share Image"));

                        } else {
                            Toast.makeText(ActivityDrawingDetails.this, getString(R.string.complete_drawing_msg), Toast.LENGTH_SHORT).show();
                            tvDevBy.setVisibility(View.INVISIBLE);
                        }

                    }
                }, 500);
            }
        });

        ivBackPress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected int getItem(int i) {
        return 0;
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            switch (strTitleName) {
                case "Car":
                    return Drawing_Image_Arrays.image_car.length;
                case "House":
                    return Drawing_Image_Arrays.image_house.length;
                case "Spider":
                    return Drawing_Image_Arrays.image_spider.length;
                case "Gun":
                    return Drawing_Image_Arrays.image_gun.length;
                case "Boat":
                    return Drawing_Image_Arrays.image_boat.length;
                case "Bird":
                    return Drawing_Image_Arrays.image_bird.length;
                case "Elephant":
                    return Drawing_Image_Arrays.image_elephant.length;
                case "Flower":
                    return Drawing_Image_Arrays.image_flower.length;
                case "Frog":
                    return Drawing_Image_Arrays.image_frog.length;
                case "Horse":
                    return Drawing_Image_Arrays.image_horse.length;
                case "Lion":
                    return Drawing_Image_Arrays.image_lion.length;
                case "Micky Mouse":
                    return Drawing_Image_Arrays.image_mickey_mouse.length;
                case "Dog":
                    return Drawing_Image_Arrays.image_dog.length;
                case "Turtle":
                    return Drawing_Image_Arrays.image_turtle.length;
                case "Princess":
                    return Drawing_Image_Arrays.image_princess.length;
                default:
                    return Drawing_Image_Arrays.image_car.length;

            }

        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = ActivityDrawingDetails.this;

            ivShow = new ImageView(context);

            switch (strTitleName) {
                case "Car":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_car[position]);
                    break;
                case "House":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_house[position]);
                    break;
                case "Spider":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_spider[position]);
                    break;
                case "Gun":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_gun[position]);
                    break;
                case "Boat":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_boat[position]);
                    break;
                case "Bird":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_bird[position]);
                    break;
                case "Elephant":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_elephant[position]);
                    break;
                case "Flower":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_flower[position]);
                    break;
                case "Frog":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_frog[position]);
                    break;
                case "Horse":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_horse[position]);
                    break;
                case "Lion":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_lion[position]);
                    break;
                case "Micky Mouse":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_mickey_mouse[position]);
                    break;
                case "Dog":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_dog[position]);
                    break;
                case "Turtle":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_turtle[position]);
                    break;
                case "Princess":
                    ivShow.setImageResource(Drawing_Image_Arrays.image_princess[position]);
                    break;
                default:
                    ivShow.setImageResource(Drawing_Image_Arrays.image_car[position]);
                    break;
            }

            container.addView(ivShow, 0);

            return ivShow;

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    private void showColorPickerDialogDemo() {

        int initialColor = Color.WHITE;

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, initialColor, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {

                ActivityDrawingDetails.this.pv.setColor(color);
            }

        });
        colorPickerDialog.show();
    }

    private static class PaintView extends View {

        private Paint paint;
        private Bitmap bmp;
        private Paint bmpPaint;
        private Canvas canvas;
        Context context;
        private float mX, mY;
        private Path path;
        private static final float TOUCH_TOLERANCE = 0.8f;
        private int colour;
        private Bitmap bgImage; // image that gets loaded
        protected Boolean pencil;

        private PaintView(Context c) {
            super(c);

            setDrawingCacheEnabled(true); // to save images

            this.context = c;
            this.colour = Color.BLACK;
            this.path = new Path();
            this.bmpPaint = new Paint();
            this.paint = new Paint();
            this.paint.setAntiAlias(true);
            this.paint.setDither(true);
            this.paint.setColor(this.colour);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.paint.setStrokeCap(Paint.Cap.ROUND);
            this.paint.setStrokeWidth(3);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            this.bgImage = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            this.bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            this.canvas = new Canvas(this.bmp);
            bgImage = BitmapFactory.decodeResource(getResources(),
                    R.drawable.draw_bg).copy(Bitmap.Config.ARGB_8888, true);

        }

        private void touchStart(float x, float y) {
            this.path.reset();
            this.path.moveTo(x, y);
            this.mX = x;
            this.mY = y;
        }

        private void touchUp() {
            this.path.lineTo(mX, mY);
            // commit the path to our offscreen
            this.canvas.drawPath(this.path, paint);
            // kill this so we don't double draw
            this.path.reset();
        }

        private void touchMove(float x, float y) {
            float dx = Math.abs(x - this.mX);
            float dy = Math.abs(y - this.mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                // draws a quadratic curve
                this.path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.touchStart(x, y);
                    this.touchMove(x + 0.8f, y + 0.8f);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    this.touchMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    this.touchUp();

                    invalidate();
                    break;
            }
            return true;
        }

        // Called on invalidate();
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(this.bgImage, 0, 0, this.bmpPaint);

            canvas.drawBitmap(this.bmp, 0, 0, this.bmpPaint);

            canvas.drawPath(this.path, this.paint);

        }

        /*
         * Menu called methods
         */
        protected void togglePencil(Boolean b) {
            if (b) { // set pencil
                paint.setXfermode(null);
                this.pencil = true;

            } else { // set eraser
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                this.pencil = false;
            }
            //Draw_Dog.this.setTitle();

        }

        public void setColor(int c) {
            this.paint.setColor(c);
            this.colour = c;
        }

        protected int getColor() {
            return this.colour;
        }

        protected void clear() {
            this.path = new Path(); // empty path
            this.canvas.drawColor(Color.WHITE);
            if (this.bgImage != null) {
                this.canvas.drawBitmap(this.bgImage, 0, 0, null);
            }
            this.invalidate();
        }
    }

    public void strokeDialog() {

        this.dialog = new Dialog(this);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        this.lytView = inflater.inflate(R.layout.stroke_dialog,
                (ViewGroup) findViewById(R.id.dialog_root_element));

        SeekBar dialogSeekBar = lytView
                .findViewById(R.id.dialog_seekbar);

        dialogSeekBar.setThumbOffset(convertDipToPixels(9.5f));
        dialogSeekBar.setProgress((int) this.stroke * 2);

        this.setTextView(this.lytView, String.valueOf(Math.round(this.stroke)));

        dialogSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // herp
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // derp
            }

            @Override
            public void onProgressChanged(SeekBar seekBark, int progress,
                                          boolean fromUser) {
                ActivityDrawingDetails.this.progress = progress / 2;
                ActivityDrawingDetails.this
                        .setTextView(ActivityDrawingDetails.this.lytView, "" + ActivityDrawingDetails.this.progress);

                Button b = ActivityDrawingDetails.this.lytView
                        .findViewById(R.id.dialog_button);
                b.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityDrawingDetails.this.stroke = ActivityDrawingDetails.this.progress;
                        ActivityDrawingDetails.this.pv.paint.setStrokeWidth(ActivityDrawingDetails.this.stroke);
                        ActivityDrawingDetails.this.dialog.dismiss();
                    }
                });
            }
        });

        dialog.setContentView(lytView);
        dialog.show();
    }

    protected void setTextView(View layout, String s) {
        TextView text = layout.findViewById(R.id.stroke_text);
        text.setText(s);
    }

    private int convertDipToPixels(float dip) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float density = metrics.density;
        return (int) (dip * density);
    }

    private File saveBitMap(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);

        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context, pictureFile.getAbsolutePath());
        return pictureFile;
    }

    private File saveBitMapShare(Context context, View drawView) {
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if (!isDirectoryCreated)
                Log.i("TAG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() + File.separator + "Share_image" + ".jpg";
        File pictureFile = new File(filename);
        Bitmap bitmap = getBitmapFromView(drawView);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        return pictureFile;
    }

    //create bitmap from view and returns it
    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    // used for scanning gallery
    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue scanning gallery.");
        }
    }

}
