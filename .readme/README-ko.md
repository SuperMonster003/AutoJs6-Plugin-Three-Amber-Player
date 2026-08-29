<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Ember Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
  </p>

  <p>파일 관리자 플러그인. 동영상 파일 직접 재생</p>

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

3-Ember Player는 AutoJs6 파일 관리자 플러그인이자 간편한 독립 동영상 플레이어입니다. 임시 읽기 전용 content URI를 받고 AndroidX Media3 ExoPlayer를 사용하며 원본 동영상을 변경하지 않습니다.

******

### 기능

******

- 기기의 내장 MPEG-4 Part 2 디코더를 통한 경량 XVID-in-MKV 호환 기능과 시스템 디코더를 사용할 수 없거나 실패할 때 명확한 외부 플레이어 복구를 제공합니다.
- 공유 `org.autojs.plugin.EXPLORER_ACTION` 프로토콜 v12를 통해 읽기 전용 탐색기 기본 작업을 등록하며 직접 형제 파일과 재생 진행률을 위한 선택적이고 제한된 기능을 제공합니다.
- AndroidX Media3 ExoPlayer와 PlayerView를 사용하여 자동 재생, 표준 컨트롤, 오디오 포커스, noisy 출력 변경 처리 및 기기 코덱 통합을 제공합니다.
- 재생성 후 재생 위치와 재생 또는 일시 정지 의도를 복원하고 동영상이 실제로 재생되는 동안에만 화면을 켜 둡니다.
- `video/*` MIME 유형의 읽기 전용 content URI를 위한 별도의 내보낸 `android.intent.action.VIEW` 진입점을 제공합니다.
- 재생에 실패하면 읽기 전용 보기 Intent를 다시 만들고 후보 목록에서 이 플러그인을 제외하는 안전한 다른 앱으로 열기 작업을 제공합니다.
- 전체 화면 몰입형 재생을 제공하며 밝기, 볼륨, 재생 위치의 제스처 조절, 더블 탭 이동, 길게 눌러 배속, 컨트롤 잠금을 지원합니다.
- 0.25×부터 3×까지의 재생 속도와 화면 비율 모드, 화면 방향을 원터치로 전환합니다.
- 이어서 재생 기록은 가장 최근에 연 미완료 동영상 하나만 SHA-256 식별 다이제스트와 시간 값으로 보관합니다. 다른 동영상을 열면 즉시 삭제되고 완료된 재생은 저장하지 않습니다.
- 내장 오디오 트랙과 자막을 선택할 수 있고 자막은 기본으로 꺼져 있으며 지원되지 않는 트랙을 명확히 표시합니다.
- 현재 동영상 반복, 상세 메타데이터 패널, 화면 비율 기반 방향 설정, API 26+ 원격 재생/일시정지 PIP 모드를 제공합니다.
- MediaSession으로 헤드셋, Bluetooth 및 시스템 컨트롤을 지원하고 미디어 알림에 제목과 재생 진행률을 표시합니다.
- 15/30/45/60분 또는 동영상 끝 취침 타이머와 영구 저장되는 제스처 감도 및 5/10/30초 더블 탭 탐색 설정.
- 0.25×–4× 핀치 확대/축소와 더블 탭 초기화, 탐색 중 목표 시간 및 선택적 메모리 미리보기 표시.
- Android 10+에서 권한 없이 현재 프레임을 MediaStore의 별도 PNG로 저장하며 원본 동영상을 수정하지 않습니다.
- Explorer Action v12를 통해 같은 폴더 동영상을 자연 정렬한 재생목록으로 만들고 이전 / 다음, 순차, 셔플, 한 항목 반복 및 다음 항목 자동 재생을 제공합니다.
- 언어 접미사를 포함하여 이름이 일치하는 .srt 및 .ass 외부 자막을 검색하며 자막은 기본으로 꺼져 있고 명시적으로 선택한 뒤에만 로드됩니다.
- 하나의 HCT 원본 색상으로 읽기 쉬운 밝은색·어두운색 의미 역할을 만들고 기본적으로 AutoJs6를 따르며 현지화된 Material 500 색상 19개와 실시간 미리보기 사용자 RGB를 제공합니다.
- 독립 런처와 호스트를 따르는 언어·야간 모드·색상, 동영상 하나 이어서 재생, 수동·자동 업데이트 확인, 무시한 버전, 릴리스 기록, 앱 및 개발자 정보 설정을 제공합니다.

