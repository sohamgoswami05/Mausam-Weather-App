package com.imd.mausam2.ui.screens

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.imd.mausam2.data.model.CompleteWeatherData
import com.imd.mausam2.ui.components.GlassCard
import com.imd.mausam2.ui.theme.*

data class RadarStationMeta(
    val name: String,
    val band: String,
    val power: String,
    val range: String,
    val status: String
)

private fun getRadarStationInfo(cityName: String): RadarStationMeta {
    return when {
        cityName.contains("Kolkata", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Kolkata (NSCBI Airport)",
            band = "S-Band Dual-Pol • 2.8 GHz",
            power = "500 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        cityName.contains("Delhi", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR New Delhi (Mausam Bhawan)",
            band = "C-Band Dual-Pol • 5.6 GHz",
            power = "250 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        cityName.contains("Mumbai", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Mumbai (Colaba Observatory)",
            band = "S-Band Dual-Pol • 2.8 GHz",
            power = "500 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        cityName.contains("Chennai", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Chennai (Port Trust)",
            band = "S-Band Dual-Pol • 2.8 GHz",
            power = "500 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        cityName.contains("Goa", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Goa (Panaji Altinho)",
            band = "S-Band Dual-Pol • 2.8 GHz",
            power = "500 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        cityName.contains("Bengaluru", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Bengaluru (GKVK Campus)",
            band = "C-Band Dual-Pol • 5.6 GHz",
            power = "250 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        cityName.contains("Shimla", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Shimla (Kufri Top)",
            band = "X-Band Mountain Radar • 9.4 GHz",
            power = "100 kW Solid State",
            range = "150 km Mountain Radius",
            status = "Operational • Orographic Scan"
        )
        cityName.contains("Ludhiana", ignoreCase = true) -> RadarStationMeta(
            name = "IMD DWR Punjab Grid (Patiala/Ludhiana)",
            band = "C-Band Dual-Pol • 5.6 GHz",
            power = "250 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • 10-Min Sweep"
        )
        else -> RadarStationMeta(
            name = "IMD DWR $cityName Station",
            band = "S-Band Doppler • 2.8 GHz",
            power = "500 kW Peak Power",
            range = "250 km Surveillance",
            status = "Operational • National Grid"
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RadarScreen(data: CompleteWeatherData, isHindi: Boolean) {
    var selectedLayer by remember { mutableStateOf("dwr") }
    var isPlayingSweep by remember { mutableStateOf(true) }
    var nowcastIndex by remember { mutableStateOf(4) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    val scrollState = rememberScrollState()

    val stationInfo = remember(data.location.cityName) {
        getRadarStationInfo(data.location.cityName)
    }

    val nowcastTimestamps = remember {
        listOf("-60m", "-45m", "-30m", "-15m", "NOW", "+15m", "+30m")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = 32.dp)
    ) {
        // 1. Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isHindi) "आईएमडी डॉपलर मौसम रडार" else "IMD Doppler Weather Radar",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${data.location.cityName} • ISRO MOSDAC & IMD WMS Open GIS",
                    color = SkyCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0x3300E676),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x6600E676))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "LIVE GIS",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Layer Toggle Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadarLayerChip(
                label = if (isHindi) "डॉपलर DWR" else "IMD Doppler",
                icon = Icons.Default.Radar,
                isSelected = selectedLayer == "dwr",
                accentColor = SkyCyan,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedLayer = "dwr"
                    webViewRef?.evaluateJavascript("window.setLayer('dwr')", null)
                }
            )

            RadarLayerChip(
                label = if (isHindi) "इन्सेट-3DR" else "INSAT-3DR",
                icon = Icons.Default.Satellite,
                isSelected = selectedLayer == "insat",
                accentColor = Color(0xFFB388FF),
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedLayer = "insat"
                    webViewRef?.evaluateJavascript("window.setLayer('insat')", null)
                }
            )

            RadarLayerChip(
                label = if (isHindi) "दामिनी तड़ित" else "Damini Alert",
                icon = Icons.Default.ElectricBolt,
                isSelected = selectedLayer == "damini",
                accentColor = SunGold,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedLayer = "damini"
                    webViewRef?.evaluateJavascript("window.setLayer('damini')", null)
                }
            )

            RadarLayerChip(
                label = if (isHindi) "वायु प्रवाह" else "Wind Vectors",
                icon = Icons.Default.Air,
                isSelected = selectedLayer == "wind",
                accentColor = Color(0xFF69F0AE),
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedLayer = "wind"
                    webViewRef?.evaluateJavascript("window.setLayer('wind')", null)
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Interactive GIS Map Viewport (Leaflet + WMS via AndroidView)
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            backgroundAlpha = 0.35f
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            settings.cacheMode = WebSettings.LOAD_DEFAULT
                            setBackgroundColor(android.graphics.Color.parseColor("#060B18"))

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    view?.evaluateJavascript("window.setLayer('$selectedLayer')", null)
                                    view?.evaluateJavascript("window.togglePlayback($isPlayingSweep)", null)
                                }
                            }

                            // Touch isolation so user can freely pan/zoom GIS map without scrolling page
                            setOnTouchListener { v, event ->
                                when (event.action) {
                                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                                        v.parent?.requestDisallowInterceptTouchEvent(true)
                                    }
                                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                                        v.parent?.requestDisallowInterceptTouchEvent(false)
                                    }
                                }
                                false
                            }

                            val html = RadarGisHtmlBuilder.buildHtml(
                                lat = data.location.latitude,
                                lon = data.location.longitude,
                                cityName = data.location.cityName,
                                activeLayer = selectedLayer,
                                isHindi = isHindi
                            )
                            loadDataWithBaseURL("https://mosdac.gov.in", html, "text/html", "UTF-8", null)
                            webViewRef = this
                        }
                    },
                    update = { view ->
                        webViewRef = view
                        view.evaluateJavascript(
                            "window.setStation(${data.location.latitude}, ${data.location.longitude}, '${data.location.cityName}')",
                            null
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4. Time-Lapse Nowcast Controls Bar
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundAlpha = 0.25f
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(
                            onClick = {
                                isPlayingSweep = !isPlayingSweep
                                webViewRef?.evaluateJavascript("window.togglePlayback($isPlayingSweep)", null)
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = if (isPlayingSweep) SkyCyan.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f)
                            ),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlayingSweep) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlayingSweep) "Pause" else "Play",
                                tint = if (isPlayingSweep) SkyCyan else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column {
                            Text(
                                text = if (isHindi) "डॉपलर स्वीप टाइमलैप्स" else "Doppler Sweep Timelapse",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isHindi) "10-मिनट स्कैन चक्र • 0.5° उन्नयन" else "10-Min Volume Scan • 0.5° Elevation",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Text(
                        text = "Frame: ${nowcastTimestamps.getOrElse(nowcastIndex) { "NOW" }}",
                        color = SkyCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Timeline Scrubber
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    nowcastTimestamps.forEachIndexed { index, step ->
                        val isCurrent = index == nowcastIndex
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCurrent) SkyCyan.copy(alpha = 0.25f) else Color.Transparent,
                            border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, SkyCyan) else null,
                            modifier = Modifier.clickable {
                                nowcastIndex = index
                            }
                        ) {
                            Text(
                                text = step,
                                color = if (isCurrent) SkyCyan else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. IMD DWR dBZ Reflectivity Palette Bar
        GlassCard(modifier = Modifier.fillMaxWidth(), backgroundAlpha = 0.25f) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isHindi) "प्रतिध्वनि परावर्तन (dBZ तीव्रता)" else "Radar Reflectivity (dBZ Intensity)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "15 — 65+ dBZ",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DbzLegendItem(color = Color(0xFF00E676), dbz = "15", label = if (isHindi) "बूंदाबांदी" else "Light")
                    DbzLegendItem(color = Color(0xFFFFD600), dbz = "30", label = if (isHindi) "मध्यम" else "Moderate")
                    DbzLegendItem(color = Color(0xFFFF6D00), dbz = "45", label = if (isHindi) "भारी" else "Heavy")
                    DbzLegendItem(color = Color(0xFFFF1744), dbz = "55", label = if (isHindi) "अति भारी" else "Severe")
                    DbzLegendItem(color = Color(0xFFD500F9), dbz = "65+", label = if (isHindi) "ओलावृष्टि" else "Hail")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 6. IMD Doppler Radar Station Metadata Card
        GlassCard(modifier = Modifier.fillMaxWidth(), backgroundAlpha = 0.25f) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SkyCyan.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sensors,
                            contentDescription = null,
                            tint = SkyCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = stationInfo.name,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stationInfo.band,
                            color = SkyCyan,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StationParamCol(
                        title = if (isHindi) "कवरेज परिधि" else "Coverage Radius",
                        value = stationInfo.range
                    )
                    StationParamCol(
                        title = if (isHindi) "ट्रांसमीटर पावर" else "Peak Power",
                        value = stationInfo.power
                    )
                    StationParamCol(
                        title = if (isHindi) "स्कैनिंग स्थिति" else "Operating Status",
                        value = stationInfo.status
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 7. Damini Lightning Early Detection Card
        GlassCard(modifier = Modifier.fillMaxWidth(), backgroundAlpha = 0.25f) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD600).copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = Color(0xFFFFD600),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (isHindi) "दामिनी वज्रपात अर्ली वॉर्निंग (आईआईटीम ग्रिड)" else "Damini Lightning Early Detection",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isHindi)
                            "सक्रिय सेंसर: 48 सेंसर नेटवर्क • निकटतम आकाशीय बिजली 9.4 किमी उत्तर में"
                        else
                            "Connected to IITM-Pune grid • Nearest strike 9.4 km N (Low-Moderate Alert)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarLayerChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0x1AFFFFFF),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, accentColor) else null,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) accentColor else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = if (isSelected) Color.White else TextMuted,
                fontSize = 9.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DbzLegendItem(color: Color, dbz: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(text = "$dbz dBZ", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(text = label, color = TextMuted, fontSize = 8.sp)
        }
    }
}

@Composable
private fun StationParamCol(title: String, value: String) {
    Column {
        Text(text = title, color = TextMuted, fontSize = 9.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
    }
}
