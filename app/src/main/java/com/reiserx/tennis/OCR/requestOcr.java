package com.reiserx.tennis.OCR;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.reiserx.tennis.Utils.FileUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class requestOcr {
    Context context;
    ProgressDialog prog;
    EditText text;
    TextView output;
    View outputHolder;
    String TAG = "ihfsidhfs";
    String result;
    JSONObject jsonObject;


    public requestOcr(Context context, EditText text, TextView output, View outputHolder) {
        this.context = context;
        this.prog = new ProgressDialog(context);
        this.text = text;
        this.output = output;
        this.outputHolder = outputHolder;
    }

    public void runCode(String code) throws IOException {
        prog.setMessage("Sending request...");
        prog.show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                JSONObject request = new JSONObject();
                request.put("code", code);

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), request.toString());
                Request apiRequest = new Request.Builder()
                        .header("Authorization", "ihiuHUGUIjbijhygyg&YGGybihgy")
                        .url("http://reiserx.com/python/api/")
                        .post(body)
                        .build();
                Response response = client.newCall(apiRequest).execute();
                result = Objects.requireNonNull(response.body()).string();
                jsonObject = new JSONObject(result);
                Log.d(TAG, result);
                prog.dismiss();
            } catch (Exception e) {
                prog.dismiss();
                Log.i(TAG, e.getMessage());
            }

            prog.setMessage("Request sent");
            prog.dismiss();

            handler.post(() -> {
                try {
                    if (jsonObject != null) {
                        if (jsonObject.getInt("success") == 1) {
                            output.setText(jsonObject.getString("response"));
                        } else {
                            output.setText("Error code 0: ".concat(jsonObject.getString("response")));
                        }
                    } else {
                        output.setText(result);
                    }
                    if (outputHolder.getVisibility() == View.GONE)
                        outputHolder.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, e.toString());
                }
            });
        });
    }

    public String prints(String res) throws JSONException {
        JSONObject json = new JSONObject(res);
        JSONArray jsonObject = json.getJSONArray("responses");
        JSONObject object = jsonObject.getJSONObject(0);
        JSONArray data = object.getJSONArray("textAnnotations");
        JSONObject object1 = data.getJSONObject(0);
        return object1.getString("description");
    }

    public String testing(String result) {
        return result;
    }

    public void write(String path, String data) throws IOException {
        FileUtil.writeFile(path, data);
        Log.d(TAG, "file written");
        Log.d(TAG, data);
    }

    public void scan_file(String uri) throws IOException {
        prog.setMessage("Sending request...");
        prog.show();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                JSONObject request = new JSONObject();

                request.put("image", uri);

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), request.toString());
                Request apiRequest = new Request.Builder()
                        .header("Authorization", "ihiuHUGUIjbijhygyg&YGGybihgy")
                        .url("http://reiserx.com/python/scan/")
                        .post(body)
                        .build();
                Response response = client.newCall(apiRequest).execute();
                result = Objects.requireNonNull(response.body()).string();
                jsonObject = new JSONObject(result);

                prog.dismiss();
            } catch (Exception e) {
                prog.dismiss();
                Log.i(TAG, e.getMessage());
            }

            prog.setMessage("Request sent");
            prog.dismiss();

            handler.post(() -> {
                try {
                    if (jsonObject != null) {
                        if (jsonObject.getInt("success") == 1) {
                            text.setText(jsonObject.getString("response"));
                        } else {
                            output.setText("Error code 0: ".concat(jsonObject.getString("response")));
                        }
                    } else {
                        output.setText(result);
                    }
                    if (outputHolder.getVisibility() == View.GONE)
                        outputHolder.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        });
    }
}