#!/bin/bash
# Папка для бэкапов (создастся, если нет)
BACKUP_DIR="./backups"
DATE=$(date +"%Y-%m-%d_%H-%M-%S")
mkdir -p "$BACKUP_DIR"

# Список контейнеров и баз
declare -A DBS=(
  ["postgres-auth"]="auth_db"
  ["postgres-order"]="order_db"
  ["postgres-product"]="inventory_db"
  ["postgres-payment"]="payment_db"
  ["postgres-saga"]="saga_db"
  ["postgres-notification"]="notification_db"
)

# Пароли (у auth свой, у остальных 12345qwert)
AUTH_PASS="password"
OTHER_PASS="12345qwert"

echo "🚀 Начинаем бэкап..."

for container in "${!DBS[@]}"; do
  db_name="${DBS[$container]}"
  file_name="${BACKUP_DIR}/${db_name}_${DATE}.sql"

  # Выбираем пароль
  if [ "$container" == "postgres-auth" ]; then
    pass=$AUTH_PASS
  else
    pass=$OTHER_PASS
  fi

  echo "📦 Дамп базы $db_name из $container..."
  docker exec -e PGPASSWORD=$pass "$container" pg_dump -U postgres -d "$db_name" > "$file_name"

  if [ $? -eq 0 ]; then
    echo "✅ Успешно: $file_name"
  else
    echo "❌ Ошибка при дампе $db_name"
  fi
done

echo "🎉 Бэкап завершен! Файлы лежат в $BACKUP_DIR"