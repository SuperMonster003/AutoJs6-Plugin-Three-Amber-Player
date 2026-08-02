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
