// ============================================================
// === МЕРЦАЮЩИЕ ЗВЁЗДЫ + КОМЕТА ===
// ============================================================
const canvas = document.getElementById('stars');
const ctx = canvas.getContext('2d');

let stars = [];
let comets = [];
const STAR_COUNT = 150;

function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
}

function createStars() {
    stars = [];
    for (let i = 0; i < STAR_COUNT; i++) {
        stars.push({
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            radius: Math.random() * 1.5 + 0.5,
            baseOpacity: Math.random() * 0.5 + 0.3,
            twinkleSpeed: Math.random() * 0.02 + 0.005,
            twinkleOffset: Math.random() * Math.PI * 2
        });
    }
}

function spawnComet() {
    const startY = Math.random() * canvas.height * 0.5;
    const startX = -100;
    const endX = canvas.width + 100;
    const endY = startY + canvas.height * 0.4;

    comets.push({
        startX, startY, endX, endY,
        progress: 0,
        speed: 0.008 + Math.random() * 0.005,
        size: 3 + Math.random() * 2,
        tailLength: 25
    });
}

function drawComets() {
    comets.forEach(comet => {
        comet.progress += comet.speed;

        const x = comet.startX + (comet.endX - comet.startX) * comet.progress;
        const y = comet.startY + (comet.endY - comet.startY) * comet.progress;

        const gradient = ctx.createLinearGradient(
            x, y,
            x - comet.tailLength * 6, y - comet.tailLength * 3
        );
        gradient.addColorStop(0, 'rgba(255, 255, 255, 0.95)');
        gradient.addColorStop(0.3, 'rgba(255, 0, 200, 0.7)');
        gradient.addColorStop(0.7, 'rgba(0, 200, 255, 0.3)');
        gradient.addColorStop(1, 'rgba(255, 100, 0, 0)');

        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x - comet.tailLength * 6, y - comet.tailLength * 3);
        ctx.lineWidth = comet.size;
        ctx.strokeStyle = gradient;
        ctx.lineCap = 'round';
        ctx.stroke();

        const glowGradient = ctx.createRadialGradient(x, y, 0, x, y, comet.size * 4);
        glowGradient.addColorStop(0, 'rgba(255, 255, 255, 1)');
        glowGradient.addColorStop(0.4, 'rgba(255, 0, 200, 0.8)');
        glowGradient.addColorStop(1, 'rgba(0, 200, 255, 0)');

        ctx.beginPath();
        ctx.arc(x, y, comet.size * 4, 0, Math.PI * 2);
        ctx.fillStyle = glowGradient;
        ctx.fill();

        if (comet.progress >= 1) {
            comet.done = true;
        }
    });

    comets = comets.filter(c => !c.done);
}

function drawStars(time) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    stars.forEach(star => {
        const opacity = star.baseOpacity +
            Math.sin(time * star.twinkleSpeed + star.twinkleOffset) * 0.3;

        const rand = Math.sin(star.x * star.y);
        let color;
        if (rand > 0.3)      color = `rgba(0, 200, 255, ${Math.max(0.1, opacity)})`;
        else if (rand < -0.3) color = `rgba(255, 0, 200, ${Math.max(0.1, opacity)})`;
        else                  color = `rgba(255, 255, 255, ${Math.max(0.1, opacity)})`;

        ctx.beginPath();
        ctx.arc(star.x, star.y, star.radius, 0, Math.PI * 2);
        ctx.fillStyle = color;
        ctx.fill();

        if (star.radius > 1.2) {
            ctx.beginPath();
            ctx.arc(star.x, star.y, star.radius * 3, 0, Math.PI * 2);
            ctx.fillStyle = `rgba(0, 200, 255, ${Math.max(0, opacity * 0.15)})`;
            ctx.fill();
        }
    });

    drawComets();
}

function animateStars(time) {
    drawStars(time);
    requestAnimationFrame(animateStars);
}

