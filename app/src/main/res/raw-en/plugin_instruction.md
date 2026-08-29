# 3-Ember Player

3-Ember Player adds a primary read-only video action to the file manager. It uses AndroidX Media3 ExoPlayer and PlayerView, starts playback automatically, handles audio focus and noisy output changes, and restores playback position and play state.

The full collaboration path requires AutoJs6 6.8.0 build 5276+ and Explorer Action v12; later plugin capabilities will not raise this baseline. When the plugin is installed, enabled, trusted, and compatible, every file recognized by the host as video opens in this player. A host without the optional session capabilities retains selected-file-only playback. If the plugin is missing, disabled, unauthorized, unavailable, incompatible, or cannot be launched, a compatible host shows recovery guidance and opens the Android application chooser only after the user explicitly selects Open with other apps. Audio playback and image viewing remain independent plugin capabilities.

Playback controls:

- Swipe vertically on the left or right half of the screen to adjust brightness or media volume.
- Swipe horizontally to seek, double-tap the left or right third to jump by the configured 5, 10, or 30 seconds, double-tap the center to play or pause, and long-press for a temporary 2× speed.
- The bottom bar provides play/pause, 10-second jumps, a draggable progress bar, playback speeds from 0.25× to 3×, resize mode, screen orientation, and a control lock against accidental touches.
- Opening one video from Explorer can build a naturally ordered, bounded queue from readable direct video siblings. Playback starts at the selected video and offers previous / next, sequence, shuffle, and repeat-one modes.
- Resume history retains exactly one most recently opened unfinished video as a SHA-256 identity digest plus time values; opening another video clears it immediately, and completed playback is never retained.
- The top bar selects embedded audio and text tracks. Matching .srt and .ass sidecars, including language suffixes, are discovered from the same folder. All subtitles are off by default and load only after explicit selection.
- Repeat-current, video information, automatic orientation for horizontal media, and Android 8.0+ picture-in-picture with play/pause actions are available from the top bar.
- MediaSession supports headset, Bluetooth, and system playback controls; the media notification exposes the current title and progress.
- A sleep timer pauses playback after 15, 30, 45, or 60 minutes or at the end of the video. Gesture settings select sensitivity and the double-tap seek interval.
- Pinch the picture to zoom from 0.25× to 4×; double-tap while zoomed to reset. Scrubbing shows a target-time bubble and a best-effort thumbnail when the container supports it.
- On Android 10 or later, Save current frame writes a PNG to Pictures/3-Ember Player. The entry stays hidden on earlier Android versions.
- Builds accessible light and dark semantic roles from one HCT source color, follows AutoJs6 by default, and offers 19 localized Material 500 presets plus live-preview custom RGB colors. Provides a standalone launcher and settings for host-following language, night mode and color, single-video resume behavior, manual and automatic update checks, ignored versions, release history, and app/developer information.

Explorer extensions:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Safety and privacy limits:

- Explorer execution requires the signature-level plugin permission.
- The plugin accepts content URIs with temporary read-only access and never writes the source.
- A request-scoped Host Session is pinned to the plugin UID and permits only non-recursive listing of the selected file's direct parent plus opening the selected file or a readable direct sibling. The plugin receives no filesystem path.
- Write and persistable grants are rejected. Prefix access is never forwarded to the player.
- A separate ACTION_VIEW entry accepts only read-only video content URIs and discards incoming extras and ClipData.
- If playback fails, Open with another app rebuilds a read-only intent and excludes this plugin.
- Validated XVID-in-MKV tracks use the device's MPEG-4 Part 2 decoder without transcoding. If the decoder is unavailable or fails, playback stops and offers Open with another app.
- Actual decoding depends on Media3 extractors and codecs available on the device.
- Resume history retains exactly one most recently opened unfinished video as a SHA-256 identity digest plus time values; opening another video clears it immediately, and completed playback is never retained.
- The information panel uses playback-session metadata plus the sanitized display name and declared size; it does not scan or modify the source video.
- A requested screenshot creates a separate PNG through MediaStore and never writes the source. Scrub thumbnails remain in memory and are silently omitted when extraction is unavailable.
