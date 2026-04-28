package ni.edu.uam.appnavigatorcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import ni.edu.uam.appnavigatorcompose.navigation.Screen
import ni.edu.uam.appnavigatorcompose.screens.*
import ni.edu.uam.appnavigatorcompose.ui.theme.AppNavigatorComposeTheme
import ni.edu.uam.appnavigatorcompose.viewmodels.FinanceViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FinanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigatorComposeTheme(darkTheme = viewModel.isDarkMode) {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: FinanceViewModel) {
    val navController = rememberNavController()
    var showRegister by remember { mutableStateOf(false) }

    if (!viewModel.isLoggedIn) {
        if (showRegister) {
            RegisterScreen(viewModel, onNavigateToLogin = { showRegister = false })
        } else {
            LoginScreen(viewModel, onNavigateToRegister = { showRegister = true })
        }
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    
                    Screen.bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Home.route) { HomeScreen(viewModel, navController) }
                composable(Screen.Tips.route) { TipsScreen() }
                composable(Screen.Transactions.route) { TransactionsScreen(viewModel) }
                composable(Screen.Profile.route) { ProfileScreen(viewModel) }
            }
        }
    }
}
