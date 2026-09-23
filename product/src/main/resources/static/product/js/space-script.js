// ============================================================
// === МЕРЦАЮЩИЕ ЗВЁЗДЫ ===
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

function drawStars(time) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    stars.forEach(star => {
        const opacity = star.baseOpacity +
            Math.sin(time * star.twinkleSpeed + star.twinkleOffset) * 0.3;
        ctx.beginPath();
        ctx.arc(star.x, star.y, star.radius, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(255, 255, 255, ${Math.max(0.1, opacity)})`;
        ctx.fill();
        if (star.radius > 1.2) {
            ctx.beginPath();
            ctx.arc(star.x, star.y, star.radius * 3, 0, Math.PI * 2);
            ctx.fillStyle = `rgba(255, 255, 200, ${Math.max(0, opacity * 0.15)})`;
            ctx.fill();
        }
    });

    comets.forEach(comet => {
        comet.progress += comet.speed;
        const x = comet.startX + (comet.endX - comet.startX) * comet.progress;
        const y = comet.startY + (comet.endY - comet.startY) * comet.progress;
        const gradient = ctx.createLinearGradient(x, y, x - comet.tailLength * 6, y - comet.tailLength * 3);
        gradient.addColorStop(0, 'rgba(255, 255, 255, 0.95)');
        gradient.addColorStop(0.3, 'rgba(255, 220, 100, 0.7)');
        gradient.addColorStop(0.7, 'rgba(255, 150, 50, 0.3)');
        gradient.addColorStop(1, 'rgba(255, 100, 0, 0)');
        ctx.beginPath();
        ctx.moveTo(x, y);
        ctx.lineTo(x - comet.tailLength * 6, y - comet.tailLength * 3);
        ctx.lineWidth = comet.size;
        ctx.strokeStyle = gradient;
        ctx.lineCap = 'round';
        ctx.stroke();
        const glow = ctx.createRadialGradient(x, y, 0, x, y, comet.size * 4);
        glow.addColorStop(0, 'rgba(255, 255, 255, 1)');
        glow.addColorStop(0.4, 'rgba(255, 220, 100, 0.8)');
        glow.addColorStop(1, 'rgba(255, 150, 0, 0)');
        ctx.beginPath();
        ctx.arc(x, y, comet.size * 4, 0, Math.PI * 2);
        ctx.fillStyle = glow;
        ctx.fill();
        if (comet.progress >= 1) comet.done = true;
    });
    comets = comets.filter(c => !c.done);
}

function animateStars(time) {
    drawStars(time);
    requestAnimationFrame(animateStars);
}

// === Параллакс ===
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

// === КОМЕТЫ ===
function spawnComet() {
    const startY = Math.random() * canvas.height * 0.5;
    comets.push({
        startX: -100,
        startY: startY,
        endX: canvas.width + 100,
        endY: startY + canvas.height * 0.4,
        progress: 0,
        speed: 0.008 + Math.random() * 0.005,
        size: 3 + Math.random() * 2,
        tailLength: 25
    });
}

setInterval(spawnComet, 30000);
setTimeout(spawnComet, 3000);

// === МУЗЫКА ===
let musicPlaying = false;

function toggleMusic() {
    const player = document.getElementById('music-player');
    const btn = document.getElementById('music-toggle');
    const icon = document.getElementById('music-icon');
    const label = document.getElementById('music-label');
    if (!player || !btn) return;

    if (musicPlaying) {
        player.pause();
        musicPlaying = false;
        btn.classList.remove('playing');
        icon.textContent = '▶';
        label.textContent = 'Lo-fi';
    } else {
        player.volume = 0.3;
        player.play().then(() => {
            musicPlaying = true;
            btn.classList.add('playing');
            icon.textContent = '❚❚';
            label.textContent = 'Стоп';
        }).catch(err => console.warn('Music error:', err));
    }
}

// === ЗОЛОТОЙ РЕЖИМ ===
function toggleGoldenMode() {
    const body = document.body;
    const btn = document.getElementById('golden-toggle');
    const icon = document.getElementById('golden-icon');
    const label = document.getElementById('golden-label');
    const isGolden = body.classList.toggle('golden-mode');

    if (isGolden) {
        btn.classList.add('active');
        icon.textContent = '🌟';
        label.textContent = 'Голубое';
        localStorage.setItem('goldenMode', 'true');
    } else {
        btn.classList.remove('active');
        icon.textContent = '💙';
        label.textContent = 'Золото';
        localStorage.setItem('goldenMode', 'false');
    }
}

function initGoldenMode() {
    const saved = localStorage.getItem('goldenMode');
    const isGolden = saved === null ? true : saved === 'true';

    if (isGolden) {
        document.body.classList.add('golden-mode');
        const btn = document.getElementById('golden-toggle');
        const icon = document.getElementById('golden-icon');
        const label = document.getElementById('golden-label');
        if (btn) {
            btn.classList.add('active');
            icon.textContent = '🌟';
            label.textContent = 'Голубое';
        }
    }
}

// === ЧАСЫ ===
function updateClock() {
    const clockEl = document.getElementById('clock-time');
    const dateEl = document.getElementById('clock-date');
    if (!clockEl || !dateEl) return;

    const now = new Date();
    clockEl.textContent =
        String(now.getHours()).padStart(2, '0') + ':' +
        String(now.getMinutes()).padStart(2, '0') + ':' +
        String(now.getSeconds()).padStart(2, '0');

    const days = ['ВОСКРЕСЕНЬЕ', 'ПОНЕДЕЛЬНИК', 'ВТОРНИК', 'СРЕДА', 'ЧЕТВЕРГ', 'ПЯТНИЦА', 'СУББОТА'];
    const months = ['ЯНВ', 'ФЕВ', 'МАР', 'АПР', 'МАЙ', 'ИЮН', 'ИЮЛ', 'АВГ', 'СЕН', 'ОКТ', 'НОЯ', 'ДЕК'];

    dateEl.textContent = `${days[now.getDay()]} · ${String(now.getDate()).padStart(2, '0')} ${months[now.getMonth()]} ${now.getFullYear()}`;
}

setInterval(updateClock, 1000);
updateClock();

// === ФРАЗЫ ===
const PHRASES = [
    'Сегодня отличный день для покупок',
    'Скидки ждут тебя в каталоге',
    'iPhone 15 Pro Max уже в наличии',
    'Бесплатная доставка от 5000 ₽',
    'Новые поступления каждый день',
    'Загляни в корзину — там что-то есть',
    'Золотые часы говорят: время покупать'
];
let phraseIndex = 0;

function rotatePhrase() {
    const el = document.getElementById('phrase-text');
    if (!el) return;
    phraseIndex = (phraseIndex + 1) % PHRASES.length;
    el.textContent = PHRASES[phraseIndex];
    el.style.animation = 'none';
    void el.offsetWidth;
    el.style.animation = 'phraseFade 10s ease-in-out infinite';
}

setInterval(rotatePhrase, 10000);
const phraseEl = document.getElementById('phrase-text');
if (phraseEl) phraseEl.textContent = PHRASES[0];

// === СЧЁТЧИК ОНЛАЙН ===
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
startOnlineCounter();

// === ПАСХАЛКА ===
const SECRET = ['ArrowUp', 'ArrowUp', 'ArrowDown', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowLeft', 'ArrowRight'];
let secretIndex = 0;
let clickArmed = false;

document.addEventListener('click', (e) => {
    if (e.target.closest('a, button, input')) return;
    clickArmed = true;
    secretIndex = 0;
    clearTimeout(window.__secretTimeout);
    window.__secretTimeout = setTimeout(() => { clickArmed = false; }, 10000);
});

document.addEventListener('keydown', (e) => {
    if (!clickArmed) return;
    if (e.key !== SECRET[secretIndex]) { secretIndex = 0; return; }
    secretIndex++;
    if (secretIndex === SECRET.length) {
        activateEasterEgg();
        secretIndex = 0;
        clickArmed = false;
    }
});

function activateEasterEgg() {
    document.body.style.transition = 'filter 0.3s';
    document.body.style.filter = 'hue-rotate(30deg) saturate(1.5) brightness(1.2)';
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
    msg.style.cssText = `position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 99999; font-family: 'Orbitron', monospace; font-size: 32px; font-weight: 900; color: #ffd700; text-shadow: 0 0 20px #ffd700, 0 0 40px #ff9500, 0 0 80px #ff4400; padding: 30px 60px; background: rgba(0, 0, 0, 0.85); border: 2px solid #ffd700; border-radius: 20px; pointer-events: none; text-align: center;`;
    document.body.appendChild(msg);
    setTimeout(() => {
        document.body.style.filter = '';
        msg.style.transition = 'opacity 0.5s';
        msg.style.opacity = '0';
        setTimeout(() => msg.remove(), 500);
    }, 5000);
}

// === ЗАПУСК ВСЕГО ===
resizeCanvas();
createStars();
window.addEventListener('resize', () => {
    resizeCanvas();
    createStars();
});
requestAnimationFrame(animateStars);
parallaxLoop();
initGoldenMode();

window.toggleMusic = toggleMusic;
window.toggleGoldenMode = toggleGoldenMode;