<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>檔案管理器外掛程式. 直接播放影片檔案</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言 (Languages)

******

目前 README.md 支援以下語言:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- 繁體中文 (台灣) [zh-Hant-TW] # 目前
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### 簡介

******

影片播放器透過暫時唯讀 content URI 播放檔案管理器或其他 Android 應用程式提供的影片內容. 外掛使用 AndroidX Media3 ExoPlayer, 且絕不修改來源影片.

******

### 功能

******

- 透過共用 `org.autojs.plugin.EXPLORER_ACTION` 通訊協定的版本 2 註冊單一檔案唯讀檔案瀏覽器主要動作.
- 使用 AndroidX Media3 ExoPlayer 和 PlayerView 提供自動播放, 標準控制項, 音訊焦點, 音訊輸出變為嘈雜環境時的處理及裝置編解碼器整合.
- 在介面重建後還原播放位置和播放/暫停意圖, 且只在影片實際播放時保持螢幕常亮.
- 為帶有 `video/*` MIME 類型的唯讀 `content://` URI 提供獨立匯出的 `android.intent.action.VIEW` 入口.
- 播放失敗後提供安全的使用其他應用程式開啟動作, 重新建立唯讀檢視 Intent 並從候選清單排除此外掛程式.

******

### 主程式整合

******

主程式將此外掛用於影片主要開啟路徑和播放動作.

安裝外掛程式並保持啟用, 受信任且相容後, 開啟相符的影片將以檔案瀏覽器主要檢視器方式啟動動作 `play-video`.

如果外掛缺少, 已停用, 不可用, 不相容或無法啟動, 主程式將回復到系統 `android.intent.action.VIEW` 路徑, 由其他已安裝的影片應用程式處理檔案.

此外掛程式只比對影片檔案. 音訊播放與影像檢視仍是獨立的外掛程式功能, 不包含在此 APK 中.

******

### 支援的格式

******

檔案瀏覽器主要動作的 MIME 比對器為空, 且僅精確比對以下 23 種主程式影片副檔名:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### 外掛程式介面

******

主程式使用以下識別資訊探索和執行外掛:

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
```

版本 1 為列出的影片副檔名提供唯讀檔案瀏覽器主要動作. 獨立外部入口接受帶有任何有效 `video/*` MIME 子類型的唯讀 content URI. 實際解碼能力取決於 Media3 擷取器和裝置可用編解碼器.

需要主程式建置版本 5269 或更新版本.

******

### 安全性

******

外掛程式不要求儲存空間或 INTERNET 權限. 受簽章權限保護的檔案瀏覽器邊界會驗證通訊協定版本 2, 來源介面, 主程式建置版本, 目標與父層 content URI, 精確的 ClipData 順序, 顯示名稱, 宣告大小, MIME 類型, 副檔名和唯讀授權旗標. 隨後它為非匯出播放器建立新的明確 Intent, 其中只包含目標 URI, MIME 類型, 安全顯示名稱, 單一目標 ClipData 項目和讀取授權. 公開 ACTION_VIEW 邊界會獨立地只接受 content URI, 影片 MIME 類型和精確讀取授權, 忽略所有傳入 extras 與 ClipData, 並重新建立相同的最小內部要求.

******

### 安全限制

******

- 每個播放要求只包含 1 個目標 content URI.
- 檔案瀏覽器執行需要簽章層級 `org.autojs.permission.PLUGIN` 權限.
- 一律拒絕寫入和持久授權. 用於驗證檔案瀏覽器父層 URI 的前綴存取權絕不會轉送到播放器.
- 公開 ACTION_VIEW 邊界拒絕寫入, 持久和前綴授權.
- 外部回復候選應用程式會先被解析, 再篩選為其他套件, 最後透過新建的唯讀 Intent 啟動.
- 列出的副檔名不保證在每部裝置上都能解碼. 實際播放支援取決於 Media3 和已安裝的平台編解碼器.

******

### 版本記錄

******

# v1.0.1

###### 2026/08/08

* `修復` 在外掛中心啟用時回傳有效的 Explorer Action 服務綁定
* `優化` 精簡外掛名稱和描述, 並讓使用者文件表達更自然

# v1.0.0

###### 2026/08/02

* `新增` 影片播放器外掛程式, 外掛程式 ID 為 `video-player`, 動作 ID 為 `play-video`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` MIME 比對器為空且僅按主程式目前 23 種影片副檔名精確比對的通訊協定 v2 唯讀檔案瀏覽器主要動作, 要求主程式建置版本 5269
* `新增` 受簽章權限保護的檔案瀏覽器入口, 嚴格驗證目標與父層 content URI, ClipData, 來源, 顯示名稱, 大小, MIME 類型, 副檔名和授權, 隨後以最小內容轉送到非匯出播放器
* `新增` 適用於唯讀影片 content URI 的獨立匯出 ACTION_VIEW 入口, 捨棄不受信任的 extras 與 ClipData, 拒絕禁止的授權並防止自我循環
* `新增` Media3 ExoPlayer 和 PlayerView 播放, 支援自動播放, 標準控制項, 音訊焦點, 音訊輸出變為嘈雜環境時的處理, 儲存播放位置與播放狀態, 以及只在實際播放時保持螢幕常亮
* `新增` 播放失敗後安全地使用其他應用程式開啟, 透過新建唯讀 Intent 並明確排除此外掛程式實現復原
* `新增` 外掛程式中繼資料, 介面文字, 使用說明, README 和 CHANGELOG 的多語言資源: 西班牙文/法文/俄文/阿拉伯文/日文/韓文/英文/簡體中文/香港繁體/台灣繁體
* `相依性` 附加 AndroidX Media3 ExoPlayer 和 UI 版本 1.10.1
* `相依性` 附加 Kotlin Parcelize 執行階段版本 2.2.21

##### 查看更多版本

* [CHANGELOG-zh-Hant-TW.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

******

### 建置

******

```powershell
.\gradlew.bat :app:assembleDebug
```

發行建置:

```powershell
.\gradlew.bat :app:assembleRelease
```

建置參數來自 `version.properties`. 目前最低 SDK 為 24, 目標 SDK 為 36.

******

### 資源配置

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 為外掛程式中繼資料和介面文字提供本地化. `plugin_instruction.md` 提供使用和安全說明. `.python/generate_markdown.py` 根據 JSON 來源檔案產生多語言 README 和更新記錄.

******

### 連結

******

- AutoJs6 文件: https://docs.autojs6.com
- Android 安全檔案分享: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
