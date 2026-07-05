<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Statistique - VolyVary</title>
    <style>
        :root {
            --bg: #f4f6fb;
            --panel: #ffffff;
            --text: #1f2937;
            --muted: #6b7280;
            --green: #22c55e;
            --red: #ef4444;
            --border: #e5e7eb;
            --sidebar: #2fb05b;
            --shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: Arial, Helvetica, sans-serif;
            background: linear-gradient(180deg, #eef2ff 0%, #f8fafc 55%, #eef6f0 100%);
            color: var(--text);
        }

        .layout {
            display: grid;
            grid-template-columns: 180px 1fr;
            min-height: 100vh;
        }

        .sidebar {
            background: linear-gradient(180deg, #33b65f 0%, #2aa44f 100%);
        }

        .content {
            padding: 28px 28px 36px;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 18px;
        }

        .title {
            font-size: 22px;
            font-weight: 800;
            letter-spacing: 0.02em;
            margin: 0;
        }

        .subtitle {
            margin: 4px 0 0;
            color: var(--muted);
            font-size: 13px;
        }

        .user-chip {
            background: rgba(255, 255, 255, 0.8);
            border: 1px solid var(--border);
            border-radius: 999px;
            padding: 10px 14px;
            box-shadow: var(--shadow);
            font-size: 13px;
        }

        .filter-card, .chart-card, .summary-card {
            background: var(--panel);
            border: 1px solid rgba(229, 231, 235, 0.9);
            box-shadow: var(--shadow);
            border-radius: 14px;
        }

        .filter-card {
            padding: 16px 18px;
            margin-bottom: 22px;
        }

        .filter-grid {
            display: grid;
            grid-template-columns: 1.5fr repeat(2, minmax(160px, 1fr)) auto;
            gap: 14px;
            align-items: end;
        }

        label {
            display: block;
            font-size: 12px;
            color: var(--muted);
            margin-bottom: 6px;
        }

        select, input[type="date"] {
            width: 100%;
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 10px 12px;
            background: #fff;
            color: var(--text);
            font-size: 14px;
            outline: none;
        }

        select:focus, input[type="date"]:focus {
            border-color: #86efac;
            box-shadow: 0 0 0 3px rgba(34, 197, 94, 0.14);
        }

        .button {
            border: 0;
            background: #111827;
            color: #fff;
            border-radius: 8px;
            padding: 11px 20px;
            font-weight: 700;
            cursor: pointer;
        }

        .summary-row {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 16px;
            margin-bottom: 22px;
        }

        .summary-card {
            padding: 18px;
        }

        .summary-label {
            color: var(--muted);
            font-size: 12px;
            margin: 0 0 8px;
        }

        .summary-value {
            font-size: 26px;
            font-weight: 800;
            margin: 0;
        }

        .summary-value.positive { color: var(--green); }
        .summary-value.negative { color: var(--red); }

        .charts-grid {
            display: grid;
            grid-template-columns: 1.1fr 0.9fr;
            gap: 18px;
        }

        .chart-card {
            padding: 18px;
            min-height: 420px;
        }

        .chart-title {
            text-align: center;
            font-size: 18px;
            margin: 4px 0 18px;
            font-weight: 500;
        }

        .chart-area {
            position: relative;
            height: 320px;
            border-left: 2px solid #111827;
            border-bottom: 2px solid #111827;
            margin: 28px 24px 18px 42px;
            padding: 14px 8px 18px 18px;
        }

        .chart-axis-label {
            position: absolute;
            font-size: 11px;
            color: #374151;
        }

        .chart-axis-label.y {
            top: -16px;
            left: -4px;
        }

        .chart-axis-label.x {
            right: -8px;
            bottom: -28px;
            text-align: right;
        }

        .bars {
            position: absolute;
            inset: 16px 8px 28px 18px;
            display: flex;
            align-items: flex-end;
            gap: 8px;
        }

        .bar-group {
            flex: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 8px;
            min-width: 0;
        }

        .bar-track {
            position: relative;
            width: 100%;
            height: 240px;
        }

        .bar {
            position: absolute;
            left: 14%;
            width: 72%;
            border-radius: 4px 4px 0 0;
            transition: transform 0.2s ease;
        }

        .bar:hover {
            transform: scaleY(1.03);
        }

        .bar.positive {
            background: var(--green);
        }

        .bar.negative {
            background: var(--red);
        }

        .bar-label {
            font-size: 11px;
            color: var(--muted);
        }

        .baseline {
            position: absolute;
            left: 18px;
            right: 8px;
            top: 120px;
            height: 2px;
            background: #111827;
        }

        .zero-line {
            position: absolute;
            left: 60px;
            top: 0;
            bottom: 28px;
            width: 2px;
            background: #111827;
        }

        .pie-wrap {
            display: grid;
            grid-template-columns: 1fr auto;
            gap: 20px;
            align-items: center;
            min-height: 320px;
            padding: 12px 10px 0;
        }

        .pie {
            width: 210px;
            height: 210px;
            border-radius: 50%;
            background: conic-gradient(var(--green) 0 75%, var(--red) 75% 100%);
            margin: 0 auto;
            position: relative;
            box-shadow: inset 0 0 0 1px rgba(255,255,255,0.3);
        }

        .pie::after {
            content: "";
            position: absolute;
            inset: 0;
            margin: auto;
            width: 0;
            height: 0;
            border-radius: 50%;
        }

        .legend {
            display: grid;
            gap: 10px;
            align-self: end;
            margin-top: auto;
        }

        .legend-item {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 12px;
            color: #374151;
        }

        .legend-color {
            width: 10px;
            height: 10px;
            border-radius: 2px;
        }

        .empty {
            padding: 18px;
            border: 1px dashed #cbd5e1;
            border-radius: 12px;
            color: var(--muted);
            text-align: center;
            margin-top: 20px;
        }

        @media (max-width: 1100px) {
            .layout { grid-template-columns: 1fr; }
            .sidebar { min-height: 16px; }
            .filter-grid,
            .summary-row,
            .charts-grid,
            .pie-wrap { grid-template-columns: 1fr; }
            .content { padding: 18px; }
            .chart-card { min-height: auto; }
            .chart-area { margin-left: 30px; }
        }
    </style>
</head>
<body>
<c:set var="netClass" value="${soldeNet >= 0 ? 'positive' : 'negative'}" />
<div class="layout">
    <aside class="sidebar"></aside>
    <main class="content">
        <div class="header">
            <div>
                <h1 class="title">STATISTIQUE</h1>
                <p class="subtitle">Voici la statistique générale du projet sélectionné sur la période choisie.</p>
            </div>
            <div class="user-chip">
                Connecté en tant que <strong><c:out value="${username}" /></strong>
            </div>
        </div>

        <form class="filter-card" method="get" action="${pageContext.request.contextPath}/home">
            <div class="filter-grid">
                <div>
                    <label for="projetId">Projet</label>
                    <select id="projetId" name="projetId">
                        <c:forEach items="${projets}" var="projet">
                            <option value="${projet.id}" <c:if test="${selectedProjectId == projet.id}">selected</c:if>>
                                <c:out value="${projet.nom}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div>
                    <label for="dateDebut">Date début</label>
                    <input id="dateDebut" name="dateDebut" type="date" value="${dateDebut}">
                </div>
                <div>
                    <label for="dateFin">Date fin</label>
                    <input id="dateFin" name="dateFin" type="date" value="${dateFin}">
                </div>
                <div>
                    <button class="button" type="submit">Valider</button>
                </div>
            </div>
        </form>

        <div class="summary-row">
            <div class="summary-card">
                <p class="summary-label">Total bénéfice</p>
                <p class="summary-value positive">Ar <c:out value="${totalBenefice}" /></p>
            </div>
            <div class="summary-card">
                <p class="summary-label">Total perte</p>
                <p class="summary-value negative">Ar <c:out value="${totalPerte}" /></p>
            </div>
            <div class="summary-card">
                <p class="summary-label">Solde net</p>
                <p class="summary-value ${netClass}">Ar <c:out value="${soldeNet}" /></p>
            </div>
            <div class="summary-card">
                <p class="summary-label">Mouvements</p>
                <p class="summary-value"><c:out value="${nbMouvements}" /></p>
            </div>
        </div>

        <c:choose>
            <c:when test="${hasData}">
                <div class="charts-grid">
                    <section class="chart-card">
                        <h2 class="chart-title">En histogramme</h2>
                        <div class="chart-area">
                            <div class="chart-axis-label y">Montant (Ar)</div>
                            <div class="chart-axis-label x">Date (Jour)</div>
                            <div class="baseline"></div>
                            <div class="zero-line"></div>
                            <div id="bars" class="bars"></div>
                        </div>
                    </section>

                    <section class="chart-card">
                        <h2 class="chart-title">En secteur</h2>
                        <div class="pie-wrap">
                            <div id="pie" class="pie"></div>
                            <div class="legend">
                                <div class="legend-item"><span class="legend-color" style="background: var(--red);"></span> <span id="legend-loss">Perte</span></div>
                                <div class="legend-item"><span class="legend-color" style="background: var(--green);"></span> <span id="legend-gain">Bénéfice</span></div>
                            </div>
                        </div>
                    </section>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty">
                    Aucun projet n'est disponible pour ce compte.
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>

<script type="application/json" id="daily-labels"><c:out value="${dailyLabelsJson}" escapeXml="false" /></script>
<script type="application/json" id="daily-values"><c:out value="${dailyValuesJson}" escapeXml="false" /></script>
<script type="application/json" id="daily-colors"><c:out value="${dailyColorsJson}" escapeXml="false" /></script>
<script type="application/json" id="pie-labels"><c:out value="${pieLabelsJson}" escapeXml="false" /></script>
<script type="application/json" id="pie-values"><c:out value="${pieValuesJson}" escapeXml="false" /></script>

<script>
    function readJson(id) {
        const raw = document.getElementById(id)?.textContent || '[]';
        return JSON.parse(raw);
    }

    const labels = readJson('daily-labels');
    const values = readJson('daily-values').map(Number);
    const colors = readJson('daily-colors');
    const pieLabels = readJson('pie-labels');
    const pieValues = readJson('pie-values').map(Number);

    function renderBars() {
        const host = document.getElementById('bars');
        if (!host) return;

        const maxAbs = Math.max(1, ...values.map((value) => Math.abs(value)));
        const hasNegative = values.some((value) => value < 0);
        host.innerHTML = '';

        labels.forEach((label, index) => {
            const group = document.createElement('div');
            group.className = 'bar-group';

            const track = document.createElement('div');
            track.className = 'bar-track';

            const value = values[index] || 0;
            const bar = document.createElement('div');
            bar.className = `bar ${value >= 0 ? 'positive' : 'negative'}`;
            bar.style.height = `${Math.max(8, Math.abs(value) / maxAbs * 100)}px`;
            bar.style.background = colors[index] || (value >= 0 ? '#22c55e' : '#ef4444');
            bar.title = `${label}: ${value.toLocaleString('fr-FR')} Ar`;

            if (value < 0 && hasNegative) {
                bar.style.top = '120px';
            } else {
                bar.style.bottom = '120px';
            }

            track.appendChild(bar);
            group.appendChild(track);

            const barLabel = document.createElement('div');
            barLabel.className = 'bar-label';
            barLabel.textContent = label;
            group.appendChild(barLabel);

            host.appendChild(group);
        });
    }

    function renderPie() {
        const host = document.getElementById('pie');
        if (!host) return;

        const loss = Math.max(0, pieValues[0] || 0);
        const gain = Math.max(0, pieValues[1] || 0);
        const total = loss + gain;
        const lossPct = total > 0 ? (loss / total) * 100 : 0;
        host.style.background = total > 0
            ? `conic-gradient(#ef4444 0 ${lossPct}%, #22c55e ${lossPct}% 100%)`
            : `conic-gradient(#d1d5db 0 100%)`;

        const lossText = document.getElementById('legend-loss');
        const gainText = document.getElementById('legend-gain');
        if (lossText) {
            lossText.textContent = `${pieLabels[0] || 'Perte'}: ${lossPct.toFixed(0)}%`;
        }
        if (gainText) {
            gainText.textContent = `${pieLabels[1] || 'Bénéfice'}: ${total > 0 ? (100 - lossPct).toFixed(0) : '0'}%`;
        }
    }

    renderBars();
    renderPie();
</script>
</body>
</html>
