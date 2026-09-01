<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
    </picture>
  </p>

  <p>재생 목록, 자막, 백그라운드 오디오를 지원하는 동영상 재생</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

README.md는 현재 다음 언어로 제공됩니다:

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

3-Ember Player는 AutoJs6 파일 관리자의 동영상 재생 플러그인이자, 단독으로도 사용할 수 있는 로컬 동영상 플레이어입니다. 파일 관리자에서 동영상 파일을 탭하면 바로 전체 화면으로 재생되며, 제스처 조작, 배속 재생, 외부 자막, 같은 폴더 연속 재생, PIP 모드 등 주요 플레이어의 핵심 기능을 모두 갖추고 있습니다. 재생 기능은 AndroidX Media3 ExoPlayer를 기반으로 합니다.

플러그인은 읽기 전용 보안 모델을 지킵니다. 동영상은 임시 읽기 전용 권한을 통해서만 플레이어로 전달되고, 저장소 권한은 전혀 요청하지 않으며, 원본 파일을 수정하거나 이동하지 않습니다. 네트워크는 플러그인 업데이트 확인에만 사용됩니다.

******

### 주요 기능

******

- 탭 한 번으로 재생: AutoJs6 파일 관리자에서 동영상 파일을 탭하면 아무 설정 없이 곧바로 몰입형 전체 화면 재생이 시작됩니다.
- 편리한 제스처: 왼쪽 절반을 위아래로 스와이프하면 밝기, 오른쪽 절반은 볼륨이 조절되고, 좌우로 스와이프해 원하는 위치로 이동합니다. 양쪽 가장자리를 더블 탭하면 앞뒤로 건너뛰고, 중앙을 더블 탭하면 재생/일시정지되며, 길게 누르면 일시적으로 2배속이 됩니다. 잠금 버튼 하나로 모든 조작을 잠가 실수로 터치하는 것도 막을 수 있습니다.
- 배속과 화면 제어: 0.25×부터 3×까지 9단계 배속, 두 손가락 핀치 확대/축소 (0.25×부터 4×까지), 맞춤/채움/자르기 3가지 화면 비율 모드, 화면 비율에 따라 자동으로 제안되는 원터치 화면 방향 전환을 제공합니다.
- 같은 폴더 연속 재생: 동영상 하나를 열면 자연 정렬 대기열이 만들어지고, 패널에서 현재 항목과 확인된 길이를 보고 탭 이동과 순차/셔플/한 항목 반복 전환이 가능하며, 다음 자동 재생 전에는 취소 가능한 3초 안내가 표시됩니다.
- 호스트 선택 순서 대기열: 읽기 전용 '선택 항목 재생'은 한 폴더의 동영상 1~128개를 호스트 선택 순서 그대로 대기열로 만들고 형제 파일을 검색하지 않으며, 선택 설정으로 재생 모드를 세션 간 기억할 수 있습니다.
- 외부 자막과 내장 자막: 이름이 같은 .srt / .ass를 자동으로 찾거나 하나를 직접 불러오고, 일반적인 구형 인코딩 감지, 자막 스타일과 ±600초 외부 자막 오프셋 조절, 내장 자막 및 오디오 트랙 전환을 지원합니다. 자막은 직접 켜기 전까지 꺼져 있습니다.
- 정밀 재생: 일시정지 중 앞뒤 프레임 이동과 길게 눌러 연속 실행을 지원하고, A-B 구간 반복을 설정하거나 해제해 세부 장면을 반복 확인할 수 있습니다.
- 이어서 재생: 가장 최근에 다 보지 못한 동영상의 재생 위치를 기억해 두었다가 다시 열면 이어서 재생합니다. 끝까지 본 동영상은 즉시 기록이 지워져 시청 흔적이 남지 않습니다.
- PIP 모드와 시스템 통합: Android 8.0 이상에서 재생 중 앱을 벗어나면 자동으로 작은 창(PIP)으로 전환됩니다. 헤드셋과 블루투스 제어, 제목과 진행률을 보여 주는 미디어 알림도 지원합니다.
- 취침 타이머: 15/30/45/60분 뒤 또는 현재 동영상이 끝날 때 자동으로 일시정지합니다.
- 탐색 미리보기: 진행 바를 드래그하면 이동할 시간이 말풍선으로 표시되고, 가능한 경우 해당 장면의 미리보기 이미지도 함께 보여 줍니다.
- 현재 프레임 저장: Android 10 이상에서 탭 한 번으로 현재 화면을 PNG로 시스템 갤러리에 저장합니다. 저장소 권한이 필요 없고 원본 동영상은 건드리지 않습니다.
- 까다로운 형식 대비: 경량 XVID-in-MKV 호환 계층을 내장했습니다. 기기가 동영상을 디코딩하지 못하면 명확한 안내를 표시하고, 다른 플레이어로 재생을 넘길 수 있습니다.
- 취향대로 테마: 테마 색상 하나로 읽기 좋은 밝은 테마와 어두운 테마 배색이 만들어집니다. 기본적으로 AutoJs6를 따르며, 미리 제공되는 19가지 색상과 실시간 미리보기가 있는 사용자 지정 RGB 색상도 선택할 수 있습니다.
- 단독 사용 가능: 자체 런처 아이콘과 설정 페이지가 있어 시스템 파일 선택기로 동영상을 직접 열 수 있고, 시스템의 "다음으로 열기" 메뉴에서 동영상 플레이어로 선택할 수도 있습니다.
- 화면 및 출력 도구: HDR10/HLG/SDR과 확인 가능한 색상 정보를 보고 한 번에 복사하며, 자막을 포함한 공유용 스크린샷, 세션 내 화면 반전, 경고 확인 후 최대 +15 dB 볼륨 증폭을 사용할 수 있습니다.
- 제스처 없이도 접근 가능: TalkBack에서 이름이 지정된 컨트롤이 계속 표시되고 모든 제스처에 버튼 또는 메뉴 대체 경로가 있습니다. 200% 글꼴/화면 배율과 키보드/DPAD 포커스, 스페이스/Enter, 탐색 및 MediaSession 키도 지원합니다.
- 선택형 백그라운드 오디오: 기본값은 꺼짐이며 알림 권한이 있을 때만 켤 수 있습니다. PIP를 사용할 수 없으면 기존 플레이어를 알림 제어가 있는 미디어 재생 포그라운드 서비스로 넘기며, 재생 완료 또는 설정 해제 시 중지합니다. PIP가 항상 우선합니다.
- 읽기 전용 설계: 저장소 권한을 요청하지 않고 원본 동영상에 절대 쓰지 않으며, 네트워크는 업데이트 확인에만 사용합니다.

