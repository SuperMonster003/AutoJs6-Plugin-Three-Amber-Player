<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
    </picture>
  </p>

  <p>プレイリスト, 字幕, バックグラウンド音声に対応した動画再生</p>

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

3-Ember Player は AutoJs6 ファイルマネージャー用の動画再生プラグインであり, 単体でも使えるローカル動画プレーヤーです. ファイルマネージャーで動画ファイルをタップするだけで全画面再生が始まります; ジェスチャー操作, 倍速再生, 外部字幕, 同一フォルダーの連続再生, ピクチャー イン ピクチャーなど, 主要な動画プレーヤーの定番機能をひととおり備えています. 再生エンジンには AndroidX Media3 ExoPlayer を採用しています.

プラグインは読み取り専用のセキュリティモデルを貫いています: 動画は一時的な読み取り専用アクセス許可を通じてプレーヤーに渡され, ストレージ権限は一切要求せず, 元のファイルを変更したり移動したりすることもありません; ネットワークアクセスはプラグインの更新確認だけに使われます.

******

### 機能ハイライト

******

- タップですぐ再生: AutoJs6 のファイルマネージャーで動画ファイルをタップするだけで, 設定いらずでそのまま没入型の全画面再生が始まります.
- 使いやすいジェスチャー: 画面の左半分を上下にスワイプすると明るさ, 右半分では音量を調節でき, 横スワイプで早送り/巻き戻し, 左右のダブルタップでジャンプ, 中央のダブルタップで再生/一時停止, 長押しで一時的な 2 倍速が使え, ワンタップのロックで誤操作も防げます.
- 倍速と画面のコントロール: 0.25× から 3× まで 9 段階の再生速度, ピンチ操作での画面ズーム (0.25× から 4×), フィット/フィル/クロップの 3 つの表示モードを備え, 画面の向きはワンタップで切り替えられ, アスペクト比に応じた向きが自動で提案されます.
- 同一フォルダーの連続再生: 動画を 1 つ開くと自然順の再生キューが作られ, キューパネルで現在項目と判明済み時間の表示, タップ移動, 順次/シャッフル/1 項目リピート切替ができ, 次へ進む前にはキャンセル可能な 3 秒案内を表示します.
- ホストの選択順キュー: 読み取り専用の`選択項目を再生`は同じフォルダーの動画 1-128 件をホストの選択順どおりにキュー化し, 同階層を探索しません; 任意設定で再生モードをセッション間に記憶できます.
- 外部字幕と内蔵字幕: 同名の .srt / .ass を自動検出または 1 件手動読込し, 一般的な旧文字コードの判別, 字幕スタイルと ±600 秒の外部字幕オフセット調整, 内蔵字幕や複数音声トラックの切り替えに対応します; 字幕は明示的にオンにするまでオフです.
- 精密再生: 一時停止中に前後のコマ送りと長押し連続実行ができ, A-B 区間リピートの設定や解除で細部を繰り返し確認できます.
- 続きから再生: まだ見終えていない直近の動画の再生位置を記憶し, 開き直すと自動で続きから再生します; 見終わるとすぐに記録が消去され, 視聴の痕跡は残りません.
- ピクチャー イン ピクチャーとシステム連携: Android 8.0 以降では再生中にアプリを離れると自動で小窓表示になり, ヘッドセットや Bluetooth からの操作に対応し, メディア通知にタイトルと進行状況が表示されます.
- スリープタイマー: 15/30/45/60 分後または現在の動画の再生終了時を選べ, 時間になると自動で一時停止します.
- シークプレビュー: シークバーをドラッグすると移動先の時刻がバブルで表示され, 可能な場合はサムネイルのプレビューも表示されます.
- フレームキャプチャ: Android 10 以降ではワンタップで現在の画面を PNG として端末のギャラリーに保存できます. ストレージ権限は不要で, 元の動画も変更しません.
- 扱いにくい形式へのフォールバック: 軽量な XVID-in-MKV 互換レイヤーを内蔵しています; 端末でデコードできない場合は分かりやすいメッセージを表示し, ワンタップで別のプレーヤーに引き継げます.
- テーマは思いのまま: 1 つのテーマカラーからライトとダークの配色を生成し, 既定では AutoJs6 に従います. さらに 19 色のプリセットと, ライブプレビュー付きのカスタム RGB カラーも用意しています.
- 単体でも使える: ランチャーアイコンと設定画面を備え, システムのファイル選択画面から動画を直接開けるほか, システムの "アプリで開く" メニューの動画プレーヤーとしても機能します.
- 表示と出力: HDR10/HLG/SDR と取得できる色情報の確認と一括コピー, 字幕を含む共有可能なスクリーンショット, セッション内の映像反転, 警告確認後の最大 +15 dB 音量ブーストを利用できます.
- ジェスチャーなしでも操作可能: TalkBack では名前付きコントロールが表示されたままになり, すべてのジェスチャーにボタンまたはメニューの代替手段があります. 200% の文字/表示倍率と, キーボード/DPAD フォーカス, Space/Enter, シーク, MediaSession キーにも対応します.
- 任意のバックグラウンド音声: 初期状態はオフで, 通知権限がある場合のみ有効化できます. PiP が利用できないときは既存プレーヤーを通知操作付きメディア再生フォアグラウンドサービスへ移し, 再生完了または設定オフで停止します. PiP が常に優先されます.
- 読み取り専用で安全: ストレージ権限を要求せず, 元の動画には決して書き込みません; ネットワークは更新確認だけに使います.

