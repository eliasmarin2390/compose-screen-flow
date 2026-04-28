package ni.edu.uam.appnavigatorcompose.models

import java.util.Date

/**
 * Representa al usuario del sistema (Encapsulamiento y POO)
 */
data class UserProfile(
    val id: String = "1",
    var name: String = "Juan Pérez",
    var email: String = "juan.perez@ahorro.com",
    var profileImageUri: String? = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=250&q=80",
    var balance: Double = 2450.00,
    val currency: String = "USD",
    val accountStatus: String = "Activa",
    val lastUpdate: String = "Hace 2 minutos"
)

/**
 * Representa una transacción (Abstracción)
 */
data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val date: String,
    val category: String,
    val isIncome: Boolean,
    val iconName: String // Para mayor flexibilidad visual
)

/**
 * Representa una meta de ahorro
 */
data class SavingsGoal(
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val iconName: String,
    val deadline: String,
    val colorHex: String = "#8B5CF6"
)