// ============================================================
// === ★ НЕОНОВЫЙ ДОЖДЬ ★ ===
// ============================================================
(function initNeonRain() {
    const rainCanvas = document.createElement('canvas');
    rainCanvas.id = 'neon-rain';
    document.body.appendChild(rainCanvas);

    const rctx = rainCanvas.getContext('2d');
    let drops = [];

    function resize() {
        rainCanvas.width = window.innerWidth;
        rainCanvas.height = window.innerHeight;
        drops = [];
        const count = Math.floor(rainCanvas.width / 6);
        for (let i = 0; i < count; i++) {
            drops.push({
                x: Math.random() * rainCanvas.width,
                y: Math.random() * rainCanvas.height,
                len: Math.random() * 25 + 10,
                speed: Math.random() * 5 + 4,
                color: Math.random() < 0.5
                    ? 'rgba(255, 0, 200, 0.55)'
                    : (Math.random() < 0.5
                        ? 'rgba(0, 200, 255, 0.55)'
                        : 'rgba(255, 120, 0, 0.45)')
            });
        }
    }

    function drawRain() {
        rctx.clearRect(0, 0, rainCanvas.width, rainCanvas.height);

        drops.forEach(d => {
            rctx.beginPath();
            rctx.moveTo(d.x, d.y);
            rctx.lineTo(d.x, d.y + d.len);
            rctx.strokeStyle = d.color;
            rctx.lineWidth = 1.2;
            rctx.shadowColor = d.color;
            rctx.shadowBlur = 8;
            rctx.stroke();

            d.y += d.speed;
            if (d.y > rainCanvas.height) {
                d.y = -d.len;
                d.x = Math.random() * rainCanvas.width;
            }
        });

        requestAnimationFrame(drawRain);
    }

    resize();
    window.addEventListener('resize', resize);
    drawRain();
})();

// ============================================================
// === ПАРАЛЛАКС ===
// ============================================================
const bg = document.querySelector('.space-bg');
let mouseX = 0, mouseY = 0;
let targetX = 0, targetY = 0;

document.addEventListener('mousemove', (e) => {
    mouseX = (e.clientX / window.innerWidth - 0.5) * 2;
    mouseY = (e.clientY / window.innerHeight - 0.5) * 2;
});

function parallaxLoop() {
    targetX += (mouseX - targetX) * 0.05;
    targetY += (mouseY - targetY) * 0.05;
    if (bg) {
        bg.style.transform = `scale(1.08) translate(${targetX * 20}px, ${targetY * 20}px)`;
    }
    requestAnimationFrame(parallaxLoop);
}

// ============================================================
// === ЧАСЫ ===
// ============================================================
function updateClock() {
    const clockEl = document.getElementById('clock-time');
    const dateEl = document.getElementById('clock-date');

    if (!clockEl || !dateEl) return;

    const now = new Date();

    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    const seconds = String(now.getSeconds()).padStart(2, '0');

    clockEl.textContent = `${hours}:${minutes}:${seconds}`;

    const days = ['ВОСКРЕСЕНЬЕ', 'ПОНЕДЕЛЬНИК', 'ВТОРНИК', 'СРЕДА',
                  'ЧЕТВЕРГ', 'ПЯТНИЦА', 'СУББОТА'];
    const months = ['ЯНВ', 'ФЕВ', 'МАР', 'АПР', 'МАЙ', 'ИЮН',
                    'ИЮЛ', 'АВГ', 'СЕН', 'ОКТ', 'НОЯ', 'ДЕК'];

    const dayName = days[now.getDay()];
    const dayNum = String(now.getDate()).padStart(2, '0');
    const monthName = months[now.getMonth()];
    const year = now.getFullYear();

    dateEl.textContent = `${dayName} · ${dayNum} ${monthName} ${year}`;
}

function startClock() {
    updateClock();
    setInterval(updateClock, 1000);
}

// ============================================================
// === ПРИВЕТСТВИЕ (для login/register) — повторяется ===
// ============================================================
function replayWelcome() {
    const lines = document.querySelectorAll('#welcome-widget .welcome-line');
    if (!lines.length) return;

    // Сброс: скрываем все строки, снимаем анимацию
    lines.forEach(line => {
        line.style.opacity = '0';
        line.style.transform = 'translateX(-30px)';
        line.style.animation = 'none';
    });

    // Форсируем reflow — браузер пересчитает анимацию
    void document.body.offsetWidth;

    // Запускаем выезд каждой строки по очереди
    lines.forEach((line, i) => {
        const delay = 0.3 + i * 0.5;
        line.style.animation =
            `welcomeSlide 0.8s ease-out ${delay}s forwards, ` +
            `featuredShine 4s linear infinite`;
    });
}

function startWelcomeLoop() {
    const widget = document.getElementById('welcome-widget');
    if (!widget) return;

    replayWelcome();
    setInterval(replayWelcome, 5000);   // каждые 5 секунд
}

// ============================================================
// === ФРАЗЫ (для главной/каталога) ===
// ============================================================
const PHRASES = [
    'Скидки ждут тебя в каталоге',
    'iPhone 15 Pro Max уже в наличии',
    'Бесплатная доставка от 5000 ₽',
    'Новые поступления каждый день',
    'Загляни в корзину — там что-то есть',
    'Гарантия 1 год на все товары',
    'Оплата картой или наличными',
    'Хиты продаж обновляются каждую неделю',
    'Бонусы за каждую покупку',
    'Круглосуточная поддержка 24/7',
    'Только оригинальные товары',
    'Тысячи довольных клиентов',
    'Удобный поиск по каталогу',
    'Быстрое оформление заказа',
    'Лучшие цены на рынке'
];

