<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Ember Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
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

3-Ember Player is both an AutoJs6 file-manager plugin and a standalone simple video player. It accepts temporary read-only content URIs, uses AndroidX Media3 ExoPlayer, and never modifies the source video.

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
- Resume history retains exactly one most recently opened unfinished video as a SHA-256 identity digest plus time values; opening another video clears it immediately, and completed playback is never retained.
- Embedded audio-track and subtitle selection with subtitles off by default and unsupported tracks clearly identified.
- Current-video repeat, a detailed metadata panel, aspect-based orientation, and API 26+ picture-in-picture with remote play/pause.
- MediaSession integration for headset, Bluetooth, and system controls, plus a media-style notification with title and playback progress.
- Sleep timer options for 15, 30, 45, or 60 minutes and end-of-video, plus persistent gesture sensitivity and 5/10/30-second double-tap seek settings.
- Pinch zoom from 0.25× to 4× with double-tap reset, and a scrub target-time bubble with best-effort in-memory thumbnails.
- Permission-free current-frame screenshots on Android 10+ saved as separate PNG files through MediaStore without modifying the source video.
- Naturally ordered same-folder video queues through Explorer Action v12, with previous / next, sequence, shuffle, repeat-one, and automatic next-item playback.
- Matching .srt and .ass sidecars, including language suffixes, discovered with subtitles off by default and loaded only after explicit selection.
- Builds accessible light and dark semantic roles from one HCT source color, follows AutoJs6 by default, and offers 19 localized Material 500 presets plus live-preview custom RGB colors.
- Provides a standalone launcher and settings for host-following language, night mode and color, single-video resume behavior, manual and automatic update checks, ignored versions, release history, and app/developer information.

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
source namespace: io.github.supermonster003.autojs6.plugin.threeemberplayer
stable application id: io.github.supermonster003.autojs6.plugin.videoplayer
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

Version 2.0 provides a protocol v12 primary read-only Explorer action. A compatible host may attach request-scoped direct-sibling and playback-progress capabilities; hosts without those optional extensions retain single-file playback. The independent external entry accepts a read-only content URI with any valid `video/*` MIME subtype.

AutoJs6 6.8.0 build 5276 or later and Explorer Action v12 are required for the full host-cooperation features; this requirement will not be raised for later plugin capabilities.

******

### Security

******

The app requests no storage permission and never writes source videos. Internet access is used only for user-triggered or daily GitHub release checks. Its signature-protected Explorer boundary validates the complete protocol v12 envelope, exact single selected target, parent relationship, ClipData, metadata, host build, and read-only grants. Optional Host Sessions are pinned by the host to the plugin UID and permit only listing the selected file's direct parent and opening the selected file or a readable direct sibling. The private player validates a bounded opaque queue and never receives a filesystem path. The public ACTION_VIEW boundary remains independent and single-file only.

******

### Safety limits

******

- Explorer starts from exactly one selected content URI; an optional Host Session can expose only readable direct siblings and never recursive directory access.
- Explorer execution requires the signature-level `org.autojs.permission.PLUGIN` permission.
- Write and persistable grants are always rejected. Prefix access used to validate the Explorer parent URI is never forwarded to the player.
- The public ACTION_VIEW boundary rejects write, persistable, and prefix grants.
- External fallback candidates are resolved first, filtered to other packages, and launched with a newly built read-only intent.
- Resume history retains exactly one most recently opened unfinished video as a SHA-256 identity digest plus time values; opening another video clears it immediately, and completed playback is never retained.
- Recognition as video or a listed legacy extension does not guarantee decoding support on every device. Media3 and the installed platform codecs determine actual playback support.

******

### Release history

******

# v2.0.0

###### 2026/08/29

* `Feature` Added a detailed HCT-based color system that generates accessible light and dark semantic roles for toolbars, controls, surfaces, outlines, and errors from one source color, with 19 localized Material 500 presets and live-preview custom RGB colors
* `Feature` Added a launcher and standalone single-file player mode, plus a dedicated settings screen for language, night mode, theme color, resume behavior, updates, release history, and app/developer information
* `Feature` Language, night mode, and source color now follow AutoJs6 by default through its official read-only settings contract; unavailable host choices remain visible but disabled and fall back to app defaults
* `Feature` Added manual and daily automatic update checks, ignored-version management, and localized bundled release history
* `Fix` Made Follow AutoJs6 reliable by exposing the protected plugin-info service entry required by the host settings provider
* `Improvement` Resume playback now remembers exactly one most recently opened video, immediately discards it when another video opens, and never keeps completed playback in either standalone or host-managed history
* `Improvement` Renamed the fixed app and plugin display name to 3-Ember Player and the source namespace to threeemberplayer while retaining the established application and plugin IDs for upgrade compatibility

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
