package com.seongokryu.relocationplanner.ui.screens.exchange

import org.junit.Assert.assertEquals
import org.junit.Test

class ExchangeConversionTest {
    @Test
    fun should_convert_usd_to_krw() {
        val result = ExchangeViewModel.convertUsdToKrw(1000.0, 1350.0)
        assertEquals(1350000.0, result, 0.01)
    }

    @Test
    fun should_convert_krw_to_usd() {
        val result = ExchangeViewModel.convertKrwToUsd(1350000.0, 1350.0)
        assertEquals(1000.0, result, 0.01)
    }

    @Test
    fun should_handle_zero_amount() {
        assertEquals(0.0, ExchangeViewModel.convertUsdToKrw(0.0, 1350.0), 0.01)
        assertEquals(0.0, ExchangeViewModel.convertKrwToUsd(0.0, 1350.0), 0.01)
    }

    @Test
    fun should_handle_custom_rate() {
        val result = ExchangeViewModel.convertUsdToKrw(100.0, 1400.0)
        assertEquals(140000.0, result, 0.01)
    }

    @Test
    fun should_handle_zero_rate() {
        val result = ExchangeViewModel.convertKrwToUsd(1000.0, 0.0)
        assertEquals(0.0, result, 0.01)
    }
}
