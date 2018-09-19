package com.termux.un7zip;

/**
 * Created by huzongyao on 17-11-24.
 */

public abstract class ExtractCallback implements IExtractCallback {

    @Override
    public void onStart() {
    }

    @Override
    public void onGetFileNum(int fileNum) {
    }

    @Override
    public void onSucceed() {
    }
}
