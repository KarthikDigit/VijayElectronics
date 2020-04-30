package com.vijayelectronics.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.vijayelectronics.R;
import com.vijayelectronics.models.device_model.AppSettingsDetails;
import com.vijayelectronics.network.APIClient;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.share.internal.DeviceShareDialogFragment.TAG;

public class ApiTestActivity extends AppCompatActivity {

    private static final String TAG = "ApiTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_test);


        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callAppSettings();

            }
        });

    }


    private void callAppSettings() {

        Call<String> call = APIClient.getInstanceTest()
                .getAppSetting1();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.e(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

//        try {
//
//            Response<AppSettingsDetails> response = call.execute();
//
//            String strJson1 = new Gson().toJson(response.body());
//
//            Log.e(TAG, "RequestAppSetting: " + strJson1);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
