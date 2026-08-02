<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Воспроизведение видео только для чтения в Проводнике AutoJs6 с безопасной обработкой content URI</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Языки (Languages)

******

Текущий README.md поддерживает следующие языки:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- Русский [ru] # текущий
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### Введение

******

Плагин AutoJs6 Video Player воспроизводит видеоконтент, переданный Проводником AutoJs6 или другим приложением Android через временный доступ только для чтения к content URI. Он использует AndroidX Media3 ExoPlayer и никогда не изменяет исходное видео.

******

### Возможности

******

- Регистрирует основное действие Проводника только для чтения для одного файла через версию 2 общего протокола `org.autojs.plugin.EXPLORER_ACTION`.
- Использует AndroidX Media3 ExoPlayer и PlayerView для автоматического воспроизведения, стандартных элементов управления, фокусировки звука, обработки перехода к noisy-аудиовыходу и интеграции с кодеками устройства.
- Восстанавливает позицию и намерение воспроизведения или паузы после повторного создания, а экран остается включенным только во время активного воспроизведения видео.
- Предоставляет отдельную экспортируемую точку входа `android.intent.action.VIEW` для content URI только для чтения с MIME-типом `video/*`.
- После ошибки воспроизведения предлагает безопасное действие Открыть в другом приложении, заново создавая Intent просмотра только для чтения и исключая этот плагин из списка кандидатов.

******

### Интеграция с хостом

******

AutoJs6 интегрирует этот плагин в основной путь открытия видео в `app/src/main/java/org/autojs/autojs/ui/explorer/ExplorerView.kt` и действие Воспроизвести в `app/src/main/java/org/autojs/autojs/ui/main/scripts/MediaInfoDialogManager.kt`.

После установки, включения, признания доверенным и проверки совместимости плагина открытие подходящего видео запускает действие `play-video` как основной просмотрщик Проводника.

Если плагин отсутствует, отключен, недоступен, несовместим или не запускается, AutoJs6 возвращается к системному маршруту `android.intent.action.VIEW`, чтобы файл могло обработать другое установленное видеоприложение.

Этот плагин работает только с видеофайлами. Воспроизведение аудио и просмотр изображений остаются отдельными возможностями плагинов и не включены в этот APK.

******

### Поддерживаемые форматы

******

Основное действие Проводника использует пустой MIME-фильтр и точно сопоставляется только со следующими 23 расширениями видео хоста:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### Интерфейс плагина

******

AutoJs6 обнаруживает и запускает плагин со следующими идентификаторами:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
engine: explorer-action
variant: default
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: empty
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5269
supported ABIs: unrestricted (supportedAbis = emptyArray())
```

Версия 1 предоставляет основное действие Проводника только для чтения для перечисленных расширений видео. Независимая внешняя точка входа принимает content URI только для чтения с любым допустимым подтипом MIME `video/*`. Фактическое декодирование зависит от экстракторов Media3 и доступных на устройстве кодеков.

Плагин полностью реализован на JVM и не содержит нативных библиотек. Он объявляет `supportedAbis = emptyArray()` и выпускается как один APK, не зависящий от ABI. Требуется сборка хоста AutoJs6 5269 или новее.

******

### Безопасность

******

Плагин не запрашивает разрешения хранилища или INTERNET. Защищенная подписью граница Проводника проверяет версию 2 протокола, исходную поверхность, сборку хоста, целевой и родительский content URI, точный порядок ClipData, отображаемое имя, заявленный размер, MIME-тип, расширение и флаги доступа только для чтения. Затем она создает новый явный Intent для неэкспортируемого проигрывателя, содержащий только целевой URI, MIME-тип, безопасное отображаемое имя, один целевой элемент ClipData и доступ для чтения. Общедоступная граница ACTION_VIEW отдельно принимает только content URI, MIME-тип видео и точный доступ для чтения, игнорирует все входящие extras и ClipData и заново создает такой же минимальный внутренний запрос.

******

### Ограничения безопасности

******

- Один целевой content URI на запрос воспроизведения.
- Для запуска из Проводника требуется разрешение уровня signature `org.autojs.permission.PLUGIN`.
- Доступ на запись и постоянный доступ всегда отклоняются. Доступ prefix, используемый для проверки родительского URI Проводника, никогда не передается проигрывателю.
- Общедоступная граница ACTION_VIEW отклоняет доступ на запись, постоянный доступ и доступ prefix.
- Сначала определяются внешние кандидаты, затем список фильтруется до других пакетов, которые запускаются с новым Intent только для чтения.
- Наличие расширения в списке не гарантирует поддержку декодирования на каждом устройстве. Фактическая поддержка воспроизведения определяется Media3 и установленными платформенными кодеками.

******

### История выпусков

******

# v1.0.0

###### 2026/08/02

* `Функция` Плагин Video Player с ID плагина `video-player`, ID действия `play-video`, движком `explorer-action` и вариантом `default`
* `Функция` Основное действие Проводника только для чтения по протоколу v2 с пустым MIME-фильтром, точно сопоставляемое только с текущими 23 расширениями видео хоста и требующее сборку хоста AutoJs6 5269
* `Функция` Защищенная подписью точка входа Проводника со строгой проверкой целевого и родительского content URI, ClipData, источника, отображаемого имени, размера, MIME-типа, расширения и доступа, после которой минимальный набор данных передается неэкспортируемому проигрывателю
* `Функция` Независимая экспортируемая точка входа ACTION_VIEW для content URI видео только для чтения, отбрасывающая недоверенные extras и ClipData, отклоняющая запрещенный доступ и защищенная от зацикливания на себе
* `Функция` Воспроизведение через Media3 ExoPlayer и PlayerView с автозапуском, стандартными элементами управления, фокусировкой звука, обработкой перехода к noisy-аудиовыходу, сохранением позиции и состояния воспроизведения, а также включенным экраном только при активном воспроизведении
* `Функция` Безопасное восстановление через действие Открыть в другом приложении после ошибки воспроизведения с использованием новых Intents только для чтения, явно исключающих этот плагин
* `Функция` Чистая реализация JVM без нативных библиотек, неограниченные ABI через `supportedAbis = emptyArray()` и один независимый от ABI APK
* `Функция` Локализованные метаданные, текст интерфейса, инструкции, README и журналы изменений на испанском, французском, русском, арабском, японском, корейском, английском, упрощенном китайском, традиционном китайском Гонконга и традиционном китайском Тайваня
* `Зависимость` Добавлена зависимость AndroidX Media3 ExoPlayer и UI версии 1.10.1
* `Зависимость` Добавлена зависимость Kotlin Parcelize runtime версии 2.2.21

##### Другие выпуски

* [CHANGELOG-ru.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ru.md)

******

### Сборка

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release-сборка:

```powershell
.\gradlew.bat :app:assembleRelease
```

Параметры сборки берутся из `version.properties`. Текущий минимальный SDK равен 24, целевой SDK равен 36.

******

### Структура ресурсов

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` локализует метаданные плагина и текст интерфейса. `plugin_instruction.md` содержит инструкции по использованию и безопасности. `.python/generate_markdown.py` создает локализованные README и журналы изменений из источников JSON.

******

### Ссылки

******

- Документация AutoJs6: https://docs.autojs6.com
- Безопасная передача файлов Android: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
