package com.example.hairbechecked.history;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hairbechecked.R;
import com.example.hairbechecked.data.model.HairHistory;
import com.example.hairbechecked.data.repository.HairHistoryRepository;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView rvHistory;
    private LinearLayout layoutEmptyState;

    private HairHistoryRepository repository;
    private HairHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // 뷰 초기화
        toolbar = findViewById(R.id.toolbar);
        rvHistory = findViewById(R.id.rvHistory);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        // Repository 초기화
        repository = new HairHistoryRepository(getApplication());

        // 툴바 설정
        setupToolbar();

        // RecyclerView 설정
        setupRecyclerView();

        // 히스토리 관찰
        observeHistories();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new HairHistoryAdapter(history -> showDeleteConfirmDialog(history));

        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(adapter);
    }

    private void observeHistories() {
        repository.getAllHistories().observe(this, new Observer<List<HairHistory>>() {
            @Override
            public void onChanged(List<HairHistory> histories) {
                if (histories == null || histories.isEmpty()) {
                    // 빈 상태 표시
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    rvHistory.setVisibility(View.GONE);
                } else {
                    // 히스토리 표시
                    layoutEmptyState.setVisibility(View.GONE);
                    rvHistory.setVisibility(View.VISIBLE);
                    adapter.setHistoryList(histories);
                }
            }
        });
    }

    private void showDeleteConfirmDialog(HairHistory history) {
        new AlertDialog.Builder(this)
                .setTitle("히스토리 삭제")
                .setMessage("'" + history.getRecommendedStyle() + "' 기록을 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> deleteHistory(history))
                .setNegativeButton("취소", null)
                .show();
    }

    private void deleteHistory(HairHistory history) {
        repository.deleteHistory(history);
        Toast.makeText(this, "히스토리가 삭제되었습니다", Toast.LENGTH_SHORT).show();
    }
}