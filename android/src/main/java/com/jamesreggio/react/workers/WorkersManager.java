package com.jamesreggio.react.workers;

import android.os.Handler;
import android.os.Looper;

import com.facebook.infer.annotation.Assertions;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;

import javax.annotation.Nullable;

public class WorkersManager extends ReactContextBaseJavaModule {

  private final HashMap<Integer, WorkersInstance> workers = new HashMap<>();
  private final ReactApplicationContext context;
  private final ReactPackage packages[];

  public WorkersManager(
    final ReactApplicationContext context,
    final ReactPackage packages[]
  ) {
    super(context);
    this.context = context;
    this.packages = packages;
  }

  /**
   * Public interface
   */

  @Override
  public String getName() {
    return "WorkersManager";
  }

  @ReactMethod
  public void startWorker(
    final Integer key,
    final String bundleRoot,
    final String bundleResource,
    final Integer bundlerPort,
    final Promise promise
  ) {
    Assertions.assertCondition(!this.workers.containsKey(key), "Key already in use");

    final WorkersInstance worker = new WorkersInstance(
      key,
      context,
      this.packages,
      bundleRoot,
      bundleResource,
      bundlerPort,
      promise
    );

    this.workers.put(key, worker);

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        worker.start();
      }
    });
  }

  @ReactMethod
  public void stopWorker(final Integer key) {
    final WorkersInstance worker = this.workers.remove(key);
    Assertions.assertNotNull(worker);

    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        worker.stop();
      }
    });
  }

  @ReactMethod
  public void postMessage(final Integer key, final String message) {
    final WorkersInstance worker = this.workers.get(key);
    Assertions.assertNotNull(worker).postMessage(message);
  }

  /**
   * Events
   */

  private interface RCTDeviceEventEmitter extends JavaScriptModule {
    void emit(String eventName, @Nullable Object data);
  }

  protected void emit(final String name, final Object body) {
    this.context
      .getJSModule(WorkersManager.RCTDeviceEventEmitter.class)
      .emit(name, body);
  }

}