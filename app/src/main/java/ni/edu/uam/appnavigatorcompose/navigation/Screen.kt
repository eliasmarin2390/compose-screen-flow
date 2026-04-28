package ni.edu.uam.appnavigatorcompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sellado de clases para las rutas de navegación (POO)
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Tips : Screen("tips", "Consejos", Icons.Default.Lightbulb)
    object Transactions : Screen("transactions", "Movimientos", Icons.AutoMirrored.Filled.ReceiptLong)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)

    companion object {
        val bottomNavItems = listOf(Home, Tips, Transactions, Profile)
    }
}
