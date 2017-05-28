
package sh.kono.micro;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.youview.tinydnssd.MDNSDiscover;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.zip.Inflater;

import javax.annotation.Nullable;

public class RNZeroconfRegistryModule extends ReactContextBaseJavaModule {
  public static final String TAG = "ZeroconfModule";

  public static final String EVENT_START = "RNZeroconfStart";
  public static final String EVENT_STOP = "RNZeroconfStop";
  public static final String EVENT_ERROR = "RNZeroconfError";
  public static final String EVENT_FOUND = "RNZeroconfFound";
  public static final String EVENT_REMOVE = "RNZeroconfRemove";
  public static final String EVENT_RESOLVE = "RNZeroconfResolved";

  public static final String KEY_SERVICE_NAME = "name";
  public static final String KEY_SERVICE_FULL_NAME = "fullName";
  public static final String KEY_SERVICE_HOST = "host";
  public static final String KEY_SERVICE_PORT = "port";
  public static final String KEY_SERVICE_ADDRESSES = "addresses";
  public static final String KEY_SERVICE_TXT = "txt";
  public static final String KEY_MICRO_SERVICE = "microService";
  public static final String KEY_MICRO_VERSION = "microVersion";

  public static final int RESOLVE_TIMEOUT = 1000; // 0 - Will wait forever
  public static final String DNSSDSERVICES = "_services._dns-sd._udp";

  protected NsdManager mNsdManager;
  protected NsdManager.DiscoveryListener mDiscoveryListener;

  private final ReactApplicationContext reactContext;

  public RNZeroconfRegistryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNZeroconfRegistry";
  }

  @ReactMethod
  public void stop() {
    if (mDiscoveryListener != null) {
      mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }

    mDiscoveryListener = null;
  }

  protected void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable Object params) {
    reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
  }

  @ReactMethod
//    public void scan(String type, String protocol, String domain) {
  public void scan(final String serviceName) {
    if (mNsdManager == null) {
      mNsdManager = (NsdManager) reactContext.getSystemService(Context.NSD_SERVICE);
    }

    this.stop();

    mDiscoveryListener = new NsdManager.DiscoveryListener() {
      @Override
      public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        String error = "Starting service discovery failed with code: " + errorCode;
        Log.e(TAG, error);
        sendEvent(getReactApplicationContext(), EVENT_ERROR, error);
      }

      @Override
      public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        String error = "Stopping service discovery failed with code: " + errorCode;
        Log.e(TAG, error);
        sendEvent(getReactApplicationContext(), EVENT_ERROR, error);
      }

      @Override
      public void onDiscoveryStarted(String serviceType) {
        Log.d(TAG, "Service discovery started");
        sendEvent(getReactApplicationContext(), EVENT_START, null);
      }

      @Override
      public void onDiscoveryStopped(String serviceType) {
        Log.d(TAG, "Service discovery stopped");
        sendEvent(getReactApplicationContext(), EVENT_STOP, null);
      }

      @Override
      public void onServiceFound(NsdServiceInfo si) {
        if (si.getServiceName().equals(serviceName)) {
          WritableMap service = new WritableNativeMap();
          service.putString(KEY_SERVICE_NAME, si.getServiceName());
          sendEvent(getReactApplicationContext(), EVENT_FOUND, service);
          resolve(si);
        }
      }

      @Override
      public void onServiceLost(NsdServiceInfo serviceInfo) {
        // TODO: determine if its a tracked service
        WritableMap service = new WritableNativeMap();
        service.putString(KEY_SERVICE_NAME, serviceInfo.getServiceName());
        sendEvent(getReactApplicationContext(), EVENT_REMOVE, service);
      }
    };

    mNsdManager.discoverServices(DNSSDSERVICES, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
  }

  public void resolve(NsdServiceInfo si) {
    WritableMap service = new WritableNativeMap();

    try {
      String serviceName =  si.getServiceName() + "." + si.getServiceType();

      MDNSDiscover.Result res = MDNSDiscover.resolve(serviceName, RESOLVE_TIMEOUT);
      Log.i(TAG, "Resolved: " + res.toString());
      Log.i(TAG, "... srv = " + res.srv.fqdn);
      Log.i(TAG, "... txt = " +  res.txt.dict.toString());
      Log.i(TAG, "lets decode!");
      ZeroconfTxt ztxt = decodeTxt(res.txt);
      Log.i(TAG, ztxt.toString());

//            if (ztxt == null) {
//                String error = "Failed decoding txt for discovered service";
//                Log.e(TAG, error);
//                sendEvent(getReactApplicationContext(), EVENT_ERROR, error);
//                return;
//            }

      // bringing in old approach, will need to update as multi-service registry is built on the js side
      service.putString(KEY_SERVICE_NAME, si.getServiceName());
      service.putString(KEY_SERVICE_FULL_NAME, res.srv.fqdn);
      service.putString(KEY_SERVICE_HOST, res.srv.target);
      service.putInt(KEY_SERVICE_PORT, res.srv.port);

      WritableMap txt = new WritableNativeMap();
      txt.putString(KEY_MICRO_SERVICE, ztxt.getService());
      txt.putString(KEY_MICRO_VERSION, ztxt.getVersion());

      service.putMap(KEY_SERVICE_TXT, txt);

      WritableArray addresses = new WritableNativeArray();
      addresses.pushString(res.a.ipaddr);

      service.putArray(KEY_SERVICE_ADDRESSES, addresses);

      sendEvent(getReactApplicationContext(), EVENT_RESOLVE, service);
    } catch (IOException e) {
      String error = "Resolving service failed with message: " + e;
      Log.e(TAG, error);
      sendEvent(getReactApplicationContext(), EVENT_ERROR, error);
    }
  }

  protected ZeroconfTxt decodeTxt(MDNSDiscover.TXT txt) {
    Log.i(TAG, "txt keys: " + txt.dict.keySet());
    Log.i(TAG, "txt values: " + txt.dict.values());
    ZeroconfTxt zeroconfTxt = null;

    try {
      ArrayList<String> keys = new ArrayList<String>(txt.dict.keySet());
      Collections.reverse(keys);
      String hex = TextUtils.join("", keys);
      Log.i(TAG, "hex string joined: " + hex);
      byte[] bytes = hexStringToByteArray(hex);
      Inflater decomp = new Inflater();
      decomp.setInput(bytes);
      byte[] result = new byte[1024];
      int len = decomp.inflate(result);
      decomp.end();
      String output = new String(result, 0, len, "UTF-8");
      zeroconfTxt = deserializeTxtJson(output);
    } catch(java.io.UnsupportedEncodingException e) {
      Log.e(TAG, "unsupported encoding while decompressiong: " + e);

    } catch (java.util.zip.DataFormatException e) {
      Log.e(TAG, "dataformatexception while decompressing: " + e);
    }

    return zeroconfTxt;
  }

  protected ZeroconfTxt deserializeTxtJson(String json) {
    Gson gson = new Gson();
    ZeroconfTxt txt = gson.fromJson(json, ZeroconfTxt.class);
    Log.i(TAG, "finished parsing gson, txt is: " + txt);
    if (txt != null) {
      Log.i(TAG, "decoded txt: " + txt.getService() +
              " " + txt.getVersion() + " " + txt.getMetadata());
    }
    return txt;
  }


  protected byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
              + Character.digit(s.charAt(i+1), 16));
    }
    return data;
  }

  @Override
  public void onCatalystInstanceDestroy() {
    super.onCatalystInstanceDestroy();
    stop();
  }
}