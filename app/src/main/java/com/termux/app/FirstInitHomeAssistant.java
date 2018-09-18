package com.termux.app;

import android.text.TextUtils;
import android.util.Log;

import com.termux.R;
import com.termux.terminal.TerminalSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FirstInitHomeAssistant {
    TermuxActivity activity;

    public FirstInitHomeAssistant(TermuxActivity termuxActivity) {
        this.activity = termuxActivity;
    }

    public void installHomeAssistant() {
        int retry = 0;
        while (activity.getCurrentTermSession() == null && retry++ < 10) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        TerminalSession terminalSession = activity.getCurrentTermSession();
        if (terminalSession != null && terminalSession.isRunning()) {
            InputStream inputStream = activity.getResources().openRawResource(R.raw.installhomeassistant);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String cmd;
                while ((cmd = reader.readLine()) != null) {
                    if (!cmd.startsWith("#")&& !TextUtils.isEmpty(cmd)) {
                        Log.e("HLA","cmd:"+cmd);
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
    public interface HassRun{
        void result(boolean result);
    }
    public void checkHassRun(HassRun hassRun){
        activity.getCurrentTermSession().isRunning();
    }
}
