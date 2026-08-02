<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>安全なcontent URI処理を備えたAutoJs6 Explorer向け読み取り専用動画再生</p>

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

AutoJs6 Video Playerプラグインは, AutoJs6 Explorerまたは別のAndroidアプリケーションから一時的な読み取り専用content URIアクセスで渡された動画コンテンツを再生します. AndroidX Media3 ExoPlayerを使用し, 元の動画を変更しません.

******

### 機能

******

- 共有 `org.autojs.plugin.EXPLORER_ACTION` プロトコルのバージョン2を通じて, 単一ファイル用の読み取り専用Explorerプライマリアクションを登録します.
- AndroidX Media3 ExoPlayerとPlayerViewを使用し, 自動再生, 標準コントロール, オーディオフォーカス, noisy出力変化への対応, 端末コーデックとの統合を提供します.
- 再生成後に再生位置と再生/一時停止の意図を復元し, 動画の再生中だけ画面を点灯状態に保ちます.
- `video/*` MIMEタイプの読み取り専用content URI向けに, エクスポートされた独立 `android.intent.action.VIEW` エントリを提供します.
- 再生に失敗した場合, 読み取り専用の表示Intentを再構築して候補一覧からこのプラグインを除外する安全な別のアプリで開くアクションを提供します.

******

### ホスト統合

******

AutoJs6は `app/src/main/java/org/autojs/autojs/ui/explorer/ExplorerView.kt` の主要な動画オープン経路と `app/src/main/java/org/autojs/autojs/ui/main/scripts/MediaInfoDialogManager.kt` の再生アクションにこのプラグインを統合します.

プラグインがインストール済み, 有効, 信頼済み, 互換であれば, 一致する動画を開くとExplorerのプライマリビューアとしてアクション `play-video` が起動します.

プラグインが未インストール, 無効, 利用不可, 非互換, または起動できない場合, AutoJs6はシステムの `android.intent.action.VIEW` 経路へフォールバックし, インストール済みの別の動画アプリケーションにファイルを渡します.

このプラグインは動画ファイルだけを対象にします. 音声再生と画像表示は独立したプラグイン機能であり, このAPKには含まれません.

******

### 対応形式

******

プライマリExplorerアクションのMIMEマッチャーは空で, ホストの次の23種類の動画拡張子だけに完全一致します:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### プラグインインターフェース

******

AutoJs6は次の識別子でプラグインを検出して実行します:

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

バージョン1は一覧の動画拡張子向けに読み取り専用のプライマリExplorerアクションを提供します. 独立した外部エントリは有効な任意の `video/*` MIMEサブタイプを持つ読み取り専用content URIを受け入れます. 実際のデコード可否はMedia3 extractorと端末で利用可能なコーデックに依存します.

プラグインは完全にJVMで実装され, ネイティブライブラリを含みません. `supportedAbis = emptyArray()` を宣言し, ABIに依存しない単一APKとして公開されます. AutoJs6ホストのビルド5269以降が必要です.

******

### セキュリティ

******

プラグインはストレージ権限とINTERNET権限を要求しません. 署名で保護されたExplorer境界は, プロトコルバージョン2, ソース画面, ホストビルド, 対象と親のcontent URI, ClipDataの正確な順序, 表示名, 宣言サイズ, MIMEタイプ, 拡張子, 読み取り専用grantフラグを検証します. その後, 対象URI, MIMEタイプ, 安全な表示名, 対象だけのClipData項目1つ, 読み取りgrantだけを含む新しい明示Intentを非エクスポートプレーヤー向けに作成します. 公開ACTION_VIEW境界はcontent URI, 動画MIMEタイプ, 正確な読み取りgrantだけを個別に受け入れ, 受信したすべてのextrasとClipDataを無視して, 同じ最小内部リクエストを再構築します.

******

### 安全制限

******

- 1回の再生リクエストにつき対象content URIは1つです.
- Explorer実行にはsignatureレベルの `org.autojs.permission.PLUGIN` 権限が必要です.
- 書き込みgrantと永続grantは常に拒否されます. Explorerの親URI検証に使うprefixアクセスはプレーヤーへ転送されません.
- 公開ACTION_VIEW境界は書き込みgrant, 永続grant, prefix grantを拒否します.
- 外部候補を先に解決して別パッケージだけに絞り込み, 新しく構築した読み取り専用Intentで起動します.
- 一覧にある拡張子でもすべての端末でデコードできるとは限りません. 実際の再生対応はMedia3とインストール済みのプラットフォームコーデックで決まります.

******

### リリース履歴

******

# v1.0.0

###### 2026/08/02

* `機能` プラグインID `video-player`, アクションID `play-video`, エンジン `explorer-action`, バリアント `default` のVideo Playerプラグイン
* `機能` MIMEマッチャーが空で現在のホスト動画拡張子23種類だけに完全一致し, AutoJs6ホストビルド5269を必要とするプロトコルv2の読み取り専用プライマリExplorerアクション
* `機能` 対象と親のcontent URI, ClipData, ソース, 表示名, サイズ, MIMEタイプ, 拡張子, grantを厳密に検証し, 非エクスポートプレーヤーへ最小限の情報だけを転送する署名保護Explorerエントリ
* `機能` 読み取り専用動画content URI向けの独立したエクスポート済みACTION_VIEWエントリ, 信頼できないextrasとClipDataの破棄, 禁止grantの拒否, 自己ループ防止
* `機能` 自動再生, 標準コントロール, オーディオフォーカス, noisy出力変化への対応, 再生位置と再生状態の保存, 再生中だけの画面点灯を備えたMedia3 ExoPlayerとPlayerViewによる再生
* `機能` 再生失敗後にこのプラグインを明示的に除外する新しい読み取り専用Intentを使う安全な別のアプリで開く復旧操作
* `機能` ネイティブライブラリを含まない純粋なJVM実装, `supportedAbis = emptyArray()` によるABI無制限宣言, ABIに依存しない単一APK
* `機能` スペイン語, フランス語, ロシア語, アラビア語, 日本語, 韓国語, 英語, 簡体字中国語, 香港繁体字中国語, 台湾繁体字中国語にローカライズされたメタデータ, UIテキスト, 使用説明, README, 変更履歴
* `依存関係` AndroidX Media3 ExoPlayer および UI バージョン 1.10.1 を追加
* `依存関係` Kotlin Parcelize runtime バージョン 2.2.21 を追加

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
