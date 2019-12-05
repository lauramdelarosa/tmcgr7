package com.grupo7.moneychange.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.grupo7.moneychange.repository.HistoryRepository
import com.grupo7.moneychange.data.entity.History

/**
 * afosorio 23.11.2019
 */

class HistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = HistoryRepository(application)
    val contacts = repository.getAll()

    fun save(history: History) {
        repository.insert(history)
    }
}