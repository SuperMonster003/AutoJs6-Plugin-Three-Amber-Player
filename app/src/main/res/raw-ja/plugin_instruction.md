# 動画プレーヤー

Video PlayerはAutoJs6 Explorerに読み取り専用のプライマリ動画アクションを追加します. AndroidX Media3 ExoPlayerとPlayerViewを使用し, 自動的に再生を開始し, オーディオフォーカスとnoisy出力変化を処理し, 再生位置と再生状態を復元します.

このプラグインにはAutoJs6 build 5269+が必要です. インストール済み, 有効, 信頼済み, 互換であれば, 一致する動画ファイルはこのプレーヤーで開きます. プラグインがないか利用できない場合, AutoJs6はAndroid ACTION_VIEW経路へフォールバックします. 音声再生と画像表示は独立したプラグイン機能です.

Explorer拡張子:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

安全性とプライバシーの制限:

- Explorer実行にはsignatureレベルのAutoJs6プラグイン権限が必要です.
- プラグインは一時的な読み取り専用アクセスを持つcontent URIだけを受け入れ, ソースへ書き込みません.
- 書き込みgrantと永続grantは拒否されます. prefixアクセスはプレーヤーへ転送されません.
- 独立したACTION_VIEWエントリは読み取り専用の動画content URIだけを受け入れ, 受信したextrasとClipDataを破棄します.
- 再生に失敗した場合, 別のアプリで開く操作は読み取り専用Intentを再構築してこのプラグインを除外します.
- 実際のデコード可否はMedia3 extractorと端末で利用可能なコーデックに依存します.
