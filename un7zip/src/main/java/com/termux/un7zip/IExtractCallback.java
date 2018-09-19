package com.termux.un7zip;

/**
 * Created by huzongyao on 17-11-24.
 */

public interface IExtractCallback {
    void onStart();

    void onGetFileNum(int fileNum);

    void onProgress(String name, long size);

    void onError(int errorCode, String message);

    void onSucceed();
}