******

### 호스트 통합

******

호스트는 기본 동영상 열기 경로와 재생 작업에 이 플러그인을 사용합니다.

플러그인이 설치되고 활성화되고 신뢰되며 호환되는 경우 호스트가 동영상으로 인식한 모든 파일을 열면 기본 탐색기 뷰어로 `play-video` 작업이 실행됩니다.

플러그인이 없거나 비활성화되거나 승인되지 않았거나 사용할 수 없거나 호환되지 않거나 실행할 수 없으면 호환 호스트가 복구 안내를 표시합니다. 시스템 앱 후보는 사용자가 다른 앱으로 열기를 명시적으로 선택한 경우에만 표시됩니다.

이 플러그인은 동영상 파일만 대상으로 합니다. 오디오 재생과 이미지 보기는 독립적인 플러그인 기능이며 이 APK에 포함되지 않습니다.

******

### 지원 형식

******

기본 탐색기 작업은 호스트가 인식하는 모든 동영상 형식을 `video/*`로 수락하며 이전 호스트와의 호환성을 위해 다음 23개 정확한 확장자 matcher도 유지합니다:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### 플러그인 인터페이스

******

호스트는 다음 식별자로 플러그인을 검색하고 실행합니다:

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

버전 2.0는 프로토콜 v12 읽기 전용 탐색기 기본 작업을 제공합니다. 호환 호스트는 요청 범위의 직접 형제 읽기 및 재생 진행률 기능을 첨부할 수 있으며 이러한 선택적 확장이 없는 호스트는 단일 파일 재생을 유지합니다. 독립 외부 진입점은 유효한 `video/*` MIME 하위 유형의 읽기 전용 content URI만 허용합니다.

전체 호스트 연동에는 AutoJs6 6.8.0 build 5276 이상과 Explorer Action v12가 필요하며 이후 플러그인 기능에서도 이 요구 사항을 높이지 않습니다.

******

### 보안

******

저장소 권한을 요청하지 않고 원본 동영상을 쓰지 않습니다. 인터넷은 사용자가 실행하거나 하루 한 번 수행하는 GitHub 릴리스 확인에만 사용합니다. 서명으로 보호된 탐색기 경계는 전체 프로토콜 v12 봉투, 정확히 하나의 선택 대상, 상위 관계, ClipData, 메타데이터, 호스트 빌드 및 읽기 전용 grant를 검증합니다. 선택적 Host Session은 호스트가 플러그인 UID에 고정하며 선택 파일의 직접 상위 폴더 나열과 선택 파일 또는 읽을 수 있는 직접 형제 파일 열기만 허용합니다. 비공개 플레이어는 제한된 불투명 재생목록을 검증하고 파일 시스템 경로를 받지 않습니다. 공개 ACTION_VIEW 경계는 독립된 단일 파일 진입점으로 유지됩니다.

******

### 안전 제한

******

- 탐색기는 정확히 하나의 선택된 content URI에서 시작합니다. 선택적 Host Session은 읽을 수 있는 직접 형제만 노출하며 재귀 폴더 접근은 허용하지 않습니다.
- 탐색기 실행에는 signature 수준의 `org.autojs.permission.PLUGIN` 권한이 필요합니다.
- 쓰기 및 영구 grant는 항상 거부됩니다. 탐색기 상위 URI를 검증하는 데 사용된 prefix 접근은 플레이어로 전달되지 않습니다.
- 공개 ACTION_VIEW 경계는 쓰기, 영구 및 prefix grant를 거부합니다.
- 외부 후보를 먼저 확인하고 다른 패키지만 남도록 필터링한 후 새로 만든 읽기 전용 Intent로 실행합니다.
- 이어서 재생 기록은 가장 최근에 연 미완료 동영상 하나만 SHA-256 식별 다이제스트와 시간 값으로 보관합니다. 다른 동영상을 열면 즉시 삭제되고 완료된 재생은 저장하지 않습니다.
- 동영상으로 인식되거나 나열된 이전 확장자에 해당해도 모든 기기의 디코딩 지원을 보장하지는 않습니다. 실제 재생 지원은 Media3와 설치된 플랫폼 코덱에 따라 결정됩니다.

