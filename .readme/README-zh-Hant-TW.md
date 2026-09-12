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

3-Ember Player 是 AutoJs6 檔案管理器的影片播放外掛程式, 同時也是一個可獨立使用的本機影片播放器. 在檔案管理器中點選影片檔案即可全螢幕播放; 手勢控制, 倍速, 外掛字幕, 同資料夾連播, 子母畫面等主流播放器的核心體驗一應俱全. 播放能力基於 AndroidX Media3 ExoPlayer 打造.

外掛程式堅持唯讀安全模型: 影片透過暫時唯讀授權進入播放器, 全程不要求儲存權限, 不修改也不移動任何來源檔案; 網路存取僅用於檢查外掛程式更新.

******

### 功能亮點

******

- 本機播放清單: 支援 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留順序, 標題和重複項目. 宿主入口讀取同目錄檔案; 獨立入口可選擇清單資料夾以讀取相對路徑. 一次開啟一個清單, 最多 128 項; 不支援網路位址, HLS 與巢狀清單.
- 點選即播: 在 AutoJs6 檔案管理器中點選影片檔案, 直接進入全螢幕沉浸式播放, 不需要任何設定.
- 順手的手勢: 螢幕左半部滑動調亮度, 右半部調音量, 橫向滑動快轉或倒轉, 雙擊兩側跳轉, 雙擊中央播放/暫停, 長按暫時 2 倍速, 還可以一鍵鎖定防止誤觸.
- 倍速與畫面控制: 0.25× 至 3× 共 9 檔倍速, 雙指捏合縮放畫面 (0.25× 至 4×), 符合/填滿/裁切三種縮放模式, 螢幕方向一鍵切換並依畫面比例自動建議.
- 同資料夾連播: 開啟一部影片就建立自然排序佇列; 佇列面板顯示目前項目與已知時長, 支援點選跳轉及循序/隨機/單項循環切換, 自動接續前也會顯示可取消的 3 秒提示.
- 主程式選取順序佇列: 唯讀`播放所選`動作可將同一資料夾內 1 至 128 部影片嚴格依主程式選取順序排隊, 不探索同層檔案; 可選設定也能跨工作階段記住播放模式.
- 外掛與內嵌字幕: 自動偵測同名 .srt / .ass 或手動載入單一字幕, 辨識常見舊編碼, 調整字幕樣式與 ±600 秒外掛字幕偏移, 並支援內嵌字幕與多音軌切換; 字幕預設關閉, 由使用者自行開啟.
- 精細查看: 暫停時可逐格後退或前進並長按連發, 也可設定或清除 A-B 區間循環, 方便反覆查看細節.
- 接續播放: 記住最近一部未看完影片的播放位置, 重新開啟自動續播; 看完就清除, 不留觀看痕跡.
- 子母畫面與系統整合: Android 8.0+ 播放中離開應用程式會自動進入小視窗, 支援耳機與藍牙控制, 媒體通知顯示標題與進度.
- 睡眠計時: 15/30/45/60 分鐘或播完停止, 時間到自動暫停播放.
- 拖曳預覽: 拖曳進度列時顯示目標時間氣泡, 並盡可能提供畫面縮圖預覽.
- 目前畫面截圖: Android 10+ 一鍵將目前畫面儲存為 PNG 到系統相簿, 不需要儲存權限, 也不更動來源影片.
- 疑難格式備援: 內建輕量 XVID-in-MKV 相容層; 裝置無法解碼時顯示明確提示, 並可一鍵轉交其他播放器.
- 主題隨心: 由一個主題色彩產生亮色與暗色兩套介面配色, 預設跟隨 AutoJs6, 另有 19 個內建色彩與可即時預覽的自訂 RGB 色彩.
- 獨立可用: 內建桌面入口與設定頁, 可透過系統檔案選擇器直接開啟影片, 也可作為系統 "開啟方式" 中的影片播放器.
- 顯示與輸出工具: 查看 HDR10/HLG/SDR 與可用色彩資訊並一鍵複製, 可選擇把字幕加入可分享截圖, 在工作階段內鏡像畫面, 或在風險提示後使用最高 +15 dB 音量增強.
- 不需手勢也能完整操作: TalkBack 下具名控制項保持可用, 每種手勢都有按鈕或選單等效入口, 版面支援 200% 字體與顯示縮放, 並支援鍵盤/DPAD 焦點, 空白鍵/Enter, 快轉倒轉及 MediaSession 媒體鍵.
- 可選背景聽音: 預設關閉且只在授予通知權限後啟用; 子母畫面無法使用時會把現有播放器交給媒體播放前景服務並提供通知控制, 播完或關閉設定就停止. 子母畫面永遠優先.
- 唯讀安全: 不要求儲存權限, 絕不寫入來源影片; 網路連線僅用於檢查更新.

