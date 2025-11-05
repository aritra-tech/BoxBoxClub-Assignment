package com.aritradas.boxboxclubassignment.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aritradas.boxboxclubassignment.R
import com.aritradas.boxboxclubassignment.data.model.Race
import com.aritradas.boxboxclubassignment.ui.viewmodel.DetailUiState
import com.aritradas.boxboxclubassignment.ui.viewmodel.DetailViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val state = uiState) {
            is DetailUiState.Loading -> {
                LoadingIndicator(
                    color = Color.Green,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is DetailUiState.Error -> {
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

            is DetailUiState.Success -> {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Upcoming race",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color(0xFF065E3B)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    RaceDetailHeader(
                        race = state.race,
                        modifier = Modifier.fillMaxWidth()
                    )

                    CircuitDetailsSection(
                        circuitId = state.race.circuitId,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF065E3B), Color.Black)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Round ${race.round}",
                color = Color.White.copy(alpha = 0.9f),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = formatRaceName(race.raceName),
                color = Color.White,
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .padding(top = 8.dp)
            )

            Text(
                text = formatCircuitName(race.circuitId),
                color = Color(0xFF02BB81),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                text = formatRaceDates(race.raceStartTime, race.raceEndTime),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            val firstUpcomingSession = race.sessions
                .filter { it.sessionState == "upcoming" }
                .minByOrNull { it.startTime }

            if (firstUpcomingSession != null) {
                Text(
                    text = "${firstUpcomingSession.sessionName} Starts in",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                CountdownTimer(
                    targetTime = firstUpcomingSession.startTime,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = "Race Starts in",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                CountdownTimer(
                    targetTime = race.raceStartTime,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Image(
            painter = painterResource(R.drawable.sao_circuit),
            contentDescription = "Circuit Image",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp, top = 24.dp)
                .size(width = 220.dp, height = 260.dp)
        )
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
            .padding(horizontal = 16.dp)
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
            .padding(horizontal = 16.dp, vertical = 16.dp)
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

    val startCal = Calendar.getInstance().apply { time = startDate }
    val endCal = Calendar.getInstance().apply { time = endDate }

    val sameMonth = startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH)
    val sameYear = startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)

    return if (sameMonth && sameYear) {
        val dayStart = startCal.get(Calendar.DAY_OF_MONTH)
        val dayEnd = endCal.get(Calendar.DAY_OF_MONTH)
        val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(startDate)
        "$dayStart - $dayEnd $monthName"
    } else if (sameYear) {
        val fmt = SimpleDateFormat("d MMM", Locale.getDefault())
        "${fmt.format(startDate)} - ${fmt.format(endDate)}"
    } else {
        val fmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        "${fmt.format(startDate)} - ${fmt.format(endDate)}"
    }
}

fun getCircuitDescription(circuitId: String): String {
    // Static descriptions for different circuits
    return when (circuitId) {
        "sao_paulo" -> "The São Paulo Grand Prix takes place at the Interlagos circuit, one of the most challenging tracks in Formula 1. Known for its elevation changes and technical corners, it provides exciting racing action."
        "sakhir" -> "The Bahrain International Circuit, located in Sakhir, was designed by Hermann Tilke. Originally a camel farm, it features a 5.412 km layout with 15 corners, 3 DRS Zones, and 57 laps. The circuit has 6 alternative layouts."
        else -> "This circuit is one of the premier venues in Formula 1, featuring challenging corners and high-speed sections that test both drivers and cars to their limits."
    }
}

private fun formatRaceName(raceName: String): String {
    return raceName.replace("Grand Prix", "GP").trim()
}

