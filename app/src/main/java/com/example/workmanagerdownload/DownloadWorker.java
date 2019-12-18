package com.example.workmanagerdownload;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadWorker extends Worker {
    private LiveDataHelper liveDataHelper;


    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        liveDataHelper = LiveDataHelper.getInstance();
    }

    @NonNull
    @Override
    public Result doWork() {
        try{
            File outputFile = new File(Environment.getExternalStorageDirectory(), "myVideo.mp4");
            URL url = new URL("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            int fileLength = urlConnection.getContentLength();
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int len1;
            long total = 0;
            while ((len1 = inputStream.read(buffer))>0){
                total += len1;
                int percentage = (int) ((total * 100)/ fileLength);
                liveDataHelper.updateDownloadPer(percentage);
                fos.write(buffer, 0, len1);
            }
            fos.close();
            inputStream.close();
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();
    }
}