package ni.edu.uam.appnavigatorcompose.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import ni.edu.uam.appnavigatorcompose.ui.theme.AccentPurple
import ni.edu.uam.appnavigatorcompose.viewmodels.FinanceViewModel

@Composable
fun ProfileScreen(viewModel: FinanceViewModel) {
    val user = viewModel.userProfile
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar Section
        Box(contentAlignment = Alignment.BottomEnd) {
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                uri?.let { viewModel.updateProfileImage(it.toString()) }
            }

            AsyncImage(
                model = user.profileImageUri ?: "https://via.placeholder.com/150",
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(2.dp, AccentPurple, CircleShape),
                contentScale = ContentScale.Crop
            )
            FloatingActionButton(
                onClick = { launcher.launch("image/*") },
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                containerColor = AccentPurple,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto", modifier = Modifier.size(20.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = user.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = user.email, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

        Spacer(modifier = Modifier.height(32.dp))

        // Menu Options
        ProfileMenuItem(Icons.Default.Edit, "Editar Perfil") { showEditDialog = true }
        ProfileMenuItem(Icons.AutoMirrored.Filled.Logout, "Cerrar Sesión", isDestructive = true) {
            viewModel.logout()
        }
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = user.name,
            currentEmail = user.email,
            onDismiss = { showEditDialog = false },
            onConfirm = { name, email ->
                viewModel.updateProfile(name, email)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) Color.Red else MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                color = if (isDestructive) Color.Red else MaterialTheme.colorScheme.onBackground
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileDialog(
    currentName: String,
    currentEmail: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var email by remember { mutableStateOf(currentEmail) }
    
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name, 
                    onValueChange = { 
                        name = it 
                        nameError = false
                    }, 
                    label = { Text("Nombre") },
                    isError = nameError,
                    supportingText = { if (nameError) Text("El nombre no puede estar vacío") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email, 
                    onValueChange = { 
                        email = it 
                        emailError = false
                    }, 
                    label = { Text("Email") },
                    isError = emailError,
                    supportingText = { if (emailError) Text("Ingresa un correo válido y diferente al actual") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    val isNameValid = name.trim().isNotEmpty()
                    val isEmailValid = email.isNotEmpty() && 
                                     android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                                     email != currentEmail
                    
                    if (isNameValid && isEmailValid) {
                        onConfirm(name, email)
                    } else {
                        nameError = !isNameValid
                        emailError = !isEmailValid
                    }
                }
            ) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
