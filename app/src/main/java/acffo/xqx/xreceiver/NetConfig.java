/**
 * Created by Admin on 2016/6/28.
 */
package acffo.xqx.xreceiver;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetConfig {


    public static RetrofitAPI retrofitAPI;
    public static String url;

    public static RetrofitAPI Retrofit() {
        if (retrofitAPI == null) {
            retrofitAPI = new Retrofit.Builder()
//                    .baseUrl("https://api.leancloud.cn/1.1/")
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(genericClient())
                    .build()
                    .create(RetrofitAPI.class);
        }
        return retrofitAPI;
    }

    public static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("User-Agent", "Magikare/PatientTraining/20180302")
                                .build();
                        return chain.proceed(request);
                    }
                })
                .build();

        return httpClient;
    }

}
