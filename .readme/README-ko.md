<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-video-player-ic-launcher" border="0" width="128" />
  </p>

  <p>안전한 content URI 처리를 지원하는 AutoJs6 탐색기용 읽기 전용 동영상 재생</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

현재 README.md는 다음 언어를 지원합니다:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### 소개

******

AutoJs6 Video Player 플러그인은 AutoJs6 탐색기 또는 다른 Android 애플리케이션이 임시 읽기 전용 content URI 접근으로 제공한 동영상 콘텐츠를 재생합니다. AndroidX Media3 ExoPlayer를 사용하며 원본 동영상을 변경하지 않습니다.

******

### 기능

******

- 공유 `org.autojs.plugin.EXPLORER_ACTION` 프로토콜 버전 2를 통해 단일 파일용 읽기 전용 탐색기 기본 작업을 등록합니다.
- AndroidX Media3 ExoPlayer와 PlayerView를 사용하여 자동 재생, 표준 컨트롤, 오디오 포커스, noisy 출력 변경 처리 및 기기 코덱 통합을 제공합니다.
- 재생성 후 재생 위치와 재생 또는 일시 정지 의도를 복원하고 동영상이 실제로 재생되는 동안에만 화면을 켜 둡니다.
- `video/*` MIME 유형의 읽기 전용 content URI를 위한 별도의 내보낸 `android.intent.action.VIEW` 진입점을 제공합니다.
- 재생에 실패하면 읽기 전용 보기 Intent를 다시 만들고 후보 목록에서 이 플러그인을 제외하는 안전한 다른 앱으로 열기 작업을 제공합니다.

******

### 호스트 통합

******

AutoJs6는 `app/src/main/java/org/autojs/autojs/ui/explorer/ExplorerView.kt`의 기본 동영상 열기 경로와 `app/src/main/java/org/autojs/autojs/ui/main/scripts/MediaInfoDialogManager.kt`의 재생 작업에 이 플러그인을 통합합니다.

플러그인이 설치되고 활성화되고 신뢰되며 호환되는 경우 일치하는 동영상을 열면 기본 탐색기 뷰어로 `play-video` 작업이 실행됩니다.

플러그인이 없거나 비활성화되거나 사용할 수 없거나 호환되지 않거나 실행할 수 없으면 AutoJs6는 시스템 `android.intent.action.VIEW` 경로로 대체하여 설치된 다른 동영상 애플리케이션이 파일을 처리하도록 합니다.

이 플러그인은 동영상 파일만 대상으로 합니다. 오디오 재생과 이미지 보기는 독립적인 플러그인 기능이며 이 APK에 포함되지 않습니다.

******

### 지원 형식

******

기본 탐색기 작업은 빈 MIME matcher를 사용하며 호스트의 다음 23개 동영상 확장자와만 정확히 일치합니다:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### 플러그인 인터페이스

******

AutoJs6는 다음 식별자로 플러그인을 검색하고 실행합니다:

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

버전 1은 나열된 동영상 확장자에 대한 읽기 전용 기본 탐색기 작업을 제공합니다. 독립적인 외부 진입점은 유효한 `video/*` MIME 하위 유형을 가진 읽기 전용 content URI를 허용합니다. 실제 디코딩은 Media3 extractor와 기기에서 사용할 수 있는 코덱에 따라 달라집니다.

플러그인은 전부 JVM으로 구현되며 네이티브 라이브러리를 포함하지 않습니다. `supportedAbis = emptyArray()`를 선언하고 ABI 독립적인 단일 APK로 배포됩니다. AutoJs6 호스트 빌드 5269 이상이 필요합니다.

******

### 보안

******

플러그인은 저장소 또는 INTERNET 권한을 요청하지 않습니다. 서명으로 보호된 탐색기 경계는 프로토콜 버전 2, 원본 화면, 호스트 빌드, 대상 및 상위 content URI, 정확한 ClipData 순서, 표시 이름, 선언된 크기, MIME 유형, 확장자 및 읽기 전용 grant 플래그를 검증합니다. 그런 다음 대상 URI, MIME 유형, 안전한 표시 이름, 대상 ClipData 항목 하나 및 읽기 grant만 포함하는 새 명시적 Intent를 내보내지 않은 플레이어용으로 만듭니다. 공개 ACTION_VIEW 경계는 content URI, 동영상 MIME 유형 및 정확한 읽기 grant만 별도로 허용하고 수신한 모든 extras와 ClipData를 무시하며 동일한 최소 내부 요청을 다시 만듭니다.

