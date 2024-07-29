package com.fengyuegames.packager;

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
    private boolean isMobileAdsStartCalled = false;
    private String url = "file:///android_asset/02/index.html";

    public static final String realBannerAdUnitID = "ca-app-pub-3940256099942544/9214589741";
    public static final String realRewardedAdUnitID = "ca-app-pub-3940256099942544/5224354917";
    public static final String realInterstitialAdUnitID = "ca-app-pub-3940256099942544/1033173712";
    public static final String testBannerAdUnitID = "ca-app-pub-3940256099942544/9214589741";
    public static final String testRewardedAdUnitID = "ca-app-pub-3940256099942544/5224354917";
    public static final String testInterstitialAdUnitID = "ca-app-pub-3940256099942544/1033173712";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 设置启动时的背景为透明
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupWebView();
        setupBannerAdView();
        startGoogleMobileAdsSDK();
    }

    private void setupWebView() {
        webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(url);
        setupBannerAdView();
        webView.setDefaultHandler((data, function) -> {
            // Default handler for JS calls
        });

        // 注册 JavaScript 调用的 handler
        webView.registerHandler("requestRewardAd", (data, function) -> {
            showReward();
            function.onCallBack("Response from ads_HasRewardedVideo");
        });
        webView.registerHandler("requestInitializeAd", (data, function) -> {
            showInterstitial();
            function.onCallBack("Response from ads_ShowInterstitial");
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

    private void setupBannerAdView() {

        FrameLayout adContainer = findViewById(R.id.ad_container);
        // 动态创建 bannerAdView
        bannerAdView = new AdView(this);
        bannerAdView.setAdSize(AdSize.BANNER);  // 设置广告尺寸为 BANNER，或者你需要的尺寸
        bannerAdView.setAdUnitId(getAdUnitID(AdType.BANNER));  // 设置广告单元 ID
        // 设置 bannerAdView 布局参数
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, // 宽度填满父容器
                FrameLayout.LayoutParams.WRAP_CONTENT // 高度自适应内容
        );
        bannerAdView.setLayoutParams(layoutParams);
        // 将 bannerAdView 添加到容器中
        adContainer.addView(bannerAdView);
//        loadBannerAd();
    }

    private void startGoogleMobileAdsSDK() {
        if (!isMobileAdsStartCalled) {
            isMobileAdsStartCalled = true;
            MobileAds.initialize(this, initializationStatus -> {});
            loadRewardedAd();
            loadInterstitialAd();
            loadBannerAd();
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
                return BuildConfig.DEBUG ? testBannerAdUnitID : realBannerAdUnitID;
            case REWARDED:
                return BuildConfig.DEBUG ? testRewardedAdUnitID : realRewardedAdUnitID;
            case INTERSTITIAL:
                return BuildConfig.DEBUG ? testInterstitialAdUnitID : realInterstitialAdUnitID;
            default:
                return "";
        }
    }

    enum AdType {
        BANNER, REWARDED, INTERSTITIAL
    }
}