# 동영상 플레이어

Video Player는 파일 관리자에 읽기 전용 기본 동영상 작업을 추가합니다. AndroidX Media3 ExoPlayer와 PlayerView를 사용하고 자동으로 재생을 시작하며 오디오 포커스와 noisy 출력 변경을 처리하고 재생 위치와 상태를 복원합니다.

플러그인에는 호스트 빌드 5269 이상이 필요합니다. 설치되고 활성화되고 신뢰되며 호환되는 경우 일치하는 동영상 파일이 이 플레이어에서 열립니다. 플러그인이 없거나 사용할 수 없으면 호스트는 Android ACTION_VIEW 경로로 대체합니다. 오디오 재생과 이미지 보기는 독립적인 플러그인 기능입니다.

탐색기 확장자:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

안전 및 개인정보 제한:

- 파일 관리자 실행에는 signature 수준의 플러그인 권한이 필요합니다.
- 플러그인은 임시 읽기 전용 접근이 있는 content URI를 허용하며 원본에 쓰지 않습니다.
- 쓰기 및 영구 grant는 거부됩니다. prefix 접근은 플레이어로 전달되지 않습니다.
- 별도의 ACTION_VIEW 진입점은 읽기 전용 동영상 content URI만 허용하고 수신한 extras와 ClipData를 폐기합니다.
- 재생에 실패하면 다른 앱으로 열기 작업이 읽기 전용 Intent를 다시 만들고 이 플러그인을 제외합니다.
- 실제 디코딩은 Media3 extractor와 기기에서 사용할 수 있는 코덱에 따라 달라집니다.
