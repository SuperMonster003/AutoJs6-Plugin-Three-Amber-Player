******

### Release History

******

# v3.1.2

###### 2026/09/29

* `Fix` Background audio handoff losing foreground service state when the Activity cancels the shared playback notification on Android 17
* `Fix` Background playback service remaining active after a media controller stops playback
* `Fix` SDK XML v4 parsing warnings with AGP 9.1 and APK native alignment checks incorrectly triggered by JVM unit-test assembly tasks, using shared build plugins 1.8.3
* `Improvement` Target Android 17 (SDK 37) with foreground service support for background audio playback
* `Improvement` Unify Three series launcher icons with light artwork on a stable dark background, while plugin-center and in-app icons remain transparent and follow the application theme; prevent nested launcher backgrounds on some devices

# v3.1.1

###### 2026/09/15

* `Improvement` Raise compileSdk to 37 (Android 17); targetSdk stays at 36 until the behavior that depends on the target is verified

# v3.1.0

###### 2026/09/13

* `Feature` Local playlists: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL preserve order, titles and repeated entries. Host playback reads the same folder; standalone playback asks for the playlist folder to resolve relative paths. One list at a time, up to 128 items. Network URLs, HLS and nested playlists are not supported
* `Fix` Keep the plugin version date in English regardless of the build machine locale
* `Improvement` Consistent localized resources, explicit plugin activation and validated release preparation

# v3.0.0

###### 2026/09/12

* `Feature` Full player accessibility pass: named and state-aware controls, persistent controls during TalkBack touch exploration, button or menu equivalents for every gesture, deterministic focus order, and layouts verified at 200% text and display scale
* `Feature` Keyboard and remote support: visible focus rings, DPAD navigation, Space/Enter play-pause, left/right seeking by the configured 5/10/30-second step, and media keys through MediaSession
* `Feature` Optional background audio, off by default: after notification permission is granted, active playback can move without interruption to a media-playback foreground service with notification controls when picture-in-picture is unavailable
* `Improvement` Picture-in-picture always wins over background audio; disabling the setting, playback completion, an error or leaving playback deliberately stops the service, while a notification tap reclaims the same player and position
* `Improvement` Headset behavior stays with Media3: one click toggles playback, the platform external-device double tap advances when a next queue item exists, and an explicit Previous media command goes back; no non-standard triple-click timing is imposed
* `Improvement` Standardize the README layout and Gradle platform version management
* `Improvement` Refine the plugin description and normalize punctuation in multilingual resources
* `Improvement` Rename the external viewing entry to External Viewer for consistent viewer semantics
* `Improvement` Open the built-in release history page from the release history button in the update dialog
* `Improvement` Build verification rejects accidental native dependencies and produces a JSON report

# v2.3.0

###### 2026/08/31

* `Feature` Video information now identifies HDR10, HLG or SDR and shows available color space, range and bit depth; copy every field at once and get a one-time warning when the display does not report support for the source HDR format
* `Feature` Screenshots can optionally include visible subtitles through a default-off setting, and a Share action sends the newly saved PNG directly to another app
* `Feature` New session-only video mirroring flips left/right, top/bottom or both axes and composes consistently with pinch zoom and screen rotation
* `Feature` New session-only volume boost offers +3 dB through +15 dB levels, stays off by default, warns about distortion and hearing risk before first use, and falls back to Off when unsupported
* `Improvement` Screenshots remain independent MediaStore PNG files without storage permission; sharing grants read-only access only to the selected image
* `Improvement` LoudnessEnhancer was adopted after evaluation because it can apply bounded playback-only gain without modifying media files; device failures are contained with immediate release and safe fallback

# v2.2.0

###### 2026/08/31

* `Feature` New Host Session playback queue sheet: file names, known durations or placeholders, current-item highlight, tap-to-jump and a live sequence/shuffle/repeat-one control
* `Feature` New read-only Play selected file-manager action: play 1 to 128 supported videos from one parent in the host selection order without sibling discovery
* `Feature` Before queue autoplay, keep the current end frame and show a cancelable three-second next-item prompt without unlocking locked controls
* `Feature` New Remember playback mode setting, off by default, to retain sequence, shuffle or repeat-one across playback sessions
* `Improvement` Every selected target is independently checked against its content URI, ClipData position, MIME type, metadata, size and parent; duplicate IDs or URIs reject the whole group
* `Improvement` Multi-selection reads only explicitly authorized targets through their bounded Host Session routes and never scans siblings for videos or subtitles