******

### 安裝與使用

******

開始前請確認以下環境需求:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

從安裝到播放第一部影片共 4 步:

1. 下載並安裝本外掛程式 APK. 安裝後桌面會出現 3-Ember Player 圖示, 外掛程式能力則由 AutoJs6 統一管理.
2. 開啟 AutoJs6, 進入 `外掛中心`, 找到 `3-Ember Player` 並啟用.
3. 在 AutoJs6 檔案管理器中找到任一影片檔案 (如 `movie.mp4`).
4. 點選該檔案, 影片隨即全螢幕開始播放.

不依賴主程式的用法: 從桌面直接開啟 3-Ember Player, 點按 `開啟影片` 並透過系統檔案選擇器挑選影片即可播放; 其他應用程式發起的影片檢視請求也可以選擇由本播放器接手. 上述環境需求中的主程式版本僅限制檔案管理器入口, 獨立播放不受影響.

播放頁手勢一覽:

- 單擊畫面: 顯示或隱藏控制列.
- 雙擊中央: 播放/暫停; 雙擊左右兩側: 倒轉/快轉 10 秒 (可在設定中改為 5/10/30 秒).
- 螢幕左半部上下滑動: 調整亮度; 右半部上下滑動: 調整音量.
- 橫向滑動: 預覽快轉/倒轉的目標位置, 放開後生效.
- 長按畫面: 暫時以 2 倍速播放, 放開後恢復原倍速.
- 雙指捏合: 縮放畫面 (0.25× 至 4×); 縮放狀態下雙擊可復位.
- 鎖定按鈕: 停用全部手勢與控制項以防誤觸; 鎖定後單擊螢幕會浮現解鎖按鈕.

******

### 支援的格式

******

檔案管理器中的播放動作精確比對以下 23 種副檔名:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

以上是為舊版主程式保留的精確副檔名允許清單; 新版主程式辨識為影片的任何檔案, 都可透過 `video/*` 交給本外掛程式. 符合清單不代表在每部裝置上都能解碼, 實際播放能力取決於 Media3 與裝置平台的解碼器. 獨立入口與外部應用程式呼叫同樣依 `video/*` MIME 類型接收請求.

******

### 常見問題

******

**點選影片檔案後沒有用本播放器開啟?**

請依序檢查: AutoJs6 版本代碼是否不低於 5276 (對應 6.8.0 及以上版本); 外掛程式是否已在 `外掛中心` 啟用; 該檔案是否被主程式辨識為影片. 三者任一不符合, 點選都不會由本外掛程式接手.

**沒有安裝或已停用本外掛程式時, 點選影片會怎樣?**

相容的主程式會顯示復原指引, 提示安裝或啟用外掛程式; 只有當使用者明確選擇 `用其他應用程式開啟` 時, 才會跳出系統應用程式候選選單, 交給裝置上的其他播放器.

**播放時只有聲音沒有畫面, 或提示無法播放?**

影片能否解碼取決於裝置平台與 Media3 的解碼能力, 符合副檔名清單不代表一定可以播放. 對常見的 XVID-in-MKV 舊格式, 外掛程式已內建相容層, 交給系統 MPEG-4 Part 2 解碼器處理; 仍無法解碼時會顯示明確提示, 可點按 `使用其他應用程式開啟` 轉交其他播放器.

**要怎麼連續播放同一個資料夾裡的影片?**

在符合主程式版本需求的前提下, 從檔案管理器開啟任一影片就會自動產生同資料夾佇列: 依自然檔名排序, 從目前影片開始, 播完自動接續下一部, 並提供依序, 隨機與單項循環模式. 主程式版本過舊或從獨立入口開啟時, 維持單檔播放.

**外掛字幕要怎麼載入?**

