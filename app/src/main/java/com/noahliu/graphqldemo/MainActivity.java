package com.noahliu.graphqldemo;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloQueryCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName() + "My";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ApolloClient apolloClient = ApolloClient.builder()
                .serverUrl("https://server.matters.news/graphql")
                .okHttpClient(
                        new OkHttpClient.Builder().addInterceptor(new AuthorizationInterceptor(""))
                                .build()
                )
                .build();
        Button btClick = findViewById(R.id.button);
        btClick.setOnClickListener(v -> {
            getMattersUserInfo(apolloClient);
        });
    }
    /***/
    private void getMattersUserInfo(ApolloClient apolloClient){
        apolloClient.query(new GetTotalUsersQuery(""))
                .enqueue(new ApolloCall.Callback<GetTotalUsersQuery.Data>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(@NotNull Response<GetTotalUsersQuery.Data> response) {
                        runOnUiThread(()->{
                            TextView textView = findViewById(R.id.textview_Respond);
                            String totalCount = String.valueOf(response.getData().oss.users.totalCount);
                            textView.setText("馬特市總人口為: "+totalCount);
                        });
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }

    /**Mutate 範例*/
    private  void mutateExample(ApolloClient apolloClient){
        apolloClient.mutate(new ReadArticleMutation("QXJ0aWNsZToxNTkxOTg"))
                .enqueue(new ApolloCall.Callback<ReadArticleMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<ReadArticleMutation.Data> response) {
                        Log.d(TAG, "onResponse: " + response.getData().readArticle.title);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }
    /**Query範例*/
    private void queryExample(ApolloClient apolloClient) {
        apolloClient.query(new GetRecommendQuery())
                .enqueue(new ApolloCall.Callback<GetRecommendQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<GetRecommendQuery.Data> response) {
                        Log.d(TAG, "onResponse: " + response.getData().viewer.recommendation.newest.totalCount);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });
    }
    /**新增Header*/
    private static class AuthorizationInterceptor implements Interceptor {
        private String token;

        public AuthorizationInterceptor(String token) {
            this.token = token;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder()
                    .addHeader("x-access-token", token)
                    .build();
            return chain.proceed(request);
        }
    }
}