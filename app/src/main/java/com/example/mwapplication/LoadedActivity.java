package com.example.mwapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

public class LoadedActivity extends AppCompatActivity {
    private int postId; // 예를 들어, 서버로부터 받은 포스트 ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loaded_layout);

        // Intent에서 데이터 받기
        String imageUrl = getIntent().getStringExtra("imageUrl");
        String title = getIntent().getStringExtra("title");
        String text = getIntent().getStringExtra("text");

        // 이미지 뷰 찾기
        ImageView imageView = findViewById(R.id.imageView);
        // 이미지 URL을 사용하여 이미지 로드
        Picasso.get().load(imageUrl).into(imageView);

        // 텍스트 뷰에 제목과 텍스트 설정
        TextView titleTextView = findViewById(R.id.titleTextView);
        TextView textTextView = findViewById(R.id.textTextView);
        titleTextView.setText(title);
        textTextView.setText(text);

        Button buttonSave = findViewById(R.id.button_save);
        Button buttonDiscard = findViewById(R.id.button_discard);

        // Save 버튼 클릭 이벤트
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestToServer("save");
            }
        });

        // Discard 버튼 클릭 이벤트
        buttonDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestToServer("discard");
            }
        });
    }

    private void sendRequestToServer(String action) {
        String url = "http://min1925k.pythonanywhere.com/post/" + postId + "/" + action + "/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 요청 성공 처리
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
            }
        });

        VolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }



}

