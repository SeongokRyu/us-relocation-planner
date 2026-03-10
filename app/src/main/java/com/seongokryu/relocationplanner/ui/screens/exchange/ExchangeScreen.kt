package com.seongokryu.relocationplanner.ui.screens.exchange

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExchangeScreen(viewModel: ExchangeViewModel = hiltViewModel()) {
    val rate by viewModel.exchangeRate.collectAsStateWithLifecycle()
    val usdInput by viewModel.usdInput.collectAsStateWithLifecycle()
    val krwInput by viewModel.krwInput.collectAsStateWithLifecycle()
    var rateInput by remember(rate) { mutableStateOf(rate.toLong().toString()) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Exchange rate setting
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "기준 환율",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "1 USD =",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    OutlinedTextField(
                        value = rateInput,
                        onValueChange = { newVal ->
                            if (newVal.isEmpty() || newVal.toDoubleOrNull() != null) {
                                rateInput = newVal
                                newVal.toDoubleOrNull()?.let { viewModel.setExchangeRate(it) }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "KRW",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }

        // USD → KRW
        ConversionCard(
            title = "USD → KRW",
            inputLabel = "USD",
            inputValue = usdInput,
            onInputChange = { viewModel.onUsdInputChanged(it) },
            result =
                usdInput.toDoubleOrNull()?.let { usd ->
                    val krw = ExchangeViewModel.convertUsdToKrw(usd, rate)
                    "₩${NumberFormat.getNumberInstance(Locale.KOREA).format(krw.toLong())}"
                } ?: "",
        )

        // KRW → USD
        ConversionCard(
            title = "KRW → USD",
            inputLabel = "KRW",
            inputValue = krwInput,
            onInputChange = { viewModel.onKrwInputChanged(it) },
            result =
                krwInput.toDoubleOrNull()?.let { krw ->
                    val usd = ExchangeViewModel.convertKrwToUsd(krw, rate)
                    val format = NumberFormat.getNumberInstance(Locale.US)
                    format.minimumFractionDigits = 2
                    format.maximumFractionDigits = 2
                    "$${format.format(usd)}"
                } ?: "",
        )
    }
}

@Composable
private fun ConversionCard(
    title: String,
    inputLabel: String,
    inputValue: String,
    onInputChange: (String) -> Unit,
    result: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { newVal ->
                        if (newVal.isEmpty() || newVal.toDoubleOrNull() != null) {
                            onInputChange(newVal)
                        }
                    },
                    label = { Text(inputLabel) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    "→",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    result.ifEmpty { "-" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
