<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>File manager plugin. Play video files directly</p>

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

Video Player plays video content supplied by the file manager or another Android application through temporary read-only content URI access. It uses AndroidX Media3 ExoPlayer and never modifies the source video.

******

### Features

******

- Lightweight XVID-in-MKV compatibility through the device's built-in MPEG-4 Part 2 decoder, with explicit external-player recovery when the system decoder is unavailable or fails.
- Registers a read-only primary Explorer action through version 12 of the shared `org.autojs.plugin.EXPLORER_ACTION` protocol, with optional bounded direct-sibling and playback-progress capabilities.
- Uses AndroidX Media3 ExoPlayer and PlayerView for automatic playback, standard controls, audio focus, audio-becoming-noisy handling, and device codec integration.
- Restores playback position and play/pause intent after recreation, and keeps the screen on only while video is actively playing.
- Provides a separate exported `android.intent.action.VIEW` entry for read-only `content://` URIs with a `video/*` MIME type.
- Offers a safe Open with another app action after playback failure by rebuilding a read-only view intent and excluding this plugin from the candidate list.
- Fullscreen immersive playback with gestures for brightness, volume and seeking, double-tap jumps, a long-press speed boost, and a control lock.
- Playback speeds from 0.25× to 3× with one-tap resize mode and screen orientation switching.
- Local resume memory keyed by content URI digests, cleared once a video finishes.
- Embedded audio-track and subtitle selection with subtitles off by default and unsupported tracks clearly identified.
- Current-video repeat, a detailed metadata panel, aspect-based orientation, and API 26+ picture-in-picture with remote play/pause.
- MediaSession integration for headset, Bluetooth, and system controls, plus a media-style notification with title and playback progress.
- Sleep timer options for 15, 30, 45, or 60 minutes and end-of-video, plus persistent gesture sensitivity and 5/10/30-second double-tap seek settings.
- Pinch zoom from 0.25× to 4× with double-tap reset, and a scrub target-time bubble with best-effort in-memory thumbnails.
- Permission-free current-frame screenshots on Android 10+ saved as separate PNG files through MediaStore without modifying the source video.
- Naturally ordered same-folder video queues through Explorer Action v12, with previous / next, sequence, shuffle, repeat-one, and automatic next-item playback.
- Matching .srt and .ass sidecars, including language suffixes, discovered with subtitles off by default and loaded only after explicit selection.
- Opt-in host-owned resume history that is off by default, can be disabled or cleared in AutoJs6 settings, and adds no watched marker to file lists.

******

### Host integration

******

The host uses this plugin for the primary video-open path and the Play action.

After the plugin is installed, enabled, trusted, and compatible, opening any file that the host recognizes as video launches action `play-video` as the primary Explorer viewer.

If the plugin is missing, disabled, unauthorized, unavailable, incompatible, or cannot be launched, a compatible host shows recovery guidance. The system application chooser opens only after the user explicitly selects Open with other apps.

This plugin matches video files only. Audio playback and image viewing remain independent plugin capabilities and are not bundled into this APK.

******

### Supported formats

******

The primary Explorer action accepts `video/*` for every video type recognized by the host and retains these 23 exact extension matchers for compatibility with older hosts:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### Plugin interface

******

The host discovers and executes the plugin with the following identities:

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

Version 1.4 provides a protocol v12 primary read-only Explorer action. A compatible host may attach request-scoped direct-sibling and playback-progress capabilities; hosts without those optional extensions retain single-file playback. The independent external entry accepts a read-only content URI with any valid `video/*` MIME subtype.

AutoJs6 6.8.0 build 5276 or later and Explorer Action v12 are required for the full host-cooperation features; this requirement will not be raised for later plugin capabilities.

******

### Security

******

The plugin requests no storage or INTERNET permission. Its signature-protected Explorer boundary validates the complete protocol v12 envelope, exact single selected target, parent relationship, ClipData, metadata, host build, and read-only grants. Optional Host Sessions are pinned by the host to the plugin UID and permit only listing the selected file's direct parent and opening the selected file or a readable direct sibling. The private player validates a bounded opaque queue and never receives a filesystem path. The public ACTION_VIEW boundary remains independent and single-file only.

******

### Safety limits

******

- Explorer starts from exactly one selected content URI; an optional Host Session can expose only readable direct siblings and never recursive directory access.
- Explorer execution requires the signature-level `org.autojs.permission.PLUGIN` permission.
- Write and persistable grants are always rejected. Prefix access used to validate the Explorer parent URI is never forwarded to the player.
- The public ACTION_VIEW boundary rejects write, persistable, and prefix grants.
- External fallback candidates are resolved first, filtered to other packages, and launched with a newly built read-only intent.
- Host playback history is disabled by default, stores only canonical-path digests and time values after explicit opt-in, and can be disabled or cleared in AutoJs6 settings.
- Recognition as video or a listed legacy extension does not guarantee decoding support on every device. Media3 and the installed platform codecs determine actual playback support.

******

### Release history

******

# v1.4.0

###### 2026/08/28

* `Feature` Built naturally ordered same-folder video queues through Explorer Action v12's request-scoped, UID-bound Host Session, with previous / next, sequence, shuffle, repeat-one, and automatic next-item playback
* `Feature` Discovered matching .srt and .ass sidecars, including language-suffix variants, kept subtitles off by default, and loaded them only after explicit selection
* `Feature` Added opt-in host-owned resume history: recording is off by default, can be disabled or cleared in AutoJs6 settings, and adds no watched marker to file lists
* `Fix` Explorer requests classified by the host as video were rejected unless their extensions appeared in the legacy 23-item allowlist; trusted `video/*` requests are now accepted uniformly
* `Improvement` Restricted sibling access to the selected file and readable direct siblings only, with no recursive directory traversal, writes, persistent grants, or plaintext paths in the plugin
* `Improvement` Bound serialized queues to 128 videos, 8 sidecars per video, and 128 total subtitle attachments while always retaining the selected item
* `Improvement` Kept compatibility at AutoJs6 6.8.0 build 5276 and Explorer Action v12; hosts without the optional extensions safely retain single-file playback
* `Dependency` Upgraded the bundled Explorer Action API from protocol v2 to the backward-compatible v12 media-session extension

# v1.3.1

###### 2026/08/27

* `Fix` Added a narrow XVID-in-MKV compatibility layer that exposes validated VFW/FourCC XVID tracks to the device's built-in MPEG-4 Part 2 decoder without transcoding or modifying the source
* `Fix` Stopped audio-only playback when no compatible system decoder exists or decoding fails, with a specific explanation and Open with another app recovery

# v1.3.0

###### 2026/08/27

* `Feature` Sleep timer: pause after 15, 30, 45, or 60 minutes or when the current video ends, with repeat-mode conflict handling
* `Feature` Picture interaction: pinch zoom from 0.25× to 4×, double-tap reset while zoomed, and coordination with the existing resize modes
* `Feature` Scrub preview: a target-time bubble plus best-effort in-memory thumbnails that silently fall back to time only
* `Feature` Android 10+ current-frame screenshots saved as separate PNG files through MediaStore without storage permission or source modification
* `Feature` Persistent gesture settings for low, normal, or high sensitivity and 5, 10, or 30-second double-tap seeking
* `Improvement` Sleep deadlines use elapsed realtime and survive playback-page state recreation without depending on wall-clock changes
* `Improvement` Thumbnail extraction coalesces rapid scrub requests on one worker and releases every transient bitmap when it becomes stale

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
