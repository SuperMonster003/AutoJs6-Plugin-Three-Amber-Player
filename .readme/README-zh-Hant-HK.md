<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Ember Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
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
- 繁體中文 (香港) [zh-Hant-HK] # 目前
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
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

3-Ember Player 既是 AutoJs6 檔案管理器外掛程式, 亦是獨立的簡易影片播放器. 應用程式接受臨時唯讀 content URI, 使用 AndroidX Media3 ExoPlayer, 且絕不修改來源影片.

******

### 功能

******

- 透過裝置內置 MPEG-4 Part 2 解碼器提供輕量 XVID-in-MKV 相容; 系統解碼器不可用或失敗時清楚提示並可轉交其他播放器.
- 透過共用 `org.autojs.plugin.EXPLORER_ACTION` 通訊協定 v12 註冊唯讀檔案瀏覽器主要動作, 可選提供有界的直接同級讀取與播放進度能力.
- 使用 AndroidX Media3 ExoPlayer 和 PlayerView 提供自動播放, 標準控制項, 音訊焦點, 音訊輸出變為嘈雜環境時的處理及裝置編解碼器整合.
- 在介面重建後還原播放位置和播放/暫停意圖, 且只在影片實際播放時保持螢幕常亮.
- 為帶有 `video/*` MIME 類型的唯讀 `content://` URI 提供獨立匯出的 `android.intent.action.VIEW` 入口.
- 播放失敗後提供安全的使用其他應用程式開啟動作, 重新建立唯讀檢視 Intent 並從候選清單排除此外掛程式.
- 全螢幕沉浸式播放, 支援手勢調節亮度, 音量與播放進度, 雙按跳轉, 長按倍速與控制鎖定.
- 0.25× 至 3× 倍速播放, 畫面縮放模式與螢幕方向一鍵切換.
- 接續播放記錄只保留最近開啟且未播完的一個影片, 內容僅為 SHA-256 身分摘要與時間數值; 開啟其他影片時立即清除舊記錄, 播放完畢不會保留位置.
- 支援內嵌音軌與字幕選擇, 字幕預設關閉, 不支援的軌道會清楚標示.
- 提供單曲循環, 詳細媒體資訊, 按畫面比例建議方向及 API 26+ 畫中畫遙控播放/暫停.
- 透過 MediaSession 支援耳機, 藍牙與系統媒體控制, 並以媒體通知顯示標題和播放進度.
- 提供 15/30/45/60 分鐘與播完停止睡眠計時, 並持久儲存手勢靈敏度及 5/10/30 秒雙按跳轉設定.
- 支援 0.25× 至 4× 雙指捏合縮放與雙按復位, 拖曳進度列時顯示目標時間氣泡及盡力提取的記憶體縮圖.
- Android 10+ 可免權限將目前畫面透過 MediaStore 儲存為獨立 PNG, 且不修改來源影片.
- 透過 Explorer Action v12 建立同目錄影片自然排序佇列, 支援上一個 / 下一個, 順序, 隨機, 單項循環及自動連播下一項.
- 探索同名 .srt / .ass 外掛字幕及語言後綴變體, 字幕預設關閉, 只在使用者明確選擇後載入.
- 由單一 HCT 色源產生清晰的亮色與暗色語意配色, 預設跟隨 AutoJs6, 並提供 19 個本地化 Material 500 預置色及可即時預覽的自訂 RGB 色彩.
- 提供獨立啟動頁面及設定頁, 可設定跟隨主程式的語言 / 夜間模式 / 色彩、單一影片接續播放、手動與自動更新、已忽略版本、發行記錄及應用程式與開發者資訊.

******

### 主程式整合

******

主程式將本插件用於影片主要開啟路徑和播放動作.

安裝外掛程式並保持啟用, 可信且相容後, 開啟主程式識別為影片的任何檔案都會以檔案瀏覽器主要檢視器方式啟動動作 `play-video`.

如果外掛程式缺少, 已停用, 未獲授權, 不可用, 不相容或無法啟動, 相容的主程式會顯示復原指引. 只有使用者明確選擇 "用其他應用程式開啟" 後才會顯示系統應用程式候選選單.

此外掛程式只配對影片檔案. 音訊播放與影像檢視仍是獨立的外掛程式功能, 不包含在此 APK 中.

******

### 支援的格式

******

檔案瀏覽器主要動作透過 `video/*` 接受主程式識別的所有影片類型, 並保留下列 23 種精確副檔名配對器以相容舊版主程式:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### 外掛程式介面

******

主程式使用以下識別資料探索和執行插件:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
source namespace: io.github.supermonster003.autojs6.plugin.threeemberplayer
stable application id: io.github.supermonster003.autojs6.plugin.videoplayer
engine: explorer-action
variant: default
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

版本 2.0 提供通訊協定 v12 唯讀檔案瀏覽器主要動作. 相容主程式可附加要求級直接同級讀取與播放進度能力; 不支援這些可選擴充的主程式保留單檔播放. 獨立外部入口仍只接受帶有效 `video/*` MIME 子類型的唯讀 content URI.

完整主程式協同能力需要 AutoJs6 6.8.0 build 5276 或更新版本及 Explorer Action v12; 後續外掛程式能力不會提高此要求.

******

### 安全性

