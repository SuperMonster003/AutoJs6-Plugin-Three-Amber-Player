3-Amber Player is the video player plugin for the AutoJs6 file explorer and also works as a standalone local video player. Tap a video in the file explorer to play it full screen, with continuous playback of the same folder, local playlists, external subtitles, picture-in-picture and background audio; source videos are never modified.

### Getting started

1. Make sure this plugin is enabled in AutoJs6 "Plugin center".
2. Tap a video in the AutoJs6 file explorer; the player opens full screen and starts playing.
3. Tap the picture to show the controls; swipe up or down on the left or right half to adjust brightness or volume, and double-tap the center to play or pause.
4. To use it without AutoJs6, open this plugin from its launcher icon and tap "Open a video file".

### Main features

- **Continuous playback**: After you open a video, the other videos in the same folder form a queue; jump within it from "Video playlist", and choose to play in sequence, shuffle or repeat the current video.
- **Play selected**: Select several videos in the file explorer and tap "Play selected" to play up to 128 of them in the order you selected them.
- **Local playlists**: Open playlist files such as M3U/M3U8, PLS and XSPF with up to 128 items; when a playlist refers to videos in another folder, open it from the launcher icon and pick that folder when asked.
- **Gestures and speed**: Swipe sideways to seek and long-press for temporary 2x speed; set the double-tap seek step in "Gesture settings", and use "Lock controls" to prevent accidental touches.
- **Subtitles and audio tracks**: Switch embedded tracks in "Subtitles" and "Audio track", use "Load from file" for an .srt or .ass subtitle, and adjust "Subtitle style" and "Subtitle offset".
- **Resume playback**: A video you did not finish continues from where it stopped the next time you open it; turn this off with "Resume playback" in the settings.
- **Picture-in-picture and background audio**: On Android 8.0 or later, use "Picture-in-picture"; with "Continue audio in background" on, the sound keeps playing after you leave the player.
- **Sleep timer and frame stepping**: "Sleep timer" stops playback when the time is up; while paused, step frame by frame and set an A-B loop.
- **Screenshots and video information**: On Android 10 or later, "Save current frame" saves a PNG to `Pictures/3-Amber Player`; "Video information" shows the resolution, frame rate and more.
- **Appearance and settings**: Colors, night mode and language follow AutoJs6 by default; in "Settings" you can also "Check for updates" and view "Release history".

### Requirements

- Android 7.0 or later.
- Opening videos from the file explorer requires AutoJs6 6.8.0 or later; the standalone player opened from its launcher icon does not need AutoJs6.
- "Continue audio in background" needs the notification permission to show the playback controls notification.

### FAQ

**Why tapping a video does not open this player**

Make sure AutoJs6 meets the version requirement and this plugin is enabled in "Plugin center". When the plugin is unavailable, AutoJs6 shows recovery guidance, and "Open by other apps" hands the video to another player.

**When a video cannot be played**

Whether a video can be decoded depends on the decoders of the device. When playback fails, tap "Open with another app" to use another player.

**What the resume record stores**

Only an identity digest and the playback position of the latest unfinished video, kept on this device; it is cleared when you open another video or playback ends.

See the [project homepage](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player) for more details.
