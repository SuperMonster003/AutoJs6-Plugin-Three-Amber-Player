<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
    </picture>
  </p>

  <p>Video playback with playlists, subtitles, and background audio</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The README.md is currently available in the following languages:

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

3-Ember Player is the video playback plugin for the AutoJs6 file manager, and a standalone local video player at the same time. Tap a video file in the file manager and it starts playing full screen; gestures, playback speed, external subtitles, folder-based continuous playback and picture-in-picture cover the core experience of mainstream players. Playback is built on AndroidX Media3 ExoPlayer.

The plugin sticks to a read-only security model: videos enter the player through temporary read-only grants, no storage permission is ever requested, and source files are never modified or moved; network access is used solely to check for plugin updates.

******

### Highlights

******

- Local playlists: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL preserve order, titles and repeated entries. Host playback reads the same folder; standalone playback asks for the playlist folder to resolve relative paths. One list at a time, up to 128 items. Network URLs, HLS and nested playlists are not supported.
- Tap to play: tap a video file in the AutoJs6 file manager and it opens directly in immersive full screen, no setup required.
- Handy gestures: swipe the left half for brightness and the right half for volume, swipe horizontally to seek, double-tap the sides to jump, double-tap the center to play/pause, long-press for temporary 2× speed, and lock all controls with one tap to prevent accidental touches.
- Speed and picture control: 9 playback speeds from 0.25× to 3×, pinch-to-zoom (0.25× to 4×), fit/fill/crop scaling modes, and one-tap orientation switching with an automatic suggestion based on the aspect ratio.
- Folder playback: opening one video builds a same-folder queue (natural filename order); its queue sheet shows the current item and known duration, jumps on tap, controls sequential/shuffle/repeat-one, and offers a cancelable three-second prompt before autoplaying the next item.
- Ordered host selections: the read-only Play selected action turns 1-128 videos from one folder into a queue in exactly the host selection order, without sibling discovery; an optional setting remembers the playback mode between sessions.
- External and embedded subtitles: automatically discover same-name .srt / .ass files or load one manually, detect common legacy encodings, adjust style and a ±600-second external-subtitle offset, and switch embedded subtitle or audio tracks; subtitles stay off until explicitly enabled.
- Precision playback: while paused, step frames backward or forward with long-press repeat, and set or clear an A-B interval loop for close inspection.
- Resume playback: remembers the position of the most recent unfinished video and resumes it on reopen; finished videos are cleared immediately, leaving no watch trail.
- Picture-in-picture and system integration: automatic PiP on Android 8.0+ when leaving during playback, headset and Bluetooth controls, and a media notification with title and progress.
- Sleep timer: pause automatically after 15/30/45/60 minutes or when the current video ends.
- Seek preview: dragging the progress bar shows a target-time bubble and, when possible, a thumbnail preview.
- Frame capture: on Android 10+ save the current frame as a PNG to the system gallery with one tap, without storage permission and without touching the source video.
- Tricky format fallback: a lightweight built-in XVID-in-MKV compatibility layer; when the device cannot decode a video, a clear message is shown and playback can be handed to another player.
- Themes your way: one seed color generates readable light and dark palettes, following AutoJs6 by default, with 19 presets and a custom RGB color with live preview.
- Works standalone: comes with a launcher icon and a settings page, opens videos through the system file picker, and can act as a video player in the system "Open with" menu.
- Display and output tools: inspect HDR10/HLG/SDR and available color details, copy the information panel, optionally include subtitles in a shared screenshot, mirror the session image, or use a warned session-only volume boost up to +15 dB.
- Accessible without gestures: TalkBack keeps named controls available, every gesture has a button or menu equivalent, layouts tolerate 200% text and display scale, and keyboard/DPAD focus, Space/Enter, seek and MediaSession keys are supported.
- Optional background audio: off by default and enabled only with notification permission; when PiP is unavailable, the existing player moves to a media-playback foreground service with notification controls and stops on completion or when the setting is disabled. PiP always takes priority.
- Read-only by design: no storage permission, source videos are never written; the network is used only for update checks.
- Launcher icon choices in Settings: adaptive light, adaptive dark (default), adaptive automatic, or transparent background. Automatic colors and transparent rendering depend on the launcher; it may cache icons or add a background. Some home-screen shortcuts may need to be added again after a change.