# v2.1.0

###### 2026/08/31

* `Feature` Subtitle style settings: choose 75%-150% text size, foreground color, opaque/translucent/no background and 0%/4%/8% bottom margin with a live preview; choices persist in playback and picture-in-picture
* `Feature` External subtitle encoding detection: BOM first, then GBK, Big5, Shift_JIS, EUC-KR, Windows-1251 and Windows-1256, with a clear warning when detection is uncertain
* `Feature` Load one .srt or .ass subtitle manually from the subtitle menu through the system document picker; files over 4 MiB or with another extension are rejected
* `Feature` Adjust the selected external subtitle from −600.0 to +600.0 seconds in 0.1-second steps with immediate on-screen feedback
* `Feature` While paused, step one frame backward or forward (long-press to repeat), and set an A-B interval loop with normalized boundaries
* `Improvement` Subtitle decoding, UTF-8 conversion and time shifting run entirely in memory without temporary files, storage permission or persistent subtitle grants
* `Improvement` Subtitle offset resets whenever the video changes; A-B looping takes priority while active, and a later explicit repeat mode or end-of-video timer clears it

# v2.0.0

###### 2026/08/29

* `Feature` Brand-new theme system: one seed color generates readable light and dark palettes, following the AutoJs6 theme by default, with 19 presets and a custom RGB color with live preview
* `Feature` The plugin becomes a standalone app: a launcher entry is added, and videos can be opened directly through the system file picker
* `Feature` New settings page: language, night mode, theme color, resume, updates and release history in one place; language, night mode and theme color follow AutoJs6 by default, and the options are disabled with app defaults when the host is unavailable
* `Feature` New update checks: manual and once-daily automatic checks against official GitHub releases, with ignorable versions and a built-in localized release history page
* `Fix` Fixed following AutoJs6 settings not taking effect in some scenarios (the plugin info service required by the host was not exposed before)
* `Improvement` Resume history is trimmed to the single most recent unfinished video; opening another video clears the old record immediately, and finished videos keep no position
* `Improvement` The app is renamed to 3-Ember Player; the application ID and plugin ID stay unchanged, so existing installations upgrade in place

# v1.4.0

###### 2026/08/28

