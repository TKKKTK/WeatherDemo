package com.wg.weatherdemo.util;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class PollingExample {
    private static final String TAG = "PollingExample";
    private HttpClient httpClient;
    private Disposable disposable;

    public PollingExample() {
        httpClient = new HttpClient();
    }

    public void startPolling(String url, long intervalInSeconds) {
        disposable = Observable.interval(0,intervalInSeconds, TimeUnit.SECONDS)
                .flatMap(tick -> Observable.fromCallable(() -> httpClient.makeRequest(url)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            // 处理响应
                            Log.d(TAG, "startPolling: response ==> " + response.toString());
                        },
                        throwable -> {
                            // 处理错误
                            Log.d(TAG, "startPolling: Error==>" + throwable.getMessage());
                        }
                );

    }

    public void startPolling(String url1, String url2, long intervalInSeconds) {
        disposable = Observable.interval(0, intervalInSeconds, TimeUnit.SECONDS)
                .flatMap(tick -> Observable.fromCallable(() -> httpClient.makeRequest(url1)))
                .flatMap(response1 -> {
                    // 处理第一个请求的响应，并使用其结果进行第二个请求
                    String modifiedUrl2 = modifyUrl(url2, response1); // 根据第一个请求的响应修改第二个请求的 URL
                    return Observable.fromCallable(() -> httpClient.makeRequest(modifiedUrl2));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response2 -> {
                            // 处理第二个请求的响应
                            System.out.println("Response: " + response2);
                        },
                        throwable -> {
                            // 处理错误
                            System.err.println("Error: " + throwable.getMessage());
                        }
                );
    }

    private String modifyUrl(String url, String response1) {
        // 根据第一个请求的响应修改第二个请求的 URL
        // 这里可以解析 response1 并生成新的 URL
        // 这是一个示例实现，可以根据具体需求修改
        return url + "?param=" + response1;
    }

    public void stopPolling() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
