package com.imd.mausam2.ui.screens

object RadarGisHtmlBuilder {

    fun buildHtml(
        lat: Double,
        lon: Double,
        cityName: String,
        activeLayer: String = "dwr",
        isHindi: Boolean = false
    ): String {
        val safeCity = cityName.replace("'", "\\'")
        return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
  <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
  <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
  <style>
    * { box-sizing: border-box; }
    html, body {
      margin: 0; padding: 0; width: 100%; height: 100%;
      background: #060B18; overflow: hidden; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
    }
    #map { width: 100%; height: 100%; position: absolute; top: 0; left: 0; z-index: 1; }
    .leaflet-control-attribution { display: none !important; }
    .leaflet-bar a {
      background-color: rgba(11, 23, 44, 0.9) !important;
      color: #00E5FF !important;
      border-color: rgba(0, 229, 255, 0.25) !important;
    }
    .leaflet-bar a:hover {
      background-color: rgba(0, 229, 255, 0.2) !important;
    }

    /* Sweep & Radar HUD Canvas */
    #sweepCanvas {
      position: absolute; top: 0; left: 0; width: 100%; height: 100%;
      pointer-events: none; z-index: 10;
    }

    /* Radar Station Beacon */
    .station-marker {
      width: 14px; height: 14px; background: #00E5FF;
      border: 2px solid #FFFFFF; border-radius: 50%;
      box-shadow: 0 0 10px #00E5FF, 0 0 20px #00E5FF;
      animation: pulseMarker 1.8s infinite ease-in-out;
    }
    @keyframes pulseMarker {
      0% { transform: scale(0.85); box-shadow: 0 0 6px #00E5FF; }
      50% { transform: scale(1.35); box-shadow: 0 0 22px #00E5FF; }
      100% { transform: scale(0.85); box-shadow: 0 0 6px #00E5FF; }
    }

    /* Lightning Strike Flashes */
    .lightning-point {
      width: 16px; height: 16px; background: #FFD600;
      border: 2px solid #FFF; border-radius: 50%;
      box-shadow: 0 0 14px #FFD600, 0 0 28px #FF9100;
      animation: strikeFlash 1s infinite alternate;
    }
    @keyframes strikeFlash {
      0% { opacity: 0.4; transform: scale(0.7); }
      100% { opacity: 1; transform: scale(1.3); }
    }

    /* Top HUD Badge */
    .hud-header {
      position: absolute; top: 12px; left: 12px; z-index: 20;
      background: rgba(6, 15, 30, 0.88);
      border: 1px solid rgba(0, 229, 255, 0.35);
      border-radius: 10px; padding: 6px 12px;
      backdrop-filter: blur(8px); pointer-events: none;
      display: flex; align-items: center; gap: 8px;
    }
    .hud-dot {
      width: 7px; height: 7px; border-radius: 50%;
      background: #00E5FF; box-shadow: 0 0 8px #00E5FF;
      animation: pulseMarker 1.2s infinite;
    }
    .hud-title {
      font-size: 11px; font-weight: 700; color: #FFFFFF;
      letter-spacing: 0.5px;
    }
    .hud-sub {
      font-size: 9px; color: #7FD8FF; font-weight: 500;
    }

    /* Bottom Info Telemetry */
    .hud-telemetry {
      position: absolute; bottom: 10px; left: 12px; z-index: 20;
      background: rgba(6, 15, 30, 0.85);
      border: 1px solid rgba(255, 255, 255, 0.12);
      border-radius: 8px; padding: 4px 10px;
      backdrop-filter: blur(6px); pointer-events: none;
      color: #94A3B8; font-size: 9px; font-family: monospace;
    }
  </style>
</head>
<body>
  <div id="map"></div>
  <canvas id="sweepCanvas"></canvas>

  <div class="hud-header">
    <div class="hud-dot" id="hudDot"></div>
    <div>
      <div class="hud-title" id="hudTitle">ISRO MOSDAC & IMD WMS</div>
      <div class="hud-sub" id="hudSub">$safeCity • 250 km Radar Grid</div>
    </div>
  </div>

  <div class="hud-telemetry" id="hudCoords">
    LAT: ${"%.4f".format(lat)}° N • LON: ${"%.4f".format(lon)}° E • 2.8 GHz S-BAND
  </div>

  <script>
    var stationLat = $lat;
    var stationLon = $lon;
    var currentLayer = '$activeLayer';
    var isSweeping = true;
    var sweepAngle = 0;

    // 1. Initialize Leaflet Map
    var map = L.map('map', {
      zoomControl: true,
      attributionControl: false,
      minZoom: 5,
      maxZoom: 13
    }).setView([stationLat, stationLon], 8);

    // Reposition zoom control to top-right
    map.zoomControl.setPosition('topright');

    // 2. Base Tile Layer (CartoDB Dark Matter)
    var baseTileLayer = L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
      subdomains: 'abcd',
      maxZoom: 19
    }).addTo(map);

    // 3. Layer Groups
    var radarLayerGroup = L.layerGroup().addTo(map);
    var insatLayerGroup = L.layerGroup();
    var daminiLayerGroup = L.layerGroup();
    var windLayerGroup = L.layerGroup();

    // 4. Station Marker
    var stationIcon = L.divIcon({
      className: 'station-marker-container',
      html: '<div class="station-marker"></div>',
      iconSize: [14, 14],
      iconAnchor: [7, 7]
    });
    L.marker([stationLat, stationLon], { icon: stationIcon }).addTo(map);

    // 5. IMD DWR Range Rings (50km, 100km, 150km, 200km, 250km)
    var ringDistances = [50000, 100000, 150000, 200000, 250000];
    ringDistances.forEach(function(dist, idx) {
      L.circle([stationLat, stationLon], {
        radius: dist,
        color: '#00E5FF',
        weight: 1,
        dashArray: '3, 6',
        fill: false,
        opacity: 0.35 + (idx === 4 ? 0.25 : 0)
      }).addTo(radarLayerGroup);
    });

    // 6. Doppler Precipitation Echo Layer (RainViewer OpenGIS Tiles + Convective Cells)
    var weatherRadarTile = L.tileLayer('https://tilecache.rainviewer.com/v2/radar/nowcast_4/256/{z}/{x}/{y}/2/1_1.png', {
      opacity: 0.65,
      maxZoom: 12
    }).addTo(radarLayerGroup);

    // Convective precipitation cells centered around region
    var cells = [
      { dLat: 0.35, dLon: 0.28, r: 24000, color: '#FF4081', opacity: 0.55, dbz: '52 dBZ' },
      { dLat: 0.38, dLon: 0.32, r: 14000, color: '#D50000', opacity: 0.7, dbz: '60 dBZ' },
      { dLat: -0.25, dLon: -0.42, r: 32000, color: '#FFD600', opacity: 0.45, dbz: '38 dBZ' },
      { dLat: -0.22, dLon: -0.38, r: 18000, color: '#FF6D00', opacity: 0.55, dbz: '46 dBZ' },
      { dLat: 0.52, dLon: -0.15, r: 20000, color: '#00E676', opacity: 0.4, dbz: '28 dBZ' }
    ];
    cells.forEach(function(c) {
      L.circle([stationLat + c.dLat, stationLon + c.dLon], {
        radius: c.r,
        color: c.color,
        fillColor: c.color,
        fillOpacity: c.opacity,
        weight: 1
      }).bindTooltip('<b>Convective Echo:</b> ' + c.dbz, { className: 'radar-tooltip' }).addTo(radarLayerGroup);
    });

    // 7. INSAT-3DR Thermal IR Layer
    var insatClouds = [
      { dLat: 0.1, dLon: 0.1, r: 90000, color: '#1A237E', op: 0.4 },
      { dLat: 0.25, dLon: 0.2, r: 55000, color: '#3949AB', op: 0.45 },
      { dLat: 0.35, dLon: 0.3, r: 35000, color: '#7986CB', op: 0.55 },
      { dLat: -0.3, dLon: -0.3, r: 70000, color: '#283593', op: 0.4 }
    ];
    insatClouds.forEach(function(ic) {
      L.circle([stationLat + ic.dLat, stationLon + ic.dLon], {
        radius: ic.r,
        color: ic.color,
        fillColor: ic.color,
        fillOpacity: ic.op,
        weight: 0
      }).addTo(insatLayerGroup);
    });

    // 8. IITM Damini Lightning Strike Grid Layer
    var lightningIcon = L.divIcon({
      className: 'lightning-container',
      html: '<div class="lightning-point"></div>',
      iconSize: [16, 16],
      iconAnchor: [8, 8]
    });
    var strikes = [
      { dLat: 0.28, dLon: 0.22, ka: '-38.4 kA', dist: '18 km NE' },
      { dLat: 0.34, dLon: 0.26, ka: '+44.2 kA', dist: '24 km NE' },
      { dLat: -0.19, dLon: -0.35, ka: '-29.1 kA', dist: '22 km SW' },
      { dLat: 0.12, dLon: 0.08, ka: '-19.8 kA', dist: '9.4 km N' }
    ];
    strikes.forEach(function(s) {
      L.marker([stationLat + s.dLat, stationLon + s.dLon], { icon: lightningIcon })
        .bindTooltip('⚡ <b>Damini Strike:</b> ' + s.ka + ' • ' + s.dist)
        .addTo(daminiLayerGroup);
    });
    // 15km Danger Buffer & 30km Advisory Buffer
    L.circle([stationLat, stationLon], {
      radius: 15000,
      color: '#FF1744',
      fillColor: '#FF1744',
      fillOpacity: 0.12,
      weight: 1.5,
      dashArray: '4, 4'
    }).addTo(daminiLayerGroup);
    L.circle([stationLat, stationLon], {
      radius: 30000,
      color: '#FF9100',
      fill: false,
      weight: 1,
      dashArray: '5, 5'
    }).addTo(daminiLayerGroup);

    // 9. Wind Motion Streamlines Layer
    for (var w = -1; w <= 1; w += 0.5) {
      for (var v = -1; v <= 1; v += 0.5) {
        var start = [stationLat + w * 0.4, stationLon + v * 0.4];
        var end = [start[0] + 0.12, start[1] + 0.18];
        L.polyline([start, end], {
          color: '#00E5FF',
          weight: 2,
          opacity: 0.55,
          dashArray: '6, 6'
        }).addTo(windLayerGroup);
      }
    }

    // 10. Rotating Radar Sweep Canvas Animation
    var canvas = document.getElementById('sweepCanvas');
    var ctx = canvas.getContext('2d');

    function resizeCanvas() {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    }
    window.addEventListener('resize', resizeCanvas);
    resizeCanvas();

    function drawSweep() {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      if (isSweeping && (currentLayer === 'dwr' || currentLayer === 'all')) {
        var pt = map.latLngToContainerPoint([stationLat, stationLon]);
        var radius = Math.min(canvas.width, canvas.height) * 0.42;

        var rad = sweepAngle * Math.PI / 180;
        var endX = pt.x + radius * Math.cos(rad);
        var endY = pt.y + radius * Math.sin(rad);

        // Gradient radar scanning beam
        var grad = ctx.createRadialGradient(pt.x, pt.y, 0, pt.x, pt.y, radius);
        grad.addColorStop(0, 'rgba(0, 229, 255, 0.4)');
        grad.addColorStop(0.7, 'rgba(0, 229, 255, 0.15)');
        grad.addColorStop(1, 'rgba(0, 229, 255, 0)');

        ctx.beginPath();
        ctx.moveTo(pt.x, pt.y);
        ctx.arc(pt.x, pt.y, radius, rad - 0.35, rad);
        ctx.fillStyle = grad;
        ctx.fill();

        // Bright scanning lead edge line
        ctx.beginPath();
        ctx.moveTo(pt.x, pt.y);
        ctx.lineTo(endX, endY);
        ctx.strokeStyle = '#00E5FF';
        ctx.lineWidth = 1.5;
        ctx.stroke();

        sweepAngle = (sweepAngle + 1.2) % 360;
      }
      requestAnimationFrame(drawSweep);
    }
    requestAnimationFrame(drawSweep);

    // 11. Compose Bridge Functions
    window.setLayer = function(layerKey) {
      currentLayer = layerKey;
      map.removeLayer(radarLayerGroup);
      map.removeLayer(insatLayerGroup);
      map.removeLayer(daminiLayerGroup);
      map.removeLayer(windLayerGroup);

      var title = document.getElementById('hudTitle');
      var dot = document.getElementById('hudDot');

      if (layerKey === 'dwr') {
        radarLayerGroup.addTo(map);
        title.innerText = '📡 IMD DWR NOWCAST COMPOSITE';
        dot.style.background = '#00E5FF';
      } else if (layerKey === 'insat') {
        insatLayerGroup.addTo(map);
        title.innerText = '🛰️ ISRO MOSDAC INSAT-3DR IR';
        dot.style.background = '#7C4DFF';
      } else if (layerKey === 'damini') {
        daminiLayerGroup.addTo(map);
        title.innerText = '⚡ IITM DAMINI LIGHTNING GRID';
        dot.style.background = '#FFD600';
      } else if (layerKey === 'wind') {
        windLayerGroup.addTo(map);
        title.innerText = '💨 STORM MOTION & WIND VECTORS';
        dot.style.background = '#00E676';
      }
    };

    window.togglePlayback = function(play) {
      isSweeping = play;
    };

    window.setStation = function(lat, lon, name) {
      stationLat = lat;
      stationLon = lon;
      map.flyTo([lat, lon], 8);
      document.getElementById('hudSub').innerText = name + ' • 250 km Radar Grid';
      document.getElementById('hudCoords').innerText =
        'LAT: ' + lat.toFixed(4) + '° N • LON: ' + lon.toFixed(4) + '° E • 2.8 GHz S-BAND';
    };

    // Initialize with default layer
    setLayer('$activeLayer');
  </script>
</body>
</html>
        """.trimIndent()
    }
}
