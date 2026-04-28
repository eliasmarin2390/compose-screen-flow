package ni.edu.uam.appnavigatorcompose.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ni.edu.uam.appnavigatorcompose.models.SavingsGoal
import ni.edu.uam.appnavigatorcompose.models.Transaction
import ni.edu.uam.appnavigatorcompose.models.UserProfile

class FinanceViewModel : ViewModel() {

    // Estado del tema
    var isDarkMode by mutableStateOf(false)
        private set

    fun toggleTheme() {
        isDarkMode = !isDarkMode
    }

    // Estado del usuario
    var userProfile by mutableStateOf(UserProfile())
        private set

    // Lista de transacciones
    private val _transactions = mutableStateListOf(
        Transaction("1", "Apple Store", -1299.00, "25 Feb, 2024", "Electrónicos", false, "Laptop"),
        Transaction("2", "Depósito Nómina", 3500.00, "20 Feb, 2024", "Salario", true, "Wallet"),
        Transaction("3", "Starbucks Coffee", -15.50, "19 Feb, 2024", "Comida", false, "Coffee"),
        Transaction("4", "Gimnasio Mensual", -45.00, "15 Feb, 2024", "Salud", false, "Fitness"),
        Transaction("5", "Amazon Prime", -14.99, "10 Feb, 2024", "Suscripción", false, "Shopping"),
        Transaction("6", "Transferencia Recibida", 250.00, "05 Feb, 2024", "Otros", true, "Swap")
    )
    val transactions: List<Transaction> get() = _transactions

    // Metas de ahorro
    private val _goals = mutableStateListOf(
        SavingsGoal("Viaje a Europa", 6000.0, 4200.0, "Flight", "Dic 2024", "#6366F1"),
        SavingsGoal("Fondo de Emergencia", 10000.0, 7500.0, "Shield", "Continua", "#10B981"),
        SavingsGoal("Nuevo MacBook Pro", 2500.0, 1250.0, "Laptop", "Jun 2024", "#F59E0B")
    )
    val goals: List<SavingsGoal> get() = _goals

    // Empresas ficticias para invertir
    val investmentCompanies = listOf(
        InvestmentCompany("TechNova", "Tecnología", 15.4, "#6366F1"),
        InvestmentCompany("EcoEnergy", "Energía", 8.2, "#10B981"),
        InvestmentCompany("GlobalFood", "Consumo", 4.5, "#F59E0B"),
        InvestmentCompany("FutureHealth", "Salud", 12.1, "#EF4444")
    )

    fun updateProfile(newName: String, newEmail: String) {
        userProfile = userProfile.copy(name = newName, email = newEmail)
    }

    fun updateProfileImage(uri: String) {
        userProfile = userProfile.copy(profileImageUri = uri)
    }

    fun updateBalance(newBalance: Double) {
        userProfile = userProfile.copy(balance = newBalance)
    }

    fun deposit(amount: Double) {
        userProfile = userProfile.copy(balance = userProfile.balance + amount)
        _transactions.add(0, Transaction(
            id = System.currentTimeMillis().toString(),
            title = "Depósito realizado",
            amount = amount,
            date = "Hoy",
            category = "Ingreso",
            isIncome = true,
            iconName = "Wallet"
        ))
    }

    fun addGoal(name: String, target: Double, icon: String, color: String) {
        _goals.add(SavingsGoal(name, target, 0.0, icon, "Nueva", color))
    }

    fun sendMoney(amount: Double) {
        if (userProfile.balance >= amount) {
            userProfile = userProfile.copy(balance = userProfile.balance - amount)
            _transactions.add(0, Transaction(
                id = System.currentTimeMillis().toString(),
                title = "Transferencia enviada",
                amount = -amount,
                date = "Hoy",
                category = "Gasto",
                isIncome = false,
                iconName = "Send"
            ))
        }
    }

    fun invest(company: InvestmentCompany, amount: Double) {
        if (userProfile.balance >= amount) {
            userProfile = userProfile.copy(balance = userProfile.balance - amount)
            _transactions.add(0, Transaction(
                id = System.currentTimeMillis().toString(),
                title = "Inversión en ${company.name}",
                amount = -amount,
                date = "Hoy",
                category = "Inversión",
                isIncome = false,
                iconName = "TrendingUp"
            ))
        }
    }

    // Estado de Autenticación
    var isLoggedIn by mutableStateOf(false)
        private set

    fun login() { isLoggedIn = true }
    fun logout() { isLoggedIn = false }
}

data class InvestmentCompany(
    val name: String,
    val sector: String,
    val growth: Double,
    val colorHex: String
)
