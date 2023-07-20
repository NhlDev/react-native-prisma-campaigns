package com.prismacampaigns;

import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import com.prismacampaigns.sdk.Client;
import com.prismacampaigns.sdk.Funnel;
import com.prismacampaigns.sdk.HtmlBannerView;
import com.prismacampaigns.sdk.PlaceholderContent;
import com.prismacampaigns.sdk.ResponseHandler;
import com.prismacampaigns.sdk.Tracker;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import us.bpsm.edn.Keyword;

@ReactModule(name = PrismaCampaignsModule.NAME)
public class PrismaCampaignsModule extends ReactContextBaseJavaModule {

  public static final String NAME = "PrismaCampaigns";

  private int mRetryCount = 3;

  public static final String REACT_CLASS = "PrismaModule";
  private final Keyword K_Placeholders = Keyword.newKeyword("placeholders");
  private final Keyword K_BannersList = Keyword.newKeyword("banners-list");
  private final Keyword K_Banner = Keyword.newKeyword("banner");
  private final Keyword K_Funnel = Keyword.newKeyword("funnel");
  private final Keyword K_Link = Keyword.newKeyword("link");
  private final Keyword K_HTML_Content = Keyword.newKeyword("html-content");
  private final Keyword K_IsPopup = Keyword.newKeyword("is-popup");
  private final Keyword K_PopupTimeout = Keyword.newKeyword("popup-timeout");
  private final Keyword K_PopupShowClose = Keyword.newKeyword(
    "popup-show-close"
  );
  private final Keyword K_TrackingToken = Keyword.newKeyword("tracking-token");

  public PrismaCampaignsModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  @ReactMethod
  public void Load(
    String Server,
    String Port,
    String AppToken,
    String CustomerId
  ) {
    Client.load(
      getCurrentActivity().getApplication(),
      Server,
      Port,
      AppToken,
      CustomerId,
      "https"
    );
  }

  @ReactMethod
  public void syncPage(String placeholderName, Promise promise) {
    List<String> placeholderList = Arrays.asList(placeholderName);

    if (Client.shared != null && Client.shared.getTracker().initialized()) {
      Client.shared.syncPageAPI(
        placeholderList,
        "",
        true,
        new ResponseHandler() {
          @Override
          public void onSuccess(Object response) {
            Log.i("PrismaBridge", "Sync successful!");

            WritableNativeMap returnData = new WritableNativeMap();

            Map<Keyword, Object> r = (Map) response;
            List<Map<Keyword, Object>> phResponses = (List) r.get(
              K_Placeholders
            );
            Iterator<Map<Keyword, Object>> iter = phResponses.iterator();

            while (iter.hasNext()) {
              long campaign = 0L;
              UUID trackingToken = new UUID(0, 0);

              Map<Keyword, Object> phConfig = (Map) iter.next();
              final PlaceholderContent phContent = new PlaceholderContent(
                Client.shared,
                phConfig
              );
              Log.i(
                "PrismaBridge",
                String.format(
                  "%d banners for placeholder %s found",
                  phContent.banners.size(),
                  placeholderName
                )
              );
              List<Map<Keyword, Object>> bannerList = (List) phConfig.get(
                K_BannersList
              );
              boolean isPopup = (boolean) phConfig.get(K_IsPopup);

              long popUpTimeout = 0;
              if (phConfig.containsKey(K_PopupTimeout)) {
                Object objPopupTimeout = phConfig.get(K_PopupTimeout);

                if (objPopupTimeout != null) {
                  popUpTimeout = ((Long) objPopupTimeout).intValue();
                }
              }

              boolean popUpShowClose = false;
              if (phConfig.containsKey(K_PopupShowClose)) {
                Object objPopUpShowClose = phConfig.get(K_PopupShowClose);

                if (objPopUpShowClose != null) {
                  popUpShowClose = (boolean) objPopUpShowClose;
                }
              }

              Map<Keyword, Object> bannerDef = bannerList.get(0);

              Map<Keyword, Object> bannerData = (Map) bannerDef.get(K_Banner);
              Map<Keyword, Object> funnelData = (Map) bannerDef.get(K_Funnel);

              long width = (Long) bannerData.get(Keyword.newKeyword("width"));
              long height = (Long) bannerData.get(Keyword.newKeyword("height"));

              Funnel funnel = new Funnel(
                Client.shared,
                funnelData,
                campaign,
                Client.shared.getTracker().trail(),
                trackingToken.toString().toLowerCase()
              );

              if (bannerData.containsKey(K_TrackingToken)) {
                trackingToken = (UUID) bannerData.get(K_TrackingToken);
              }

              // banner tipo imagen
              if (bannerData.containsKey(K_Link)) {
                returnData.putBoolean("IsHtml", false);

                String URLContent = (String) bannerData.get(K_Link);
                String url = String.format(
                  "%s%s",
                  Client.shared.getBaseURL(),
                  URLContent
                );

                returnData.putString("Content", url);
              }
              // banner tipo HTML
              else if (bannerData.containsKey(K_HTML_Content)) {
                returnData.putBoolean("IsHtml", true);

                final String htmlContent = (String) bannerData.get(
                  K_HTML_Content
                );
                HtmlBannerView banner = new HtmlBannerView(
                  htmlContent,
                  width,
                  height,
                  funnel,
                  "",
                  ""
                );

                returnData.putString(
                  "Content",
                  banner
                    .getHTMLContent()
                    .replace(
                      "prisma.startFunnel.call(this)",
                      "window.ReactNativeWebView.postMessage(\"StartFunnel\")"
                    )
                    .replace(
                      "prisma.dismissCampaign.call(this)",
                      "window.ReactNativeWebView.postMessage(\"DismissFunnel\")"
                    )
                    .replace(
                      "prisma.closePopup.call(this)",
                      "window.ReactNativeWebView.postMessage(\"CloseFunnel\")"
                    )
                );
              }

              returnData.putBoolean("IsPopup", isPopup);
              returnData.putInt("PopUpTimeout", (int) popUpTimeout);
              returnData.putBoolean("PopUpShowClose", popUpShowClose);
              returnData.putString("CampaignLink", funnel.getLandingUrl());
              returnData.putString("TrackingToken", trackingToken.toString());
            }
            promise.resolve(returnData);
            mRetryCount = 3;
          }

          @Override
          public void onError(Object o) {
            retry(placeholderName, promise);
          }
        }
      );
    } else {
      Log.w("PrismaBridge", "Client is not initialized");
      retry(placeholderName, promise);
    }
  }

  @ReactMethod
  public void Dismiss(String trackingToken) {
    Tracker t = Client.shared.getTracker();
    Map<String, String> trackData = new HashMap();
    trackData.put("tracking-token", trackingToken);
    t.track("dismiss", "dismiss", trackData);
    t.reset();
  }

  private void retry(String placeholderName, Promise promise) {
    if (this.mRetryCount > 0) {
      mRetryCount -= 1;
      Log.w("PrismaBridge", "retrying in 3 seconds");
      Timer t = new Timer();

      t.schedule(
        new TimerTask() {
          @Override
          public void run() {
            syncPage(placeholderName, promise);
          }
        },
        3000l
      );
    } else {
      Log.e("PrismaBridge", "Sync retries failed.");
      promise.reject("PrismaBridge", "Client not initialized");
    }
  }
}