******

### インストールと使い方

******

始める前に次の動作環境を確認してください:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

インストールから最初の動画再生までは 4 ステップです:

1. 本プラグインの APK をダウンロードしてインストールします. インストール後はホーム画面に 3-Ember Player のアイコンが追加され, プラグインとしての機能は AutoJs6 側でまとめて管理されます.
2. AutoJs6 を開いて `プラグインセンター` に入り, `3-Ember Player` を見つけて有効にします.
3. AutoJs6 のファイルマネージャーで任意の動画ファイル (例: `movie.mp4`) を探します.
4. そのファイルをタップすると, すぐに全画面で再生が始まります.

ホストに頼らない使い方: ホーム画面から 3-Ember Player を直接開き, `動画を開く` をタップしてシステムのファイル選択画面で動画を選ぶだけで再生できます; 他のアプリからの動画表示リクエストをこのプレーヤーで引き受けることもできます. 上記の動作環境にあるホストのバージョン要件はファイルマネージャーからの入口だけに関わるもので, 単体での再生には影響しません.

再生画面のジェスチャー早見表:

- 画面をシングルタップ: コントロールバーを表示または非表示にします.
- 中央をダブルタップ: 再生/一時停止; 左右をダブルタップ: 10 秒の巻き戻し/早送り (設定で 5/10/30 秒に変更できます).
- 左半分を上下にスワイプ: 明るさを調節; 右半分を上下にスワイプ: 音量を調節します.
- 横にスワイプ: 早送り/巻き戻しの移動先をプレビューし, 指を離すと確定します.
- 画面を長押し: 一時的に 2 倍速で再生し, 指を離すと元の速度に戻ります.
- 2 本の指でピンチ: 画面をズームします (0.25× から 4×); ズーム中はダブルタップで元に戻せます.
- ロックボタン: すべてのジェスチャーとコントロールを無効にして誤操作を防ぎます; ロック中に画面をタップするとロック解除ボタンが現れます.

******

### 対応形式

******

ファイルマネージャーでの再生アクションは次の 23 種類の拡張子に完全一致で対応します:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

