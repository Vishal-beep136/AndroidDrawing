package com.example.androiddrawing;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.util.JsonUtils;
import com.example.util.StatusBarUtil;
import com.viaviapp.androiddrawingdemo.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ActivityAboutUs extends AppCompatActivity {

    TextView txtAppName, txtVersion, txtCompany, txtEmail, txtWebsite, txtContact;
    WebView webView;
    JsonUtils jsonUtils;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setStatusBarGradiant(ActivityAboutUs.this);
        setContentView(R.layout.activity_aboutus);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.menu_about));
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.RobotoTextViewStyle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        jsonUtils = new JsonUtils(this);
        jsonUtils.forceRTLIfSupported(getWindow());

        txtAppName = findViewById(R.id.text_app_name);
        txtVersion = findViewById(R.id.text_version);
        txtCompany = findViewById(R.id.text_company);
        txtEmail = findViewById(R.id.text_email);
        txtWebsite = findViewById(R.id.text_website);
        txtContact = findViewById(R.id.text_contact);
        webView = findViewById(R.id.webView);

        txtAppName.setText(getString(R.string.app_name));
        txtVersion.setText(getString(R.string.version_name));
        txtCompany.setText(getString(R.string.company_name));
        txtEmail.setText(getString(R.string.email_id));
        txtWebsite.setText(getString(R.string.website_name));
        txtContact.setText(getString(R.string.contact_no));

        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        String mimeType = "text/html";
        String encoding = "utf-8";
        String htmlText = getString(R.string.about_desc);

        String text = "<html><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/Montserrat-Medium_0.otf\")}body{font-family: MyFont;color: #8D8D8D;text-align:justify}"
                + "</style></head>"
                + "<body>"
                +  htmlText
                + "</body></html>";

        webView.loadDataWithBaseURL(null,text, mimeType, encoding,null);

        LinearLayout linearLayout_email = findViewById(R.id.linearLayout_email_about_us);
        LinearLayout linearLayout_website = findViewById(R.id.linearLayout_web_about_us);
        LinearLayout linearLayout_phone = findViewById(R.id.linearLayout_contact_about_us);

        linearLayout_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{
                            getResources().getString(R.string.email_id)
                    });
                    emailIntent.setData(Uri.parse("mailto:?subject="));
                    startActivity(emailIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    jsonUtils.alertBox(getResources().getString(R.string.wrong));
                }
            }
        });

        linearLayout_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String url = getResources().getString(R.string.website_name);
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://" + url;
                    }
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    jsonUtils.alertBox(getResources().getString(R.string.wrong));
                }
            }
        });

        linearLayout_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + getResources().getString(R.string.contact_no)));
                    startActivity(callIntent);
                } catch (Exception e) {
                    jsonUtils.alertBox(getResources().getString(R.string.wrong));
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}