******

### 설치 및 사용

******

시작하기 전에 아래 환경 요구 사항을 확인해 주세요:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

설치부터 첫 동영상 재생까지 4단계면 충분합니다:

1. 플러그인 APK를 다운로드하여 설치합니다. 설치하면 홈 화면에 3-Ember Player 아이콘이 나타나고, 플러그인 기능은 AutoJs6에서 관리됩니다.
2. AutoJs6를 열고 `플러그인 센터`에 들어가 `3-Ember Player`를 찾아 활성화합니다.
3. AutoJs6 파일 관리자에서 아무 동영상 파일 (예: `movie.mp4`)이나 찾습니다.
4. 파일을 탭하면 동영상이 곧바로 전체 화면으로 재생됩니다.

호스트 없이 사용하기: 홈 화면에서 3-Ember Player를 직접 열고 `동영상 열기`를 탭한 뒤 시스템 파일 선택기에서 동영상을 고르면 바로 재생됩니다. 다른 앱에서 보낸 동영상 보기 요청도 이 플레이어로 받을 수 있습니다. 위 환경 요구 사항의 호스트 버전은 파일 관리자 진입에만 적용되며, 단독 재생에는 영향이 없습니다.

재생 화면 제스처 한눈에 보기:

- 화면 한 번 탭: 컨트롤 바를 표시하거나 숨깁니다.
- 중앙 더블 탭: 재생/일시정지. 왼쪽/오른쪽 더블 탭: 10초 되감기/빨리 감기 (설정에서 5/10/30초로 변경 가능).
- 왼쪽 절반 위아래 스와이프: 밝기 조절. 오른쪽 절반 위아래 스와이프: 볼륨 조절.
- 좌우 스와이프: 이동할 위치를 미리 확인하고, 손을 떼면 적용됩니다.
- 길게 누르기: 일시적으로 2배속 재생, 손을 떼면 원래 속도로 돌아옵니다.
- 두 손가락 핀치: 화면 확대/축소 (0.25×부터 4×까지). 확대된 상태에서 더블 탭하면 원래 크기로 돌아옵니다.
- 잠금 버튼: 모든 제스처와 컨트롤을 잠가 실수로 터치하는 것을 막습니다. 잠근 뒤 화면을 탭하면 잠금 해제 버튼이 나타납니다.

******

### 지원 형식

******

파일 관리자의 재생 동작은 다음 23가지 확장자와 정확히 일치하는 파일에 적용됩니다:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

