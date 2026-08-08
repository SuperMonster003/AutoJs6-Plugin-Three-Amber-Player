# 影片播放器

影片播放器為檔案管理器提供影片唯讀主要動作. 插件使用 AndroidX Media3 ExoPlayer 和 PlayerView, 自動開始播放, 處理音訊焦點與嘈雜輸出變化, 並還原播放位置和播放狀態.

插件需要主程式組建版本 5269+. 安裝插件並保持啟用, 可信且相容後, 相符的影片檔案將在此播放器中開啟. 如果插件缺少或不可用, 主程式將回復到 Android ACTION_VIEW 路徑. 音訊播放與影像檢視仍是獨立的插件功能.

檔案瀏覽器副檔名:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

安全與私隱限制:

- 檔案管理器執行需要簽章層級插件權限.
- 外掛程式接受具有臨時唯讀存取權的 content URI, 且絕不寫入來源檔案.
- 拒絕寫入和持久授權. 前綴存取權絕不會轉送到播放器.
- 獨立 ACTION_VIEW 入口只接受唯讀影片 content URI, 並捨棄傳入 extras 與 ClipData.
- 播放失敗後, 使用其他應用程式開啟會重新建立唯讀 Intent 並排除此外掛程式.
- 實際解碼能力取決於 Media3 擷取器和裝置可用編解碼器.
