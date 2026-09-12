package com.imd.mausam2.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imd.mausam2.data.model.PersonaType
import com.imd.mausam2.ui.theme.SkyCyan

@Composable
fun PersonaQuickSelector(
    activePersonas: Set<PersonaType>,
    selectedFilter: PersonaType?,
    onFilterSelected: (PersonaType?) -> Unit,
    isHindi: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isHindi) "आपकी प्राथमिकताएं (Personalized Cards)" else "Your Priority Cards",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = if (isHindi) "${activePersonas.size} सक्रिय" else "${activePersonas.size} Active",
                color = SkyCyan,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" chip
            item {
                PersonaChip(
                    label = if (isHindi) "सभी कार्ड" else "All Cards",
                    isSelected = selectedFilter == null,
                    icon = Icons.Default.GridView,
                    onClick = { onFilterSelected(null) }
                )
            }

            items(PersonaType.values()) { persona ->
                val isPresent = activePersonas.contains(persona)
                val icon = when (persona) {
                    PersonaType.HEALTH -> Icons.Default.Favorite
                    PersonaType.FITNESS -> Icons.Default.DirectionsRun
                    PersonaType.BEACH -> Icons.Default.Waves
                    PersonaType.AGRICULTURE -> Icons.Default.Yard
                    PersonaType.COMMUTE -> Icons.Default.DirectionsCar
                    PersonaType.PARENTS -> Icons.Default.School
                    PersonaType.TRAVEL -> Icons.Default.FlightTakeoff
                    PersonaType.SEVERE -> Icons.Default.Bolt
                }

                PersonaChip(
                    label = if (isHindi) persona.titleHi else persona.titleEn,
                    isSelected = selectedFilter == persona,
                    isFaded = !isPresent,
                    icon = icon,
                    onClick = { onFilterSelected(if (selectedFilter == persona) null else persona) }
                )
            }
        }
    }
}

@Composable
private fun PersonaChip(
    label: String,
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isFaded: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) SkyCyan.copy(alpha = 0.25f) else Color.White.copy(alpha = if (isFaded) 0.04f else 0.10f),
        border = BorderStroke(
            1.dp,
            if (isSelected) SkyCyan else Color.White.copy(alpha = if (isFaded) 0.08f else 0.18f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) SkyCyan else if (isFaded) Color.White.copy(alpha = 0.4f) else Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = if (isSelected) SkyCyan else if (isFaded) Color.White.copy(alpha = 0.4f) else Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
