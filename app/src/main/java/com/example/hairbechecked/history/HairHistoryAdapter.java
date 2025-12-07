package com.example.hairbechecked.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hairbechecked.R;
import com.example.hairbechecked.data.model.HairHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HairHistoryAdapter extends RecyclerView.Adapter<HairHistoryAdapter.HistoryViewHolder> {

    private List<HairHistory> historyList = new ArrayList<>();
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(HairHistory history);
    }

    public HairHistoryAdapter(OnDeleteClickListener listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hair_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HairHistory history = historyList.get(position);
        holder.bind(history, onDeleteClickListener);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void setHistoryList(List<HairHistory> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivHistoryImage;
        private TextView tvHistoryDate;
        private TextView tvHistoryStyle;
        private TextView tvHistoryFaceShape;
        private ImageButton btnDeleteHistory;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHistoryImage = itemView.findViewById(R.id.ivHistoryImage);
            tvHistoryDate = itemView.findViewById(R.id.tvHistoryDate);
            tvHistoryStyle = itemView.findViewById(R.id.tvHistoryStyle);
            tvHistoryFaceShape = itemView.findViewById(R.id.tvHistoryFaceShape);
            btnDeleteHistory = itemView.findViewById(R.id.btnDeleteHistory);
        }

        public void bind(HairHistory history, OnDeleteClickListener listener) {
            // 날짜 포맷팅
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA);
            tvHistoryDate.setText(dateFormat.format(new Date(history.getUploadDate())));

            // 추천 스타일
            tvHistoryStyle.setText(history.getRecommendedStyle());

            // 얼굴형
            String faceShapeText = (history.getFaceShape() == null || history.getFaceShape().isEmpty())
                    ? "얼굴형: 정보 없음"
                    : "얼굴형: " + history.getFaceShape();
            tvHistoryFaceShape.setText(faceShapeText);

            // 이미지 로드
            if (history.getImagePath() != null && !history.getImagePath().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(history.getImagePath())
                        .centerCrop()
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(ivHistoryImage);
            } else {
                ivHistoryImage.setImageResource(R.drawable.ic_launcher_foreground);
            }

            // 삭제 버튼 클릭
            btnDeleteHistory.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(history);
                }
            });
        }
    }
}