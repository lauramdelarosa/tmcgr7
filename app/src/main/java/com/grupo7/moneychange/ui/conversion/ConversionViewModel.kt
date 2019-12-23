package com.grupo7.moneychange.ui.conversion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grupo7.moneychange.data.local.entity.Currency
import com.grupo7.moneychange.data.local.entity.History
import com.grupo7.moneychange.repository.*
import com.grupo7.moneychange.repository.local.CurrencyRepository
import com.grupo7.moneychange.repository.local.HistoryRepository
import com.grupo7.moneychange.repository.network.LiveRepository
import com.grupo7.moneychange.utils.PermissionChecker
import kotlinx.coroutines.launch
import java.util.*

class ConversionViewModel(

    private val liveRepository: LiveRepository,
    private val currentRepository: CurrencyRepository,
    private val historyRepository: HistoryRepository,
    private val countryRepository: CountryRepository

) : ViewModel() {

    val textViewCurrency: MutableLiveData<Currency> = MutableLiveData()
    val editTextConversionTo: MutableLiveData<String> = MutableLiveData("0$")
    val textViewRateConversion: MutableLiveData<String> = MutableLiveData()
    val textViewConversionFrom: MutableLiveData<String> = MutableLiveData()

    private val countryMutable = MutableLiveData<String>()

    val country: LiveData<String>
        get() = countryMutable

    private var _currencyList = MutableLiveData<List<Currency>>().apply {
        value = emptyList()
    }
    val currencyList: LiveData<List<Currency>>
        get() = _currencyList

    private var _historyList = MutableLiveData<List<History>>().apply {
        value = emptyList()
    }
    val historyList: LiveData<List<History>>
        get() = _historyList

    init {
        initServiceCall()
        initCurrency()
        initHistory()
    }

    private fun initServiceCall() {
        viewModelScope.launch {
            liveRepository.getLive().observeForever {
                it?.takeIf {
                    it.success
                }?.let { response ->
                    successPath(response.quotes)
                } ?: errorPath()
            }
        }
    }

    private fun initCurrency() {
        currentRepository.getAll().observeForever {
            _currencyList.value = it
        }
    }

    private fun initHistory() {
        historyRepository.getAll().observeForever {
            _historyList.value = it
        }
    }

    private fun errorPath() {
    }

    private fun successPath(coins: MutableMap<String, Double>) {
        saveCurrencyList(coins)
    }

    private fun saveCurrencyList(coins: MutableMap<String, Double>) {

        val listFromServer = mutableListOf<Currency>()
        coins.forEach {
            val objectToSave = Currency(0, it.key.substring(3, 6), "", it.value )
            listFromServer.add(objectToSave)
        }
        currentRepository.insertCurrencyList(listFromServer)
    }

    fun onClickChange(textViewConversionFrom: String?, textViewCurrency: Currency?) {

        if (textViewConversionFrom.isNullOrBlank() || textViewCurrency == null) {
            return
        }

        val result = (textViewConversionFrom.toInt() * textViewCurrency.value)
        val history = History(0, Date(), 1, textViewCurrency.id, textViewConversionFrom.toDouble(), result)
        saveHistory(history)

        this.editTextConversionTo.value = result.toString()
    }

    private fun saveHistory(history: History) {
        viewModelScope.launch {
            historyRepository.insert(history)
        }
    }

    fun getLocation(permissionChecker: PermissionChecker) {
        viewModelScope.launch {
            countryMutable.value = countryRepository.getCountryLocation(permissionChecker)
        }
    }

    fun clickDataUp(item: History) {
        //TODO("se debe actualizar el spinner de la moneda seleccionada")
        textViewConversionFrom.value = item.valueFrom.toInt().toString()
        editTextConversionTo.value = item.valueTo.toString()
    }
}