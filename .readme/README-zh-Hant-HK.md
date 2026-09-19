<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
    </picture>
  </p>

  <p>支援播放清單, 字幕與背景音訊的影片播放器</p>

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

3-Ember Player 是 AutoJs6 檔案管理器的影片播放外掛程式, 同時亦是一個可獨立使用的本機影片播放器. 在檔案管理器中輕按影片檔案即可全螢幕播放; 手勢控制, 倍速, 外掛字幕, 同目錄連播, 畫中畫等主流播放器的核心體驗一應俱全. 播放能力建基於 AndroidX Media3 ExoPlayer.

外掛程式堅持唯讀安全模型: 影片透過臨時唯讀授權進入播放器, 全程不申請儲存權限, 不修改亦不移動任何來源檔案; 網絡存取只用於檢查外掛程式更新.

******

### 功能亮點

******

- 本機播放清單: 支援 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留順序, 標題和重複項目. 宿主入口讀取同目錄檔案; 獨立入口可選擇清單資料夾以讀取相對路徑. 一次開啟一個清單, 最多 128 項; 不支援網絡位址, HLS 與巢狀清單.
- 一按即播: 在 AutoJs6 檔案管理器中輕按影片檔案, 直接進入全螢幕沉浸式播放, 無需任何設定.
- 順手的手勢: 左半螢幕滑動調亮度, 右半螢幕調音量, 橫向滑動快進快退, 雙按兩側跳轉, 雙按中央播放/暫停, 長按臨時 2 倍速, 還可一鍵鎖定屏蔽誤觸.
- 倍速與畫面控制: 0.25× 至 3× 共 9 檔倍速, 雙指捏合縮放畫面 (0.25× 至 4×), 適應/填滿/裁剪三種縮放模式, 螢幕方向一鍵切換並按畫面比例自動建議.
- 同目錄連播: 開啟一個影片即建立自然排序佇列; 佇列面板顯示目前項目與已知時長, 支援點按跳轉及順序/隨機/單項循環切換, 自動接續前亦會顯示可取消的 3 秒提示.
- 主程式選取順序佇列: 唯讀`播放所選`動作可將同一資料夾內 1 至 128 個影片嚴格按主程式選取順序排隊, 不探索同層檔案; 可選設定亦可跨工作階段記住播放模式.
- 外掛與內嵌字幕: 自動探索同名 .srt / .ass 或手動載入單個字幕, 偵測常見舊編碼, 調整字幕樣式與 ±600 秒外掛字幕偏移, 並支援內嵌字幕與多音軌切換; 字幕預設關閉, 由使用者明確開啟.
- 精細查看: 暫停時可逐格後退或前進並長按連發, 亦可設定或清除 A-B 區間循環, 方便重複查看細節.
- 接續播放: 記住最近一個未看完影片的播放位置, 重新開啟自動續播; 看完即清, 不留觀看痕跡.
- 畫中畫與系統整合: Android 8.0+ 播放中離開應用程式自動進入小視窗, 支援耳機與藍牙控制, 媒體通知顯示標題與進度.
- 睡眠計時: 15/30/45/60 分鐘或播完停止, 時間一到自動暫停播放.
- 拖曳預覽: 拖曳進度列時顯示目標時間氣泡, 並盡力提供畫面縮圖預覽.
- 目前畫面截圖: Android 10+ 一鍵將目前畫面儲存為 PNG 到系統相簿, 無需儲存權限, 不改動來源影片.
- 疑難格式後備: 內置輕量 XVID-in-MKV 相容層; 裝置無法解碼時給出清楚提示, 並可一鍵轉交其他播放器.
- 主題隨心: 由一個主題色生成亮暗兩套介面配色, 預設跟隨 AutoJs6, 另有 19 個預置色與可即時預覽的自訂 RGB 色彩.
- 獨立可用: 自帶主畫面入口與設定頁, 可透過系統檔案選擇器直接開啟影片, 亦可作為系統 "開啟方式" 中的影片播放器.
- 顯示與輸出工具: 查看 HDR10/HLG/SDR 與可用色彩資料並一鍵複製, 可選擇把字幕加入可分享截圖, 在工作階段內鏡像畫面, 或在風險提示後使用最高 +15 dB 音量增強.
- 無需手勢亦可完整操作: TalkBack 下具名控制項保持可用, 每種手勢都有按鈕或選單等效入口, 版面支援 200% 字體與顯示縮放, 並支援鍵盤/DPAD 焦點, 空白鍵/Enter, 快進快退及 MediaSession 媒體鍵.
- 可選背景聽音: 預設關閉且只在授予通知權限後啟用; 畫中畫無法使用時會把現有播放器交給媒體播放前景服務並提供通知控制, 播完或關閉設定便停止. 畫中畫永遠優先.
- 唯讀安全: 不申請儲存權限, 絕不寫入來源影片; 網絡存取只用於檢查更新.

******

### 安裝與使用

******

開始前請確認以下環境要求:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

