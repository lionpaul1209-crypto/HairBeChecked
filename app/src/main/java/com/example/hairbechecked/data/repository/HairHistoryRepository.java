package com.example.hairbechecked.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.hairbechecked.data.local.AppDatabase;
import com.example.hairbechecked.data.local.HairHistoryDao;
import com.example.hairbechecked.data.model.HairHistory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HairHistoryRepository {

    private HairHistoryDao hairHistoryDao;
    private LiveData<List<HairHistory>> allHistories;
    private ExecutorService executorService;

    public HairHistoryRepository(Application application) {
        AppDatabase database = AppDatabase.getDatabase(application);
        hairHistoryDao = database.hairHistoryDao();
        allHistories = hairHistoryDao.getAllHistories();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<HairHistory>> getAllHistories() {
        return allHistories;
    }

    public void insertHistory(HairHistory history) {
        executorService.execute(() -> {
            hairHistoryDao.insertHistory(history);
        });
    }

    public void deleteHistory(HairHistory history) {
        executorService.execute(() -> {
            hairHistoryDao.deleteHistory(history);
        });
    }

    public void deleteAllHistories() {
        executorService.execute(() -> {
            hairHistoryDao.deleteAllHistories();
        });
    }
}