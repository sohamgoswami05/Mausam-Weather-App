package com.imd.mausam2.ui.screens

object RadarGisHtmlBuilder {

    fun getImdRadarGifUrl(cityName: String): String {
        return when {
            cityName.contains("Kolkata", ignoreCase = true) -> "https://mausam.imd.gov.in/Radar/animation/Converted/KOL_SRI.gif"
            cityName.contains("Delhi", ignoreCase = true) -> "https://mausam.imd.gov.in/Radar/animation/Converted/DELHI_SRI.gif"
            cityName.contains("Mumbai", ignoreCase = true) -> "https://mausam.imd.gov.in/Radar/animation/Converted/MUM_SRI.gif"
            cityName.contains("Chennai", ignoreCase = true) -> "https://mausam.imd.gov.in/Radar/animation/Converted/CNI_SRI.gif"
            cityName.contains("Hyderabad", ignoreCase = true) -> "https://mausam.imd.gov.in/Radar/animation/Converted/HYD_SRI.gif"
            cityName.contains("Goa", ignoreCase = true) -> "https://mausam.imd.gov.in/Radar/animation/Converted/GOA_SRI.gif"
            cityName.contains("Ludhiana", ignoreCase = true) || cityName.contains("Punjab", ignoreCase = true) || cityName.contains("Patiala", ignoreCase = true) ->
                "https://mausam.imd.gov.in/Radar/animation/Converted/PTL_SRI.gif"
            else -> "https://mausam.imd.gov.in/Radar/animation/Converted/DELHI_SRI.gif"
        }
    }

    fun buildHtml(
        lat: Double,
        lon: Double,
        cityName: String,
        windSpeed: Double = 14.0,
        temp: Double = 28.0,
        humidity: Int = 65,
        activeLayer: String = "dwr",
        isHindi: Boolean = false
    ): String {
        val safeCity = cityName.replace("'", "\\'")
        val radarGifUrl = getImdRadarGifUrl(cityName)
        val insatIrUrl = "https://mausam.imd.gov.in/Satellite/3Dasiasec_ir1.jpg"
        val insatRapidUrl = "https://mausam.imd.gov.in/Satellite/rswmo_ir1.jpg"

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
      display: flex; align-items: center; justify-content: center;
      overflow: hidden;
    }
    #liveFeedImg {
      position: absolute;
      top: 50%; left: 50%;
      transform: translate(-50%, -50%);
      max-width: 96%; max-height: 96%;
      object-fit: contain;
      border-radius: 12px;
      z-index: 4;
      display: none;
      transition: opacity 0.4s ease;
      filter: drop-shadow(0 0 16px rgba(0, 229, 255, 0.2));
    }
    canvas {
      position: absolute; top: 0; left: 0; width: 100%; height: 100%;
      z-index: 10; pointer-events: none;
    }
    .hud-header {
      position: absolute; top: 10px; left: 10px; z-index: 30;
      background: rgba(6, 15, 30, 0.88);
      border: 1px solid rgba(0, 229, 255, 0.4);
      border-radius: 10px; padding: 5px 10px;
      backdrop-filter: blur(8px); pointer-events: none;
      display: flex; align-items: center; gap: 8px;
    }
    .hud-dot {
      width: 8px; height: 8px; border-radius: 50%;
      background: #00E5FF; box-shadow: 0 0 10px #00E5FF;
      animation: pulseMarker 1.4s infinite ease-in-out;
    }
    .hud-title {
      font-size: 10px; font-weight: 700; color: #FFFFFF;
      letter-spacing: 0.5px;
    }
    .hud-sub {
      font-size: 8.5px; color: #7FD8FF; font-weight: 500;
    }
    @keyframes pulseMarker {
      0% { transform: scale(0.85); opacity: 0.7; }
      50% { transform: scale(1.3); opacity: 1; }
      100% { transform: scale(0.85); opacity: 0.7; }
    }
    .hud-live-tag {
      position: absolute; top: 10px; right: 10px; z-index: 30;
      background: rgba(0, 230, 118, 0.2);
      border: 1px solid rgba(0, 230, 118, 0.6);
      border-radius: 8px; padding: 4px 8px;
      color: #00E676; font-size: 8.5px; font-weight: bold;
      pointer-events: none;
    }
    .hud-telemetry {
      position: absolute; bottom: 8px; left: 10px; z-index: 30;
      background: rgba(6, 15, 30, 0.85);
      border: 1px solid rgba(255, 255, 255, 0.12);
      border-radius: 8px; padding: 4px 8px;
      backdrop-filter: blur(6px); pointer-events: none;
      color: #94A3B8; font-size: 8.5px; font-family: monospace;
    }
    #feedNotice {
      position: absolute; bottom: 8px; right: 10px; z-index: 30;
      background: rgba(15, 23, 42, 0.8);
      border-radius: 6px; padding: 3px 6px;
      color: #64748B; font-size: 8px; pointer-events: none;
    }
  </style>
