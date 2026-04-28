package ni.edu.uam.appnavigatorcompose.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ni.edu.uam.appnavigatorcompose.components.*
import ni.edu.uam.appnavigatorcompose.ui.theme.*
import ni.edu.uam.appnavigatorcompose.viewmodels.FinanceViewModel
import ni.edu.uam.appnavigatorcompose.viewmodels.InvestmentCompany
import java.util.*

@Composable
fun HomeScreen(viewModel: FinanceViewModel, navController: NavController) {
    val user = viewModel.userProfile
    var showDepositDialog by remember { mutableStateOf(false) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var showSendDialog by remember { mutableStateOf(false) }
    var showInvestDialog by remember { mutableStateOf(false) }
    var showUpdateBalanceDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
    ) {
        item {
            HomeHeader(
                name = user.name,
                isDarkMode = viewModel.isDarkMode,
                onThemeToggle = { viewModel.toggleTheme() }
            )
        }

        item {
            PremiumBalanceCard(
                balance = user.balance,
                currency = user.currency,
                status = user.accountStatus,
                lastUpdate = user.lastUpdate,
                onEditClick = { showUpdateBalanceDialog = true }
            )
        }

        item {
            QuickActionsSection(
                onSendClick = { showSendDialog = true },
                onDepositClick = { showDepositDialog = true },
                onInvestClick = { showInvestDialog = true }
            )
        }

        item {
            SectionHeader(
                title = "Metas de Ahorro", 
                actionText = "Añadir",
                onActionClick = { showAddGoalDialog = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                viewModel.goals.take(2).forEach { goal ->
                    GoalCard(goal = goal, modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            SectionHeader(
                title = "Actividad Reciente", 
                actionText = "Ver más",
                onActionClick = { navController.navigate("transactions") }
            )
        }

        items(viewModel.transactions.take(4)) { transaction ->
            TransactionRow(transaction)
        }
    }

    if (showDepositDialog) {
        DepositDialog(
            onDismiss = { showDepositDialog = false },
            onConfirm = { amount ->
                viewModel.deposit(amount)
                showDepositDialog = false
            }
        )
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onConfirm = { name, target ->
                viewModel.addGoal(name, target, "Laptop", "#6366F1")
                showAddGoalDialog = false
            }
        )
    }

    if (showSendDialog) {
        SendMoneyDialog(
            onDismiss = { showSendDialog = false },
            onConfirm = { amount ->
                viewModel.sendMoney(amount)
                showSendDialog = false
            }
        )
    }

    if (showInvestDialog) {
        InvestmentDialog(
            companies = viewModel.investmentCompanies,
            onDismiss = { showInvestDialog = false },
            onInvest = { company, amount ->
                viewModel.invest(company, amount)
                showInvestDialog = false
            }
        )
    }

    if (showUpdateBalanceDialog) {
        UpdateBalanceDialog(
            currentBalance = user.balance,
            onDismiss = { showUpdateBalanceDialog = false },
            onConfirm = { newBalance ->
                viewModel.updateBalance(newBalance)
                showUpdateBalanceDialog = false
            }
        )
    }
}

@Composable
fun HomeHeader(name: String, isDarkMode: Boolean, onThemeToggle: () -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 0..11 -> "Buenos días"
        in 12..18 -> "Buenas tardes"
        else -> "Buenas noches"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = "$greeting,", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onThemeToggle,
                modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).size(44.dp)
            ) {
                Icon(if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { },
                modifier = Modifier.clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)).size(44.dp)
            ) {
                Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
fun PremiumBalanceCard(balance: Double, currency: String, status: String, lastUpdate: String, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.background(brush = Brush.linearGradient(colors = listOf(GradientStart, GradientEnd))).padding(24.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Column {
                        Text(text = "Saldo disponible", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
                        Text(
                            text = "$currency ${String.format(Locale.getDefault(), "%,.2f", balance)}",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar saldo", tint = Color.White.copy(alpha = 0.7f))
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text(text = "Número de cuenta", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
                        Text(text = "**** **** 4582", color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                    Text(text = status, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(onSendClick: () -> Unit, onDepositClick: () -> Unit, onInvestClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        QuickActionItem(Icons.AutoMirrored.Filled.Send, "Enviar", InfoBlue, onSendClick)
        QuickActionItem(Icons.Default.Add, "Depositar", SuccessGreen, onDepositClick)
        QuickActionItem(Icons.AutoMirrored.Filled.TrendingUp, "Invertir", GradientStart, onInvestClick)
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 4.dp)) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(28.dp))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun InvestmentDialog(companies: List<InvestmentCompany>, onDismiss: () -> Unit, onInvest: (InvestmentCompany, Double) -> Unit) {
    var selectedCompany by remember { mutableStateOf<InvestmentCompany?>(null) }
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Invertir Capital") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                companies.forEach { company ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                            .background(if (selectedCompany == company) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
                            .clickable { selectedCompany = company }.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedCompany == company, onClick = { selectedCompany = company })
                        Column {
                            Text(company.name, fontWeight = FontWeight.Bold)
                            Text("${company.sector} • +${company.growth}% anual", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto a invertir") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { selectedCompany?.let { c -> amount.toDoubleOrNull()?.let { a -> onInvest(c, a) } } }, enabled = selectedCompany != null && amount.isNotEmpty()) { Text("Invertir") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun UpdateBalanceDialog(currentBalance: Double, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf(currentBalance.toString()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Modificar Saldo") },
        text = {
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Nuevo saldo total") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = {
            Button(onClick = { amount.toDoubleOrNull()?.let { onConfirm(it) } }) { Text("Actualizar") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun SendMoneyDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enviar Dinero") },
        text = {
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto a enviar") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = { Button(onClick = { amount.toDoubleOrNull()?.let { onConfirm(it) } }) { Text("Enviar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun DepositDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Realizar Depósito") },
        text = {
            OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Monto a depositar") }, modifier = Modifier.fillMaxWidth())
        },
        confirmButton = { Button(onClick = { amount.toDoubleOrNull()?.let { onConfirm(it) } }) { Text("Confirmar") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Meta de Ahorro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre de la meta") })
                OutlinedTextField(value = target, onValueChange = { target = it }, label = { Text("Monto objetivo") })
            }
        },
        confirmButton = { Button(onClick = { target.toDoubleOrNull()?.let { onConfirm(name, it) } }) { Text("Crear") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