const VISIBLE_COUNT = 7;
let phraseStart = 0;

function renderPhrases(startIdx, activeIdx) {
    const container = document.getElementById('phrase-list');
    if (!container) return;

    let html = '';
    for (let i = 0; i < VISIBLE_COUNT; i++) {
        const phraseIdx = (startIdx + i) % PHRASES.length;
        const isActive = i === activeIdx ? 'active' : '';
        html += `<div class="phrase-item ${isActive}">${PHRASES[phraseIdx]}</div>`;
    }
    container.innerHTML = html;
}

function startPhrases() {
    const container = document.getElementById('phrase-list');
    if (!container) return;

    let activeIdx = 0;
    renderPhrases(phraseStart, activeIdx);

    setInterval(() => {
        activeIdx++;
        if (activeIdx >= VISIBLE_COUNT) {
            activeIdx = 0;
            phraseStart = (phraseStart + VISIBLE_COUNT) % PHRASES.length;
        }
        renderPhrases(phraseStart, activeIdx);
    }, 3000);
}

// ============================================================
// === СЧЁТЧИК ОНЛАЙН ===
// ============================================================
function startOnlineCounter() {
    const el = document.getElementById('online-count');
    if (!el) return;

    let count = Math.floor(Math.random() * 60) + 20;

    function update() {
        const delta = Math.floor(Math.random() * 5) - 2;
        count = Math.max(15, Math.min(120, count + delta));
        el.textContent = count;
        setTimeout(update, 3000);
    }

    el.textContent = count;
    setTimeout(update, 3000);
}

// ============================================================
// === ★ ПАСХАЛКА: КЛИК + 8 СТРЕЛОК ★ ===
// ============================================================
const SECRET = [
    'ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown',
    'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight'
];
let secretIndex = 0;
let clickArmed = false;

document.addEventListener('click', (e) => {
    if (e.target.closest('a, button, input')) return;

    clickArmed = true;
    secretIndex = 0;

    clearTimeout(window.__secretTimeout);
    window.__secretTimeout = setTimeout(() => {
        clickArmed = false;
    }, 10000);
});

document.addEventListener('keydown', (e) => {
    if (!clickArmed) return;
    if (e.key !== SECRET[secretIndex]) {
        secretIndex = 0;
        return;
    }

    secretIndex++;

    if (secretIndex === SECRET.length) {
        activateEasterEgg();
        secretIndex = 0;
        clickArmed = false;
    }
});

function activateEasterEgg() {
    document.body.style.transition = 'filter 0.3s';
    document.body.style.filter = 'saturate(2) contrast(1.3) brightness(1.2)';

    for (let i = 0; i < 200; i++) {
        stars.push({
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            radius: Math.random() * 3 + 1,
            baseOpacity: 1,
            twinkleSpeed: 0.5,
            twinkleOffset: Math.random() * Math.PI * 2
        });
    }

    const msg = document.createElement('div');
    msg.textContent = '🎉 СЕКРЕТНЫЙ КОД АКТИВИРОВАН 🎉';
    msg.style.cssText = `
        position: fixed;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 99999;
        font-family: 'Orbitron', monospace;
        font-size: 32px;
        font-weight: 900;
        color: #00ffff;
        text-shadow: 0 0 20px #00ffff, 0 0 40px #ff00c8, 0 0 80px #ff0080;
        padding: 30px 60px;
        background: rgba(0, 0, 0, 0.85);
        border: 2px solid #00ffff;
        border-radius: 20px;
        pointer-events: none;
        text-align: center;
    `;
    document.body.appendChild(msg);

    setTimeout(() => {
        document.body.style.filter = '';
        msg.style.transition = 'opacity 0.5s';
        msg.style.opacity = '0';
        setTimeout(() => msg.remove(), 500);
    }, 5000);
}

// ============================================================
// === ЗАПУСК ВСЕГО ===
// ============================================================
resizeCanvas();
createStars();
window.addEventListener('resize', () => {
    resizeCanvas();
    createStars();
});
requestAnimationFrame(animateStars);
parallaxLoop();

setInterval(spawnComet, 30000);
setTimeout(spawnComet, 3000);

startClock();
startPhrases();        // работает на главной/каталоге (если есть #phrase-list)
startWelcomeLoop();    // работает на login/register (если есть #welcome-widget)
startOnlineCounter();  // работает где есть счётчик онлайн