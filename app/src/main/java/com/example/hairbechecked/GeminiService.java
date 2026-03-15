package com.example.hairbechecked;

import android.graphics.Bitmap;
import android.util.Base64;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiService {
    private static final String TAG = "GeminiService";
    private GenerativeModelFutures analysisModel;
    private String apiKey;
    private OkHttpClient httpClient;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;

        // ★ 핵심 해결 부분: AI가 이미지를 그릴 때까지 기다리는 시간을 기본 10초 -> 60초로 대폭 늘렸습니다!
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // 연결 대기 시간 60초
                .readTimeout(60, TimeUnit.SECONDS)    // 응답 대기 시간 60초
                .writeTimeout(60, TimeUnit.SECONDS)   // 쓰기 대기 시간 60초
                .build();

        GenerativeModel gm = new GenerativeModel("gemini-2.5-flash", apiKey);
        this.analysisModel = GenerativeModelFutures.from(gm);
    }

    public ListenableFuture<GenerateContentResponse> analyzeFace(Bitmap bitmap) {
        Content content = new Content.Builder()
                .addImage(bitmap)
                .addText("당신은 전문 헤어 디자이너입니다. 사진 속 인물의 성별(남/여)과 얼굴형을 분석해서 가장 잘 어울리는 머리 스타일을 추천해 주세요. 반드시 다음 형식을 지켜주세요.\n" +
                        "얼굴형: [내용]\n" +
                        "피부톤: [내용]\n" +
                        "추천 1: [정확한 스타일명 1개만 (예: 가르마 펌, 리프 컷, 레이어드 컷 등)]\n" +
                        "추천 2: [스타일명]\n" +
                        "이유: [왜 이 사람에게 어울리는지 1~2줄 설명]")
                .build();
        return analysisModel.generateContent(content);
    }

    public void generateImageDirectly(Bitmap bitmap, String styleName, ImageResultCallback callback) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

        try {
            JSONObject root = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject contentObj = new JSONObject();
            JSONArray parts = new JSONArray();

            String prompt = "Perform a drastic hair makeover on this person. " +
                    "You MUST physically change the structure, length, and shape of their hair to a highly realistic [" + styleName + "] hairstyle. " +
                    "The new [" + styleName + "] hair must completely replace the old hair and be clearly visible. " +
                    "While doing this heavy hair modification, you must keep the person's face identity, gender, and skin tone EXACTLY identical to the original photo. " +
                    "Do NOT just smooth the skin or add a filter. I need a real hairstyle transformation. " +
                    "Return ONLY the edited image.";

            parts.put(new JSONObject().put("text", prompt));
            parts.put(new JSONObject().put("inline_data", new JSONObject()
                    .put("mime_type", "image/jpeg")
                    .put("data", base64Image)));

            contentObj.put("parts", parts);
            contents.put(contentObj);
            root.put("contents", contents);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent?key=" + apiKey;
            RequestBody body = RequestBody.create(root.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder().url(url).post(body).build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Timeout 에러가 날 경우 여기서 잡힘
                    callback.onError("네트워크 요청 실패: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";

                    if (!response.isSuccessful()) {
                        callback.onError("API 에러(" + response.code() + "): 권한이 없거나 지원하지 않는 모델입니다.");
                        return;
                    }

                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        if (!jsonResponse.has("candidates")) {
                            callback.onError("응답 오류: 서버가 결과를 주지 않았습니다.");
                            return;
                        }

                        JSONArray candidates = jsonResponse.getJSONArray("candidates");
                        JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
                        JSONArray responseParts = content.getJSONArray("parts");

                        String base64Result = null;
                        String textResponse = "";

                        for (int i = 0; i < responseParts.length(); i++) {
                            JSONObject part = responseParts.getJSONObject(i);
                            if (part.has("text")) {
                                textResponse += part.getString("text");
                            }
                            if (part.has("inlineData")) {
                                base64Result = part.getJSONObject("inlineData").getString("data");
                                break;
                            } else if (part.has("inline_data")) {
                                base64Result = part.getJSONObject("inline_data").getString("data");
                                break;
                            }
                        }

                        if (base64Result != null) {
                            base64Result = base64Result.replaceAll("\\s", "");
                            byte[] decodedBytes = Base64.decode(base64Result, Base64.DEFAULT);
                            callback.onSuccess(decodedBytes);
                        } else {
                            callback.onError("AI 응답: " + textResponse.substring(0, Math.min(textResponse.length(), 50)));
                        }

                    } catch (Exception e) {
                        callback.onError("데이터 해석 실패: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            callback.onError("요청 구성 실패: " + e.getMessage());
        }
    }

    public interface ImageResultCallback {
        void onSuccess(byte[] imageBytes);
        void onError(String error);
    }
}