上記は旧バージョンのホスト向けに残している拡張子の完全一致許可リストです; 新しいホストでは, 動画として認識されたファイルはすべて `video/*` として本プラグインに渡されます. リストに含まれていても, すべての端末でデコードできるとは限りません. 実際に再生できるかどうかは Media3 と端末プラットフォームのデコーダーで決まります. 単体の入口や外部アプリからの呼び出しも同じく `video/*` MIME タイプでリクエストを受け付けます.

******

### よくある質問

******

**動画ファイルをタップしてもこのプレーヤーで開かないときは?**

次の順に確認してください: AutoJs6 のバージョンコードが 5276 以上か (6.8.0 以降のバージョンに相当); プラグインが `プラグインセンター` で有効になっているか; そのファイルがホストに動画として認識されているか. どれか 1 つでも満たしていないと, タップは本プラグインでは処理されません.

**プラグインが未インストールまたは無効のとき, 動画をタップするとどうなる?**

対応ホストではインストールや有効化を促す復旧ガイドが表示されます; ユーザーが明示的に `別のアプリで開く` を選んだ場合にだけシステムのアプリ選択メニューが開き, 端末上の別のプレーヤーに引き継がれます.

**音だけ出て映像が表示されない, または再生できないと表示されるときは?**

動画をデコードできるかどうかは端末プラットフォームと Media3 のデコード能力に依存し, 拡張子リストに一致しても必ず再生できるわけではありません. よくある旧形式の XVID-in-MKV については, 内蔵の互換レイヤーがシステムの MPEG-4 Part 2 デコーダーに処理を渡します; それでもデコードできない場合は分かりやすいメッセージが表示され, `別のアプリで開く` をタップして別のプレーヤーに引き継げます.

**同じフォルダーにある動画を続けて再生するには?**

ホストがバージョン要件を満たしていれば, ファイルマネージャーから動画を開くだけで同一フォルダーのキューが自動で作られます: ファイル名の自然順に並び, 現在の動画から始まり, 再生が終わると自動で次に進み, 順次, シャッフル, 1 項目リピートの各モードを選べます. ホストのバージョンが古い場合や単体の入口から開いた場合は, 単一ファイル再生のままになります.

**外部字幕を読み込むには?**

`.srt` または `.ass` の字幕ファイルを動画と同じフォルダーに同じ名前で置いてください (`movie.ja.srt` のような言語サフィックスも使えます). ファイルマネージャーから動画を開くと, 字幕メニューで選んで読み込めます. 字幕は既定でオフになっており, 自動でオンになることはありません.

**続きから再生は何を記録している? アップロードはされる?**

記録するのは, まだ見終えていない直近の動画の再生位置 1 件だけです. 内容はファイルの SHA-256 ダイジェストと時間の数値のみで, ファイル名やパスは含まれません; 別の動画を開くか最後まで再生すると直ちに消去されます. すべてのデータは端末内だけに保存され, アップロードされることはありません.

**プラグインが必要とする権限は?**

ストレージ, カメラ, マイクなどの機微な実行時権限は一切要求しません. 宣言しているのは, プラグインの更新確認に使うネットワーク権限 (ユーザーの手動操作または 1 日 1 回まで) と, ファイルマネージャー入口のためのホスト署名で保護されたプラグイン権限だけです.

**AutoJs6 なしで単体でも使える?**

使えます. v2.0.0 からプラグインにはランチャー入口があります: 開いてシステムのファイル選択画面で動画を選ぶだけで再生でき, 他のアプリの `アプリで開く` メニューからこのプレーヤーを選ぶこともできます. 同一フォルダーの連続再生, 外部字幕の検出, ホスト設定への追従などの機能には AutoJs6 との連携が必要です.

******

### セキュリティ

******

プラグインは "既定で拒否" の原則に基づいて作られており, 以下の対策はすべて常に有効で, 無効にはできません:

