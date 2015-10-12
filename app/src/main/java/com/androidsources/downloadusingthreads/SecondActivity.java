package com.androidsources.downloadusingthreads;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Gowtham Chandrasekar on 12-10-2015.
 */
public class SecondActivity extends AppCompatActivity implements OnClickListener {

    String url="http://www.androidsources.com/wp-content/uploads/2015/09/android-flashlight-app-tutorial.png";
    RelativeLayout loadingSection;
    int count = 0;
    int execution = 0;
    Thread myThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        Button downloadButton = (Button) findViewById(R.id.download_image);
        loadingSection = (RelativeLayout) findViewById(R.id.progressLayout);
        downloadButton.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        myThread.interrupt();
    }

    public boolean downloadImagesUsingThreads(String url) {
        Boolean downloadStatus = false;
        File file = null;
        URL downloadUrl = null;
        HttpURLConnection connection = null;
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            downloadUrl = new URL(url);
            connection = (HttpURLConnection) downloadUrl.openConnection();
            inputStream = connection.getInputStream();
            int read = -1;
            double contentLength;
            file = new File((Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() +
                    "/" + Uri.parse(url).getLastPathSegment()));
            fileOutputStream = new FileOutputStream(file);
            contentLength=connection.getContentLength();
            byte[] buffer = new byte[1024];
            execution = execution + 1;
            while ((read = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, read);
            }
            downloadStatus = true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingSection.setVisibility(View.GONE);
                }
            });
        }
        return downloadStatus;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_image:
                myThread = new Thread(new DownloadImagesThread(url));
                myThread.start();
                break;
        }

    }

    private class DownloadImagesThread implements Runnable {
        String url;


        DownloadImagesThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            SecondActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    loadingSection.setVisibility(View.VISIBLE);
                }
            });
            count = count + 1;

            Log.d("countertime", "" + count);
            downloadImagesUsingThreads(url);

        }
    }
}
