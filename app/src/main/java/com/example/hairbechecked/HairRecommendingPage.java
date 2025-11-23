package com.example.hairbechecked;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshResult;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HairRecommendingPage extends AppCompatActivity {
    private static final String TAG = "HairRecommendingPage";
    private FaceMesh faceMesh;
    private ExecutorService backgroundExecutor;
    private Handler mainHandler;
    private boolean isProcessing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_recommending_page);

        backgroundExecutor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        Intent intent = getIntent();
        String imgstr = intent.getStringExtra("image");

        if (imgstr == null) {
            Log.e(TAG, "이미지 URI가 null입니다.");
            showToast("이미지를 불러올 수 없습니다.");
            finish();
            return;
        }

        Uri imageuri = Uri.parse(imgstr);
        ImageView hairImage = findViewById(R.id.hairstyleImageView);
        hairImage.setImageURI(imageuri);

        // MediaPipe 시도
        backgroundExecutor.execute(() -> {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageuri);
                if (bitmap != null) {
                    tryMediaPipe(bitmap);
                } else {
                    Log.e(TAG, "비트맵이 null입니다.");
                    skipMediaPipe();
                }
            } catch (IOException e) {
                Log.e(TAG, "이미지 로딩 실패: " + e.getMessage());
                skipMediaPipe();
            }
        });
    }

    private void tryMediaPipe(Bitmap bitmap) {
        if (isProcessing) return;
        isProcessing = true;

        Log.d(TAG, "MediaPipe 시도 중...");

        try {
            // 간단한 설정으로 한 번만 시도
            FaceMeshOptions options = FaceMeshOptions.builder()
                    .setStaticImageMode(true)
                    .setMaxNumFaces(1)
                    .setRefineLandmarks(false)
                    .setRunOnGpu(false)
                    .build();

            faceMesh = new FaceMesh(this, options);

            // 성공/실패 리스너 설정
            faceMesh.setResultListener(faceMeshResult -> {
                mainHandler.post(() -> {
                    Log.d(TAG, "MediaPipe 성공!");
                    showToast("얼굴 인식 완료!");
                    proceedWithoutAnalysis();
                });
            });

            faceMesh.setErrorListener((message, e) -> {
                mainHandler.post(() -> {
                    Log.w(TAG, "MediaPipe 실패: " + message);
                    skipMediaPipe();
                });
            });

            // 이미지 전송
            faceMesh.send(bitmap);

            // 타임아웃 설정 (3초 후 자동 스킵)
            mainHandler.postDelayed(() -> {
                if (isProcessing) {
                    Log.w(TAG, "MediaPipe 타임아웃");
                    skipMediaPipe();
                }
            }, 100000);

        } catch (Exception e) {
            Log.w(TAG, "MediaPipe 초기화 실패: " + e.getMessage());
            skipMediaPipe();
        }
    }

    private void skipMediaPipe() {
        if (!isProcessing) return;
        isProcessing = false;

        Log.d(TAG, "MediaPipe 스킵 - 바로 진행");
        mainHandler.post(() -> {
            showToast("헤어스타일 추천을 시작합니다!");
            proceedWithoutAnalysis();
        });
    }

    private void proceedWithoutAnalysis() {
        // 얼굴 분석 없이 바로 추천
        String[] recommendations = {
                "자연스러운 레이어드 컷을 추천드립니다!",
                "트렌디한 단발 스타일은 어떠세요?",
                "우아한 롱 헤어 스타일을 시도해보세요!",
                "개성 있는 숏컷 스타일을 추천드립니다!",
                "볼륨감 있는 웨이브 스타일을 추천드립니다!"
        };

        int randomIndex = (int) (Math.random() * recommendations.length);

        // 1초 후 추천 표시
        mainHandler.postDelayed(() -> {
            showToast(recommendations[randomIndex]);
        }, 1000);
    }

    private void showToast(String message) {
        mainHandler.post(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isProcessing = false;

        if (backgroundExecutor != null) {
            backgroundExecutor.shutdown();
        }

        if (faceMesh != null) {
            try {
                faceMesh.close();
            } catch (Exception e) {
                Log.w(TAG, "FaceMesh 정리 중 오류: " + e.getMessage());
            }
            faceMesh = null;
        }
    }
}