- 機微な権限ゼロ: ストレージなどの実行時権限を要求しません; ネットワークアクセスはユーザー操作時または 1 日 1 回の GitHub リリース確認だけに使われます.
- 書き込みは一切なし: 再生, フレームキャプチャ, サムネイル抽出はすべて読み取り専用で行われ, どのような場合でも元の動画を変更, 移動, 削除しません.
- 入口での項目別検証: ファイルマネージャー入口は署名レベルのプラグイン権限で保護され, リクエストごとにプロトコルバージョン, 対象 URI, ClipData, メタデータ, ホストのビルド, 読み取り専用アクセス許可を 1 つずつ検証し, 1 つでも一致しなければ拒否します.
- 範囲を限定した同一フォルダーアクセス: キュー作成と字幕検出はホストが管理する短期セッションを通じて行われ, 列挙できるのは選択したファイルの直接の同階層だけです. フォルダーの再帰探索, 書き込み, 永続的なアクセス許可は禁止されています; プレーヤー内部は検証済みの範囲限定キューだけを受け取り, ファイルシステムのパスには触れません.
- 2 つの入口の相互分離: システム向けの `ACTION_VIEW` 入口は読み取り専用 content URI の `video/*` リクエストだけを受け付け, 書き込み, 永続, プレフィックスの各アクセス許可を拒否し, ファイルマネージャー入口からは独立しています.
- 続きから再生のデータ最小化: 再開履歴は見終えていない直近の 1 件だけを保持し, 内容は SHA-256 ダイジェストと時間の数値で, 最後まで再生するとすぐ消去されます.
- 安全な引き継ぎ: `別のアプリで開く` は読み取り専用の Intent を作り直し, 候補から本プラグインを自動で除外して, アクセス許可の拡散と自己ループを防ぎます.

******

### プラグインインターフェース (開発者向け)

******

ホストは次の識別子でプラグインを検出して呼び出します:

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

実装は explorer-action v12 に基づき, 読み取り専用の主要アクション `play-video` と選択ツールバーアクション `play-video-selection` を登録し, `video/*` と旧ホスト向け 23 拡張子で動画を受け入れます. 主要アクションは同一フォルダーキューと字幕用の有界な同階層読み取りを利用できますが, 選択アクションはホストの 1-128 対象を順序どおり保持し同階層読み取りを有効にしません. 対応セッションは再生進捗も運べ, 未対応の任意機能は安全にフォールバックします. 音声と画像は別プラグインです.

ホストとの完全な連携には AutoJs6 6.8.0 (build 5276) 以降と Explorer Action v12 が必要です; 今後のプラグイン更新でこの要件を引き上げることはありません.

******

### ロードマップ

******

実装済みの機能と今後の計画は, チェック可能なリストとして Roadmap.md で管理しています. 未チェックの項目は計画上の意向を示すもので, 現在のバージョンが備えている機能ではありません.

