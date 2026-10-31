#!/bin/bash
# Использование: ./restore.sh <имя_файла.sql> <имя_контейнера>
# Пример: ./restore.sh backups/inventory_db_2026-10-20_12-00-00.sql postgres-product

if [ -z "$1" ] || [ -z "$2" ]; then
  echo "Использование: $0 <файл.sql> <контейнер>"
  exit 1
fi

FILE=$1
CONTAINER=$2

# Выбираем пароль
if [ "$CONTAINER" == "postgres-auth" ]; then
  PASS="password"
else
  PASS="12345qwert"
fi

echo "🔄 Восстанавливаем $FILE в $CONTAINER..."
# Загружаем файл в контейнер и восстанавливаем
cat "$FILE" | docker exec -i -e PGPASSWORD=$PASS "$CONTAINER" psql -U postgres

if [ $? -eq 0 ]; then
  echo "✅ База успешно восстановлена!"
else
  echo "❌ Ошибка восстановления"
fi