******

### 릴리스 기록

******

# v2.0.0

###### 2026/08/29

* `기능` 하나의 원본 색상으로 도구 모음, 제어, 표면, 윤곽선 및 오류에 사용할 읽기 쉬운 밝은색·어두운색 의미 색상을 만드는 상세 HCT 색상 시스템과 현지화된 Material 500 색상 19개 및 실시간 미리보기 사용자 RGB를 추가했습니다
* `기능` 런처와 단일 파일 독립 플레이어를 추가하고 언어, 야간 모드, 테마 색상, 이어서 재생, 업데이트, 릴리스 기록, 앱 및 개발자 정보 전용 설정 화면을 제공합니다
* `기능` 언어, 야간 모드 및 원본 색상은 공식 읽기 전용 설정 계약을 통해 기본적으로 AutoJs6를 따릅니다. 호스트를 사용할 수 없어도 선택지는 비활성 상태로 표시되고 앱 기본값으로 대체됩니다
* `기능` 수동 및 일일 자동 업데이트 확인, 무시한 버전 관리, 현지화된 내장 릴리스 기록을 추가했습니다
* `수정` 호스트 설정 공급자가 요구하는 보호된 플러그인 정보 서비스 진입점을 공개하여 AutoJs6 따르기가 안정적으로 작동합니다
* `개선` 이어서 재생은 가장 최근에 연 동영상 하나만 기억하고 다른 동영상을 열면 이전 기록을 즉시 삭제하며 독립 및 호스트 기록 모두 재생을 마친 위치는 저장하지 않습니다
* `개선` 고정 앱 및 플러그인 표시 이름을 3-Ember Player로, 소스 네임스페이스를 threeemberplayer로 변경하고 업그레이드 호환성을 위해 기존 ID는 유지했습니다

# v1.4.0

###### 2026/08/28

* `기능` Explorer Action v12의 요청 범위·UID 고정 Host Session으로 같은 폴더 동영상을 자연 정렬한 재생목록을 만들고 이전 / 다음, 순차, 셔플, 한 항목 반복 및 자동 다음 항목 재생 지원
* `기능` 이름이 같은 .srt / .ass 외부 자막과 언어 접미사 변형을 검색하며 자막은 기본적으로 끈 상태이고 사용자가 명시적으로 선택한 뒤에만 로드
* `기능` 호스트가 관리하는 선택형 이어보기 기록 추가: 기록은 기본적으로 꺼져 있고 AutoJs6 설정에서 끄거나 지울 수 있으며 파일 목록에 시청 표시를 추가하지 않음
* `수정` 호스트가 동영상으로 분류한 Explorer 요청도 확장자가 이전 23개 허용 목록에 없으면 거부되던 문제. 신뢰된 `video/*` 요청을 일관되게 허용
* `개선` 같은 폴더 접근을 선택한 파일과 읽을 수 있는 바로 인접한 파일로만 제한하고 재귀 탐색, 쓰기, 영구 grant 및 플러그인의 일반 텍스트 경로 저장을 금지
* `개선` 직렬화 재생목록을 동영상 128개, 동영상당 자막 8개, 전체 자막 연결 128개로 제한하면서 선택한 항목은 항상 유지
* `개선` 호환 요구 사항을 AutoJs6 6.8.0 build 5276 및 Explorer Action v12로 유지하며 선택 확장이 없는 호스트는 안전하게 단일 파일 재생 유지
* `의존성` 포함된 Explorer Action API를 프로토콜 v2에서 하위 호환 v12 미디어 세션 확장으로 업그레이드

# v1.3.1

###### 2026/08/27

* `수정` 검증된 VFW/FourCC XVID 트랙을 기기의 내장 MPEG-4 Part 2 디코더에 전달하는 경량 XVID-in-MKV 호환 계층을 추가했습니다. 트랜스코딩하거나 원본 파일을 변경하지 않습니다
* `수정` 호환 시스템 디코더가 없거나 디코딩에 실패하면 오디오만 재생되는 상태를 중지하고 전용 설명과 다른 앱으로 열기 복구를 제공합니다

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
