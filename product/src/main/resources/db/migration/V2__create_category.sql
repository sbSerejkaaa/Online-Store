-- Таблица категорий
CREATE TABLE category (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(50) NOT NULL UNIQUE,
    icon VARCHAR(10),
    sort_order INTEGER NOT NULL DEFAULT 0
);

-- Добавляем FK на products
ALTER TABLE product
    ADD COLUMN category_id UUID REFERENCES category(id);

-- Индекс для быстрой фильтрации
CREATE INDEX idx_product_category ON product(category_id);

-- Начальные категории
INSERT INTO category (id, name, slug, icon, sort_order) VALUES
    (gen_random_uuid(), 'Телефоны', 'phones', '📱', 1),
    (gen_random_uuid(), 'Ноутбуки', 'laptops', '💻', 2),
    (gen_random_uuid(), 'Наушники', 'headphones', '🎧', 3),
    (gen_random_uuid(), 'Роботы', 'robots', '🤖', 4),
    (gen_random_uuid(), 'Часы', 'watches', '⌚', 5),
    (gen_random_uuid(), 'Аксессуары', 'accessories', '🎮', 6);