把 `.srt` 或 `.ass` 字幕檔與影片放在同一個資料夾並保持同名 (允許 `movie.zh.srt` 這類語言後綴), 從檔案管理器開啟影片後, 就能在字幕選單中選擇載入. 字幕預設關閉, 不會自動開啟.

**續播功能記錄了什麼? 會上傳嗎?**

只記錄最近一部未播完影片的位置, 內容僅為檔案的 SHA-256 摘要與時間數值, 不含檔名與路徑; 開啟其他影片或播放完畢都會立即清除. 所有資料僅保存在本機, 不會上傳.

**外掛程式需要哪些權限?**

不要求儲存, 相機, 麥克風等任何敏感執行階段權限. 僅宣告網路權限用於檢查外掛程式更新 (使用者手動觸發或每日最多一次), 以及受主程式簽章保護的外掛程式權限用於檔案管理器入口.

**可以脫離 AutoJs6 獨立使用嗎?**

可以. 自 v2.0.0 起外掛程式擁有桌面入口: 開啟後透過系統檔案選擇器挑選影片即可播放, 也可以在其他應用程式的 `開啟方式` 中選擇本播放器. 同資料夾連播, 外掛字幕偵測與跟隨主程式設定等能力則需要搭配 AutoJs6 使用.

******

### 安全性

******

外掛程式依預設拒絕原則打造, 以下措施全部預設開啟且無法關閉:

- 零敏感權限: 不要求儲存或其他執行階段權限; 網路存取僅用於使用者觸發或每日一次的 GitHub 發行版檢查.
- 絕不寫入: 播放, 截圖, 縮圖擷取全程唯讀, 任何情況下都不修改, 不移動, 不刪除來源影片.
- 入口逐項驗證: 檔案管理器入口受簽章層級的外掛程式權限保護, 每個請求的通訊協定版本, 目標 URI, ClipData, 中繼資料, 主程式建置版本與唯讀授權都會逐項核驗, 任一不符即拒絕.
- 有界的同資料夾存取: 佇列與字幕偵測經由主程式管理的短期工作階段完成, 僅能列舉選取檔案的直接同層, 禁止遞迴資料夾, 寫入與持久授權; 播放器內部只接收經驗證的有界佇列, 不接觸檔案系統路徑.
- 雙入口相互隔離: 對系統公開的 `ACTION_VIEW` 入口僅接受唯讀 content URI 的 `video/*` 請求, 拒絕寫入, 持久與前綴授權, 與檔案管理器入口彼此獨立.
- 續播最小化: 續播歷史僅保留最近一筆未播完記錄, 內容為 SHA-256 摘要與時間數值, 播完即清除.
- 安全轉交: `使用其他應用程式開啟` 會重新建立唯讀 Intent 並自動排除本外掛程式, 避免授權擴散與自我循環.

******

### 外掛程式介面 (供開發者參考)

******

主程式透過以下識別資訊探索並呼叫外掛程式:

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

目前實作基於 explorer-action 通訊協定 v12: 註冊唯讀主要動作 `play-video` 與選取工具列動作 `play-video-selection`, 透過 `video/*` 及供舊版主程式使用的 23 種精確副檔名接收影片. 主要動作可使用有界同層讀取建立同資料夾佇列和偵測字幕; 選取動作嚴格保留主程式給出的 1 至 128 個目標且永不啟用同層讀取. 相容工作階段也可攜帶播放進度, 缺少任何選用能力時都會安全降級. 音訊與影像仍是獨立外掛程式.

完整主程式協同能力需要 AutoJs6 6.8.0 (build 5276) 或更高版本及 Explorer Action v12; 後續外掛程式更新不會提高此要求.

******

### 開發路線圖

******

已完成能力與後續計畫以可勾選清單維護在 Roadmap.md 中. 未勾選的項目表示規劃意向, 不代表目前版本已具備的能力.

