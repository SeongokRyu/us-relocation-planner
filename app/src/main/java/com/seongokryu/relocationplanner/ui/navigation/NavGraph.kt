package com.seongokryu.relocationplanner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.ThemeMode
import com.seongokryu.relocationplanner.ui.screens.checklist.ChecklistScreen
import com.seongokryu.relocationplanner.ui.screens.contact.ContactScreen
import com.seongokryu.relocationplanner.ui.screens.dashboard.DashboardScreen
import com.seongokryu.relocationplanner.ui.screens.dashboard.DashboardViewModel
import com.seongokryu.relocationplanner.ui.screens.detail.TaskDetailScreen
import com.seongokryu.relocationplanner.ui.screens.exchange.ExchangeScreen
import com.seongokryu.relocationplanner.ui.screens.expense.ExpenseScreen
import com.seongokryu.relocationplanner.ui.screens.timeline.TimelineScreen
import kotlinx.coroutines.launch

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")

    data object Checklist : Screen("checklist/{category}") {
        fun createRoute(category: Category) = "checklist/${category.name}"
    }

    data object TaskDetail : Screen("task/{taskId}") {
        fun createRoute(taskId: Long) = "task/$taskId"
    }

    data object Timeline : Screen("timeline")

    data object Expense : Screen("expense")

    data object Contact : Screen("contact")

    data object Exchange : Screen("exchange")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelocationNavHost(
    themeMode: ThemeMode,
    onThemeModeChanged: suspend (ThemeMode) -> Unit,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val isChecklist = currentRoute == Screen.Checklist.route
    val isDashboard = currentRoute == Screen.Dashboard.route
    val isDetail = currentRoute == Screen.TaskDetail.route
    val isTimeline = currentRoute == Screen.Timeline.route
    val isExpense = currentRoute == Screen.Expense.route
    val isContact = currentRoute == Screen.Contact.route
    val isExchange = currentRoute == Screen.Exchange.route
    val categoryName = navBackStackEntry?.arguments?.getString("category")
    val category = categoryName?.let { runCatching { Category.valueOf(it) }.getOrNull() }

    val dashboardViewModel: DashboardViewModel = hiltViewModel()
    val isSearchActive by dashboardViewModel.isSearchActive.collectAsStateWithLifecycle()
    val searchQuery by dashboardViewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            when {
                isTimeline -> {
                    TopAppBar(
                        title = { Text("타임라인") },
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
                isExpense -> {
                    TopAppBar(
                        title = { Text("비용 관리") },
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
                isContact -> {
                    TopAppBar(
                        title = { Text("연락처") },
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
                isExchange -> {
                    TopAppBar(
                        title = { Text("환율 계산기") },
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
                isDetail -> {
                    TopAppBar(
                        title = { Text("상세 정보") },
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
                            IconButton(
                                onClick = {
                                    val next =
                                        when (themeMode) {
                                            ThemeMode.SYSTEM -> ThemeMode.DARK
                                            ThemeMode.DARK -> ThemeMode.LIGHT
                                            ThemeMode.LIGHT -> ThemeMode.SYSTEM
                                        }
                                    scope.launch { onThemeModeChanged(next) }
                                },
                            ) {
                                val icon =
                                    when (themeMode) {
                                        ThemeMode.DARK -> Icons.Filled.DarkMode
                                        ThemeMode.LIGHT -> Icons.Filled.LightMode
                                        ThemeMode.SYSTEM -> Icons.Outlined.DarkMode
                                    }
                                val description =
                                    when (themeMode) {
                                        ThemeMode.DARK -> "다크 모드 (클릭: 라이트)"
                                        ThemeMode.LIGHT -> "라이트 모드 (클릭: 시스템)"
                                        ThemeMode.SYSTEM -> "시스템 모드 (클릭: 다크)"
                                    }
                                Icon(icon, contentDescription = description)
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
                    onTimelineClick = {
                        navController.navigate(Screen.Timeline.route)
                    },
                    onExpenseClick = {
                        navController.navigate(Screen.Expense.route)
                    },
                    onContactClick = {
                        navController.navigate(Screen.Contact.route)
                    },
                    onExchangeClick = {
                        navController.navigate(Screen.Exchange.route)
                    },
                    viewModel = dashboardViewModel,
                )
            }
            composable(Screen.Checklist.route) {
                ChecklistScreen(
                    onTaskClick = { taskId ->
                        navController.navigate(Screen.TaskDetail.createRoute(taskId))
                    },
                )
            }
            composable(
                route = Screen.TaskDetail.route,
                arguments = listOf(navArgument("taskId") { type = NavType.LongType }),
            ) {
                TaskDetailScreen()
            }
            composable(Screen.Timeline.route) {
                TimelineScreen()
            }
            composable(Screen.Expense.route) {
                ExpenseScreen()
            }
            composable(Screen.Contact.route) {
                ContactScreen()
            }
            composable(Screen.Exchange.route) {
                ExchangeScreen()
            }
        }
    }
}
