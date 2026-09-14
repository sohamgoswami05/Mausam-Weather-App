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
  <style>
    * { box-sizing: border-box; -webkit-touch-callout: none; user-select: none; }
    html, body {
      margin: 0; padding: 0; width: 100%; height: 100%;
      background: #060C18; overflow: hidden; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, monospace;
      color: #E2E8F0;
    }
    #radarContainer {
      position: absolute; top: 0; left: 0; width: 100%; height: 100%;
      background: radial-gradient(circle at 50% 50%, #0D1B2E 0%, #060C18 90%);
    }
    canvas {
      position: absolute; top: 0; left: 0; width: 100%; height: 100%;
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
      width: 8px; height: 8px; border-radius: 50%;
      background: #00E5FF; box-shadow: 0 0 10px #00E5FF;
      animation: pulseMarker 1.4s infinite ease-in-out;
    }
    .hud-title {
      font-size: 11px; font-weight: 700; color: #FFFFFF;
      letter-spacing: 0.5px;
    }
    .hud-sub {
      font-size: 9px; color: #7FD8FF; font-weight: 500;
    }
    @keyframes pulseMarker {
      0% { transform: scale(0.85); opacity: 0.7; }
      50% { transform: scale(1.3); opacity: 1; }
      100% { transform: scale(0.85); opacity: 0.7; }
    }
    /* Bottom Telemetry */
    .hud-telemetry {
      position: absolute; bottom: 10px; left: 12px; z-index: 20;
      background: rgba(6, 15, 30, 0.85);
      border: 1px solid rgba(255, 255, 255, 0.12);
      border-radius: 8px; padding: 4px 10px;
      backdrop-filter: blur(6px); pointer-events: none;
      color: #94A3B8; font-size: 9px; font-family: monospace;
    }
    /* Range Compass Labels */
    .range-label {
      position: absolute; color: rgba(0, 229, 255, 0.6);
      font-size: 9px; font-weight: bold; pointer-events: none; z-index: 15;
    }
  </style>