******

應用程式不要求儲存權限且絕不寫入來源影片. 互聯網權限只用於使用者主動觸發或每日一次的 GitHub 發行版檢查. 受簽章權限保護的檔案瀏覽器邊界會驗證完整通訊協定 v12 信封, 精確的單一選取目標, 父子關係, ClipData, 元資料, 主程式組建版本和唯讀授權. 可選 Host Session 由主程式綁定至外掛程式 UID, 只允許列出選取檔案的直接父目錄及開啟選取檔案或可讀直接同級檔案. 私有播放器只接收已驗證的有界不透明佇列, 不接收檔案系統路徑. 公開 ACTION_VIEW 邊界保持獨立且只支援單檔.

******

### 安全限制

******

- 檔案瀏覽器永遠從 1 個選取的 content URI 開始; 可選 Host Session 只公開可讀直接同級檔案, 絕不允許遞迴目錄存取.
- 檔案瀏覽器執行需要簽章層級 `org.autojs.permission.PLUGIN` 權限.
- 一律拒絕寫入和持久授權. 用於驗證檔案瀏覽器父層 URI 的前綴存取權絕不會轉送到播放器.
- 公開 ACTION_VIEW 邊界拒絕寫入, 持久和前綴授權.
- 外部回復候選應用程式會先被解析, 再篩選為其他套件, 最後透過新建的唯讀 Intent 啟動.
- 接續播放記錄只保留最近開啟且未播完的一個影片, 內容僅為 SHA-256 身分摘要與時間數值; 開啟其他影片時立即清除舊記錄, 播放完畢不會保留位置.
- 識別為影片或符合列出的舊版副檔名均不保證在每部裝置上都能解碼. 實際播放支援取決於 Media3 和已安裝的平台編解碼器.

******

### 版本記錄

******

# v2.0.0

###### 2026/08/29

* `新增` 新增以 HCT 建立的細緻單一色源系統: 由一個色彩產生適用於亮色與暗色外觀的工具列、控制、表面、輪廓及錯誤等清晰語意色, 提供 19 個本地化 Material 500 預置色及可即時預覽的自訂 RGB 色彩
* `新增` 新增啟動頁面與獨立單檔案播放器模式, 並提供獨立設定頁面, 包含語言、夜間模式、主題色彩、接續播放、更新、發行記錄及應用程式與開發者資訊
* `新增` 語言、夜間模式和色源預設透過 AutoJs6 官方唯讀設定契約跟隨主程式; 主程式不可用時仍顯示但停用相應選項, 並使用應用程式預設值
* `新增` 新增手動與每日自動檢查更新、已忽略版本管理及本地化內置發行記錄
* `修復` 公開主程式設定提供者要求的受保護外掛程式資訊服務入口, 確保跟隨 AutoJs6 功能可靠可用
* `優化` 接續播放現在只記住最近開啟的一個影片, 開啟其他影片時立即捨棄舊記錄, 獨立及主程式記錄均不會保留已播放完畢的位置
* `優化` 應用程式與外掛程式固定顯示名稱改為 3-Ember Player, 原始碼命名空間改為 threeemberplayer, 同時保留既有應用程式及外掛程式 ID 以兼容覆蓋更新

# v1.4.0

###### 2026/08/28

* `新增` 透過 Explorer Action v12 要求級且綁定 UID 的 Host Session 建立同目錄影片自然排序佇列, 支援上一個 / 下一個, 順序, 隨機, 單項循環及自動連播下一項
* `新增` 探索同名 .srt / .ass 外掛字幕及語言後綴變體, 字幕預設關閉, 只在使用者明確選擇後載入
* `新增` 新增由主程式管理的可選續播歷史: 預設不記錄, 可在 AutoJs6 設定中關閉或清除, 檔案清單不顯示已看標記
* `修復` 主程式已識別為影片的 Explorer 要求仍會因副檔名不在舊版 23 項允許清單中而被拒絕的問題; 現在統一接受可信的 `video/*` 要求
* `優化` 同級存取只限選取檔案及可讀的直接同級檔案, 禁止遞迴目錄, 寫入, 持久授權, 且外掛程式不儲存純文字路徑
* `優化` 序列化佇列限制為 128 個影片, 每個影片 8 個外掛字幕且字幕關聯總數 128, 同時永遠保留使用者選取項目
* `優化` 相容要求固定為 AutoJs6 6.8.0 build 5276 與 Explorer Action v12; 不支援可選擴充的主程式安全保留單檔播放
* `依賴` 將內置 Explorer Action API 從通訊協定 v2 升級至向後相容的 v12 媒體工作階段擴充

# v1.3.1

###### 2026/08/27

* `修復` 新增輕量 XVID-in-MKV 相容層: 將嚴格驗證的 VFW/FourCC XVID 軌道交由裝置內置 MPEG-4 Part 2 解碼器處理, 不轉碼且不修改來源檔案
* `修復` 裝置沒有相容系統解碼器或解碼失敗時停止只播放音訊, 顯示專項說明並提供使用其他應用程式開啟

##### 查看更多版本

* [CHANGELOG-zh-Hant-HK.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-HK.md)

******

### 建置

******

```powershell
.\gradlew.bat :app:assembleDebug
```

發佈建置:

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
