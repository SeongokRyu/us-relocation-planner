package com.seongokryu.relocationplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.seongokryu.relocationplanner.ui.navigation.RelocationNavHost
import com.seongokryu.relocationplanner.ui.theme.RelocationPlannerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RelocationPlannerTheme {
                RelocationNavHost()
            }
        }
    }
}
