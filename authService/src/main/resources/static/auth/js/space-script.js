// === Мерцающие звёзды ===
const canvas = document.getElementById('stars');
const ctx = canvas.getContext('2d');

let stars = [];
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
    bg.style.transform = `scale(1.08) translate(${targetX * 20}px, ${targetY * 20}px)`;
    requestAnimationFrame(parallaxLoop);
}

// === Запуск ===
resizeCanvas();
createStars();
window.addEventListener('resize', () => {
    resizeCanvas();
    createStars();
});
requestAnimationFrame(animateStars);
parallaxLoop();