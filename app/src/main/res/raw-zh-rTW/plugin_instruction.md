# 影片播放器

影片播放器為 AutoJs6 檔案瀏覽器提供影片唯讀主要動作. 外掛程式使用 AndroidX Media3 ExoPlayer 和 PlayerView, 自動開始播放, 處理音訊焦點與嘈雜輸出變化, 並還原播放位置和播放狀態.

外掛程式需要 AutoJs6 build 5269+. 安裝外掛程式並保持啟用, 受信任且相容後, 相符的影片檔案將在此播放器中開啟. 如果外掛程式缺少或不可用, AutoJs6 將回復到 Android ACTION_VIEW 路徑. 音訊播放與影像檢視仍是獨立的外掛程式功能.

檔案瀏覽器副檔名:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

安全與隱私限制:

- 檔案瀏覽器執行需要簽章層級 AutoJs6 外掛程式權限.
- 外掛程式接受具有暫時唯讀存取權的 content URI, 且絕不寫入來源檔案.
- 拒絕寫入和持久授權. 前綴存取權絕不會轉送到播放器.
- 獨立 ACTION_VIEW 入口只接受唯讀影片 content URI, 並捨棄傳入 extras 與 ClipData.
- 播放失敗後, 使用其他應用程式開啟會重新建立唯讀 Intent 並排除此外掛程式.
- 實際解碼能力取決於 Media3 擷取器和裝置可用編解碼器.
