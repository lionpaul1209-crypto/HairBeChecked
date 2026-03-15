package com.example.hairbechecked.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "hair_history")
public class HairHistory {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long uploadDate;  // 업로드 날짜 (timestamp)

    private String recommendedStyle;  // 추천 헤어스타일

    private String imagePath;  // 사진 경로 (선택사항)

    private String faceShape;  // 얼굴형 (선택사항)

    // 생성자
    public HairHistory(long uploadDate, String recommendedStyle, String imagePath, String faceShape) {
        this.uploadDate = uploadDate;
        this.recommendedStyle = recommendedStyle;
        this.imagePath = imagePath;
        this.faceShape = faceShape;
    }

    // Getter & Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(long uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getRecommendedStyle() {
        return recommendedStyle;
    }

    public void setRecommendedStyle(String recommendedStyle) {
        this.recommendedStyle = recommendedStyle;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getFaceShape() {
        return faceShape;
    }

    public void setFaceShape(String faceShape) {
        this.faceShape = faceShape;
    }
}