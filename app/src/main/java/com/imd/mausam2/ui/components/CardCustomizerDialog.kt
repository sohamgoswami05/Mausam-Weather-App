package com.imd.mausam2.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.ui.theme.*

@Composable
fun CardCustomizerDialog(
    currentPersonas: Set<PersonaType>,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    onSave: (Set<PersonaType>) -> Unit
) {
    val selected = remember { mutableStateOf(currentPersonas.toMutableSet()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DeepBlue,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "कार्ड प्राथमिकताएं चुनें" else "Choose Priority Cards",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Text(
                    text = if (isHindi) "होमपेज पर अपनी रुचि के अनुसार कार्ड सक्रिय या निष्क्रिय करें" else "Toggle weather cards according to your daily lifestyle needs",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(PersonaType.values()) { persona ->
                        val isChecked = selected.value.contains(persona)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isChecked) SkyCyan.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isChecked) SkyCyan.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    val newSet = selected.value.toMutableSet()
                                    if (isChecked) {
                                        if (newSet.size > 1) newSet.remove(persona)
                                    } else {
                                        newSet.add(persona)
                                    }
                                    selected.value = newSet
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isHindi) persona.titleHi else persona.titleEn,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = getPersonaSubtitle(persona, isHindi),
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        val newSet = selected.value.toMutableSet()
                                        if (checked) newSet.add(persona) else if (newSet.size > 1) newSet.remove(persona)
                                        selected.value = newSet
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = SkyCyan,
                                        uncheckedColor = Color.White.copy(alpha = 0.4f),
                                        checkmarkColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save button
                Button(
                    onClick = {
                        onSave(selected.value)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkyCyan)
                ) {
                    Text(
                        text = if (isHindi) "प्राथमिकताएं सहेजें (${selected.value.size} चयनित)" else "Save Homepage Cards (${selected.value.size} Selected)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun getPersonaSubtitle(persona: PersonaType, isHindi: Boolean): String = when (persona) {
    PersonaType.HEALTH -> if (isHindi) "AQI, PM2.5, UV, एलर्जी व दमा अलर्ट" else "AQI, PM2.5, UV & Asthma risk"
    PersonaType.FITNESS -> if (isHindi) "दौड़ का समय, रन स्कोर, हीट स्ट्रेस" else "Best running hours, run score, heat stress"
    PersonaType.BEACH -> if (isHindi) "ज्वार-भाटा, लहर ऊंचाई, तटीय सुरक्षा" else "Tides, wave height & sea safety flag"
    PersonaType.AGRICULTURE -> if (isHindi) "मृदा नमी, कीटनाशक छिड़काव, मेघदूत" else "Soil moisture, spray window, Agromet"
    PersonaType.COMMUTE -> if (isHindi) "सड़क दृश्यता, कोहरा, जलभराव" else "Road visibility, fog meter, waterlogging"
    PersonaType.PARENTS -> if (isHindi) "सुबह 7-9 स्कूल कम्यूट, अचानक बारिश" else "Morning 7-9 AM school window, rain alerts"
    PersonaType.TRAVEL -> if (isHindi) "स्मार्ट पैकिंग, फ्लाइट देरी अलर्ट" else "Packing wizard & transit weather risk"
    PersonaType.SEVERE -> if (isHindi) "दामिनी वज्रपात, चक्रवात, आपातकालीन सेवा" else "Damini lightning sensor & cyclone warnings"
}
