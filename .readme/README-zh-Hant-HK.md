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

3-Ember Player 是 AutoJs6 檔案管理器的影片播放外掛程式, 同時亦是一個可獨立使用的本機影片播放器. 在檔案管理器中輕按影片檔案即可全螢幕播放; 手勢控制, 倍速, 外掛字幕, 同目錄連播, 畫中畫等主流播放器的核心體驗一應俱全. 播放能力建基於 AndroidX Media3 ExoPlayer.

外掛程式堅持唯讀安全模型: 影片透過臨時唯讀授權進入播放器, 全程不申請儲存權限, 不修改亦不移動任何來源檔案; 網絡存取只用於檢查外掛程式更新.

******

### 功能亮點

******

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

#### v2.2.0

###### 2026/08/31

* `新增` 新增 Host Session 播放佇列面板:顯示檔案名稱,已知時長或佔位,高亮目前項目,點按跳轉,並即時連動順序,隨機與單項循環
* `新增` 新增唯讀檔案管理器動作`播放所選`:按主程式選取順序播放同一父資料夾內 1 至 128 個支援的影片,且不探索同層檔案
* `新增` 佇列自動連播前保留目前末幀並顯示可取消的 3 秒下一項提示,控制鎖定狀態不會被解除
* `新增` 新增預設關閉的`記住播放模式`設定,可跨播放工作階段保留順序,隨機或單項循環
* `優化` 每個所選目標都會分別核對 content URI,ClipData 位置,MIME,元數據,大小與父資料夾;ID 或 URI 重複時整組拒絕
* `優化` 多選只透過有界 Host Session 路由讀取主程式明確授權的目標,不掃描同層影片或字幕

#### v2.1.0

###### 2026/08/31

* `新增` 新增字幕樣式設定: 字體大小可選 75%-150%, 前景色, 不透明/半透明/無背景及 0%/4%/8% 底部邊距均可即時預覽, 並持續套用於播放頁與畫中畫
* `新增` 新增外掛字幕編碼偵測: BOM 優先, 其後識別 GBK, Big5, Shift_JIS, EUC-KR, Windows-1251 及 Windows-1256, 無法可靠識別時明確提示可能出現亂碼
* `新增` 字幕選單新增從檔案載入: 透過系統文件選擇器暫時讀取單個 .srt 或 .ass 字幕, 超過 4 MiB 或副檔名不符時拒絕載入
* `新增` 所選外掛字幕可在 −600.0 至 +600.0 秒之間按 0.1 秒調整, 畫面提示即時顯示目前偏移
* `新增` 暫停時可逐格後退或前進 (長按連續執行), 並可設定邊界自動正規化的 A-B 區間循環
* `優化` 字幕解碼, UTF-8 轉換與時間平移全部在記憶體完成, 不建立暫存檔, 不申請儲存權限, 亦不持久保留字幕授權
* `優化` 切換影片時字幕偏移自動歸零; A-B 循環啟用期間優先執行, 其後明確選擇播放模式或播完停止計時會將其清除

#### v2.0.0

###### 2026/08/29

* `新增` 全新主題系統: 由一個主題色自動生成亮色與暗色兩套介面配色, 預設跟隨 AutoJs6 主題, 亦可從 19 個預置色中挑選或自訂 RGB 色彩並即時預覽
* `新增` 外掛程式升級為獨立應用程式: 新增主畫面啟動入口, 可透過系統檔案選擇器直接開啟影片播放
* `新增` 新增設定頁面: 集中管理語言, 夜間模式, 主題色, 續播, 更新與發行記錄; 語言, 夜間模式與主題色預設跟隨 AutoJs6, 主程式不可用時自動停用相應選項並退回應用程式預設值
* `新增` 新增檢查更新: 支援手動檢查與每日自動檢查官方 GitHub 發行版, 可忽略指定版本, 並內置多語言發行記錄頁面
* `修復` 修復跟隨 AutoJs6 設定在部分情況不生效的問題 (主程式要求的外掛程式資訊服務先前未公開)
* `優化` 續播記錄精簡為只保留最近一個未播完的影片, 開啟其他影片立即清除舊記錄, 已播完的影片不再保留位置
* `優化` 應用程式改名為 3-Ember Player; 應用程式 ID 與外掛程式 ID 保持不變, 舊版本可直接覆蓋升級

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