- [チェック可能な Roadmap.md を開く](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### リリース履歴

******

#### v3.0.0

###### 2026/09/11

* `機能` プレーヤーのアクセシビリティを全面監査: 名前と動的状態を持つコントロール, TalkBack タッチ探索中に常時表示される操作, すべてのジェスチャーに対するボタンまたはメニューの代替手段, 確定したフォーカス順序, 200% の文字/表示倍率での検証を追加
* `機能` キーボードとリモコンに対応: 見えるフォーカスリング, DPAD ナビゲーション, Space/Enter の再生/一時停止, 設定した 5/10/30 秒単位の左右シーク, MediaSession 経由のメディアキーをサポート
* `機能` 初期状態がオフの任意バックグラウンド音声: 通知権限を許可すると, PiP が利用できない場合に再生中のプレーヤーを途切れなく通知操作付きメディア再生フォアグラウンドサービスへ移行可能
* `改善` PiP が常にバックグラウンド音声より優先されます; 設定オフ, 再生完了, エラー, 明示的な終了でサービスを停止し, 通知タップでは同じプレーヤーを連続した位置で取り戻します
* `改善` ヘッドセット動作は Media3 に準拠: 1 回で再生/一時停止, 外部機器の 2 回操作で次のキュー項目がある場合に進み, 明示的な Previous メディアコマンドで前へ戻ります; 非標準の 3 回操作タイミングは追加しません
* `改善` README のレイアウトと Gradle プラットフォームのバージョン管理方式を統一
* `改善` プラグインの説明を簡潔にし, 多言語リソースの句読点を統一
* `改善` 外部表示エントリを External Viewer に改名し, ビューアーの意味を統一
* `改善` 更新ダイアログのリリース履歴ボタンから内蔵のリリース履歴ページを開くように変更
* `改善` 意図しないネイティブ依存関係をビルド時に拒否し, JSON レポートを生成

#### v2.3.0

###### 2026/08/31

* `機能` 動画情報が HDR10,HLG,SDR を識別し,取得できる色空間,範囲,ビット深度を表示するようになりました.全項目を一括コピーでき,ディスプレイがソース HDR への対応を報告していない場合は一度だけ警告します
* `機能` 初期状態では無効の設定で表示中の字幕をスクリーンショットに含められ,保存したばかりの PNG を共有操作から別のアプリへ直接送れます
* `機能` セッション限定の映像反転を追加し,左右,上下,または両軸を反転できます.ピンチズームや画面回転とも一貫して組み合わせられます
* `機能` セッション限定の音量ブーストを +3 dB から +15 dB まで追加しました.初期状態は無効で,初回利用前に歪みと聴力への危険を警告し,非対応時は自動的にオフへ戻ります
* `改善` スクリーンショットはストレージ権限なしで独立した MediaStore PNG として保存され,共有時は選択した画像への読み取り権限だけを付与します
* `改善` 評価の結果 LoudnessEnhancer を採用しました.メディアファイルを変更せず再生中だけ上限付きゲインを適用でき,効果の作成に失敗した場合は即座に解放して安全に戻せるためです

#### v2.2.0

###### 2026/08/31

* `機能` Host Session 再生キューパネルを追加:ファイル名,判明済み時間またはプレースホルダー,現在項目の強調,タップ移動,順次/シャッフル/1項目リピートのリアルタイム連動を提供
* `機能` 読み取り専用の`選択項目を再生`アクションを追加:同じ親フォルダーの対応動画 1-128 件をホストの選択順で再生し,同階層ファイルは探索しない
* `機能` キューの自動再生前に現在の最終フレームを保ち,キャンセル可能な 3 秒の次項目案内を表示.ロック中の操作は解除しない
* `機能` 既定でオフの`再生モードを記憶`設定を追加し,順次/シャッフル/1項目リピートを再生セッション間で保持可能に
* `改善` 各選択対象の content URI,ClipData 位置,MIME,メタデータ,サイズ,親フォルダーを個別照合し,ID または URI が重複すればグループ全体を拒否
* `改善` 複数選択では明示的に許可された対象だけを有界な Host Session ルートで読み取り,同階層の動画や字幕を走査しない

##### 完全な履歴

* [CHANGELOG-ja.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release ビルド:

```powershell
.\gradlew.bat :app:assembleRelease
```

ビルドパラメーターは `version.properties` から取得します. 現在の最小 SDK は 24, ターゲット SDK は 36 です.

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

`strings.xml` はプラグイン情報とプレーヤー画面のローカライズを提供し, `plugin_instruction.md` はホスト側に表示される使用説明を提供します. すべての README と CHANGELOG は `.python/generate_markdown.py` が JSON ソースから生成します: ドキュメントを変更するときは `.readme` と `.changelog` 配下の `lang_*.json` を編集してスクリプトを再実行し, 生成された Markdown ファイルを直接編集しないでください.

******

### 関連リンク

******

- AutoJs6 ドキュメント: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (再生エンジン): https://developer.android.com/media/media3/exoplayer
- Android の安全なファイル共有: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Ember-Player/blob/master/docs/16kb.md)
