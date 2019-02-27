package org.pixelexperience.weather.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

class Utils {

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    @SuppressLint("PrivateApi")
    private static String getSystemProperty(String key) {
        String value;

        try {
            value = (String) Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class).invoke(null, key);
            return (value == null || value.isEmpty()) ? "" : value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    static Boolean isBuildValid(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm != null && !pm.hasSystemFeature("org.pixelexperience.weather.client.SUPPORTED")) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.version").isEmpty()) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.build_date").isEmpty()) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.build_type").isEmpty()) {
            return false;
        }
        if (getSystemProperty("org.pixelexperience.fingerprint").isEmpty()) {
            return false;
        }
        return !getSystemProperty("org.pixelexperience.device").isEmpty();
    }

    public static class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (originalRequest.body() == null || originalRequest.header("Content-Encoding") != null) {
                return chain.proceed(originalRequest);
            }

            Request compressedRequest = originalRequest.newBuilder()
                    .header("Content-Encoding", "gzip")
                    .method(originalRequest.method(), gzip(originalRequest.body()))
                    .build();
            return chain.proceed(compressedRequest);
        }

        private RequestBody gzip(final RequestBody body) {
            return new RequestBody() {
                @Override
                public MediaType contentType() {
                    return body.contentType();
                }

                @Override
                public long contentLength() {
                    return -1; // We don't know the compressed length in advance!
                }

                @Override
                public void writeTo(BufferedSink sink) throws IOException {
                    BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                    body.writeTo(gzipSink);
                    gzipSink.close();
                }
            };
        }
    }
}
