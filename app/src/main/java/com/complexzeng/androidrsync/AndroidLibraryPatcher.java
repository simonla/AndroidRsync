package com.complexzeng.androidrsync;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class AndroidLibraryPatcher {
    private static final String TAG = "AndroidLibraryPatcher";
    private static RsyncStatusListener sListener;

    public interface RsyncStatusListener {
        void onRsyncStop();
    }

    public static void addListener(RsyncStatusListener listener) {
        sListener = listener;
    }

    static void start(Context ctx) {
        Thread thread = new Thread(() -> startInternal(ctx));
        thread.start();
    }

    private static void startInternal(Context ctx) {
        String libraryPath = ctx.getApplicationInfo().nativeLibraryDir;

        File libraryDir = new File(libraryPath);
        boolean canWrite = libraryDir.canWrite();
        boolean canRead = libraryDir.canRead();
        boolean canExecute = libraryDir.canExecute();
        Log.d(TAG, "libraryDir: " + libraryDir.getAbsolutePath() + ", canWrite: " + canWrite + ", canRead: " + canRead + ", canExecute: " + canExecute);

        File rsyncOutputPath = ctx.getCacheDir();
        boolean rsyncOutputPathCanWrite = rsyncOutputPath.canWrite();
        boolean rsyncOutputPathCanRead = rsyncOutputPath.canRead();
        boolean rsyncOutputPathCanExecute = rsyncOutputPath.canExecute();
        Log.d(TAG, "rsyncOutputPath: " + rsyncOutputPath.getAbsolutePath() + ", canWrite: " + rsyncOutputPathCanWrite + ", canRead: " + rsyncOutputPathCanRead + ", canExecute: " + rsyncOutputPathCanExecute);

        File configFile = new File(rsyncOutputPath, "rsyncd.conf");

        try {
            boolean success = configFile.createNewFile();
            Log.d(TAG, "create rsyncd.conf res= " + success);
            String config = "address = 0.0.0.0\n" +
                    "port = 1873\n" +
                    "[root]\n" +
                    "path = /\n" +
                    "use chroot = false\n" +
                    "read only = false\n";
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(config);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File rsyncExec = new File(libraryPath, "librsync.so");
        Runtime rt = Runtime.getRuntime();
        try {
            File logFile = new File(rsyncOutputPath, "rsync.log");
            Process process = rt.exec(rsyncExec.getAbsolutePath() + " -v --daemon --no-detach --config=" + configFile.getAbsolutePath() + " --log-file=" + logFile.getAbsolutePath());
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = outputReader.readLine()) != null) {
                Log.d(TAG, line);
            }
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                Log.e(TAG, line);
            }
            int exitCode = process.waitFor();
            sListener.onRsyncStop();
            Log.d(TAG, "exitCode: " + exitCode);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