</head>
<body>
  <div id="radarContainer">
    <canvas id="gisCanvas"></canvas>
  </div>

  <div class="hud-header">
    <div class="hud-dot" id="hudDot"></div>
    <div>
      <div class="hud-title" id="hudTitle">IMD DWR NOWCAST COMPOSITE</div>
      <div class="hud-sub" id="hudSub">$safeCity • 250 km Surveillance Grid</div>
    </div>
  </div>

  <div class="hud-telemetry" id="hudCoords">
    LAT: ${"%.4f".format(lat)}° N • LON: ${"%.4f".format(lon)}° E • 2.8 GHz S-BAND
  </div>

  <script>
    (function() {
      const canvas = document.getElementById('gisCanvas');
      const ctx = canvas.getContext('2d');

      let currentLayer = '$activeLayer';
      let isSweeping = true;
      let sweepAngle = 0;
      let nowcastFrame = 4; // 0:-60m, 1:-45m, 2:-30m, 3:-15m, 4:NOW, 5:+15m, 6:+30m
      let cityName = '$safeCity';
      let stationLat = $lat;
      let stationLon = $lon;

      // Pan and Zoom
      let scale = 1.0;
      let panX = 0;
      let panY = 0;
      let isDragging = false;
      let startX = 0;
      let startY = 0;
      let initialDist = 0;

      function resize() {
        const dpr = window.devicePixelRatio || 1;
        canvas.width = window.innerWidth * dpr;
        canvas.height = window.innerHeight * dpr;
        ctx.resetTransform();
        ctx.scale(dpr, dpr);
      }
      window.addEventListener('resize', resize);
      resize();

      // Touch / Drag Handlers
      window.addEventListener('touchstart', function(e) {
        if (e.touches.length === 1) {
          isDragging = true;
          startX = e.touches[0].clientX - panX;
          startY = e.touches[0].clientY - panY;
        } else if (e.touches.length === 2) {
          initialDist = Math.hypot(
            e.touches[0].clientX - e.touches[1].clientX,
            e.touches[0].clientY - e.touches[1].clientY
          );
        }
      }, { passive: false });

      window.addEventListener('touchmove', function(e) {
        if (e.touches.length === 1 && isDragging) {
          panX = e.touches[0].clientX - startX;
          panY = e.touches[0].clientY - startY;
          // Restrict pan bounds
          const maxPan = 180 * scale;
          panX = Math.max(-maxPan, Math.min(maxPan, panX));
          panY = Math.max(-maxPan, Math.min(maxPan, panY));
        } else if (e.touches.length === 2) {
          const dist = Math.hypot(
            e.touches[0].clientX - e.touches[1].clientX,
            e.touches[0].clientY - e.touches[1].clientY
          );
          if (initialDist > 0) {
            scale *= (dist / initialDist);
            scale = Math.max(0.75, Math.min(2.5, scale));
            initialDist = dist;
          }
        }
      }, { passive: false });

      window.addEventListener('touchend', function() {
        isDragging = false;
        initialDist = 0;
      });

      // Wind Particle System
      const particleCount = 140;
      const particles = [];
      for (let i = 0; i < particleCount; i++) {
        particles.push({
          x: (Math.random() - 0.5) * 480,
          y: (Math.random() - 0.5) * 480,
          age: Math.floor(Math.random() * 80),
          maxAge: 70 + Math.floor(Math.random() * 50),
          speed: 1.2 + Math.random() * 1.6
        });
      }

      // Convective Echo Cells Definition (Rel to radar center in km)
      const stormCells = [
        { dx: 45, dy: -35, r: 28, maxDbz: 62, vx: -0.6, vy: 0.4 },
        { dx: 70, dy: -15, r: 22, maxDbz: 54, vx: -0.5, vy: 0.5 },
        { dx: -55, dy: 60, r: 38, maxDbz: 46, vx: -0.7, vy: 0.3 },
        { dx: -25, dy: 30, r: 18, maxDbz: 50, vx: -0.6, vy: 0.4 },
        { dx: 95, dy: 85, r: 42, maxDbz: 42, vx: -0.4, vy: 0.2 },
        { dx: -80, dy: -70, r: 30, maxDbz: 38, vx: -0.5, vy: 0.3 }
      ];

      // Damini Lightning Strikes
      const strikes = [
        { dx: 38, dy: -28, ka: -42.4, type: '-CG', time: '2m ago' },
        { dx: 48, dy: -18, ka: +55.1, type: '+CG', time: '4m ago' },
        { dx: -45, dy: 52, ka: -31.8, type: '-CG', time: '1m ago' },
        { dx: -20, dy: 24, ka: -19.5, type: 'IC',  time: '6m ago' }
      ];

      function dbzToColor(dbz, alpha) {
        if (dbz >= 60) return 'rgba(213, 0, 249, ' + alpha + ')'; // Purple / Hail
        if (dbz >= 50) return 'rgba(255, 23, 68, ' + alpha + ')';  // Deep Red / Extreme
        if (dbz >= 40) return 'rgba(255, 109, 0, ' + alpha + ')';  // Orange / Heavy
        if (dbz >= 30) return 'rgba(255, 214, 0, ' + alpha + ')';  // Yellow / Moderate
        if (dbz >= 20) return 'rgba(0, 230, 118, ' + alpha + ')';  // Green / Light
        return 'rgba(0, 229, 255, ' + alpha + ')';                 // Cyan / Trace
      }

      function render() {
        const w = window.innerWidth;
        const h = window.innerHeight;
        ctx.clearRect(0, 0, w, h);

        const cx = w / 2 + panX;
        const cy = h / 2 + panY;
        const maxRadius = Math.min(w, h) * 0.44 * scale;

        // 1. Regional GIS Shoreline & River Geography
        ctx.save();
        ctx.beginPath();
        // Coastal outline simulation tailored to Indian eastern/western littoral
        const isEastCoast = stationLon > 80;
        if (isEastCoast) {
          // Bay of Bengal shoreline curve running SW to NE
          ctx.moveTo(cx + maxRadius * 0.45, cy + maxRadius * 0.95);
          ctx.bezierCurveTo(
            cx + maxRadius * 0.35, cy + maxRadius * 0.20,
            cx + maxRadius * 0.65, cy - maxRadius * 0.30,
            cx + maxRadius * 0.85, cy - maxRadius * 0.95
          );
          ctx.strokeStyle = 'rgba(0, 229, 255, 0.22)';
          ctx.lineWidth = 1.5;
          ctx.stroke();

          // River drainage network
          ctx.beginPath();
          ctx.moveTo(cx - maxRadius * 0.25, cy - maxRadius * 0.95);
          ctx.quadraticCurveTo(cx - maxRadius * 0.05, cy - maxRadius * 0.20, cx + maxRadius * 0.20, cy + maxRadius * 0.85);
          ctx.strokeStyle = 'rgba(56, 189, 248, 0.16)';
          ctx.lineWidth = 1.2;
          ctx.stroke();
        } else {
          // West coast running NNW to SSE
          ctx.moveTo(cx - maxRadius * 0.45, cy - maxRadius * 0.95);
          ctx.bezierCurveTo(
            cx - maxRadius * 0.30, cy,
            cx - maxRadius * 0.55, cy + maxRadius * 0.50,
            cx - maxRadius * 0.25, cy + maxRadius * 0.95
          );
          ctx.strokeStyle = 'rgba(0, 229, 255, 0.22)';
          ctx.lineWidth = 1.5;
          ctx.stroke();
        }
        ctx.restore();

        // 2. Range Rings (50, 100, 150, 200, 250 km)
        const ringSteps = [0.2, 0.4, 0.6, 0.8, 1.0];
        const kmLabels = ['50 km', '100 km', '150 km', '200 km', '250 km MAX'];
        ringSteps.forEach((step, idx) => {
          const r = maxRadius * step;
          ctx.beginPath();
          ctx.arc(cx, cy, r, 0, Math.PI * 2);
          ctx.strokeStyle = idx === 4 ? 'rgba(0, 229, 255, 0.45)' : 'rgba(0, 229, 255, 0.18)';
          ctx.lineWidth = idx === 4 ? 1.5 : 0.8;
          ctx.setLineDash(idx === 4 ? [] : [4, 6]);
          ctx.stroke();
          ctx.setLineDash([]);

          // Range text
          ctx.fillStyle = 'rgba(0, 229, 255, 0.55)';
          ctx.font = '9px monospace';
          ctx.fillText(kmLabels[idx], cx + 4, cy - r + 11);
        });

        // 3. Azimuth Radials (every 30 deg)
        for (let deg = 0; deg < 360; deg += 30) {
          const rad = deg * Math.PI / 180;
          ctx.beginPath();
          ctx.moveTo(cx, cy);
          ctx.lineTo(cx + maxRadius * Math.cos(rad), cy + maxRadius * Math.sin(rad));
          ctx.strokeStyle = (deg % 90 === 0) ? 'rgba(0, 229, 255, 0.25)' : 'rgba(0, 229, 255, 0.08)';
          ctx.lineWidth = 0.8;
          ctx.stroke();

          // Cardinal azimuth labels
          if (deg % 90 === 0) {
            const card = deg === 0 ? 'E 090°' : deg === 90 ? 'S 180°' : deg === 180 ? 'W 270°' : 'N 360°';
            ctx.fillStyle = 'rgba(0, 229, 255, 0.75)';
            ctx.font = 'bold 9px monospace';
            ctx.fillText(card, cx + (maxRadius + 14) * Math.cos(rad) - 14, cy + (maxRadius + 14) * Math.sin(rad) + 3);
          }
        }

        // ==========================================
        // 4. LAYER SPECIFIC SCIENTIFIC RENDERING
        // ==========================================

        // Time offset multiplier for nowcast scrubbing (-60m to +30m)
        const frameOffset = (nowcastFrame - 4) * 15; // in minutes

        if (currentLayer === 'dwr' || currentLayer === 'all') {
          // A. IMD Doppler Weather Radar Convective Echoes
          stormCells.forEach(c => {
            // Extrapolate cell position based on velocity & selected nowcast frame
            const curX = cx + (c.dx + c.vx * frameOffset) * (maxRadius / 250);
            const curY = cy + (c.dy + c.vy * frameOffset) * (maxRadius / 250);
            const cr = c.r * (maxRadius / 250);

            // Multi-tier Gaussian reflectivity gradient (outer light rain to core hail)
            const g = ctx.createRadialGradient(curX, curY, 0, curX, curY, cr);
            g.addColorStop(0.0, dbzToColor(c.maxDbz, 0.85));
            g.addColorStop(0.35, dbzToColor(c.maxDbz - 12, 0.65));
            g.addColorStop(0.70, dbzToColor(c.maxDbz - 24, 0.40));
            g.addColorStop(1.0, 'rgba(0, 229, 255, 0)');

            ctx.beginPath();
            ctx.arc(curX, curY, cr, 0, Math.PI * 2);
            ctx.fillStyle = g;
            ctx.fill();

            // Cell core contour ring
            ctx.beginPath();
            ctx.arc(curX, curY, cr * 0.45, 0, Math.PI * 2);
            ctx.strokeStyle = dbzToColor(c.maxDbz, 0.8);
            ctx.lineWidth = 1;
            ctx.stroke();

            // Cell label
            ctx.fillStyle = '#FFFFFF';
            ctx.font = 'bold 8px monospace';
            ctx.fillText(c.maxDbz + ' dBZ', curX - 12, curY + 3);
          });

          // B. 360° Rotating Doppler Sweep Beam
          if (isSweeping) {
            const rad = sweepAngle * Math.PI / 180;
            const endX = cx + maxRadius * Math.cos(rad);
            const endY = cy + maxRadius * Math.sin(rad);

            const sweepGrad = ctx.createRadialGradient(cx, cy, 0, cx, cy, maxRadius);
            sweepGrad.addColorStop(0, 'rgba(0, 229, 255, 0.45)');
            sweepGrad.addColorStop(0.65, 'rgba(0, 229, 255, 0.18)');
            sweepGrad.addColorStop(1, 'rgba(0, 229, 255, 0)');

            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.arc(cx, cy, maxRadius, rad - 0.42, rad);
            ctx.fillStyle = sweepGrad;
            ctx.fill();

            // Lead edge beam
            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.lineTo(endX, endY);
            ctx.strokeStyle = '#00E5FF';
            ctx.lineWidth = 1.8;
            ctx.shadowColor = '#00E5FF';
            ctx.shadowBlur = 8;
            ctx.stroke();
            ctx.shadowBlur = 0;

            sweepAngle = (sweepAngle + 1.25) % 360;
          }
        } else if (currentLayer === 'insat') {
          // B. ISRO MOSDAC INSAT-3DR Satellite Thermal IR & Cloud Top Temperature
          const cloudDecks = [
            { x: -30, y: -40, rx: 110, ry: 70, tMin: -72, tMax: -35 },
            { x: 50,  y: 40,  rx: 90,  ry: 80, tMin: -65, tMax: -28 },
            { x: 70,  y: -60, rx: 80,  ry: 60, tMin: -58, tMax: -20 }
          ];

          cloudDecks.forEach(cd => {
            const px = cx + cd.x * (maxRadius / 250);
            const py = cy + cd.y * (maxRadius / 250);
            const rx = cd.rx * (maxRadius / 250);
            const ry = cd.ry * (maxRadius / 250);

            const g = ctx.createRadialGradient(px, py, 0, px, py, rx);
            g.addColorStop(0.0, 'rgba(124, 77, 255, 0.85)'); // Deep Convective Core (< -70°C)
            g.addColorStop(0.35, 'rgba(68, 138, 255, 0.60)'); // Cold Anvil (-50°C)
            g.addColorStop(0.70, 'rgba(0, 188, 212, 0.35)');  // Mid Level (-35°C)
            g.addColorStop(1.0, 'rgba(0, 0, 0, 0)');

            ctx.save();
            ctx.beginPath();
            ctx.ellipse(px, py, rx, ry, 0.35, 0, Math.PI * 2);
            ctx.fillStyle = g;
            ctx.fill();
            ctx.restore();

            ctx.fillStyle = '#E0E7FF';
            ctx.font = '9px monospace';
            ctx.fillText(cd.tMin + '°C Core', px - 18, py);
          });

          // Orbital trajectory marker
          ctx.strokeStyle = 'rgba(179, 136, 255, 0.35)';
          ctx.setLineDash([6, 8]);
          ctx.beginPath();
          ctx.arc(cx, cy, maxRadius * 0.9, -0.4, 0.8);
          ctx.stroke();
          ctx.setLineDash([]);
          ctx.fillStyle = '#B388FF';
          ctx.fillText('🛰️ INSAT-3DR (74.0°E GEO)', cx + maxRadius * 0.5, cy - maxRadius * 0.65);
        } else if (currentLayer === 'damini') {
          // C. IITM Damini Lightning Strike Sensor Grid
          // Danger zone (15 km)
          const r15 = 15 * (maxRadius / 250);
          ctx.beginPath();
          ctx.arc(cx, cy, r15, 0, Math.PI * 2);
          ctx.fillStyle = 'rgba(239, 68, 68, 0.16)';
          ctx.fill();
          ctx.strokeStyle = 'rgba(239, 68, 68, 0.85)';
          ctx.lineWidth = 1.5;
          ctx.setLineDash([4, 4]);
          ctx.stroke();
          ctx.setLineDash([]);
          ctx.fillStyle = '#F87171';
          ctx.font = 'bold 9px monospace';
          ctx.fillText('⚠️ 15km DANGER ZONE', cx - 48, cy - r15 - 4);

          // Caution zone (30 km)
          const r30 = 30 * (maxRadius / 250);
          ctx.beginPath();
          ctx.arc(cx, cy, r30, 0, Math.PI * 2);
          ctx.strokeStyle = 'rgba(245, 158, 11, 0.55)';
          ctx.lineWidth = 1;
          ctx.setLineDash([6, 6]);
          ctx.stroke();
          ctx.setLineDash([]);

          // Lightning strike points
          const t = Date.now() * 0.004;
          strikes.forEach((s, idx) => {
            const sx = cx + s.dx * (maxRadius / 250);
            const sy = cy + s.dy * (maxRadius / 250);
            const pulse = 1 + 0.3 * Math.sin(t + idx);

            // Flash aura
            ctx.beginPath();
            ctx.arc(sx, sy, 14 * pulse, 0, Math.PI * 2);
            ctx.fillStyle = 'rgba(254, 240, 138, 0.25)';
            ctx.fill();

            // Strike marker
            ctx.beginPath();
            ctx.arc(sx, sy, 5, 0, Math.PI * 2);
            ctx.fillStyle = '#FFD600';
            ctx.shadowColor = '#FFD600';
            ctx.shadowBlur = 12;
            ctx.fill();
            ctx.shadowBlur = 0;

            // Telemetry
            ctx.fillStyle = '#FFFFFF';
            ctx.font = 'bold 9px monospace';
            ctx.fillText('⚡ ' + s.ka + ' kA (' + s.type + ')', sx + 8, sy + 3);
          });
        } else if (currentLayer === 'wind') {
          // D. Storm Motion & Wind Vector Streamlines
          ctx.save();
          // Inbound / Outbound radial velocity background contour
          const gV = ctx.createRadialGradient(cx, cy, 0, cx, cy, maxRadius);
          gV.addColorStop(0, 'rgba(0, 230, 118, 0.15)'); // Inbound
          gV.addColorStop(0.5, 'rgba(255, 23, 68, 0.15)'); // Outbound
          gV.addColorStop(1, 'rgba(0, 0, 0, 0)');
          ctx.beginPath();
          ctx.arc(cx, cy, maxRadius, 0, Math.PI * 2);
          ctx.fillStyle = gV;
          ctx.fill();

          // Particle Streamlines
          particles.forEach(p => {
            // Steering vector toward NE (cyclonic curvature)
            const angle = Math.atan2(p.y, p.x) + 0.8;
            p.x += Math.cos(angle) * p.speed;
            p.y += Math.sin(angle) * p.speed;
            p.age++;

            if (p.age > p.maxAge || Math.hypot(p.x, p.y) > maxRadius) {
              p.x = (Math.random() - 0.5) * maxRadius * 1.5;
              p.y = (Math.random() - 0.5) * maxRadius * 1.5;
              p.age = 0;
            }

            const px = cx + p.x;
            const py = cy + p.y;
            const alpha = Math.sin((p.age / p.maxAge) * Math.PI) * 0.75;

            ctx.beginPath();
            ctx.moveTo(px, py);
            ctx.lineTo(px - Math.cos(angle) * 8, py - Math.sin(angle) * 8);
            ctx.strokeStyle = 'rgba(105, 240, 174, ' + alpha + ')';
            ctx.lineWidth = 1.6;
            ctx.stroke();
          });
          ctx.restore();
        }

        // 5. Central Radar Beacon
        ctx.beginPath();
        ctx.arc(cx, cy, 5, 0, Math.PI * 2);
        ctx.fillStyle = '#00E5FF';
        ctx.shadowColor = '#00E5FF';
        ctx.shadowBlur = 12;
        ctx.fill();
        ctx.shadowBlur = 0;

        ctx.beginPath();
        ctx.arc(cx, cy, 9, 0, Math.PI * 2);
        ctx.strokeStyle = '#FFFFFF';
        ctx.lineWidth = 1.5;
        ctx.stroke();

        ctx.fillStyle = '#FFFFFF';
        ctx.font = 'bold 10px monospace';
        ctx.fillText(cityName, cx - 22, cy + 20);

        requestAnimationFrame(render);
      }

      requestAnimationFrame(render);

      // JavaScript Bridge APIs called by Android Compose
      window.setLayer = function(layerKey) {
        currentLayer = layerKey;
        const title = document.getElementById('hudTitle');
        const dot = document.getElementById('hudDot');

        if (layerKey === 'dwr') {
          title.innerText = '📡 IMD DWR NOWCAST COMPOSITE';
          dot.style.background = '#00E5FF';
        } else if (layerKey === 'insat') {
          title.innerText = '🛰️ ISRO MOSDAC INSAT-3DR IR';
          dot.style.background = '#7C4DFF';
        } else if (layerKey === 'damini') {
          title.innerText = '⚡ IITM DAMINI LIGHTNING GRID';
          dot.style.background = '#FFD600';
        } else if (layerKey === 'wind') {
          title.innerText = '💨 STORM MOTION & WIND VECTORS';
          dot.style.background = '#69F0AE';
        }
      };

      window.setNowcastFrame = function(frameIdx) {
        nowcastFrame = frameIdx;
      };

      window.togglePlayback = function(play) {
        isSweeping = play;
      };

      window.setStation = function(lat, lon, name) {
        stationLat = lat;
        stationLon = lon;
        cityName = name;
        document.getElementById('hudSub').innerText = name + ' • 250 km Surveillance Grid';
        document.getElementById('hudCoords').innerText =
          'LAT: ' + lat.toFixed(4) + '° N • LON: ' + lon.toFixed(4) + '° E • 2.8 GHz S-BAND';
      };

      // Set initial active layer
      setLayer('$activeLayer');
    })();
  </script>
</body>
</html>
        """.trimIndent()
    }
}
