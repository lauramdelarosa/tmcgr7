package com.grupo7.moneychange.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo7.moneychange.data.local.entity.History
import com.grupo7.moneychange.repository.local.HistoryRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class DetailConversionViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {


    private val _historyData = MutableLiveData<History>()
    val historyData: LiveData<History> get() = _historyData

    val dateTextView: MutableLiveData<String> = MutableLiveData()

    fun fetchHistoryById(id: Int) {
        viewModelScope.launch {
            _historyData.value = historyRepository.findById(id)
            setDateFormat();
        }
    }

    private fun setDateFormat(){
        if (historyData.value != null){
            var sdf = SimpleDateFormat("dd-MMMM-yyyy");
            dateTextView.value  = sdf.format(historyData.value?.date)
        }
    }
}