由安裝到播放第一個影片共 4 步:

1. 下載並安裝本外掛程式 APK. 安裝後主畫面會出現 3-Ember Player 圖示, 外掛程式能力則由 AutoJs6 統一管理.
2. 開啟 AutoJs6, 進入 `外掛程式中心`, 找到 `3-Ember Player` 並啟用.
3. 在 AutoJs6 檔案管理器中找到任何影片檔案 (如 `movie.mp4`).
4. 輕按該檔案, 影片隨即全螢幕開始播放.

不依賴主程式的用法: 由主畫面直接開啟 3-Ember Player, 輕按 `開啟影片` 並透過系統檔案選擇器挑選影片即可播放; 其他應用程式發起的影片檢視要求亦可選擇由本播放器承接. 上述環境要求中的主程式版本只約束檔案管理器入口, 獨立播放不受影響.

播放頁手勢一覽:

- 輕按畫面: 顯示或隱藏控制列.
- 雙按中央: 播放/暫停; 雙按左右兩側: 後退/前進 10 秒 (可在設定中改為 5/10/30 秒).
- 左半螢幕上下滑動: 調節亮度; 右半螢幕上下滑動: 調節音量.
- 橫向滑動: 預覽快進/快退目標位置, 鬆手生效.
- 長按畫面: 臨時 2 倍速播放, 鬆手回復原倍速.
- 雙指捏合: 縮放畫面 (0.25× 至 4×); 縮放狀態下雙按可復位.
- 鎖定按鈕: 屏蔽全部手勢與控制項防誤觸; 鎖定後輕按螢幕會浮現解鎖按鈕.

******

### 支援的格式

******

檔案管理器中的播放動作精確配對以下 23 種副檔名:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

以上是為舊版主程式保留的精確副檔名允許清單; 新版主程式識別為影片的任何檔案均可透過 `video/*` 交給本外掛程式. 符合清單不代表在每部裝置上都能解碼, 實際播放能力取決於 Media3 與裝置平台解碼器. 獨立入口與外部應用程式調用同樣按 `video/*` MIME 類型接收要求.

******

### 常見問題

******

**輕按影片檔案後沒有用本播放器開啟?**

請依次檢查: AutoJs6 版本代碼是否不低於 5276 (對應 6.8.0 及以上版本); 外掛程式是否已在 `外掛程式中心` 啟用; 該檔案是否被主程式識別為影片. 三者任一不滿足, 輕按都不會由本外掛程式承接.

**沒有安裝或已停用本外掛程式時, 輕按影片會怎樣?**

相容的主程式會顯示復原指引, 提示安裝或啟用外掛程式; 只有當使用者明確選擇 `用其他應用程式開啟` 時, 才會彈出系統應用程式候選選單, 交給裝置上的其他播放器.

**播放時只有聲音沒有畫面, 或提示無法播放?**

影片能否解碼取決於裝置平台與 Media3 的解碼能力, 符合副檔名清單不代表一定可以播放. 對常見的 XVID-in-MKV 舊格式, 外掛程式已內置相容層交給系統 MPEG-4 Part 2 解碼器處理; 仍無法解碼時會顯示清楚提示, 可輕按 `使用其他應用程式開啟` 轉交其他播放器.

**怎樣連續播放同一資料夾內的影片?**

在滿足主程式版本要求的前提下, 由檔案管理器開啟任何影片即會自動建立同目錄佇列: 按自然檔名排序, 由目前影片開始, 播完自動接續下一個, 並提供順序, 隨機與單項循環模式. 主程式版本過舊或由獨立入口開啟時, 保持單檔播放.

**外掛字幕怎樣載入?**

把 `.srt` 或 `.ass` 字幕檔案與影片放在同一目錄並保持同名 (允許 `movie.zh.srt` 這類語言後綴), 由檔案管理器開啟影片後即可在字幕選單中選擇載入. 字幕預設關閉, 不會自動開啟.

**續播功能記錄了甚麼? 會上傳嗎?**

只記錄最近一個未播完影片的位置, 內容僅為檔案的 SHA-256 摘要與時間數值, 不含檔案名稱與路徑; 開啟其他影片或播放完畢都會立即清除. 所有資料只儲存在本機, 不會上傳.

**外掛程式需要哪些權限?**

不申請儲存, 相機, 麥克風等任何敏感執行階段權限. 只聲明網絡權限用於檢查外掛程式更新 (使用者手動觸發或每日最多一次), 以及受主程式簽章保護的外掛程式權限用於檔案管理器入口.

**可不可以脫離 AutoJs6 獨立使用?**

可以. 自 v2.0.0 起外掛程式擁有主畫面入口: 開啟後透過系統檔案選擇器挑選影片即可播放, 亦可以在其他應用程式的 `開啟方式` 中選擇本播放器. 同目錄連播, 外掛字幕探索與跟隨主程式設定等能力則需要配合 AutoJs6 使用.

******

### 安全性

******

外掛程式按預設拒絕原則建構, 以下措施全部預設開啟且無法關閉:

