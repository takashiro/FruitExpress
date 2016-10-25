package cn.weifruit.fruitexp.util;

import android.webkit.CookieManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpClient {

    private String serverUrl;
    private String cookie;

    public HttpClient(String serverUrl){
        this.serverUrl = serverUrl;
        this.cookie = CookieManager.getInstance().getCookie(serverUrl);
    }

    public HttpClient(String serverUrl, String cookie){
        this.serverUrl = serverUrl;
        this.cookie = cookie;
    }

    void setServerUrl(String serverUrl){
        this.serverUrl = serverUrl;
    }

    String getServerUrl(){
        return serverUrl;
    }

    InputStream post(String api, String data){
        URL url;
        try {
            url = new URL(serverUrl + api);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        HttpURLConnection link;
        try {
            link = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        link.setRequestProperty("User-Agent", "NativeApp");
        link.setRequestProperty("Cookie", cookie);

        link.setDoOutput(true);

        try {
            link.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        OutputStream output;
        try {
            output = link.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        byte[] bytes;
        try {
            bytes = data.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        try {
            output.write(bytes);
            return link.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    InputStream get(String api){
        URL url = null;
        try {
            url = new URL(serverUrl + api);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        HttpURLConnection link = null;
        try {
            link = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        link.setRequestProperty("User-Agent", "NativeApp");
        link.setRequestProperty("Cookie", cookie);

        try {
            link.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try {
            return link.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