******

### Install and Use

******

Before starting, make sure the environment meets the following requirements:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

From installation to the first video in 4 steps:

1. Download and install the plugin APK. A 3-Ember Player icon appears on the launcher, while the plugin capabilities are managed by AutoJs6.
2. Open AutoJs6, enter the `Plugin Center`, locate `3-Ember Player` and enable it.
3. Locate any video file (such as `movie.mp4`) in the AutoJs6 file manager.
4. Tap the file and the video starts playing full screen.

Using it without the host: open 3-Ember Player from the launcher, tap `Open video` and pick a video through the system file picker; video viewing requests from other apps can also be handled by this player. The host build requirement above only applies to the file manager entry; standalone playback is unaffected.

Player gesture cheat sheet:

- Single tap: show or hide the control bar.
- Double-tap the center: play/pause; double-tap the left/right side: rewind/forward 10 seconds (adjustable to 5/10/30 seconds in settings).
- Swipe vertically on the left half: adjust brightness; on the right half: adjust volume.
- Swipe horizontally: preview the seek target, release to apply.
- Long-press: temporary 2× speed, release to restore.
- Pinch with two fingers: zoom the picture (0.25× to 4×); double-tap while zoomed to reset.
- Lock button: blocks all gestures and controls against accidental touches; tap the screen afterwards to reveal the unlock button.

******

### Supported Formats

******

The playback action in the file manager exactly matches the following 23 extensions:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

The list above is an exact-extension allowlist kept for older hosts; on newer hosts, any file recognized as video is handed to this plugin via `video/*`. Matching the list does not guarantee decoding on every device; actual playability depends on Media3 and the platform decoders. The standalone entry and calls from other apps are likewise accepted by `video/*` MIME type.

******

### FAQ

******

**Tapping a video file does not open this player?**

Check in order: the AutoJs6 version code is at least 5276 (version 6.8.0 and above); the plugin is enabled in the `Plugin Center`; the file is recognized as a video by the host. If any of the three fails, the tap is not handled by this plugin.

**What happens when the plugin is missing or disabled?**

A compatible host shows recovery guidance suggesting to install or enable the plugin; the system app chooser appears only after the user explicitly picks `Open with another app`, handing the video to other players on the device.

**Audio plays but the screen stays black, or playback fails?**

Whether a video decodes depends on the device platform and Media3; matching the extension list does not guarantee playback. For the common legacy XVID-in-MKV format, a built-in compatibility layer hands the track to the system MPEG-4 Part 2 decoder; if decoding still fails, a clear message is shown and `Open with another app` forwards the video to another player.

**How do I play all videos in a folder continuously?**

With a host meeting the build requirement, opening any video from the file manager builds a same-folder queue automatically: natural filename order, starting from the current video, autoplaying the next when one ends, with sequential, shuffle and single-item repeat modes. On older hosts or via the standalone entry, single-file playback is kept.

**How do external subtitles load?**

Place an `.srt` or `.ass` file with the same name next to the video (language suffixes such as `movie.en.srt` are allowed), open the video from the file manager, then pick the subtitle in the subtitle menu. Subtitles are off by default and never enable themselves.

**What does resume playback record? Is anything uploaded?**

Only the position of the most recent unfinished video is kept, stored as a SHA-256 digest of the file plus time values, with no file name or path; opening another video or finishing playback clears it immediately. Everything stays on the device and nothing is uploaded.

**Which permissions does the plugin need?**

No storage, camera, microphone or other sensitive runtime permissions. It only declares the network permission for update checks (user-triggered or at most once per day), plus the signature-protected plugin permission for the file manager entry.

**Can it be used independently of AutoJs6?**

Yes. Since v2.0.0 the plugin has a launcher entry: pick a video through the system file picker and play it, or choose this player from another app's `Open with` menu. Folder playback, external subtitle discovery and following host settings still require AutoJs6.

******

### Security

******

