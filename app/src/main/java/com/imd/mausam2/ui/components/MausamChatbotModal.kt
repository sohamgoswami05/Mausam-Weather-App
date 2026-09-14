package com.imd.mausam2.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.domain.ChatMessage
import com.imd.mausam2.domain.Weather7DayChatEngine
import com.imd.mausam2.ui.MausamViewModel
import com.imd.mausam2.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ThunderstormCloudSymbol(
    modifier: Modifier = Modifier,
    sizeDp: Int = 32
) {
    Box(
        modifier = modifier.size(sizeDp.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudQueue,
            contentDescription = "Cloud",
            tint = Color.White,
            modifier = Modifier.size((sizeDp * 0.85).dp)
        )
        Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = "Thunderstorm",
            tint = Color(0xFFFFD600),
            modifier = Modifier
                .size((sizeDp * 0.52).dp)
                .offset(x = 1.dp, y = (sizeDp * 0.15).dp)
        )
    }
}

@Composable
fun MausamChatbotFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "fabPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Surface(
        shape = CircleShape,
        color = Color.Transparent,
        modifier = modifier
            .scale(pulseScale)
            .size(62.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        shadowElevation = 14.dp,
        border = BorderStroke(2.dp, Brush.linearGradient(listOf(SkyCyan, NeonOrange)))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            ThunderstormCloudSymbol(sizeDp = 36)
        }
    }
}

@Composable
fun MausamChatbotModal(
    data: CompleteWeatherData,
    isHindi: Boolean,
    viewModel: MausamViewModel,
    onDismiss: () -> Unit
) {
    var messages by remember {
        mutableStateOf(
            listOf(Weather7DayChatEngine.generateInitialGreeting(data.location.cityName, isHindi))
        )
    }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val quickQuestions = remember(isHindi) {
        if (isHindi) {
            listOf(
                "🌧️ क्या आगामी 7 दिनों में बारिश होगी?",
                "☀️ इस सप्ताह का सबसे गर्म दिन कौन सा है?",
                "🏕️ सप्ताहांत (शनिवार-रविवार) का मौसम कैसा रहेगा?",
                "⚡ क्या कोई आंधी या तूफान की चेतावनी है?",
                "🏃 दौड़ व फिटनेस के लिए कौन से दिन उत्तम हैं?",
                "📅 पूरे 7 दिनों का मौसम सारांश बताएं"
            )
        } else {
            listOf(
                "🌧️ Any rain in the next 7 days?",
                "☀️ Which day will be the hottest?",
                "🏕️ Weekend trip weather forecast?",
                "⚡ Any thunderstorms or storm alerts?",
                "🏃 Best days for outdoor workout?",
                "📅 7-day overall weather summary"
            )
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return
        val userMsg = ChatMessage(text = query, isUser = true)
        val botAnswer = Weather7DayChatEngine.answerQuery(query, data, isHindi)
        messages = messages + userMsg + botAnswer
        inputText = ""

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0B1329), Color(0xFF020617)))),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xDD0F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.1f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SkyCyan.copy(alpha = 0.15f))
                                .border(1.dp, SkyCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ThunderstormCloudSymbol(sizeDp = 24)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "मौसम 7-दिवसीय AI चैटबॉट" else "Mausam 7-Day AI Chatbot",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SkyCyan.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        color = SkyCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${data.location.cityName} • 7-Day Weather Assistant",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick Question Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(quickQuestions) { question ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, SkyCyan.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { sendMessage(question) }
                    ) {
                        Text(
                            text = question,
                            color = SkyCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // Messages Feed
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    if (msg.isUser) {
                        UserChatBubble(text = msg.text)
                    } else {
                        BotChatBubble(
                            msg = msg,
                            onSpeak = {
                                viewModel.toggleAudio()
                            }
                        )
                    }
                }
            }

            // Input Bar at bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xEE0F172A),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shadowElevation = 14.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = {
                                Text(
                                    text = if (isHindi) "7 दिनों के मौसम के बारे में पूछें..." else "Ask about the next 7 days...",
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.White.copy(alpha = 0.06f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                                focusedBorderColor = SkyCyan,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { sendMessage(inputText) })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = { sendMessage(inputText) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (inputText.isNotBlank()) SkyCyan else Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Send",
                                tint = if (inputText.isNotBlank()) Color.Black else Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserChatBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp).copy(bottomEnd = androidx.compose.foundation.shape.CornerSize(4.dp)),
            color = SkyCyan.copy(alpha = 0.25f),
            border = BorderStroke(1.dp, SkyCyan.copy(alpha = 0.6f)),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun BotChatBubble(
    msg: ChatMessage,
    onSpeak: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(1.dp, SkyCyan.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            ThunderstormCloudSymbol(sizeDp = 22)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(18.dp).copy(topStart = androidx.compose.foundation.shape.CornerSize(4.dp)),
            color = Color(0xDD0F172A),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = msg.text,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                // Day highlights if present
                if (msg.dayHighlights.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        msg.dayHighlights.forEach { day ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.Black.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = day.dayName, color = SkyCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = day.condition, color = TextMuted, fontSize = 10.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = day.tempRange, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            text = "🌧️ ${day.rainChance}%",
                                            color = if (day.rainChance > 40) NeonOrange else TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
