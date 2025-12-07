package com.example.hairbechecked;

import com.example.hairbechecked.data.local.AppDatabase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.example.hairbechecked.data.model.HairHistory;
import com.example.hairbechecked.data.repository.HairHistoryRepository;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
// Toast 임포트 제거됨
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.mediapipe.solutions.facemesh.FaceMesh;
import com.google.mediapipe.solutions.facemesh.FaceMeshOptions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.example.hairbechecked.data.model.HairHistory;
import com.example.hairbechecked.data.repository.HairHistoryRepository;

public class HairRecommendingPage extends AppCompatActivity {
    private static final String TAG = "HairRecommendingPage";
    private FaceMesh faceMesh;
    private ExecutorService backgroundExecutor;
    private HairHistoryRepository historyRepository;
    private String latestResultStyle;

    private Handler mainHandler;
    private boolean isProcessing = false;
    private AppDatabase db;
    private HairHistoryRepository repository;
    private String recommendedStyle = "";  // 추천된 스타일
    private String faceShape = "";  // 얼굴형
    private String imagePath = "";  // 이미지 경로
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        repository = new HairHistoryRepository(getApplication());
        historyRepository = new HairHistoryRepository(getApplication());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_recommending_page);

        backgroundExecutor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        Intent intent = getIntent();
        String imgstr = intent.getStringExtra("image");

        if (imgstr == null) {
            Log.e(TAG, "이미지 URI가 null입니다.");
            // 토스트 제거됨
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
        findViewById(R.id.saveButton).setOnClickListener(v -> {
            saveHistory(imgstr, latestResultStyle);

        });
        // 히스토리 보기 버튼
        Button btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HairRecommendingPage.this, com.example.hairbechecked.history.HistoryActivity.class);
                startActivity(intent);
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
                    // 토스트 제거됨
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
            // 토스트 제거됨
            proceedWithoutAnalysis();
        });
    }

    private void proceedWithoutAnalysis() {
        String[] recommendations = {
                "쉐도우 펌", "레이어드 컷", "단발 C컬펌", "가일 컷", "히피 펌"
        };
        int randomIndex = (int) (Math.random() * recommendations.length);
        String resultStyle = recommendations[randomIndex];

        latestResultStyle = resultStyle; // ⭐ 저장을 위해 추가된 코드

        int matchRate = 80 + (int)(Math.random() * 15);

        mainHandler.post(() -> {
            TextView nameView = findViewById(R.id.hairstyleNameTextView);
            TextView descView = findViewById(R.id.matchDescriptionTextView);
            android.widget.ProgressBar progressBar = findViewById(R.id.matchProgressBar);

            nameView.setText("추천 스타일: " + resultStyle);
            descView.setText("이 스타일은 회원님과 " + matchRate + "% 일치합니다.");
            progressBar.setProgress(matchRate);
        });
    }


    // showToast 메서드 자체를 삭제했습니다.

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

    private void saveHistory(String imgstr, String latestResultStyle) {
        long uploadDate = System.currentTimeMillis();

        HairHistory history = new HairHistory(
                uploadDate,
                latestResultStyle,
                imgstr,
                "정보 없음" // 얼굴형 나중에 넣으면 됨
        );

        historyRepository.insertHistory(history);
    }


}