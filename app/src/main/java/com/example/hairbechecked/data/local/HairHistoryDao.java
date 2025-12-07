package com.example.hairbechecked.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hairbechecked.data.model.HairHistory;

import java.util.List;

@Dao
public interface HairHistoryDao {

    @Insert
    long insertHistory(HairHistory history);

    @Query("SELECT * FROM hair_history ORDER BY uploadDate DESC")
    LiveData<List<HairHistory>> getAllHistories();

    @Delete
    void deleteHistory(HairHistory history);

    @Query("DELETE FROM hair_history")
    void deleteAllHistories();
}