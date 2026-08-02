<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Read-only video playback for AutoJs6 Explorer with secure content URI handling</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The current README.md supports the following languages:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- English [en] # current
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### Introduction

******

The AutoJs6 Video Player plugin plays video content supplied by AutoJs6 Explorer or another Android application through temporary read-only content URI access. It uses AndroidX Media3 ExoPlayer and never modifies the source video.

******

### Features

******

- Registers a read-only single-file primary Explorer action through version 2 of the shared `org.autojs.plugin.EXPLORER_ACTION` protocol.
- Uses AndroidX Media3 ExoPlayer and PlayerView for automatic playback, standard controls, audio focus, audio-becoming-noisy handling, and device codec integration.
- Restores playback position and play/pause intent after recreation, and keeps the screen on only while video is actively playing.
- Provides a separate exported `android.intent.action.VIEW` entry for read-only `content://` URIs with a `video/*` MIME type.
- Offers a safe Open with another app action after playback failure by rebuilding a read-only view intent and excluding this plugin from the candidate list.

******

### Host integration

******

AutoJs6 integrates this plugin into the primary video-open path in `app/src/main/java/org/autojs/autojs/ui/explorer/ExplorerView.kt` and the Play action in `app/src/main/java/org/autojs/autojs/ui/main/scripts/MediaInfoDialogManager.kt`.

After the plugin is installed, enabled, trusted, and compatible, opening a matching video launches action `play-video` as the primary Explorer viewer.

If the plugin is missing, disabled, unavailable, incompatible, or cannot be launched, AutoJs6 falls back to its system `android.intent.action.VIEW` route so another installed video application can handle the file.

This plugin matches video files only. Audio playback and image viewing remain independent plugin capabilities and are not bundled into this APK.

******

### Supported formats

******

The primary Explorer action has an empty MIME matcher and matches only these 23 host video extensions:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### Plugin interface

******

AutoJs6 discovers and executes the plugin with the following identities:

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

Version 1 provides a primary read-only Explorer action for the listed video extensions. The independent external entry accepts a read-only content URI with any valid `video/*` MIME subtype. Actual decoding depends on Media3 extractors and codecs available on the device.

The plugin is implemented entirely on the JVM and contains no native library. It declares `supportedAbis = emptyArray()` and is released as one ABI-independent APK. AutoJs6 host build 5269 or later is required.

******

### Security

******

The plugin requests no storage or INTERNET permission. Its signature-protected Explorer boundary validates protocol version 2, source surface, host build, target and parent content URIs, exact ClipData ordering, display name, declared size, MIME type, extension, and read-only grant flags. It then creates a new explicit intent for the non-exported player containing only the target URI, MIME type, safe display name, one target ClipData item, and the read grant. The public ACTION_VIEW boundary separately accepts only a content URI, video MIME type, and exact read grant, ignores all incoming extras and ClipData, and rebuilds the same minimal internal request.

******

### Safety limits

******

- One target content URI per playback request.
- Explorer execution requires the signature-level `org.autojs.permission.PLUGIN` permission.
- Write and persistable grants are always rejected. Prefix access used to validate the Explorer parent URI is never forwarded to the player.
- The public ACTION_VIEW boundary rejects write, persistable, and prefix grants.
- External fallback candidates are resolved first, filtered to other packages, and launched with a newly built read-only intent.
- A listed extension does not guarantee decoding support on every device. Media3 and the installed platform codecs determine actual playback support.

******

### Release history

******

# v1.0.0

###### 2026/08/02

* `Feature` Video Player plugin with plugin ID `video-player`, action ID `play-video`, engine `explorer-action`, and variant `default`
* `Feature` Protocol v2 primary read-only Explorer action with an empty MIME matcher, matched only by the current 23 host video extensions and requiring AutoJs6 host build 5269
* `Feature` Signature-protected Explorer entry with strict target and parent content URI, ClipData, source, display name, size, MIME type, extension, and grant validation followed by minimal forwarding to a non-exported player
* `Feature` Independent exported ACTION_VIEW entry for read-only video content URIs, with untrusted extras and ClipData discarded, forbidden grants rejected, and self-loop protection
* `Feature` Media3 ExoPlayer and PlayerView playback with autoplay, standard controls, audio focus, audio-becoming-noisy handling, saved position and play state, and keep-screen-on only during active playback
* `Feature` Safe Open with another app recovery after playback failure, using newly built read-only intents that explicitly exclude this plugin
* `Feature` Pure JVM implementation with no native library, unrestricted ABIs declared by `supportedAbis = emptyArray()`, and one ABI-independent APK
* `Feature` Localized metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese
* `Dependency` Added AndroidX Media3 ExoPlayer and UI version 1.10.1
* `Dependency` Added Kotlin Parcelize runtime version 2.2.21

##### For more releases

* [CHANGELOG-en.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release build:

```powershell
.\gradlew.bat :app:assembleRelease
```

Build parameters come from `version.properties`. The current minimum SDK is 24 and the target SDK is 36.

******

### Resource layout

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localizes plugin metadata and UI text. `plugin_instruction.md` provides usage and security instructions. `.python/generate_markdown.py` generates localized README and changelog files from JSON sources.

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- Android secure file sharing: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
