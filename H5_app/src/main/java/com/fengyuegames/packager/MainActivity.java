package com.fengyuegames.packager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.library.BuildConfig;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private BridgeWebView webView;
    private AdView bannerAdView;
    private RewardedAd rewardedAd;
    private InterstitialAd interstitialAd;
    private boolean isMobileAdsInitialized = false;
    private final String url = "file:///android_asset/02/index.html";

    private static final String REAL_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
    private static final String REAL_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String REAL_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
    private static final String TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
    private static final String TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Preview Window设置的背景图如果不做处理，图片就会一直存在于内存中
        getWindow().setBackgroundDrawable(null);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWebView();
        setupBannerAdView();
        initializeAds();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        registerJsHandlers();
    }

    private void setupBannerAdView() {
        FrameLayout adContainer = findViewById(R.id.ad_container);
        bannerAdView = new AdView(this);
        bannerAdView.setAdSize(AdSize.BANNER);
        bannerAdView.setAdUnitId(getAdUnitID(AdType.BANNER));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        );
        bannerAdView.setLayoutParams(layoutParams);
        adContainer.addView(bannerAdView);
        loadBannerAd();
    }

    private void initializeAds() {
        if (!isMobileAdsInitialized) {
            isMobileAdsInitialized = true;
            MobileAds.initialize(this, initializationStatus -> {
                Log.d(TAG, "Google Mobile Ads SDK initialized.");
                loadRewardedAd();
                loadInterstitialAd();
            });
        }
    }

    private void loadBannerAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);
    }

    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, getAdUnitID(AdType.REWARDED), adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
                rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        rewardedAd = null;
                        loadRewardedAd();
                    }
                });
                Log.d(TAG, "Rewarded ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                Log.e(TAG, "Failed to load rewarded ad: " + error.getMessage());
            }
        });
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getAdUnitID(AdType.INTERSTITIAL), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd ad) {
                interstitialAd = ad;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                        loadInterstitialAd();
                    }
                });
                Log.d(TAG, "Interstitial ad loaded.");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                Log.e(TAG, "Failed to load interstitial ad: " + error.getMessage());
            }
        });
    }

    private void showReward() {
        if (rewardedAd != null) {
            rewardedAd.show(this, rewardItem -> {
                // Handle reward
            });
        } else {
            Log.e(TAG, "Rewarded ad is not ready.");
            loadRewardedAd();
        }
    }

    private void showInterstitial() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            Log.e(TAG, "Interstitial ad is not ready.");
            loadInterstitialAd();
        }
    }

    private void registerJsHandlers() {
        webView.registerHandler("requestRewardAd", (data, function) -> {
            showReward();
            function.onCallBack("Response from requestRewardAd");
        });
        webView.registerHandler("requestInitializeAd", (data, function) -> {
            showInterstitial();
            function.onCallBack("Response from requestInitializeAd");
        });
        webView.registerHandler("app_RemoveBG", (data, function) -> {
            // Remove background logic
            function.onCallBack("Response from app_RemoveBG");
        });
        webView.registerHandler("showComment", (data, function) -> {
            requestReview();
            function.onCallBack("Response from showComment");
        });
        webView.registerHandler("app_MoreGames", (data, function) -> {
            showMoreGames(data);
            function.onCallBack("Response from app_MoreGames");
        });
    }

    private void requestReview() {
        // Assuming you have a way to request a review in your app
    }

    private void showMoreGames(String developerId) {
        String url = "https://play.google.com/store/apps/developer?id=" + developerId;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private String getAdUnitID(AdType adType) {
        switch (adType) {
            case BANNER:
                return BuildConfig.DEBUG ? TEST_BANNER_AD_UNIT_ID : REAL_BANNER_AD_UNIT_ID;
            case REWARDED:
                return BuildConfig.DEBUG ? TEST_REWARDED_AD_UNIT_ID : REAL_REWARDED_AD_UNIT_ID;
            case INTERSTITIAL:
                return BuildConfig.DEBUG ? TEST_INTERSTITIAL_AD_UNIT_ID : REAL_INTERSTITIAL_AD_UNIT_ID;
            default:
                return "";
        }
    }

    enum AdType {
        BANNER, REWARDED, INTERSTITIAL
    }
}
