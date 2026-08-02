# Video Player

Video Player adds a primary read-only video action to AutoJs6 Explorer. It uses AndroidX Media3 ExoPlayer and PlayerView, starts playback automatically, handles audio focus and noisy output changes, and restores playback position and play state.

The plugin requires AutoJs6 build 5269+. When it is installed, enabled, trusted, and compatible, matching video files open in this player. If it is missing or unavailable, AutoJs6 falls back to the Android ACTION_VIEW route. Audio playback and image viewing remain independent plugin capabilities.

Explorer extensions:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

Safety and privacy limits:

- Explorer execution requires the signature-level AutoJs6 plugin permission.
- The plugin accepts content URIs with temporary read-only access and never writes the source.
- Write and persistable grants are rejected. Prefix access is never forwarded to the player.
- A separate ACTION_VIEW entry accepts only read-only video content URIs and discards incoming extras and ClipData.
- If playback fails, Open with another app rebuilds a read-only intent and excludes this plugin.
- Actual decoding depends on Media3 extractors and codecs available on the device.
