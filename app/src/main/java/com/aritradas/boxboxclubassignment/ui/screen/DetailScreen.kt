package com.aritradas.boxboxclubassignment.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aritradas.boxboxclubassignment.R
import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.ui.viewmodel.DetailViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val state = uiState) {
            is com.aritradas.boxboxclubassignment.ui.viewmodel.DetailUiState.Loading -> {
                CircularProgressIndicator(
                    color = Color.Green,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            is com.aritradas.boxboxclubassignment.ui.viewmodel.DetailUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { 
                        viewModel.retry()
                    }) {
                        Text("Retry")
                    }
                }
            }
            
            is com.aritradas.boxboxclubassignment.ui.viewmodel.DetailUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Section - Race Info Card
                    RaceDetailHeader(
                        race = state.race,
                        onBackClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Circuit Details Section
                    CircuitDetailsSection(
                        circuitId = state.race.circuitId,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Circuit Facts Section
                    CircuitFactsSection(
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun RaceDetailHeader(
    race: Race,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Green, Color.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Title
            Text(
                text = "Upcoming race",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Race Details
            Text(
                text = "Round ${race.round}",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
            
            Text(
                text = race.raceName,
                color = Color.White,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Text(
                text = formatCircuitName(race.circuitId),
                color = Color.Green,
                style = MaterialTheme.typography.titleMedium
            )
            
            Text(
                text = formatRaceDates(race.raceStartTime, race.raceEndTime),
                color = Color.Green,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Countdown Timer - use first upcoming session
            val firstUpcomingSession = race.sessions
                .filter { it.sessionState == "upcoming" }
                .minByOrNull { it.startTime }
            
            if (firstUpcomingSession != null) {
                Text(
                    text = "${firstUpcomingSession.sessionName} Starts in",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                CountdownTimer(
                    targetTime = firstUpcomingSession.startTime,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                // Fallback to race start time
                Text(
                    text = "Race Starts in",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                CountdownTimer(
                    targetTime = race.raceStartTime,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Circuit Graphic Placeholder (right side)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(200.dp, 300.dp)
                .padding(end = 24.dp)
        ) {
            // Placeholder for circuit graphic
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.Green.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
//                Image(
//                    painter = painterResource(R.drawable.sao_paulo_circuit),
//                    contentDescription = "Circuit Image"
//                )
            }
        }
    }
}

@Composable
fun CountdownTimer(
    targetTime: Long,
    modifier: Modifier = Modifier
) {
    val currentTime = remember { System.currentTimeMillis() / 1000 }
    var timeRemaining by remember { mutableLongStateOf(maxOf(0, targetTime - currentTime)) }
    
    LaunchedEffect(targetTime) {
        while (timeRemaining > 0) {
            delay(1000)
            val now = System.currentTimeMillis() / 1000
            timeRemaining = maxOf(0, targetTime - now)
        }
    }
    
    val days = timeRemaining / 86400
    val hours = (timeRemaining % 86400) / 3600
    val minutes = (timeRemaining % 3600) / 60
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CountdownItem(
            value = String.format("%02d", days),
            label = "Days"
        )
        CountdownItem(
            value = String.format("%02d", hours),
            label = "Hours"
        )
        CountdownItem(
            value = String.format("%02d", minutes),
            label = "Minutes"
        )
    }
}

@Composable
fun CountdownItem(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.Green,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun CircuitDetailsSection(
    circuitId: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "${formatCircuitName(circuitId)} Circuit",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = getCircuitDescription(circuitId),
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun CircuitFactsSection(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            text = "Circuit Facts",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Divider(
            color = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Text(
            text = "His brother Arthur Leclerc is currently set to race for DAMS in the 2023 F2 Championship",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Divider(
            color = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Text(
            text = "He's not related to Édouard Leclerc, the founder of a French supermarket chain",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

fun formatCircuitName(circuitId: String): String {
    return circuitId.split("_")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
        .replace("Sao", "São")
}

fun formatRaceDates(startTime: Long, endTime: Long): String {
    val startDate = Date(startTime * 1000)
    val endDate = Date(endTime * 1000)
    val format = SimpleDateFormat("d MMMM", Locale.getDefault())
    val endFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
    return "${format.format(startDate)} - ${endFormat.format(endDate)}"
}

fun getCircuitDescription(circuitId: String): String {
    // Static descriptions for different circuits
    return when (circuitId) {
        "sao_paulo" -> "The São Paulo Grand Prix takes place at the Interlagos circuit, one of the most challenging tracks in Formula 1. Known for its elevation changes and technical corners, it provides exciting racing action."
        "sakhir" -> "The Bahrain International Circuit, located in Sakhir, was designed by Hermann Tilke. Originally a camel farm, it features a 5.412 km layout with 15 corners, 3 DRS Zones, and 57 laps. The circuit has 6 alternative layouts."
        else -> "This circuit is one of the premier venues in Formula 1, featuring challenging corners and high-speed sections that test both drivers and cars to their limits."
    }
}