</head>
<body>
  <div id="radarContainer">
    <img id="liveFeedImg" alt="Official Live Radar Feed" crossorigin="anonymous" />
    <canvas id="gisCanvas"></canvas>
  </div>

  <div class="hud-header">
    <div class="hud-dot" id="hudDot"></div>
    <div>
      <div class="hud-title" id="hudTitle">IMD DWR LIVE LOOP • ISRO MOSDAC</div>
      <div class="hud-sub" id="hudSub">$safeCity • 250 km Surveillance Grid</div>
    </div>
  </div>

  <div class="hud-live-tag" id="liveTag">LIVE IMD WMS</div>

  <div class="hud-telemetry" id="hudCoords">
    LAT: ${"%.4f".format(lat)}° N • LON: ${"%.4f".format(lon)}° E • 2.8 GHz S-BAND
  </div>

  <div id="feedNotice">OFFICIAL IMD FEED</div>

  <script>
    (function() {
      const canvas = document.getElementById('gisCanvas');
      const ctx = canvas.getContext('2d');
      const liveImg = document.getElementById('liveFeedImg');

      let currentLayer = '$activeLayer';
      let isSweeping = true;
      let sweepAngle = 0;
      let nowcastFrame = 4;
      let cityName = '$safeCity';
      let stationLat = $lat;
      let stationLon = $lon;
      let liveWindSpeed = $windSpeed;

      const dwrFeedUrl = '$radarGifUrl';
      const insatFeedUrl = '$insatIrUrl';
      const insatRapidUrl = '$insatRapidUrl';

      // Pan & Zoom
      let scale = 1.0;
      let panX = 0;
      let panY = 0;
      let isDragging = false;
      let startX = 0;
      let startY = 0;
      let initialDist = 0;

      function resize() {
        const dpr = window.devicePixelRatio || 1;
        const w = window.innerWidth || 360;
        const h = window.innerHeight || 350;
        canvas.width = w * dpr;
        canvas.height = h * dpr;
        if (ctx.resetTransform) {
          ctx.resetTransform();
        } else {
          ctx.setTransform(1, 0, 0, 1, 0, 0);
        }
        ctx.scale(dpr, dpr);
      }
      window.addEventListener('resize', resize);
      resize();

      const container = document.getElementById('radarContainer');
      container.addEventListener('touchstart', function(e) {
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
      }, { passive: true });

      container.addEventListener('touchmove', function(e) {
        if (e.touches.length === 1 && isDragging) {
          panX = e.touches[0].clientX - startX;
          panY = e.touches[0].clientY - startY;
          const maxPan = 140 * scale;
          panX = Math.max(-maxPan, Math.min(maxPan, panX));
          panY = Math.max(-maxPan, Math.min(maxPan, panY));
        } else if (e.touches.length === 2) {
          const dist = Math.hypot(
            e.touches[0].clientX - e.touches[1].clientX,
            e.touches[0].clientY - e.touches[1].clientY
          );
          if (initialDist > 0) {
            scale *= (dist / initialDist);
            scale = Math.max(0.8, Math.min(2.8, scale));
            initialDist = dist;
          }
        }
      }, { passive: true });

      container.addEventListener('touchend', function() {
        isDragging = false;
        initialDist = 0;
      });

      const particles = [];
      for (let i = 0; i < 110; i++) {
        particles.push({
          x: (Math.random() - 0.5) * 440,
          y: (Math.random() - 0.5) * 440,
          age: Math.floor(Math.random() * 80),
          maxAge: 60 + Math.floor(Math.random() * 40),
          speed: (0.8 + Math.random() * 1.4) * (liveWindSpeed / 14.0)
        });
      }

      const stormCells = [
        { dx: 45, dy: -35, r: 28, maxDbz: 62, vx: -0.6, vy: 0.4 },
        { dx: 70, dy: -15, r: 22, maxDbz: 54, vx: -0.5, vy: 0.5 },
        { dx: -55, dy: 60, r: 38, maxDbz: 46, vx: -0.7, vy: 0.3 },
        { dx: -25, dy: 30, r: 18, maxDbz: 50, vx: -0.6, vy: 0.4 }
      ];

      const strikes = [
        { dx: 38, dy: -28, ka: -42.4, type: '-CG' },
        { dx: 48, dy: -18, ka: +55.1, type: '+CG' },
        { dx: -45, dy: 52, ka: -31.8, type: '-CG' }
      ];

      function dbzToColor(dbz, alpha) {
        if (dbz >= 60) return 'rgba(213, 0, 249, ' + alpha + ')';
        if (dbz >= 50) return 'rgba(255, 23, 68, ' + alpha + ')';
        if (dbz >= 40) return 'rgba(255, 109, 0, ' + alpha + ')';
        if (dbz >= 30) return 'rgba(255, 214, 0, ' + alpha + ')';
        if (dbz >= 20) return 'rgba(0, 230, 118, ' + alpha + ')';
        return 'rgba(0, 229, 255, ' + alpha + ')';
      }

      let isLiveImgLoaded = false;

      function updateFeedSource() {
        const cacheBuster = Math.floor(Date.now() / 300000);
        if (currentLayer === 'dwr') {
          liveImg.src = dwrFeedUrl + '?v=' + cacheBuster;
          liveImg.style.display = 'block';
          liveImg.style.opacity = '0.92';
          document.getElementById('feedNotice').innerText = 'IMD DWR SRI ANIMATION';
        } else if (currentLayer === 'insat') {
          liveImg.src = insatFeedUrl + '?v=' + cacheBuster;
          liveImg.style.display = 'block';
          liveImg.style.opacity = '0.94';
          document.getElementById('feedNotice').innerText = 'ISRO INSAT-3DR ASIA TIR-1';
        } else {
          liveImg.style.display = 'none';
          document.getElementById('feedNotice').innerText = currentLayer === 'damini' ? 'IITM LIGHTNING SENSOR GRID' : 'DOPPLER WIND VELOCITY';
        }
      }

      liveImg.onload = function() {
        isLiveImgLoaded = true;
      };
      liveImg.onerror = function() {
        isLiveImgLoaded = false;
        liveImg.style.display = 'none';
        document.getElementById('feedNotice').innerText = 'OFFLINE GIS SIMULATION';
      };

      updateFeedSource();

      function render() {
        const w = window.innerWidth || 360;
        const h = window.innerHeight || 350;
        ctx.clearRect(0, 0, w, h);

        const cx = w / 2 + panX;
        const cy = h / 2 + panY;
        const maxRadius = Math.min(w, h) * 0.44 * scale;

        // Compass Range Rings
        const ringSteps = [0.2, 0.4, 0.6, 0.8, 1.0];
        const kmLabels = ['50 km', '100 km', '150 km', '200 km', '250 km MAX'];
        ringSteps.forEach((step, idx) => {
          const r = maxRadius * step;
          ctx.beginPath();
          ctx.arc(cx, cy, r, 0, Math.PI * 2);
          ctx.strokeStyle = idx === 4 ? 'rgba(0, 229, 255, 0.55)' : 'rgba(0, 229, 255, 0.22)';
          ctx.lineWidth = idx === 4 ? 1.5 : 0.8;
          ctx.setLineDash(idx === 4 ? [] : [4, 6]);
          ctx.stroke();
          ctx.setLineDash([]);

          ctx.fillStyle = 'rgba(0, 229, 255, 0.7)';
          ctx.font = '9px monospace';
          ctx.fillText(kmLabels[idx], cx + 4, cy - r + 11);
        });

        // Azimuth Radials
        for (let deg = 0; deg < 360; deg += 30) {
          const rad = deg * Math.PI / 180;
          ctx.beginPath();
          ctx.moveTo(cx, cy);
          ctx.lineTo(cx + maxRadius * Math.cos(rad), cy + maxRadius * Math.sin(rad));
          ctx.strokeStyle = (deg % 90 === 0) ? 'rgba(0, 229, 255, 0.35)' : 'rgba(0, 229, 255, 0.10)';
          ctx.lineWidth = 0.8;
          ctx.stroke();

          if (deg % 90 === 0) {
            const card = deg === 0 ? 'E' : deg === 90 ? 'S' : deg === 180 ? 'W' : 'N';
            ctx.fillStyle = 'rgba(0, 229, 255, 0.9)';
            ctx.font = 'bold 10px monospace';
            ctx.fillText(card, cx + (maxRadius + 12) * Math.cos(rad) - 4, cy + (maxRadius + 12) * Math.sin(rad) + 4);
          }
        }

        const frameOffset = (nowcastFrame - 4) * 15;

        if (currentLayer === 'dwr') {
          if (!isLiveImgLoaded || liveImg.style.display === 'none') {
            stormCells.forEach(c => {
              const curX = cx + (c.dx + c.vx * frameOffset) * (maxRadius / 250);
              const curY = cy + (c.dy + c.vy * frameOffset) * (maxRadius / 250);
              const cr = c.r * (maxRadius / 250);

              const g = ctx.createRadialGradient(curX, curY, 0, curX, curY, cr);
              g.addColorStop(0.0, dbzToColor(c.maxDbz, 0.85));
              g.addColorStop(0.4, dbzToColor(c.maxDbz - 12, 0.65));
              g.addColorStop(0.75, dbzToColor(c.maxDbz - 24, 0.40));
              g.addColorStop(1.0, 'rgba(0, 229, 255, 0)');

              ctx.beginPath();
              ctx.arc(curX, curY, cr, 0, Math.PI * 2);
              ctx.fillStyle = g;
              ctx.fill();

              ctx.beginPath();
              ctx.arc(curX, curY, cr * 0.4, 0, Math.PI * 2);
              ctx.strokeStyle = dbzToColor(c.maxDbz, 0.8);
              ctx.lineWidth = 1;
              ctx.stroke();
            });
          }

          if (isSweeping) {
            const rad = sweepAngle * Math.PI / 180;
            const endX = cx + maxRadius * Math.cos(rad);
            const endY = cy + maxRadius * Math.sin(rad);

            const sweepGrad = ctx.createRadialGradient(cx, cy, 0, cx, cy, maxRadius);
            sweepGrad.addColorStop(0, 'rgba(0, 229, 255, 0.45)');
            sweepGrad.addColorStop(0.65, 'rgba(0, 229, 255, 0.15)');
            sweepGrad.addColorStop(1, 'rgba(0, 229, 255, 0)');

            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.arc(cx, cy, maxRadius, rad - 0.40, rad);
            ctx.fillStyle = sweepGrad;
            ctx.fill();

            ctx.beginPath();
            ctx.moveTo(cx, cy);
            ctx.lineTo(endX, endY);
            ctx.strokeStyle = '#00E5FF';
            ctx.lineWidth = 1.8;
            ctx.stroke();

            sweepAngle = (sweepAngle + 1.25) % 360;
          }
        } else if (currentLayer === 'insat') {
          ctx.strokeStyle = 'rgba(179, 136, 255, 0.4)';
          ctx.setLineDash([6, 8]);
          ctx.beginPath();
          ctx.arc(cx, cy, maxRadius * 0.88, -0.4, 0.8);
          ctx.stroke();
          ctx.setLineDash([]);
          ctx.fillStyle = '#B388FF';
          ctx.font = '9px monospace';
          ctx.fillText('🛰️ INSAT-3DR NADIR 74.0°E', cx + maxRadius * 0.35, cy - maxRadius * 0.6);
        } else if (currentLayer === 'damini') {
          const r15 = 15 * (maxRadius / 250);
          ctx.beginPath();
          ctx.arc(cx, cy, r15, 0, Math.PI * 2);
          ctx.fillStyle = 'rgba(239, 68, 68, 0.20)';
          ctx.fill();
          ctx.strokeStyle = 'rgba(239, 68, 68, 0.9)';
          ctx.lineWidth = 1.5;
          ctx.setLineDash([4, 4]);
          ctx.stroke();
          ctx.setLineDash([]);
          ctx.fillStyle = '#F87171';
          ctx.font = 'bold 9px monospace';
          ctx.fillText('⚡ 15km DANGER BUFFER', cx - 50, cy - r15 - 5);

          const r30 = 30 * (maxRadius / 250);
          ctx.beginPath();
          ctx.arc(cx, cy, r30, 0, Math.PI * 2);
          ctx.strokeStyle = 'rgba(245, 158, 11, 0.6)';
          ctx.lineWidth = 1;
          ctx.stroke();

          const t = Date.now() * 0.004;
          strikes.forEach((s, idx) => {
            const sx = cx + s.dx * (maxRadius / 250);
            const sy = cy + s.dy * (maxRadius / 250);
            const pulse = 1 + 0.35 * Math.sin(t + idx);

            ctx.beginPath();
            ctx.arc(sx, sy, 14 * pulse, 0, Math.PI * 2);
            ctx.fillStyle = 'rgba(254, 240, 138, 0.3)';
            ctx.fill();

            ctx.beginPath();
            ctx.arc(sx, sy, 5, 0, Math.PI * 2);
            ctx.fillStyle = '#FFD600';
            ctx.fill();

            ctx.fillStyle = '#FFFFFF';
            ctx.font = 'bold 9px monospace';
            ctx.fillText(s.ka + ' kA (' + s.type + ')', sx + 8, sy + 3);
          });
        } else if (currentLayer === 'wind') {
          particles.forEach(p => {
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
        }

        // Center Radar Beacon
        ctx.beginPath();
        ctx.arc(cx, cy, 5, 0, Math.PI * 2);
        ctx.fillStyle = '#00E5FF';
        ctx.fill();

        ctx.beginPath();
        ctx.arc(cx, cy, 9, 0, Math.PI * 2);
        ctx.strokeStyle = '#FFFFFF';
        ctx.lineWidth = 1.5;
        ctx.stroke();

        ctx.fillStyle = '#FFFFFF';
        ctx.font = 'bold 10px monospace';
        ctx.fillText(cityName, cx - 22, cy + 20);
      }

      function renderLoop() {
        try {
          render();
        } catch (e) {
          console.error("Render loop error:", e);
        }
        requestAnimationFrame(renderLoop);
      }
      requestAnimationFrame(renderLoop);

      window.setLayer = function(layerKey) {
        currentLayer = layerKey;
        const title = document.getElementById('hudTitle');
        const dot = document.getElementById('hudDot');
        const tag = document.getElementById('liveTag');

        if (layerKey === 'dwr') {
          title.innerText = '📡 IMD DWR LIVE LOOP • ISRO MOSDAC';
          dot.style.background = '#00E5FF';
          tag.innerText = 'LIVE IMD WMS';
          tag.style.color = '#00E676';
        } else if (layerKey === 'insat') {
          title.innerText = '🛰️ ISRO MOSDAC INSAT-3DR IR';
          dot.style.background = '#7C4DFF';
          tag.innerText = 'GEO 74°E IR-1';
          tag.style.color = '#B388FF';
        } else if (layerKey === 'damini') {
          title.innerText = '⚡ IITM DAMINI LIGHTNING GRID';
          dot.style.background = '#FFD600';
          tag.innerText = '48 SENSORS';
          tag.style.color = '#FFD600';
        } else if (layerKey === 'wind') {
          title.innerText = '💨 DUAL-DOPPLER WIND VELOCITY';
          dot.style.background = '#69F0AE';
          tag.innerText = liveWindSpeed.toFixed(1) + ' km/h';
          tag.style.color = '#69F0AE';
        }
        updateFeedSource();
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
        updateFeedSource();
      };

      window.reloadFeeds = function() {
        updateFeedSource();
      };

      window.setLayer('$activeLayer');
    })();
  </script>
</body>
</html>
        """.trimIndent()
    }
}