- 零敏感權限: 不申請儲存或其他執行階段權限; 網絡存取只用於使用者觸發或每日一次的 GitHub 發行版檢查.
- 絕不寫入: 播放, 截圖, 縮圖提取全程唯讀, 任何情況下不修改, 不移動, 不刪除來源影片.
- 入口逐項核驗: 檔案管理器入口受簽章級外掛程式權限保護, 每個要求的通訊協定版本, 目標 URI, ClipData, 中繼資料, 主程式組建版本與唯讀授權都會逐項核驗, 任一不符即拒絕.
- 有界的同目錄存取: 佇列與字幕探索經由主程式管理的短期工作階段完成, 只能列舉選取檔案的直接同級, 禁止遞迴目錄, 寫入與持久授權; 播放器內部只接收經驗證的有界佇列, 不接觸檔案系統路徑.
- 雙入口相互隔離: 對系統公開的 `ACTION_VIEW` 入口只接受唯讀 content URI 的 `video/*` 要求, 拒絕寫入, 持久與前綴授權, 與檔案管理器入口彼此獨立.
- 續播最小化: 續播歷史只保留最近一條未播完記錄, 內容為 SHA-256 摘要與時間數值, 播完即清.
- 安全轉交: `使用其他應用程式開啟` 會重新建立唯讀 Intent 並自動排除本外掛程式, 避免授權擴散與自我循環.

******

### 外掛程式介面 (適用於開發者)

******

主程式透過以下識別資料探索並調用外掛程式:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
source namespace: io.github.supermonster003.autojs6.plugin.threeemberplayer
stable application id: io.github.supermonster003.autojs6.plugin.videoplayer
engine: explorer-action
variant: default
protocol version: v12
Explorer action id: play-video
Explorer placement: primary
access mode: read-only
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

目前實作基於 explorer-action 通訊協定 v12: 註冊唯讀主要動作 `play-video` 與選取工具列動作 `play-video-selection`, 透過 `video/*` 及供舊版主程式使用的 23 種精確副檔名接收影片. 主要動作可使用有界同級讀取建立同目錄佇列和探索字幕; 選取動作嚴格保留主程式給出的 1 至 128 個目標且永不啟用同級讀取. 相容工作階段亦可攜帶播放進度, 缺少任何可選能力時均安全降級. 音訊與影像仍是獨立外掛程式.

完整主程式協同能力需要 AutoJs6 6.8.0 (build 5276) 或更高版本及 Explorer Action v12; 後續外掛程式更新不會提高此要求.

******

### 開發路線圖

******

已完成能力與後續計劃以可勾選清單維護於 Roadmap.md. 未勾選條目表示規劃意向, 不代表目前版本已具備的能力.

- [查看可勾選的 Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### 版本記錄

******

#### v3.1.2

###### 2026/09/19

* `修復` Android 17 背景音訊交接時, 播放介面取消共用通知導致前景服務狀態遺失的問題
* `修復` 媒體控制器停止播放後, 背景播放服務仍保持執行的問題
* `修復` AGP 9.1 構建時的 SDK XML v4 解析警告及 JVM 單元測試組裝任務誤觸發 APK 原生程式庫對齊檢查的問題 (共用構建外掛 1.8.3)
* `優化` 適配 Android 17 (SDK 37) 目標版本及背景音訊播放前景服務要求

#### v3.1.1

###### 2026/09/15

* `優化` 將 compileSdk 提升到 37 (Android 17), targetSdk 保持 36, 待依賴目標版本的行為驗證後再提升

#### v3.1.0

###### 2026/09/13

* `新增` 本機播放清單: 支援 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留順序, 標題和重複項目. 宿主入口讀取同目錄檔案; 獨立入口可選擇清單資料夾以讀取相對路徑. 一次開啟一個清單, 最多 128 項; 不支援網絡位址, HLS 與巢狀清單
* `修復` 外掛版本日期固定使用英文, 不隨建置機器的語言變化
* `優化` 統一多語言資源, 明確插件啟用契約並驗證發佈產物

##### 完整記錄

* [CHANGELOG-zh-Hant-HK.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-HK.md)

******

### 建置

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 建置:

```powershell
.\gradlew.bat :app:assembleRelease
```

建置參數來自 `version.properties`, 目前最低 SDK 為 24, 目標 SDK 為 36.

******

### 資源結構

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 提供外掛程式資訊與播放器介面的本地化, `plugin_instruction.md` 提供主程式側展示的使用說明. 全部 README 與 CHANGELOG 由 `.python/generate_markdown.py` 依據 JSON 來源生成: 修改文件時請編輯 `.readme` 與 `.changelog` 下的 `lang_*.json` 並重新執行指令碼, 不要直接編輯生成的 Markdown 檔案.

******

### 相關連結

******

- AutoJs6 文件: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (播放引擎): https://developer.android.com/media/media3/exoplayer
- Android 安全檔案分享: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Ember-Player/blob/master/docs/16kb.md)