The plugin is built on a deny-by-default principle; all measures below are always on and cannot be disabled:

- Zero sensitive permissions: no storage or other runtime permissions; network access is used only for user-triggered or once-daily GitHub release checks.
- Never writes: playback, frame capture and thumbnail extraction are read-only end to end; source videos are never modified, moved or deleted.
- Entry-by-entry validation: the file manager entry is protected by a signature-level plugin permission, and every request has its protocol version, target URI, ClipData, metadata, host build and read-only grants verified item by item; any mismatch rejects the request.
- Bounded sibling access: queueing and subtitle discovery go through a short-lived host-managed session that can only enumerate direct siblings of the selected file, with recursive directories, writes and persistable grants all forbidden; the internal player receives only a validated bounded queue and never touches file system paths.
- Isolated dual entries: the system-facing `ACTION_VIEW` entry accepts only read-only content URI `video/*` requests, rejects write, persistable and prefix grants, and stays independent from the file manager entry.
- Minimal resume data: the resume history keeps only the latest unfinished record as a SHA-256 digest plus time values, cleared once playback finishes.
- Safe handover: `Open with another app` rebuilds a read-only intent and excludes this plugin from the candidates, preventing grant spread and self-loops.

******

### Plugin Interface (for Developers)

******

The host discovers and invokes the plugin via the following identifiers:

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

The current implementation is based on explorer-action protocol v12: the plugin registers the read-only primary action `play-video` and selection-toolbar action `play-video-selection`, accepts every video type the host recognizes via `video/*`, and keeps 23 exact extension matchers for older hosts. The primary action may use bounded sibling reading for same-folder queues and subtitle discovery; the selection action preserves 1-128 host targets and never enables sibling reading. Compatible sessions may also carry playback progress, while unavailable optional capabilities fall back safely. Audio playback and image viewing are separate plugin capabilities and are not part of this APK.

Full host cooperation requires AutoJs6 6.8.0 (build 5276) or later with Explorer Action v12; future plugin updates will not raise this requirement.

******

### Roadmap

******

Shipped capabilities and upcoming plans are maintained as a checkable list in Roadmap.md. Unchecked items express intent and do not describe current version capabilities.

- [Open the checkable Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### Release History

******

#### v3.2.0

###### 2026/09/29

* `Feature` Launcher icon choices in Settings: adaptive light, adaptive dark (default), adaptive automatic, or transparent background. Automatic colors and transparent rendering depend on the launcher; it may cache icons or add a background. Some home-screen shortcuts may need to be added again after a change
* `Fix` Correct the left-heavy Ember artwork with optical centering in launcher and transparent icons; include the offset in every safe-area check

#### v3.1.2

###### 2026/09/29

* `Fix` Background audio handoff losing foreground service state when the Activity cancels the shared playback notification on Android 17
* `Fix` Background playback service remaining active after a media controller stops playback
* `Fix` SDK XML v4 parsing warnings with AGP 9.1 and APK native alignment checks incorrectly triggered by JVM unit-test assembly tasks, using shared build plugins 1.8.3
* `Improvement` Target Android 17 (SDK 37) with foreground service support for background audio playback
* `Improvement` Unify Three series launcher icons with light artwork on a stable dark background, while plugin-center and in-app icons remain transparent and follow the application theme; prevent nested launcher backgrounds on some devices

#### v3.1.1

###### 2026/09/15

* `Improvement` Raise compileSdk to 37 (Android 17); targetSdk stays at 36 until the behavior that depends on the target is verified

##### Full history

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

Build parameters come from `version.properties`; the current minimum SDK is 24 and the target SDK is 36.

******

### Resource Layout

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localizes the plugin information and player interface, and `plugin_instruction.md` provides the usage notes shown on the host side. All README and CHANGELOG files are generated from JSON sources by `.python/generate_markdown.py`: to change the documentation, edit `lang_*.json` under `.readme` and `.changelog` and rerun the script instead of editing the generated Markdown files.

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (playback engine): https://developer.android.com/media/media3/exoplayer
- Android secure file sharing: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Ember-Player/blob/master/docs/16kb.md)
