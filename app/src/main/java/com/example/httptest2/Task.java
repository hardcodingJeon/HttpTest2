package com.example.httptest2;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Task extends AsyncTask<String, Integer, String> {

    public String str, receiveMsg;
    public StringBuffer buffer;

    protected String doInBackground(String... urls) {
        int count = urls.length;
//        for (int i = 0; i < count; i++) {
//
//            if (isCancelled()) break;
//        }
        URL url = null;
        try {
            url = new URL("http://bnc-iot.com:33333/app/DALONG");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestMethod("POST");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            HashMap<String, String> map = new HashMap<>();
            map.put("req_msg", "1");

            StringBuffer sbParams = new StringBuffer();


            boolean isAnd = false;

            for (String key : map.keySet()) {

                if (isAnd)
                    sbParams.append("&");
                    sbParams.append(key).append("=").append(map.get(key));

                if (!isAnd)
                    if (map.size() >= 2)
                        isAnd = true;
            }


            wr.write(sbParams.toString());
            wr.flush();
            wr.close();

            if (conn.getResponseCode() == conn.HTTP_OK) {   //conn.HTTP_OK 는 상수 200
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);

                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return receiveMsg;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(String result) {
        Gson gson = new Gson();
        VO vo = gson.fromJson(result, VO.class);
        ArrayList<VO.member> items = vo.device;

        for (VO.member e : items){
            buffer.append(e.device_id+"\n"+
                          e.last_latitude+"\n"+
                          e.last_longitude+"\n"+
                          e.last_device_battery+"\n"+
                          e.last_bike_battery+"\n"+
                          e.bike_error_code);
        }

    }
}