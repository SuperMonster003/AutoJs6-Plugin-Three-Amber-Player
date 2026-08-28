<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>ファイルマネージャープラグイン. 動画ファイルを直接再生</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語に対応しています:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### 概要

******

Video Playerは, ファイルマネージャーまたは別のAndroidアプリケーションから一時的な読み取り専用content URIアクセスで渡された動画コンテンツを再生します. AndroidX Media3 ExoPlayerを使用し, 元の動画を変更しません.

******

### 機能

******

- 端末内蔵の MPEG-4 Part 2 デコーダーによる軽量な XVID-in-MKV 互換機能。システムデコーダーが利用できない場合や失敗した場合は明確に案内し、別のプレーヤーへ渡せます.
- 共有 `org.autojs.plugin.EXPLORER_ACTION` プロトコル v12 を通じて読み取り専用Explorerプライマリアクションを登録し, 直接同階層ファイルと再生進捗の任意かつ限定的な機能を提供します.
- AndroidX Media3 ExoPlayerとPlayerViewを使用し, 自動再生, 標準コントロール, オーディオフォーカス, noisy出力変化への対応, 端末コーデックとの統合を提供します.
- 再生成後に再生位置と再生/一時停止の意図を復元し, 動画の再生中だけ画面を点灯状態に保ちます.
- `video/*` MIMEタイプの読み取り専用content URI向けに, エクスポートされた独立 `android.intent.action.VIEW` エントリを提供します.
- 再生に失敗した場合, 読み取り専用の表示Intentを再構築して候補一覧からこのプラグインを除外する安全な別のアプリで開くアクションを提供します.
- 全画面の没入型再生を提供し, 明るさ, 音量, 再生位置のジェスチャー調節, ダブルタップ移動, 長押し倍速, 操作ロックに対応します.
- 0.25×から3×までの再生速度, 画面サイズモード, 画面の向きをワンタップで切り替えます.
- content URIダイジェストをキーとするローカルな再生位置の記憶を提供し, 視聴完了で自動的に消去します.
- 内蔵音声トラックと字幕を選択でき, 字幕は既定でオフ, 非対応トラックは明確に識別表示されます.
- 現在の動画のリピート, 詳細なメタデータパネル, 画面比率による方向設定, API 26以降の遠隔再生/一時停止対応ピクチャー イン ピクチャーを提供します.
- MediaSessionでヘッドセット, Bluetooth, システム操作に対応し, メディア通知にタイトルと進行状況を表示します.
- 15/30/45/60 分後または動画終了時のスリープタイマーと, 永続化されるジェスチャー感度および 5/10/30 秒のダブルタップシーク設定.
- 0.25× から 4× のピンチズームとダブルタップリセット, シーク中の目標時刻と任意のメモリ内サムネイル表示.
- Android 10+ で権限なしに現在のフレームを MediaStore 経由の別 PNG として保存し, 元の動画は変更しません.
- Explorer Action v12 により同じフォルダーの動画を自然順のキューにし, 前へ / 次へ, 順次, シャッフル, 1項目リピート, 次項目の自動再生を提供します.
- 言語サフィックスを含む一致する .srt / .ass 外部字幕を検出し, 既定ではオフのまま明示的に選択した場合だけ読み込みます.
- ホスト管理の再開履歴は任意で既定オフです. AutoJs6 設定で無効化または消去でき, ファイル一覧に視聴済み印は追加しません.

******

### ホスト統合

******

ホストは主要な動画オープン経路と再生アクションでこのプラグインを使用します.

プラグインがインストール済み, 有効, 信頼済み, 互換であれば, ホストが動画として認識した任意のファイルを開くとExplorerのプライマリビューアとしてアクション `play-video` が起動します.

プラグインが未インストール, 無効, 未承認, 利用不可, 非互換, または起動できない場合, 対応ホストは復旧案内を表示します. システムのアプリ候補は, ユーザーが明示的に「他のアプリで開く」を選択した場合にのみ表示されます.

このプラグインは動画ファイルだけを対象にします. 音声再生と画像表示は独立したプラグイン機能であり, このAPKには含まれません.

******

### 対応形式

******

プライマリExplorerアクションはホストが認識するすべての動画形式を `video/*` で受け入れ, 旧ホストとの互換性のために次の23種類の完全一致拡張子マッチャーも保持します:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### プラグインインターフェース

******

ホストは次の識別子でプラグインを検出して実行します:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: video-player
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

バージョン1.4はプロトコル v12 の読み取り専用Explorerプライマリアクションを提供します. 対応ホストはリクエスト単位の直接同階層読み取りと再生進捗機能を追加でき, これらの任意拡張がないホストでは単一ファイル再生を維持します. 独立した外部エントリは有効な `video/*` MIMEサブタイプを持つ読み取り専用content URIだけを受け入れます.

完全なホスト連携には AutoJs6 6.8.0 build 5276 以降と Explorer Action v12 が必要です. 今後のプラグイン機能でもこの要件は引き上げません.

******

### セキュリティ

******

