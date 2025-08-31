import os
import time
import requests

# Папка, куда будут сохраняться иконки
SAVE_DIR = "src/main/resources/static/item-icons/neutral-items"
os.makedirs(SAVE_DIR, exist_ok=True)

# API MediaWiki (Liquipedia)
API_URL = "https://liquipedia.net/commons/api.php"
CATEGORY = "Category:Dota_2_neutral_item_icons"

# для отслеживания уже скачанных файлов
seen_files = set()
downloaded = 0

# стартовые параметры запроса
params = {
    "action": "query",
    "format": "json",
    "list": "categorymembers",
    "cmtitle": CATEGORY,
    "cmtype": "file",
    "cmlimit": "50"
}

session = requests.Session()

# начальная задержка
delay = 2
# максимальная задержка
max_delay = 120
# минимальная задержка
min_delay = 2

print("🚀 Начинаю загрузку иконок предметов Dota 2...")

while True:
    response = session.get(API_URL, params=params, headers={
        "User-Agent": "LutiyProjectBot/1.0 (https://example.com)"
    }, timeout=10)

    if response.status_code == 429:
        delay = min(delay * 2, max_delay)  # увеличиваем задержку в 2 раза
        print(f"⚠️ Rate limit! Жду {delay} секунд...")
        time.sleep(delay)
        continue
    elif response.status_code != 200:
        print(f"⚠️ Ошибка HTTP {response.status_code}, текст: {response.text[:200]}")
        break

    try:
        data = response.json()
    except Exception as e:
        print(f"⚠️ Не JSON ответ: {e}, первые символы: {response.text[:200]}")
        break

    members = data.get("query", {}).get("categorymembers", [])
    if not members:
        print("⚠️ Нет файлов в ответе API, выходим")
        break

    for page in members:
        if page["title"].startswith("File:"):
            filename = page["title"].replace("File:", "")
            if filename in seen_files:
                continue
            seen_files.add(filename)

            print(f"⬇️ Скачиваю {filename}...")

            try:
                # второй запрос – чтобы достать прямой URL картинки
                image_info = session.get(API_URL, params={
                    "action": "query",
                    "format": "json",
                    "titles": page["title"],
                    "prop": "imageinfo",
                    "iiprop": "url"
                }, headers={"User-Agent": "LutiyProjectBot/1.0"}, timeout=10)

                if image_info.status_code == 429:
                    delay = min(delay * 2, max_delay)
                    print(f"⚠️ Rate limit при получении URL! Жду {delay} секунд...")
                    time.sleep(delay)
                    continue

                pages = image_info.json()["query"]["pages"]
                image_url = list(pages.values())[0]["imageinfo"][0]["url"]

                img_data = session.get(image_url, timeout=10, headers={"User-Agent": "LutiyProjectBot/1.0"}).content
                with open(os.path.join(SAVE_DIR, filename), "wb") as f:
                    f.write(img_data)

                downloaded += 1
                print(f"✅ Сохранён {filename}")

                # если всё ок — уменьшаем задержку (постепенно возвращаемся к min_delay)
                delay = max(min_delay, delay // 2)

                time.sleep(delay)

            except Exception as e:
                print(f"❌ Не удалось скачать {filename}: {e}")

    # пауза между страницами категории
    time.sleep(delay)

    # если есть ещё страницы — продолжаем
    if "continue" in data:
        params.update(data["continue"])
    else:
        break

print(f"🎉 Скачано {downloaded} файлов в {SAVE_DIR}")