위 목록은 구버전 호스트를 위해 유지되는 정확한 확장자 허용 목록입니다. 신버전 호스트에서는 동영상으로 인식되는 모든 파일이 `video/*`를 통해 이 플러그인으로 전달됩니다. 목록에 있다고 해서 모든 기기에서 디코딩된다는 보장은 없으며, 실제 재생 가능 여부는 Media3와 기기 플랫폼 디코더에 따라 달라집니다. 단독 진입점과 다른 앱의 호출도 마찬가지로 `video/*` MIME 유형으로 요청을 받습니다.

******

### 자주 묻는 질문

******

**동영상 파일을 탭해도 이 플레이어로 열리지 않나요?**

다음을 순서대로 확인해 주세요. AutoJs6 버전 코드가 5276 이상인지 (6.8.0 및 이후 버전), 플러그인이 `플러그인 센터`에서 활성화되어 있는지, 해당 파일이 호스트에서 동영상으로 인식되는지 확인합니다. 셋 중 하나라도 충족되지 않으면 탭해도 이 플러그인이 처리하지 않습니다.

**플러그인이 설치되어 있지 않거나 비활성화된 상태에서 동영상을 탭하면 어떻게 되나요?**

호환되는 호스트는 플러그인을 설치하거나 활성화하라는 복구 안내를 표시합니다. 사용자가 `다른 앱으로 열기`를 직접 선택했을 때만 시스템 앱 선택 메뉴가 나타나, 기기의 다른 플레이어로 동영상을 넘깁니다.

**재생할 때 소리만 나오고 화면이 나오지 않거나, 재생할 수 없다고 표시되나요?**

동영상 디코딩 가능 여부는 기기 플랫폼과 Media3의 디코딩 능력에 따라 달라지며, 확장자 목록에 있다고 해서 반드시 재생되는 것은 아닙니다. 흔히 쓰이던 구형 XVID-in-MKV 형식은 내장 호환 계층이 시스템 MPEG-4 Part 2 디코더로 넘겨 처리합니다. 그래도 디코딩되지 않으면 명확한 안내가 표시되며, `다른 앱으로 열기`로 다른 플레이어에 동영상을 넘길 수 있습니다.

**같은 폴더의 동영상을 연속으로 재생하려면 어떻게 하나요?**

호스트 버전 요구 사항을 충족한 상태에서 파일 관리자로 아무 동영상이나 열면 같은 폴더 재생목록이 자동으로 만들어집니다. 파일 이름 자연 정렬 순서로 현재 동영상부터 시작해, 하나가 끝나면 다음이 자동으로 이어지며, 순차, 셔플, 한 항목 반복 모드를 제공합니다. 호스트 버전이 오래되었거나 단독 진입점으로 열었을 때는 단일 파일 재생이 유지됩니다.

**외부 자막은 어떻게 불러오나요?**

`.srt` 또는 `.ass` 자막 파일을 동영상과 같은 폴더에 같은 이름으로 두고 (`movie.ko.srt` 같은 언어 접미사도 허용), 파일 관리자에서 동영상을 연 다음 자막 메뉴에서 선택하면 됩니다. 자막은 기본적으로 꺼져 있으며 저절로 켜지지 않습니다.

**이어서 재생은 무엇을 기록하나요? 업로드되기도 하나요?**

가장 최근에 다 보지 못한 동영상의 위치만 기록합니다. 저장되는 내용은 파일의 SHA-256 다이제스트와 시간 값뿐이며, 파일 이름이나 경로는 포함하지 않습니다. 다른 동영상을 열거나 재생을 마치면 즉시 지워집니다. 모든 데이터는 기기 안에만 저장되며 아무것도 업로드되지 않습니다.

**플러그인에는 어떤 권한이 필요한가요?**

저장소, 카메라, 마이크 같은 민감한 런타임 권한은 전혀 요청하지 않습니다. 업데이트 확인에 쓰이는 네트워크 권한 (사용자가 직접 실행하거나 하루 최대 한 번)과 파일 관리자 진입에 필요한 서명 보호 플러그인 권한만 선언합니다.

**AutoJs6 없이 단독으로 사용할 수 있나요?**

가능합니다. v2.0.0부터 플러그인에 런처 진입점이 생겨, 시스템 파일 선택기로 동영상을 골라 재생하거나 다른 앱의 `다음으로 열기` 메뉴에서 이 플레이어를 선택할 수 있습니다. 같은 폴더 연속 재생, 외부 자막 찾기, 호스트 설정 따르기 기능은 여전히 AutoJs6와 함께 사용해야 합니다.

******

### 보안

******

플러그인은 기본 거부 원칙에 따라 만들어졌으며, 아래 조치는 모두 항상 켜져 있고 끌 수 없습니다:

