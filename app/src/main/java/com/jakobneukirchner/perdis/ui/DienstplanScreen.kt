package com.jakobneukirchner.perdis.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
private fun DienstCard(
    dienst: Dienst
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                dienst.datum,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                dienst.bezeichnung,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            dienst.fahrten.forEach { fahrt ->
                FahrtPerlschnur(fahrt)
            }
        }
    }
}

@Composable
private fun FahrtPerlschnur(fahrt: Fahrt) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                fahrt.linie,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.weight(0.2f)
            )
            Text(
                fahrt.abfahrtszeit,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(0.2f)
            )
            Text(
                "→",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(0.1f)
            )
            Text(
                fahrt.ankunftszeit,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(0.2f)
            )
            Text(
                fahrt.ort,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(0.3f)
            )
        }
        Divider(modifier = Modifier.padding(vertical = 4.dp))
    }
}