プラグインはストレージ権限とINTERNET権限を要求しません. 署名で保護されたExplorer境界は, 完全なプロトコル v12 エンベロープ, 選択対象が正確に1つであること, 親子関係, ClipData, メタデータ, ホストビルド, 読み取り専用grantを検証します. 任意のHost SessionはホストによってプラグインUIDに固定され, 選択ファイルの直接の親の列挙と, 選択ファイルまたは読み取り可能な直接同階層ファイルのオープンだけを許可します. 非公開プレーヤーは上限付きの不透明キューを検証し, ファイルシステムパスを受け取りません. 公開ACTION_VIEW境界は独立した単一ファイル入口のままです.

******

### 安全制限

******

- Explorerは正確に1つの選択content URIから開始します. 任意のHost Sessionが公開できるのは読み取り可能な直接同階層ファイルだけで, 再帰的なフォルダーアクセスはできません.
- Explorer実行にはsignatureレベルの `org.autojs.permission.PLUGIN` 権限が必要です.
- 書き込みgrantと永続grantは常に拒否されます. Explorerの親URI検証に使うprefixアクセスはプレーヤーへ転送されません.
- 公開ACTION_VIEW境界は書き込みgrant, 永続grant, prefix grantを拒否します.
- 外部候補を先に解決して別パッケージだけに絞り込み, 新しく構築した読み取り専用Intentで起動します.
- ホストの再生履歴は既定で無効です. 明示的に有効化した後も正規化パスのダイジェストと時刻値だけを保存し, AutoJs6設定で無効化または消去できます.
- 動画としての認識や一覧にある旧拡張子は, すべての端末でのデコードを保証しません. 実際の再生対応はMedia3とインストール済みのプラットフォームコーデックで決まります.

******

### リリース履歴

******

# v1.4.0

###### 2026/08/28

* `機能` Explorer Action v12 のリクエスト限定・UID 固定 Host Session により同じフォルダーの動画を自然順でキュー化し、前へ / 次へ、順次、シャッフル、1 項目リピート、自動次項再生に対応
* `機能` 同名の .srt / .ass 外部字幕とその言語サフィックス形式を検出し、既定では字幕をオフのまま、ユーザーが明示的に選択した場合のみ読み込み
* `機能` ホスト管理の任意の再開履歴を追加。記録は既定でオフで、AutoJs6 設定から無効化または消去でき、ファイル一覧に視聴済み表示は追加しません
* `修正` ホストが動画と分類した Explorer リクエストでも拡張子が旧 23 項目の許可リストになければ拒否される問題. 信頼済みの `video/*` リクエストを一律に受け入れるよう変更
* `改善` 同階層アクセスを選択ファイルと読み取り可能な直下の兄弟ファイルだけに限定し、再帰探索、書き込み、永続 grant、プラグイン内の平文パス保存を禁止
* `改善` シリアライズするキューを動画 128 件、動画ごとの字幕 8 件、字幕関連付け合計 128 件に制限しつつ、選択項目を必ず保持
* `改善` 互換要件を AutoJs6 6.8.0 build 5276 と Explorer Action v12 に固定。任意拡張に未対応のホストでは安全に単一ファイル再生を維持
* `依存関係` 同梱 Explorer Action API をプロトコル v2 から後方互換の v12 メディアセッション拡張へ更新

# v1.3.1

###### 2026/08/27

* `修正` 検証済みの VFW/FourCC XVID トラックを端末内蔵の MPEG-4 Part 2 デコーダーへ渡す軽量な XVID-in-MKV 互換レイヤーを追加しました。トランスコードや元ファイルの変更は行いません
* `修正` 互換システムデコーダーがない場合やデコードに失敗した場合は音声のみの再生を停止し、専用の説明と別のアプリで開く操作を表示します

# v1.3.0

###### 2026/08/27

* `機能` スリープタイマー: 15, 30, 45, 60 分後または現在の動画終了時に一時停止し, リピートとの競合も処理
* `機能` 画面操作: 0.25× から 4× のピンチズーム, ズーム中のダブルタップリセット, 既存表示モードとの連携
* `機能` シークプレビュー: 目標時刻と任意のメモリ内サムネイルを表示し, 抽出失敗時は通知せず時刻表示だけに切り替え
* `機能` Android 10+ で現在のフレームをストレージ権限なしに MediaStore 経由の別 PNG として保存し, 元の動画は変更しない
* `機能` 低/標準/高のジェスチャー感度と 5/10/30 秒のダブルタップシークを永続設定
* `改善` スリープ期限は経過時間を使い, 時計変更に依存せず再生ページの状態再作成後も復元
* `改善` サムネイル抽出は高速なシーク要求を単一ワーカーで統合し, 古くなった一時 Bitmap をすべて解放

##### その他のリリース

* [CHANGELOG-ja.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Releaseビルド:

```powershell
.\gradlew.bat :app:assembleRelease
```

ビルドパラメーターは `version.properties` から取得します. 現在の最小SDKは24, ターゲットSDKは36です.

******

### リソース構成

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` はプラグインのメタデータとUIテキストをローカライズします. `plugin_instruction.md` は使用方法とセキュリティの説明を提供します. `.python/generate_markdown.py` はJSONソースからローカライズされたREADMEと変更履歴を生成します.

******

### リンク

******

- AutoJs6ドキュメント: https://docs.autojs6.com
- Androidの安全なファイル共有: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