- 민감 권한 제로: 저장소를 비롯한 어떤 런타임 권한도 요청하지 않습니다. 네트워크는 사용자가 실행하거나 하루 한 번 수행되는 GitHub 릴리스 확인에만 사용됩니다.
- 절대 쓰지 않음: 재생, 프레임 저장, 미리보기 이미지 추출까지 전 과정이 읽기 전용이며, 어떤 경우에도 원본 동영상을 수정, 이동, 삭제하지 않습니다.
- 진입점 항목별 검증: 파일 관리자 진입점은 서명 수준의 플러그인 권한으로 보호되며, 모든 요청의 프로토콜 버전, 대상 URI, ClipData, 메타데이터, 호스트 빌드, 읽기 전용 권한 부여를 항목별로 검증해 하나라도 맞지 않으면 요청을 거부합니다.
- 제한된 같은 폴더 접근: 재생목록 생성과 자막 찾기는 호스트가 관리하는 단기 세션을 통해서만 이루어지며, 선택한 파일의 직접 형제 파일만 나열할 수 있고, 하위 폴더 재귀 접근, 쓰기, 영구 권한 부여는 모두 금지됩니다. 내부 플레이어는 검증된 제한적 재생목록만 받으며 파일 시스템 경로에는 접근하지 않습니다.
- 서로 격리된 두 진입점: 시스템을 향한 `ACTION_VIEW` 진입점은 읽기 전용 content URI의 `video/*` 요청만 받아들이고, 쓰기, 영구, 접두사 권한 부여를 거부하며, 파일 관리자 진입점과 서로 독립적으로 동작합니다.
- 최소한의 이어서 재생 데이터: 이어서 재생 기록은 가장 최근의 미완료 기록 하나만 SHA-256 다이제스트와 시간 값으로 보관하며, 재생을 마치면 즉시 지웁니다.
- 안전한 넘기기: `다른 앱으로 열기`는 읽기 전용 Intent를 새로 만들고 후보 목록에서 이 플러그인을 제외해, 권한 확산과 자기 자신으로 되돌아오는 순환을 방지합니다.

******

### 플러그인 인터페이스 (개발자용)

******

호스트는 다음 식별자로 플러그인을 발견하고 호출합니다:

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

현재 구현은 explorer-action v12에 기반해 읽기 전용 기본 동작 `play-video`와 선택 도구 모음 동작 `play-video-selection`을 등록하고, `video/*` 및 구버전용 23개 확장자로 동영상을 받습니다. 기본 동작은 같은 폴더 대기열과 자막을 위한 제한된 형제 읽기를 사용할 수 있지만, 선택 동작은 호스트 대상 1~128개를 순서대로 유지하며 형제 읽기를 켜지 않습니다. 호환 세션은 재생 진행률도 전달하고, 없는 선택 기능은 안전하게 대체됩니다. 오디오와 이미지는 별도 플러그인입니다.

완전한 호스트 연동 기능에는 AutoJs6 6.8.0 (build 5276) 이상 버전과 Explorer Action v12 지원이 필요합니다. 이후 플러그인 업데이트에서 이 요구 사항을 높이지 않습니다.

******

### 개발 로드맵

******

완성된 기능과 앞으로의 계획은 체크 가능한 목록 형태로 Roadmap.md에서 관리됩니다. 체크되지 않은 항목은 계획 의도를 나타낼 뿐, 현재 버전이 갖춘 기능을 의미하지 않습니다.

