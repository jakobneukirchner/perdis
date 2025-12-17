package com.jakobneukirchner.perdis.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jakobneukirchner.perdis.model.Dienst
import com.jakobneukirchner.perdis.model.Fahrt
import com.jakobneukirchner.perdis.viewmodel.DienstplanViewModel

@Composable
fun DienstplanScreen(
    viewModel: DienstplanViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDienstplan()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perdis Dienstplan") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Abmelden")
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Fehler: ${state.errorMessage}")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(8.dp)
                ) {
                    items(state.dienste) { dienst ->
                        DienstCard(dienst)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DienstCard(dienst: Dienst) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(dienst.datum, style = MaterialTheme.typography.titleMedium)
            Text(dienst.bezeichnung, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            
            dienst.fahrten.forEach { fahrt ->
                FahrtPerlschnur(fahrt)
            }
        }
    }
}

@Composable
private fun FahrtPerlschnur(fahrt: Fahrt) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(fahrt.linie, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(0.2f))
            Text(fahrt.abfahrtszeit, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.2f))
            Text("→", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.1f))
            Text(fahrt.ankunftszeit, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.2f))
            Text(fahrt.ort, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(0.3f))
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
