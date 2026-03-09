package com.seongokryu.relocationplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.ui.screens.checklist.ChecklistScreen
import com.seongokryu.relocationplanner.ui.screens.dashboard.DashboardScreen
import com.seongokryu.relocationplanner.ui.screens.dashboard.DashboardViewModel

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")

    data object Checklist : Screen("checklist/{category}") {
        fun createRoute(category: Category) = "checklist/${category.name}"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelocationNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isChecklist = currentRoute == Screen.Checklist.route
    val isDashboard = currentRoute == Screen.Dashboard.route
    val categoryName = navBackStackEntry?.arguments?.getString("category")
    val category = categoryName?.let { runCatching { Category.valueOf(it) }.getOrNull() }

    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val isSearchActive by dashboardViewModel.isSearchActive.collectAsStateWithLifecycle()
    val searchQuery by dashboardViewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            when {
                isChecklist && category != null -> {
                    TopAppBar(
                        title = { Text("${category.icon} ${category.label}") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "뒤로",
                                )
                            }
                        },
                    )
                }
                isDashboard && isSearchActive -> {
                    TopAppBar(
                        title = {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { dashboardViewModel.onSearchQueryChanged(it) },
                                placeholder = { Text("검색...") },
                                singleLine = true,
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { dashboardViewModel.toggleSearch() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "검색 닫기",
                                )
                            }
                        },
                        actions = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { dashboardViewModel.onSearchQueryChanged("") },
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "지우기")
                                }
                            }
                        },
                    )
                }
                else -> {
                    TopAppBar(
                        title = { Text("미국 이주 플래너") },
                        actions = {
                            IconButton(onClick = { dashboardViewModel.toggleSearch() }) {
                                Icon(Icons.Default.Search, contentDescription = "검색")
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onCategoryClick = { cat ->
                        navController.navigate(Screen.Checklist.createRoute(cat))
                    },
                    viewModel = dashboardViewModel,
                )
            }
            composable(Screen.Checklist.route) {
                ChecklistScreen()
            }
        }
    }
}
