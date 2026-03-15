package com.example.hairbechecked.network;

import com.example.hairbechecked.data.model.GoogleImagenRequest;
import com.example.hairbechecked.data.model.GoogleImagenResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GoogleImagenService {
    // 3.0 모델이 404가 뜨므로, 안정적인 'imagen-2' 모델로 변경했습니다.
    @POST("v1beta/models/imagen-2:predict")
    Call<GoogleImagenResponse> generateImage(
            @Header("x-goog-api-key") String apiKey,
            @Body GoogleImagenRequest request
    );
}