* `Feature` Folder playback: opening one video builds a same-folder queue automatically (natural filename order) with previous/next, sequential, shuffle, single-item repeat and autoplay of the next item
* `Feature` External subtitles: same-name .srt / .ass files and their language-suffix variants are discovered automatically; subtitles stay off until picked in the subtitle menu
* `Feature` Optional host-managed resume history: off by default, can be enabled, disabled or cleared in AutoJs6 settings; file lists show no watched markers
* `Fix` Fixed files recognized as video by the host being rejected because their extension was missing from the legacy allowlist; trusted video/* requests are now accepted uniformly
* `Improvement` Sibling access is strictly limited to the selected file and its readable direct siblings, with recursive directories, writes and persistable grants forbidden; the plugin stores no plain-text paths
* `Improvement` The playback queue is capped at 128 videos with up to 8 external subtitles each, and the user-selected video always stays in the queue
* `Improvement` The compatibility baseline is fixed at AutoJs6 6.8.0 (build 5276); older hosts fall back to single-file playback with base features unaffected
* `Dependency` Upgraded the bundled Explorer Action API from protocol v2 to the backward-compatible v12

# v1.3.1

###### 2026/08/27

* `Fix` Added an XVID-in-MKV compatibility layer: such videos now play through the device built-in MPEG-4 Part 2 decoder, without transcoding or touching the source file
* `Fix` When the device lacks a compatible decoder or decoding fails, audio-only playback no longer occurs; a clear message is shown instead with the option to open with another app

# v1.3.0

###### 2026/08/27

* `Feature` Sleep timer: pause automatically after 15, 30, 45 or 60 minutes, or when the current video ends
* `Feature` Pinch-to-zoom (0.25× to 4×) with one double-tap reset while zoomed
* `Feature` Dragging the progress bar shows a target-time bubble and a thumbnail preview, falling back to time only when extraction fails
* `Feature` Frame capture: on Android 10+ save the current frame as a PNG to the system gallery, without storage permission and without modifying the source video
* `Feature` New gesture sensitivity (low/standard/high) and double-tap seek step (5/10/30 seconds) settings, remembered automatically
* `Improvement` The sleep timer counts on system uptime, so changing the wall clock does not affect the countdown, and it survives screen rotation
* `Improvement` Thumbnail extraction merges requests during fast scrubbing and releases stale images promptly, saving memory

# v1.2.0

###### 2026/08/27

* `Feature` Track selection: switch embedded audio tracks by language and channels, and enable the embedded subtitles that stay off by default
* `Feature` Picture-in-picture: on Android 8.0+ leaving during playback enters a floating window automatically, with a manual entry and remote play/pause
* `Feature` System media integration: headset and Bluetooth controls, plus a media notification with title, progress and playback actions
* `Feature` Playback tools: single-item repeat, a video info panel, and one-shot automatic orientation based on the aspect ratio
* `Improvement` Unsupported tracks are clearly marked and not selectable; entries hide automatically when no selectable track exists
* `Improvement` The file size in the video info panel comes from the validated declared size, making it more reliable
* `Dependency` Added AndroidX Media3 Session 1.10.1

# v1.1.0

###### 2026/08/27

* `Feature` Gesture controls: swipe vertically on the left half for brightness and the right half for volume, swipe horizontally to seek, double-tap the sides to jump 10 seconds, double-tap the center to play/pause, long-press for temporary 2× speed
* `Feature` Playback speed: 9 steps from 0.25× to 3×, with the control bar highlighting the current speed when it is not 1×
* `Feature` Immersive full screen: system bars hidden with notch support, auto-hiding title and control bars, one-tap orientation and scaling mode switching
* `Feature` Control lock: block all buttons and gestures with one tap to prevent accidental touches
* `Feature` Resume playback: the position is remembered automatically and restored when the same video reopens; finished videos are cleared, up to 200 records are kept, storing file digests instead of paths
* `Improvement` The bottom control bar is rebuilt: play/pause, 10-second jumps, a draggable progress bar and buffer progress all included
* `Improvement` The playback failure panel gains a retry button while keeping open with another app
* `Improvement` Interface changes such as rotation no longer rebuild the player, making transitions smoother

# v1.0.1

###### 2026/08/08

* `Fix` Fixed the service binding being invalid after enabling the plugin in the AutoJs6 plugin center, which prevented the host from using the plugin
* `Improvement` Streamlined the plugin name and description, with more natural documentation wording across all languages

# v1.0.0

###### 2026/08/02

* `Feature` First release: published as an AutoJs6 file manager plugin; tapping a video file in the file manager plays it directly
* `Feature` Covers 23 common video extensions (MP4, MKV, AVI, MOV, FLV, WEBM and more), taking over the file manager video-open action in read-only mode
* `Feature` Playback on AndroidX Media3 ExoPlayer: autoplay, standard controls, audio focus handling, auto-pause on headset unplug, and keeping the screen on while playing
* `Feature` Strict security validation: the playback entry is protected by a signature permission, request origins, target files and read-only grants are verified item by item, and the player is not exported to the system
* `Feature` A separate system open-with entry: other apps can invoke this player with a read-only content URI to watch videos
* `Feature` When playback fails, the video can be handed to another app safely, with this plugin excluded from the candidates
* `Feature` Ships with 10 languages: Simplified Chinese, Traditional Chinese (Hong Kong/Taiwan), English, French, Spanish, Japanese, Korean, Russian and Arabic
* `Dependency` Introduced AndroidX Media3 ExoPlayer and UI 1.10.1
* `Dependency` Introduced Kotlin Parcelize runtime 2.2.21
