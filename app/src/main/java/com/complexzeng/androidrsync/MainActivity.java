package com.complexzeng.androidrsync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.FileObserver;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private TextView consoleTextView;
    private RsyncFileObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        observer
                = new RsyncFileObserver(getCacheDir().getAbsolutePath());
        observer.startWatching();
        AndroidLibraryPatcher.start(getApplicationContext());
        consoleTextView = findViewById(R.id.console);
        Button button = findViewById(R.id.clear_button);
        button.setOnClickListener(v -> clearLog());
    }

    private void clearLog() {
        consoleTextView.setText("File Watcher Console:\n");
    }

    class RsyncFileObserver extends FileObserver {


        public RsyncFileObserver(String path) {
            super(path);
        }

        @Override
        public void onEvent(int event, @Nullable String path) {
            consoleTextView.post(() -> {
                if (consoleTextView.length() > 1024) {
                    clearLog();
                }
                consoleTextView.append("event=" + event + ", path=" + path + "\n");
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        observer.stopWatching();
    }
}