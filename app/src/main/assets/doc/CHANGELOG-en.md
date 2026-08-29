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

# v1.3.0

###### 2026/08/27

* `Feature` Sleep timer: pause after 15, 30, 45, or 60 minutes or when the current video ends, with repeat-mode conflict handling
* `Feature` Picture interaction: pinch zoom from 0.25× to 4×, double-tap reset while zoomed, and coordination with the existing resize modes
* `Feature` Scrub preview: a target-time bubble plus best-effort in-memory thumbnails that silently fall back to time only
* `Feature` Android 10+ current-frame screenshots saved as separate PNG files through MediaStore without storage permission or source modification
* `Feature` Persistent gesture settings for low, normal, or high sensitivity and 5, 10, or 30-second double-tap seeking
* `Improvement` Sleep deadlines use elapsed realtime and survive playback-page state recreation without depending on wall-clock changes
* `Improvement` Thumbnail extraction coalesces rapid scrub requests on one worker and releases every transient bitmap when it becomes stale

# v1.2.0

###### 2026/08/27

* `Feature` Track selection: switch among embedded audio tracks with language and channel details, and opt into embedded subtitles that remain off by default
* `Feature` Picture-in-picture on Android 8.0+: automatic entry when leaving during playback, a manual entry, video aspect ratio matching, and remote play/pause
* `Feature` System media integration: MediaSession headset and Bluetooth controls plus a media-style notification with title, playback actions, and progress
* `Feature` Playback utilities: repeat-current mode, a video information panel, and one-time aspect-based orientation selection
* `Improvement` Unsupported tracks are clearly marked and cannot be selected; track controls stay hidden when no real choice is available
* `Improvement` The strict internal request now preserves only the validated declared size alongside the safe display name for the information panel
* `Dependency` Added AndroidX Media3 Session version 1.10.1

# v1.1.0

###### 2026/08/27

* `Feature` Gesture controls: vertical swipes on the left or right half adjust brightness or media volume, horizontal swipes seek, side double taps jump 10 seconds, a center double tap toggles play/pause, and a long press engages a temporary 2× speed
* `Feature` Playback speed: nine selectable rates from 0.25× to 3×, with the control bar highlighting the current rate when it is not 1×
* `Feature` Immersive fullscreen: hidden system bars with display cutout support, floating auto-hiding title and control bars, and one-tap screen orientation and resize mode switching
* `Feature` Control lock: one tap disables every control and gesture to prevent accidental touches
* `Feature` Resume memory: playback continues from the last local position keyed by a SHA-256 digest of the content URI, cleared after finishing and capped at 200 entries
* `Improvement` Rebuilt bottom control bar with play/pause, 10-second jumps, a draggable progress bar, and buffered progress display
* `Improvement` Playback error panel now offers a retry action next to opening with another app
* `Improvement` Interface configuration changes such as rotation no longer rebuild the player for smoother switching

# v1.0.1

###### 2026/08/08

* `Fix` Return a valid Explorer Action service binding when enabled from Plugin Center
* `Improvement` Use a shorter plugin name and description with more natural user documentation

# v1.0.0

###### 2026/08/02

* `Feature` Video Player plugin with plugin ID `video-player`, action ID `play-video`, engine `explorer-action`, and variant `default`
* `Feature` Protocol v2 primary read-only Explorer action with an empty MIME matcher, matched only by the current 23 host video extensions and requiring host build 5269
* `Feature` Signature-protected Explorer entry with strict target and parent content URI, ClipData, source, display name, size, MIME type, extension, and grant validation followed by minimal forwarding to a non-exported player
* `Feature` Independent exported ACTION_VIEW entry for read-only video content URIs, with untrusted extras and ClipData discarded, forbidden grants rejected, and self-loop protection
* `Feature` Media3 ExoPlayer and PlayerView playback with autoplay, standard controls, audio focus, audio-becoming-noisy handling, saved position and play state, and keep-screen-on only during active playback
* `Feature` Safe Open with another app recovery after playback failure, using newly built read-only intents that explicitly exclude this plugin
* `Feature` Localized metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese
* `Dependency` Added AndroidX Media3 ExoPlayer and UI version 1.10.1
* `Dependency` Added Kotlin Parcelize runtime version 2.2.21
