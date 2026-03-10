package com.seongokryu.relocationplanner.ui.screens.exchange

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : ViewModel() {
        val exchangeRate: StateFlow<Double> =
            dataStore.data
                .map { prefs -> prefs[EXCHANGE_RATE_KEY] ?: DEFAULT_RATE }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DEFAULT_RATE)

        val usdInput = MutableStateFlow("")
        val krwInput = MutableStateFlow("")

        fun setExchangeRate(rate: Double) {
            viewModelScope.launch {
                dataStore.edit { prefs ->
                    prefs[EXCHANGE_RATE_KEY] = rate
                }
            }
        }

        fun onUsdInputChanged(value: String) {
            usdInput.value = value
        }

        fun onKrwInputChanged(value: String) {
            krwInput.value = value
        }

        companion object {
            val EXCHANGE_RATE_KEY = doublePreferencesKey("exchange_rate")
            const val DEFAULT_RATE = 1350.0

            fun convertUsdToKrw(
                usd: Double,
                rate: Double,
            ): Double = usd * rate

            fun convertKrwToUsd(
                krw: Double,
                rate: Double,
            ): Double = if (rate == 0.0) 0.0 else krw / rate
        }
    }
