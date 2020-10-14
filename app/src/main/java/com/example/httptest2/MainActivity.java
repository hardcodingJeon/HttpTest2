package com.example.httptest2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tv,tv_timer;
    String resultText;
    int num = 0;

    public String str, receiveMsg;
    public StringBuffer buffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Http통신, asyncTask");
        tv = findViewById(R.id.textView);
        tv_timer = findViewById(R.id.textView2);

        resultText = "데이터 없음";

        // asyncTask가 백그라운드에서 별도로 작동되는지 확인 하기 위해 메인스레드는 1~100까지 숫자가 증가되는 동작을 시키고 http응답을 백그라운드(doInBackground메소드)에서 작동시키고 결과값을 받는지 확인
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=0;i<100;i++){
                    num++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_timer.setText(num+"");
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    public void clickBtn(View view) {
        try {
            final Task task = new Task();
            task.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class Task extends AsyncTask<String, Integer, String> {
    // AsyncTask를 상속받은 커스텀 Task클래스


        protected String doInBackground(String... urls) {
            //doInBackground() 비동기 스레드 (백그라운드에서 작업하는 메소드)
            URL url = null;
            try {

                url = new URL("https://bnc-iot.com:33333/app/DALONG");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("content-type", "application/json; charset=utf-8"); // charset 추가해서 에러 고쳤음, 원래는 utf=8만 썼었음
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // POST방식으로 json 데이터를 전송하고 서버로부터 InputStreamReader로 json데이터 값을 응답받는다.
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                String query = "{\"req_msg\":1}";   // {"req_msg":1}

                wr.write(query,0,query.length());
                wr.flush();
                wr.close();

                // request(요청)를 ouputstream으로 한후 responsecode가 200으로 정상 리스폰 되었다면
                if (conn.getResponseCode() == conn.HTTP_OK) {   //conn.HTTP_OK 는 상수 200
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);    // http응답이 reader에 담겨 있다.
                    StringBuffer buffer_in = new StringBuffer();

                    while ((str = reader.readLine()) != null) {
                        buffer_in.append(str);
                    }

                    receiveMsg = buffer_in.toString();

                    reader.close();

                    // 받은 json형식의 문자열중 마지막에 SUCCESS를 삭제후 GSON으로 파싱할것, 마지막 SUCCESS 문자때문에 Json형식이 깨져서 파싱이 불가하기 때문 , .replace()후 리턴받아야함(원본값이 바뀌는것이 아님)
                    receiveMsg = receiveMsg.replace("SUCCESS","");

                    // Internal storage 저장(데이터가 갱신되는게 아니라 누적됨), 경로 : data - data - 해당패키지명 - files
//                    FileOutputStream fos = openFileOutput("data.txt",MODE_APPEND);  //MODE_APPEND 이어쓰기 , MODE_PRIVATE 덮어쓰기
//                    // APK 생성되는 폴더에 데이터가 저장된다,앱을 지우지 않는 이상 이 파일은 저장되어 있다, 프로그램 꺼도 저장되있음 , 앱을 지우면 그 데이터도 같이 날아간다
//                    //fos.write("aaa"); //write는 byte단위라 문자열 전송 불가
//                    PrintWriter writer = new PrintWriter(fos);  //바이트스트림을 문자열스트림으로 바꾸다, java수업때 썼던 stream구조 확인해라
//                    writer.println(receiveMsg);   //writer의 기능중 문자열로 전송할수 있는 print()메소드 사용한다
//                    //writer.print(data);   //println()이 아닌 print()메소드 쓰면 줄 바꿈 발생하지 않는다.
//                    writer.flush(); //병목현상 생길수 있기 때문에 밀어넣는 기능
//                    writer.close(); //스트림 전송하고 나면 닫아 줘야함


                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            }  catch (IOException e) {
                Log.e("error","아웃풋스트림 에러 : "+e.toString());
            }

            if (receiveMsg!=null){

//                Gson gson = new Gson();
                Gson gson = new GsonBuilder().setLenient().create();
                VO vo = gson.fromJson(receiveMsg, VO.class);
                ArrayList<VO.member> items = vo.device;

                buffer = new StringBuffer();

                for (int i=0 ; i<items.size() ; i++){
                    buffer.append("device_id_"+i+" : "+items.get(i).device_id+"\n"+
                            "last_latitude_"+i+" : "+items.get(i).last_latitude+"\n"+
                            "last_longitude_"+i+" : "+items.get(i).last_longitude+"\n"+
                            "last_device_battery_"+i+" : "+items.get(i).last_device_battery+"\n"+
                            "last_bike_battery_"+i+" : "+items.get(i).last_bike_battery+"\n"+
                            "bike_error_code_"+i+" : "+items.get(i).bike_error_code+"\n\n");
                }

            }

            return buffer.toString();
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(String result) {

            //doInBackground() 메소드의 리턴값이 onPostExecute 메소드의 파라미터로 받아진다, onPostExecute에서 화면 갱신(UI스레드)작업 해주자.
            tv.setText(result);

        }
    }
}