- [체크 가능한 Roadmap.md 열기](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### 릴리스 기록

******

#### v3.0.0

###### 2026/09/01

* `기능` 플레이어 접근성 전체 점검: 이름과 동적 상태가 있는 컨트롤, TalkBack 터치 탐색 중 계속 표시되는 조작부, 모든 제스처의 버튼 또는 메뉴 대체 경로, 명확한 포커스 순서, 200% 글꼴/화면 배율 검증을 추가했습니다
* `기능` 키보드 및 리모컨 지원: 보이는 포커스 링, DPAD 탐색, Space/Enter 재생 및 일시정지, 설정한 5/10/30초 단위 좌우 탐색, MediaSession을 통한 미디어 키를 지원합니다
* `기능` 기본값이 꺼진 선택형 백그라운드 오디오: 알림 권한을 허용하면 PIP를 사용할 수 없을 때 재생 중인 플레이어를 끊김 없이 알림 제어가 있는 미디어 재생 포그라운드 서비스로 넘길 수 있습니다
* `개선` PIP가 항상 백그라운드 오디오보다 우선합니다; 설정 해제, 재생 완료, 오류 또는 명시적 종료 시 서비스를 중지하고, 알림을 탭하면 같은 플레이어와 이어지는 위치를 되찾습니다
* `개선` 헤드셋 동작은 Media3를 따릅니다: 한 번 누르면 재생/일시정지, 외부 기기 두 번 누르기는 다음 대기열 항목이 있을 때 이동, 명시적 Previous 미디어 명령은 이전 항목으로 이동합니다; 비표준 세 번 누르기 타이밍을 추가하지 않습니다
* `개선` README 레이아웃과 Gradle 플랫폼 버전 관리 방식을 통일
* `개선` 플러그인 설명을 간결하게 다듬고 다국어 리소스의 문장 부호를 통일
* `개선` 외부 보기 진입점을 External Viewer로 변경해 뷰어 의미를 통일
* `개선` 업데이트 대화상자의 릴리스 기록 버튼에서 내장 릴리스 기록 페이지를 열도록 변경

#### v2.3.0

###### 2026/08/31

* `기능` 동영상 정보에서 HDR10,HLG 또는 SDR을 식별하고 사용 가능한 색 공간,범위 및 비트 심도를 표시합니다.모든 필드를 한 번에 복사할 수 있으며 디스플레이가 원본 HDR 형식 지원을 보고하지 않으면 한 번만 경고합니다
* `기능` 기본적으로 꺼진 설정을 통해 표시 중인 자막을 스크린샷에 포함할 수 있고,공유 작업으로 방금 저장한 PNG를 다른 앱에 바로 보낼 수 있습니다
* `기능` 세션 전용 영상 미러링으로 좌우,상하 또는 두 축을 뒤집을 수 있으며 핀치 확대와 화면 회전에 일관되게 결합됩니다
* `기능` 세션 전용 볼륨 증폭을 +3 dB부터 +15 dB까지 제공하며 기본값은 꺼짐입니다.처음 사용하기 전에 왜곡과 청력 위험을 경고하고 지원되지 않으면 자동으로 끕니다
* `개선` 스크린샷은 저장소 권한 없이 독립적인 MediaStore PNG로 유지되며 공유 시 선택한 이미지에 읽기 전용 접근만 부여합니다
* `개선` 평가 결과 LoudnessEnhancer를 채택했습니다.미디어 파일을 수정하지 않고 재생 중에만 제한된 게인을 적용할 수 있고 효과 생성 실패 시 즉시 해제하여 안전하게 되돌릴 수 있기 때문입니다

#### v2.2.0

###### 2026/08/31

* `기능` Host Session 재생 대기열 패널 추가: 파일명, 확인된 길이 또는 자리표시자, 현재 항목 강조, 탭 이동, 순차·셔플·한 항목 반복 모드의 실시간 연동 제공
* `기능` 읽기 전용 '선택 항목 재생' 동작 추가: 같은 상위 폴더의 지원 동영상 1~128개를 호스트 선택 순서대로 재생하며 형제 파일을 검색하지 않음
* `기능` 대기열 자동 재생 전에 현재 마지막 프레임을 유지하고 취소 가능한 3초 다음 항목 안내를 표시하며 잠긴 컨트롤은 해제하지 않음
* `기능` 기본값이 꺼짐인 '재생 모드 기억' 설정을 추가해 순차, 셔플 또는 한 항목 반복을 재생 세션 간 유지 가능
* `개선` 각 선택 대상의 content URI, ClipData 위치, MIME, 메타데이터, 크기와 상위 폴더를 개별 검증하며 ID 또는 URI가 중복되면 전체 그룹을 거부
* `개선` 다중 선택은 제한된 Host Session 경로를 통해 명시적으로 승인된 대상만 읽고 형제 동영상이나 자막을 검색하지 않음

##### 전체 기록

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

빌드 매개변수는 `version.properties`에서 가져오며, 현재 최소 SDK는 24, 대상 SDK는 36입니다.

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

`strings.xml`은 플러그인 정보와 플레이어 인터페이스를 현지화하고, `plugin_instruction.md`는 호스트 쪽에 표시되는 사용 안내를 제공합니다. 모든 README와 CHANGELOG는 `.python/generate_markdown.py`가 JSON 원본에서 생성합니다. 문서를 수정할 때는 `.readme`와 `.changelog` 아래의 `lang_*.json`을 편집한 뒤 스크립트를 다시 실행해 주시고, 생성된 Markdown 파일은 직접 편집하지 마세요.

******

### 관련 링크

******

- AutoJs6 문서: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (재생 엔진): https://developer.android.com/media/media3/exoplayer
- Android 보안 파일 공유: https://developer.android.com/training/secure-file-sharing
