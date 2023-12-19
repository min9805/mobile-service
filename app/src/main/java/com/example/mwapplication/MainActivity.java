package com.example.mwapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    ImageView imgView;
    Bitmap bmImg = null;
    CLoadImage task;

    String hostUrl="https://min1925k2.pythonanywhere.com";
    String pk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgView = (ImageView) findViewById(R.id.imgView);
        task= new CLoadImage();
    }


    private void loadDataFromServer() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://min1925k2.pythonanywhere.com/api/confirm"; // pythonanywhere 서버 URL

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버 응답 처리 및 데이터 전달
                        openLoadedActivityWithData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
                Log.d("API", "Error: " + error.toString());
            }
        });

        queue.add(stringRequest);
    }

    private void openLoadedActivityWithData(String data) {
        Intent intent = new Intent(MainActivity.this, LoadedActivity.class);
        intent.putExtra("extraData", data);
        startActivity(intent);
    }



    // 이미지를불러오기위한함수
//    public void onButtonLoad(View v){
//        // AsyncTask동작
//        task.execute(imageUrl);
//        Toast.makeText(getApplicationContext(), "Load", Toast.LENGTH_LONG).show();
//    }

    public void onButtonSave(View view) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(hostUrl + "/post/" + pk + "/save/");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        refreshActivity();
                    }

                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void onButtonDiscard(View v) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(hostUrl + "/post/" + pk + "/discard/");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");

                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        refreshActivity();
                    }

                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    private void refreshActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(getIntent());
            }
        });
    }


    public void onButtonLoad(View v) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://min1925k2.pythonanywhere.com/api/confirm");
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        String response = readStream(in);
                        updateUI(response);
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private String readStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }


    private void updateUI(String jsonResponse) {
        try {
            JSONArray jsonArray = new JSONArray(jsonResponse);
            if (jsonArray.length() > 0) {
                JSONObject firstItem = jsonArray.getJSONObject(0);
                String text = firstItem.getString("text");
                String title = firstItem.getString("title");
                String image_url = firstItem.getString("image_url");
                pk = firstItem.getString("pk");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView = findViewById(R.id.textView);
                        TextView titleView = findViewById(R.id.titleView);
                        ImageView imageView = findViewById(R.id.imgView);

                        textView.setText(text);
                        titleView.setText(title);
                        task.execute(image_url);
                        Toast.makeText(getApplicationContext(), "Load", Toast.LENGTH_LONG).show();

                    }
                });
            }
            else{
                TextView titleView = findViewById(R.id.titleView);

                titleView.setText("Nothing to Confirm!!");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void savaBitmapToJpeg(Bitmap bitmap, String folder, String name) {
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
        String folder_name = "/" + folder + "/";
        String file_name = name + ".jpg";
        String string_path = ex_storage + folder_name;
        Log.d("경로", string_path);

        File file_path;
        file_path = new File(string_path);

        if(!file_path.exists()) {
            file_path.mkdirs();
        }

        try {
            FileOutputStream out = new FileOutputStream(string_path+file_name);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch(FileNotFoundException e) {
            Log.e("FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
        }
    }



    private class CLoadImage extends AsyncTask<String, Integer, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try
            {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();

                bmImg = BitmapFactory.decodeStream(is);
            } catch(IOException e)
            {
                e.printStackTrace();
            }

            return bmImg;
        }

        protected void onPostExecute(Bitmap img)
        {
            imgView.setImageBitmap(bmImg);
        }

    }
}

