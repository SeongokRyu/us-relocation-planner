package com.seongokryu.relocationplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.ui.screens.checklist.ChecklistScreen
import com.seongokryu.relocationplanner.ui.screens.dashboard.DashboardScreen

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Dashboard : Screen("dashboard", "대시보드", Icons.Default.Dashboard)
    data object Checklist : Screen("checklist/{category}", "체크리스트", Icons.Default.Checklist) {
        fun createRoute(category: Category) = "checklist/${category.name}"
    }
}

@Composable
fun RelocationNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Dashboard tab
                NavigationBarItem(
                    icon = { Icon(Screen.Dashboard.icon, contentDescription = null) },
                    label = { Text(Screen.Dashboard.label) },
                    selected = currentRoute == Screen.Dashboard.route,
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )

                // Category tabs
                Category.entries.forEach { category ->
                    val route = Screen.Checklist.createRoute(category)
                    NavigationBarItem(
                        icon = { Text(category.icon) },
                        label = { Text(category.label, maxLines = 1) },
                        selected = currentRoute == Screen.Checklist.route && run {
                            navBackStackEntry?.arguments?.getString("category") == category.name
                        },
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen()
            }
            composable(Screen.Checklist.route) {
                ChecklistScreen()
            }
        }
    }
}