- [查看可勾選的 Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### 版本記錄

******

#### v3.0.0

###### 2026/09/12

* `新增` 本機播放清單: 支援 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留順序, 標題和重複項目. 宿主入口讀取同目錄檔案; 獨立入口可選擇清單資料夾以讀取相對路徑. 一次開啟一個清單, 最多 128 項; 不支援網路位址, HLS 與巢狀清單
* `新增` 完成播放頁無障礙稽核: 控制項具備名稱與動態狀態, TalkBack 觸控探索時不會自動隱藏, 每種手勢都有按鈕或選單等效入口, 焦點順序明確, 並通過 200% 字體與顯示縮放驗證
* `新增` 支援鍵盤與遙控器: 清楚焦點環, DPAD 導覽, 空白鍵/Enter 播放暫停, 左右鍵依已設定的 5/10/30 秒步長跳轉, 媒體鍵交由 MediaSession 處理
* `新增` 新增預設關閉的可選背景聽音: 授予通知權限後, 子母畫面無法使用時可把目前播放無縫交給媒體播放前景服務並透過通知控制
* `優化` 子母畫面永遠優先於背景聽音; 關閉設定, 播放完成, 發生錯誤或主動離開播放都會停止服務, 點選通知則以相同播放器和連續進度返回播放頁
* `優化` 耳機行為遵循 Media3: 單擊切換播放暫停, 外接裝置雙擊在佇列有下一項時前進, 明確 Previous 媒體指令返回上一項; 不加入非標準三擊計時
* `優化` 統一 README 版式與 Gradle 平台版本管理方式
* `優化` 精簡外掛描述並規範多語言資源中的標點符號
* `優化` 將外部檢視入口統一命名為 External Viewer
* `優化` 外掛更新對話框的發行歷史按鈕改為開啟內建發行歷史頁面
* `優化` 建置階段阻止意外引入原生相依套件, 並輸出 JSON 校驗報告

#### v2.3.0

###### 2026/08/31

* `新增` 影片資訊現在可標示 HDR10,HLG 或 SDR,並在可取得時顯示色彩空間,範圍與位元深度;支援一次複製全部欄位,顯示裝置未回報支援來源 HDR 格式時會顯示一次提示
* `新增` 新增預設關閉的`螢幕擷取畫面包含字幕`設定,可將目前可見字幕繪製到擷取畫面;儲存 PNG 後可直接透過`分享`傳送到其他應用程式
* `新增` 新增僅在目前工作階段生效的畫面鏡像,支援左右,上下或雙軸翻轉,並與雙指縮放及螢幕旋轉一致組合
* `新增` 新增僅在目前工作階段生效的 +3 dB 至 +15 dB 音量增強,預設關閉,首次啟用前提示失真與聽力風險,裝置不支援時自動退回關閉
* `優化` 擷取畫面繼續以獨立 PNG 透過 MediaStore 儲存,不需要儲存權限;分享時只向目標應用程式授予所選圖片的唯讀存取
* `優化` 評估後採用 LoudnessEnhancer:它能在不修改媒體檔案的前提下提供設有上限的播放增益;裝置效果建立失敗時會立即釋放並安全退回

#### v2.2.0

###### 2026/08/31

* `新增` 新增 Host Session 播放佇列面板:顯示檔名,已知時長或預留位置,高亮目前項目,點按跳轉,並即時連動循序,隨機與單項循環
* `新增` 新增唯讀檔案管理器動作`播放所選`:依主程式選取順序播放同一父資料夾內 1 至 128 個支援的影片,且不探索同層檔案
* `新增` 佇列自動連續播放前保留目前末幀並顯示可取消的 3 秒下一項提示,控制鎖定狀態不會被解除
* `新增` 新增預設關閉的`記住播放模式`設定,可跨播放工作階段保留循序,隨機或單項循環
* `優化` 每個所選目標都會分別核對 content URI,ClipData 位置,MIME,中繼資料,大小與父資料夾;ID 或 URI 重複時整組拒絕
* `優化` 多選只透過有界 Host Session 路由讀取主程式明確授權的目標,不掃描同層影片或字幕

##### 完整記錄

* [CHANGELOG-zh-Hant-TW.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-TW.md)

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

`strings.xml` 提供外掛程式資訊與播放器介面的本地化, `plugin_instruction.md` 提供主程式端顯示的使用說明. 全部 README 與 CHANGELOG 由 `.python/generate_markdown.py` 依據 JSON 來源產生: 修改文件時請編輯 `.readme` 與 `.changelog` 下的 `lang_*.json` 並重新執行指令碼, 不要直接編輯產生的 Markdown 檔案.

******

### 相關連結

******

- AutoJs6 文件: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (播放引擎): https://developer.android.com/media/media3/exoplayer
- Android 安全檔案分享: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Ember-Player/blob/master/docs/16kb.md)
