package com.termux.home.assistant;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.termux.R;
import com.termux.app.TermuxActivity;
import com.termux.terminal.TerminalSession;
import com.termux.un7zip.ExtractCallback;
import com.termux.un7zip.Z7Extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.flowable.FlowableInternalHelper;
import io.reactivex.schedulers.Schedulers;

public class FirstInitHomeAssistant {
    private static final String TAG = FirstInitHomeAssistant.class.getSimpleName();

    private TermuxActivity activity;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public FirstInitHomeAssistant(TermuxActivity termuxActivity) {
        this.activity = termuxActivity;
    }

    public void startHomeAssistant() {
        if (!Preference.hasInstallService()) return;
        int retry = 0;
        while (activity.getCurrentTermSession() == null && retry++ < 10) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!Preference.hasRunChmod()) {
            runChmod();
            Preference.saveRunChmod(activity, true);
        }
        if (!checkHassRun()) {
            if (activity.getCurrentTermSession() == null) {
                activity.addNewSession(false, "HomeAssistant");
                mainHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runHass();
                        Preference.saveHassRun(activity, true);
                    }
                }, 100);
            }
        }
    }

    private void runChmod() {
        runCmd(R.raw.runchmod);
    }

    private void runHass() {
        runCmd("hass");
    }

    private void runCmd(int resource) {
        TerminalSession terminalSession = activity.getCurrentTermSession();
        if (terminalSession != null && terminalSession.isRunning()) {
            InputStream inputStream = activity.getResources().openRawResource(resource);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String cmd;
                while ((cmd = reader.readLine()) != null) {
                    if (!cmd.startsWith("#") && !TextUtils.isEmpty(cmd)) {
                        Log.e("HLA", "cmd:" + cmd);
                        terminalSession.write(cmd + "\n\r");
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void runCmd(String cmd) {
        TerminalSession terminalSession = activity.getCurrentTermSession();
        if (terminalSession != null && terminalSession.isRunning()) {
            if (!cmd.startsWith("#") && !TextUtils.isEmpty(cmd)) {
                Log.e("HLA", "cmd:" + cmd);
                terminalSession.write(cmd + "\n\r");
            }
        }
    }

    public void startInstallService() {
        activity.runOnUiThread(new Runnable() {
            ProgressDialog mProgressDialog;

            @Override
            public void run() {
                mProgressDialog = new ProgressDialog(activity);
                mProgressDialog.setTitle(R.string.input_service_title);
                final File outFile = activity.getFilesDir();
                mProgressDialog.show();
                Flowable.create(new FlowableOnSubscribe<MsgInfo>() {
                    @Override
                    public void subscribe(final FlowableEmitter<MsgInfo> e) throws Exception {
                        Z7Extractor.extractAsset(activity.getAssets(), "files.7z"/*TestAsset.7z*/, outFile.getPath(), new ExtractCallback() {
                            @Override
                            public void onProgress(String name, long size) {
                                e.onNext(new MsgInfo(1, "name:" + name + " size: " + size));
                            }

                            @Override
                            public void onError(int errorCode, String message) {
                                e.onNext(new MsgInfo(2, message));
                            }
                        });
                        e.onNext(new MsgInfo(3, "Complete"));
                        e.onComplete();
                    }
                }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MsgInfo>() {
                        @Override
                        public void accept(MsgInfo msgInfo) throws Exception {
                            switch (msgInfo.type) {
                                case 1:
                                    Log.e(TAG, "1-" + msgInfo.msg);
                                    mProgressDialog.setMessage(msgInfo.msg);
                                    break;
                                case 2:
                                    Log.e(TAG, "2-" + msgInfo.msg);
                                    Toast.makeText(activity, msgInfo.msg, Toast.LENGTH_SHORT).show();
                                    break;
                                case 3:
                                    Log.e(TAG, "3-" + msgInfo.msg);
                                    Toast.makeText(activity, msgInfo.msg, Toast.LENGTH_SHORT).show();

                                    break;
                            }
                        }
                    }, Functions.ERROR_CONSUMER, new Action() {
                        @Override
                        public void run() throws Exception {

                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Preference.saveInstallService(activity, true);
                                    startHomeAssistant();
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }, FlowableInternalHelper.RequestMax.INSTANCE);
            }
        });
    }

    public static void onApplicationFinish(BaseApplication baseApplication) {
        Preference.saveHassRun(baseApplication, false);
    }

    public interface HassRun {
        void result(boolean result);
    }

    public boolean checkHassRun() {
        return (activity.getCurrentTermSession() != null &&
            activity.getCurrentTermSession().isRunning()
            && Preference.isHassRunning());
    }

}
