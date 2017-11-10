package net.butterflytv.rtmp_client.test;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.butterflytv.rtmp_client.RtmpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class MainActivity extends Activity {

    private TextView tv_text;

    byte[] data;
    String outFilepath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mtv.h264";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_text = (TextView)findViewById(R.id.tv_text);
        tv_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RtmpClient rtmpClient = new RtmpClient();
                int time = 0;
                try {
                    rtmpClient.open("rtmp://192.168.3.78/live/stream",true);
                    boolean flag = rtmpClient.isConnected();
                    if (flag){
                        Toast.makeText(MainActivity.this,"连接上",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this,"未连接上",Toast.LENGTH_SHORT).show();
                    }
                    data = getBytes(outFilepath);
                    int total = data.length;
                    int current = 0;
                    while((total-current) > 0){
                        byte[] bs = null;
                        if (total-current > 1024){
                            bs = new byte[1024];
                        } else {
                            bs = new byte[total-current];
                        }
                        System.arraycopy(data, current, bs, 0, bs.length);
                        int size = rtmpClient.write(bs,bs.length,time);
                        if (total-current > 1024){
                            current += 1024;
                        } else {
                            current += total-current;
                        }
//                        time += 40;
                        Log.e("RTMP","大小为："+current);
                    }
                    rtmpClient.close();
                } catch (RtmpClient.RtmpIOException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
//                _rtmpUrl = "rtmp://192.168.3.78/live/stream";
//                Start(_rtmpUrl);
            }
        });
    }

//    /**
//     * 适配Android6.0动态权限申请
//     */
//    private void initPermission() {
//        final int REQUEST_EXTERNAL_STORAGE = 1;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
//                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
//                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
//                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
//                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
//                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
//                            Manifest.permission.READ_PHONE_STATE,
//                    }
//                    , REQUEST_EXTERNAL_STORAGE);
//        }
//    }

    /**
     * 获得指定文件的byte数组
     */
    private byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