******

### 안전 제한

******

- 재생 요청당 대상 content URI 1개.
- 탐색기 실행에는 signature 수준의 `org.autojs.permission.PLUGIN` 권한이 필요합니다.
- 쓰기 및 영구 grant는 항상 거부됩니다. 탐색기 상위 URI를 검증하는 데 사용된 prefix 접근은 플레이어로 전달되지 않습니다.
- 공개 ACTION_VIEW 경계는 쓰기, 영구 및 prefix grant를 거부합니다.
- 외부 후보를 먼저 확인하고 다른 패키지만 남도록 필터링한 후 새로 만든 읽기 전용 Intent로 실행합니다.
- 나열된 확장자가 모든 기기의 디코딩 지원을 보장하지는 않습니다. 실제 재생 지원은 Media3와 설치된 플랫폼 코덱에 따라 결정됩니다.

******

### 릴리스 기록

******

# v1.0.0

###### 2026/08/02

* `기능` 플러그인 ID `video-player`, 작업 ID `play-video`, 엔진 `explorer-action` 및 변형 `default`를 사용하는 Video Player 플러그인
* `기능` 빈 MIME matcher로 현재 호스트 동영상 확장자 23개와만 정확히 일치하며 AutoJs6 호스트 빌드 5269를 요구하는 프로토콜 v2 읽기 전용 기본 탐색기 작업
* `기능` 대상 및 상위 content URI, ClipData, 원본, 표시 이름, 크기, MIME 유형, 확장자 및 grant를 엄격하게 검증한 뒤 내보내지 않은 플레이어로 최소한의 정보만 전달하는 서명 보호 탐색기 진입점
* `기능` 읽기 전용 동영상 content URI를 위한 독립적인 내보낸 ACTION_VIEW 진입점, 신뢰할 수 없는 extras 및 ClipData 폐기, 금지된 grant 거부 및 자체 루프 방지
* `기능` 자동 재생, 표준 컨트롤, 오디오 포커스, noisy 출력 변경 처리, 저장된 재생 위치와 상태 및 실제 재생 중에만 화면 켜짐을 지원하는 Media3 ExoPlayer와 PlayerView 재생
* `기능` 재생 실패 후 이 플러그인을 명시적으로 제외하는 새 읽기 전용 Intent를 사용하는 안전한 다른 앱으로 열기 복구
* `기능` 네이티브 라이브러리가 없는 순수 JVM 구현, `supportedAbis = emptyArray()`로 선언한 무제한 ABI 및 ABI 독립적인 단일 APK
* `기능` 스페인어, 프랑스어, 러시아어, 아랍어, 일본어, 한국어, 영어, 중국어 간체, 홍콩 중국어 번체 및 대만 중국어 번체로 현지화된 메타데이터, 인터페이스 텍스트, 사용 안내, README 및 변경 기록
* `의존성` AndroidX Media3 ExoPlayer 및 UI 버전 1.10.1 추가
* `의존성` Kotlin Parcelize runtime 버전 2.2.21 추가

##### 더 많은 릴리스

* [CHANGELOG-ko.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 빌드:

```powershell
.\gradlew.bat :app:assembleRelease
```

빌드 매개변수는 `version.properties`에서 가져옵니다. 현재 최소 SDK는 24이고 대상 SDK는 36입니다.

******

### 리소스 구성

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml`은 플러그인 메타데이터와 UI 텍스트를 현지화합니다. `plugin_instruction.md`는 사용 및 보안 안내를 제공합니다. `.python/generate_markdown.py`는 JSON 원본에서 현지화된 README와 변경 기록을 생성합니다.

******

### 링크

******

- AutoJs6 문서: https://docs.autojs6.com
- Android 보안 파일 공유: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
