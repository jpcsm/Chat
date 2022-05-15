//package com.test.js.chat;
//
//import java.io.IOException;
//
//import okhttp3.Request;
//import okhttp3.Response;
//
///**
// * Created by lenovo on 2017-07-27.
// */
//
//public class okhttp3 {
//    okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
//
//    String run(String url) throws IOException {
//        Request request = new okhttp3.Request.Builder()
//                .url(url)
//                .build();
//
//        Response response = client.newCall(request).execute();
//        return response.body().string();
//    }
//
//    public static void main(String[] args) throws IOException {
//        okhttp3 example = new okhttp3();
//        String response = example.run("https://raw.github.com/square/okhttp/master/README.md");
//        System.out.println(response);
//    }
//}
