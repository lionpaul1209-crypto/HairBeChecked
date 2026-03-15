package com.example.hairbechecked.data.model;

import java.util.List;

public class GoogleImagenResponse {
    // 구글은 이미지 생성 결과를 'predictions'라는 이름의 리스트로 줍니다.
    public List<Prediction> predictions;

    // 내부 클래스: 실제 이미지 데이터가 담긴 부분
    public static class Prediction {
        public String bytesBase64Encoded; // 이게 제일 중요! (이미지 데이터)
        public String mimeType;           // 이미지 형식 (예: image/png)
    }
}