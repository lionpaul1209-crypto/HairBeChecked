
package com.example.hairbechecked;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StyleRankingActivity extends AppCompatActivity {

    private RecyclerView rankingRecyclerView;
    private Button btnConfirm, btnMaleTab, btnFemaleTab;
    private RankingAdapter adapter;

    // ⭐ 현재 선택된 성별 (true: 남자, false: 여자)
    private boolean isMaleSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_ranking);

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    private void initViews() {
        rankingRecyclerView = findViewById(R.id.rankingRecyclerView);
        btnConfirm = findViewById(R.id.btnConfirmRanking);
        btnMaleTab = findViewById(R.id.btnMaleTab);
        btnFemaleTab = findViewById(R.id.btnFemaleTab);
    }

    private void setupRecyclerView() {
        // 초기 데이터는 남자 스타일
        List<StyleRankingItem> rankingList = getMaleRankingData();

        adapter = new RankingAdapter(rankingList);
        rankingRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankingRecyclerView.setAdapter(adapter);
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(v -> finish());

        // ⭐ 남자 탭 클릭
        btnMaleTab.setOnClickListener(v -> {
            if (!isMaleSelected) {
                isMaleSelected = true;
                updateTabUI();
                updateRankingData(getMaleRankingData());
            }
        });

        // ⭐ 여자 탭 클릭
        btnFemaleTab.setOnClickListener(v -> {
            if (isMaleSelected) {
                isMaleSelected = false;
                updateTabUI();
                updateRankingData(getFemaleRankingData());
            }
        });
    }

    /**
     * ⭐ 탭 UI 업데이트 (선택된 탭 강조)
     */
    private void updateTabUI() {
        if (isMaleSelected) {
            // 남자 탭 활성화
            btnMaleTab.setBackgroundColor(0xFF1E88E5); // 파란색
            btnMaleTab.setTextColor(0xFFFFFFFF);       // 흰색
            btnMaleTab.setTypeface(null, android.graphics.Typeface.BOLD);

            // 여자 탭 비활성화
            btnFemaleTab.setBackgroundColor(0xFFE0E0E0); // 회색
            btnFemaleTab.setTextColor(0xFF666666);       // 어두운 회색
            btnFemaleTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        } else {
            // 여자 탭 활성화
            btnFemaleTab.setBackgroundColor(0xFF1E88E5);
            btnFemaleTab.setTextColor(0xFFFFFFFF);
            btnFemaleTab.setTypeface(null, android.graphics.Typeface.BOLD);

            // 남자 탭 비활성화
            btnMaleTab.setBackgroundColor(0xFFE0E0E0);
            btnMaleTab.setTextColor(0xFF666666);
            btnMaleTab.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    /**
     * ⭐ 순위 데이터 업데이트
     */
    private void updateRankingData(List<StyleRankingItem> newData) {
        adapter.updateData(newData);
    }

    /**
     * 👨 남자 헤어스타일 순위 데이터
     */
    private List<StyleRankingItem> getMaleRankingData() {
        List<StyleRankingItem> list = new ArrayList<>();

        list.add(new StyleRankingItem(1, "쉐도우 펌", 1320, "▲ 2"));
        list.add(new StyleRankingItem(2, "가일 컷", 1180, "▼ 1"));
        list.add(new StyleRankingItem(3, "댄디 컷", 980, "▲ 1"));
        list.add(new StyleRankingItem(4, "두블럭 컷", 875, "―"));
        list.add(new StyleRankingItem(5, "스포츠 컷", 720, "▲ 3"));
        list.add(new StyleRankingItem(6, "히피 펌", 650, "▼ 2"));
        list.add(new StyleRankingItem(7, "울프 컷", 580, "NEW"));
        list.add(new StyleRankingItem(8, "리젠트 컷", 520, "▲ 1"));
        list.add(new StyleRankingItem(9, "언더컷", 480, "▼ 3"));
        list.add(new StyleRankingItem(10, "크롭 컷", 420, "▼ 1"));

        return list;
    }

    /**
     * 👩 여자 헤어스타일 순위 데이터
     */
    private List<StyleRankingItem> getFemaleRankingData() {
        List<StyleRankingItem> list = new ArrayList<>();

        list.add(new StyleRankingItem(1, "레이어드 컷", 1450, "▲ 1"));
        list.add(new StyleRankingItem(2, "단발 C컬펌", 1280, "―"));
        list.add(new StyleRankingItem(3, "시스루 뱅", 1050, "▲ 2"));
        list.add(new StyleRankingItem(4, "허쉬 컷", 920, "▲ 3"));
        list.add(new StyleRankingItem(5, "울프 컷", 810, "▼ 2"));
        list.add(new StyleRankingItem(6, "볼드 펌", 720, "▲ 1"));
        list.add(new StyleRankingItem(7, "웨이브 펌", 650, "▼ 1"));
        list.add(new StyleRankingItem(8, "앞머리 히피펌", 580, "NEW"));
        list.add(new StyleRankingItem(9, "셋팅 펌", 520, "―"));
        list.add(new StyleRankingItem(10, "태슬 펌", 460, "▼ 3"));

        return list;
    }

    /**
     * 순위 아이템 데이터 모델
     */
    static class StyleRankingItem {
        int rank;
        String styleName;
        int popularity;
        String change;

        public StyleRankingItem(int rank, String styleName, int popularity, String change) {
            this.rank = rank;
            this.styleName = styleName;
            this.popularity = popularity;
            this.change = change;
        }
    }

    /**
     * RecyclerView 어댑터
     */
    static class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

        private List<StyleRankingItem> rankingList;

        public RankingAdapter(List<StyleRankingItem> rankingList) {
            this.rankingList = rankingList;
        }

        /**
         * ⭐ 데이터 업데이트 메서드
         */
        public void updateData(List<StyleRankingItem> newData) {
            this.rankingList = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_style_ranking, parent, false);
            return new RankingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
            StyleRankingItem item = rankingList.get(position);

            // 순위 표시
            if (item.rank == 1) {
                holder.rankIcon.setVisibility(View.VISIBLE);
                holder.rankText.setVisibility(View.GONE);
                holder.rankIcon.setText("🥇");
            } else if (item.rank == 2) {
                holder.rankIcon.setVisibility(View.VISIBLE);
                holder.rankText.setVisibility(View.GONE);
                holder.rankIcon.setText("🥈");
            } else if (item.rank == 3) {
                holder.rankIcon.setVisibility(View.VISIBLE);
                holder.rankText.setVisibility(View.GONE);
                holder.rankIcon.setText("🥉");
            } else {
                holder.rankIcon.setVisibility(View.GONE);
                holder.rankText.setVisibility(View.VISIBLE);
                holder.rankText.setText(String.valueOf(item.rank));
            }

            // 스타일 정보 표시
            holder.styleName.setText(item.styleName);
            holder.popularity.setText(item.popularity + "명 선택");
            holder.change.setText(item.change);

            // 순위 변동 색상
            if (item.change.startsWith("▲")) {
                holder.change.setTextColor(0xFFFF5722);
            } else if (item.change.startsWith("▼")) {
                holder.change.setTextColor(0xFF2196F3);
            } else if (item.change.equals("NEW")) {
                holder.change.setTextColor(0xFF4CAF50);
            } else {
                holder.change.setTextColor(0xFF9E9E9E);
            }
        }

        @Override
        public int getItemCount() {
            return rankingList.size();
        }

        static class RankingViewHolder extends RecyclerView.ViewHolder {
            TextView rankIcon, rankText, styleName, popularity, change;

            public RankingViewHolder(@NonNull View itemView) {
                super(itemView);
                rankIcon = itemView.findViewById(R.id.rankIcon);
                rankText = itemView.findViewById(R.id.rankText);
                styleName = itemView.findViewById(R.id.styleName);
                popularity = itemView.findViewById(R.id.popularity);
                change = itemView.findViewById(R.id.change);
            }
        }
    }
}