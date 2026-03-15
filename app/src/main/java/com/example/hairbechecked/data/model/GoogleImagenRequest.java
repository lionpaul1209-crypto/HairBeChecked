package com.example.hairbechecked.data.model;

import java.util.Collections;
import java.util.List;

public class GoogleImagenRequest {
    // 1. 구글 API가 요구하는 데이터 구조들
    public List<Instance> instances;
    public Parameters parameters;

    // 2. 생성자: 프롬프트(명령어)를 받아서 객체를 만드는 부분
    public GoogleImagenRequest(String prompt) {
        this.instances = Collections.singletonList(new Instance(prompt));
        this.parameters = new Parameters(1, "1:1"); // 1장 생성, 비율 1:1
    }

    // 3. 내부 클래스: 실제 프롬프트를 담는 그릇
    public static class Instance {
        public String prompt;
        public Instance(String prompt) { this.prompt = prompt; }
    }

    // 4. 내부 클래스: 이미지 개수나 비율 설정
    public static class Parameters {
        public int sampleCount;
        public String aspectRatio;
        public Parameters(int cnt, String ratio) {
            this.sampleCount = cnt;
            this.aspectRatio = ratio;
        }
    }
}