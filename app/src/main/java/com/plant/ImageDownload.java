package com.plant;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

/**
 * Created by Kim on 2016-07-26.
 */
public class ImageDownload extends AsyncTask<String, Void, Bitmap> {
    private String fileName;
    private final String SAVE_FOLDER = "/save_folder";
    private Context mContext;

    public ImageDownload(Context context){
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        //다운로드 경로를 지정
        String savePath = Environment.getExternalStorageDirectory().toString() + SAVE_FOLDER;
        File dir = new File(savePath);

        //상위 디렉토리가 존재하지 않을 경우 생성
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //파일 이름은 url
       fileName = params[0];
        fileName =  fileName.replace('/', '_');

        //웹 서버 쪽 파일이 있는 경로
        String fileUrl = params[0];

        //다운로드 폴더에 동일한 파일명이 존재하는지 확인
        if (new File(savePath + "/" + fileName).exists() == false) {

        } else {
            Log.d("ImageDownload", "same file name!");
            return null;
        }

        String localPath = savePath + "/" + fileName;

        try {
            URL imgUrl = new URL(fileUrl);
            //서버와 접속하는 클라이언트 객체 생성
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            int len = conn.getContentLength();
            byte[] tmpByte = new byte[len];
            //입력 스트림을 구한다
            InputStream is = conn.getInputStream();
            File file = new File(localPath);
            //파일 저장 스트림 생성
            FileOutputStream fos = new FileOutputStream(file);
            int read;
            //입력 스트림을 파일로 저장
            for (;;) {
                read = is.read(tmpByte);
                if (read <= 0) {
                    break;
                }
                fos.write(tmpByte, 0, read); //file 생성
            }
            is.close();
            fos.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("ImageDownload", "complete");
        return null;
    }
}
