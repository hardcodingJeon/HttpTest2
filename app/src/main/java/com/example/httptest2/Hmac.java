package com.example.httptest2;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Hmac {
    // todo : hash 알고리즘 선택, HMAC SHA256    SHA256 둘다 시도해볼것
    private static final String ALGOLISM = "HmacSHA256";
    // Mac init할때 필요한 secretKey
    private static final String secretKey = "xJaV4Vj5mHcxORxV9kQdEVTfAkO1b8Ne7qIz5Y6c";

    public static String accessKey = "qDUB08P6S1vdjEFvN9lv";

    public static String keyMsg;

    public Hmac(String time) {
        this.keyMsg = "POST TEST\n"+time+"\n"+this.accessKey;
    }

    public String hget() {
        String base = "";
        try {
            // hash 알고리즘과 암호화 key 적용
            Mac hasher = Mac.getInstance(ALGOLISM);

            // secretKey, algolism 넣기
            hasher.init(new SecretKeySpec(secretKey.getBytes(), ALGOLISM));

            // 암호화 문구를 해시화 한후 > base64 인코딩
            // 암호화 문구를 해시화 적용
            byte[] hash = hasher.doFinal(keyMsg.getBytes());

            // ### 방법 2
            // byte[] 을 바로 base64 인코딩한다.
            base = Base64.encodeToString(hash, Base64.NO_WRAP);
            
             
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        catch (InvalidKeyException e){
            e.printStackTrace();
        }
        return base;
    }

    // byte[]의 값을 16진수 형태의 문자로 변환하는 함수
//    private static String byteToString(byte[] hash) {
//        StringBuffer buffer = new StringBuffer();
//
//        for (int i = 0; i < hash.length; i++) {
//            int d = hash[i];
//            d += (d < 0)? 256 : 0;
//            if (d < 16) {
//                buffer.append("0");
//            }
//            buffer.append(Integer.toString(d, 16));
//        }
//        return buffer.toString();
//    }

//    public String encode(String base){
//        byte[] data = null;
//        try {
//            data = base.getBytes("UTF-8");
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return Base64.encodeToString(data, Base64.NO_WRAP);
//    }

}
