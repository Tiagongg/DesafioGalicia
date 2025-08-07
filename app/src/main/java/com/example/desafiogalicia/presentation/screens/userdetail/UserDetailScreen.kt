package com.example.desafiogalicia.presentation.screens.userdetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.desafiogalicia.data.model.User
import com.example.desafiogalicia.presentation.components.LoadingIndicator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    user: User,
    onBackClick: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        viewModel.loadUserDetail(user)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Barra superior de navegación
        TopAppBar(
            title = { Text("Detalle del Usuario") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            actions = {
                IconButton(
                    onClick = { viewModel.toggleFavorite(user) }
                ) {
                    Icon(
                        imageVector = if (uiState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (uiState.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = if (uiState.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        // Contenido
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Button(
                        onClick = { viewModel.loadUserDetail(user) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        } else {
            UserDetailContent(
                user = user,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun UserDetailContent(
    user: User,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
                        // Foto de perfil
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(user.picture.large)
                .crossfade(true)
                .build(),
            contentDescription = "Foto de perfil de ${user.name.fullName}",
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(24.dp))

                        // Nombre
        Text(
            text = user.name.fullName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

                        // Tarjetas de información
        InfoCard(
            title = "Información Personal",
            items = listOf(
                "Género" to user.gender.replaceFirstChar { it.uppercase() },
                "Edad" to "${user.dob.age} años",
                "Fecha de Nacimiento" to formatDate(user.dob.date),
                "Teléfono" to user.phone,
                "Celular" to user.cell
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(
            title = "Contacto",
            items = listOf(
                "Email" to user.email,
                "Usuario" to user.login.username
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(
            title = "Dirección",
            items = listOf(
                "Dirección Completa" to user.location.fullAddress,
                "Ciudad" to user.location.city,
                "Estado/Provincia" to user.location.state,
                "País" to user.location.country,
                "Código Postal" to user.location.postcode.toString()
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        InfoCard(
            title = "Información Adicional",
            items = listOf(
                "Nacionalidad" to user.nat,
                "Registrado" to formatDate(user.registered.date),
                "Zona Horaria" to "${user.location.timezone.offset} (${user.location.timezone.description})"
            )
        )
    }
}

@Composable
private fun InfoCard(
    title: String,
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            items.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$label:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1.5f)
                    )
                }
            }
        }
    }
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        date?.let { outputFormat.format(it) } ?: dateString
    } catch (e: Exception) {
        dateString
    }
}