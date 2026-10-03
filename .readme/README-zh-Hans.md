<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-three-amber-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>支持播放列表, 字幕与后台音频的视频播放器</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/.readme/README-ar.md)

******

### 简介

******

3-Amber Player 是 AutoJs6 文件管理器的视频播放插件, 同时也是一个可独立使用的本地视频播放器. 在文件管理器中点击视频文件即可全屏播放; 手势控制, 倍速, 外挂字幕, 同目录连播, 画中画等主流播放器的核心体验一应俱全. 播放能力基于 AndroidX Media3 ExoPlayer 构建.

3-Amber Player 4.0 使用新应用 ID io.github.supermonster003.autojs6.plugin.three.amber.player. Android 将其作为独立应用安装, 旧播放器设置不会自动迁移, 旧应用可以保留并存

插件坚持只读安全模型: 视频通过临时只读授权进入播放器, 全程不申请存储权限, 不修改也不移动任何源文件; 网络访问仅用于检查插件更新.

******

### 功能亮点

******

- 本地播放列表: 支持 M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL; 保留顺序, 标题和重复项. 宿主入口读取同目录文件; 独立入口可选择列表文件夹以读取相对路径. 一次打开一个列表, 最多 128 项; 不支持网络地址, HLS 与嵌套列表. [格式与使用说明](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/docs/Playlists.md).
- 点击即播: 在 AutoJs6 文件管理器中点击视频文件, 直接进入全屏沉浸式播放, 无需任何配置.
- 顺手的手势: 左半屏滑动调亮度, 右半屏调音量, 横向滑动快进快退, 双击两侧跳转, 双击中央播放/暂停, 长按临时 2 倍速, 还可一键锁定屏蔽误触.
- 倍速与画面控制: 0.25× 至 3× 共 9 档倍速, 双指捏合缩放画面 (0.25× 至 4×), 适应/填充/裁剪三种缩放模式, 屏幕方向一键切换并按画面比例自动建议.
- 同目录连播: 打开一个视频即生成自然排序队列; 队列面板展示当前项与已知时长, 支持点按跳转及顺序/随机/单项循环切换, 自动接续前还会显示可取消的 3 秒提示.
- 宿主选择顺序队列: 只读`播放所选`动作可将同一目录内 1 至 128 个视频严格按宿主选择顺序排队, 不发现同级文件; 可选设置还能跨会话记住播放模式.
- 外挂与内嵌字幕: 自动发现同名 .srt / .ass 或手动加载单个字幕, 探测常见旧编码, 调整字幕样式与 ±600 秒外挂字幕偏移, 并支持内嵌字幕与多音轨切换; 字幕默认关闭, 由用户显式开启.
- 精细查看: 暂停时可逐帧后退或前进并长按连发, 还可设置或清除 A-B 区间循环, 方便反复查看细节.
- 断点续播: 记住最近一个未看完视频的播放位置, 重新打开自动续播; 看完即清, 不留观看痕迹.
- 画中画与系统集成: Android 8.0+ 播放中离开应用自动进入小窗, 支持耳机与蓝牙控制, 媒体通知显示标题与进度.
- 睡眠定时: 15/30/45/60 分钟或播完停止, 到点自动暂停播放.
- 拖动预览: 拖动进度条时显示目标时间气泡, 并尽力提供画面缩略图预览.
- 当前帧截图: Android 10+ 一键将当前画面保存为 PNG 到系统相册, 无需存储权限, 不改动源视频.
- 疑难格式兜底: 内置轻量 XVID-in-MKV 兼容层; 设备无法解码时给出明确提示, 并可一键转交其他播放器.
- 统一语言, 夜间模式, 主题色与启动器图标设置, 选择后须确认保存, 支持 16 种预设色与 HEX/RGB 局部预览. 应用外观默认跟随 AutoJs6, 宿主不可用时安全回退.
- 独立可用: 自带桌面入口与设置页, 可通过系统文件选择器直接打开视频, 也可作为系统 "打开方式" 中的视频播放器.
- 显示与输出工具: 查看 HDR10/HLG/SDR 与可用色彩信息并一键复制, 可选择把字幕合入可分享截图, 在会话内镜像画面, 或在风险提示后使用最高 +15 dB 音量增强.
- 无需手势也能完整操作: TalkBack 下具名控件保持可用, 每种手势都有按钮或菜单等价入口, 布局适配 200% 字体与显示缩放, 并支持键盘/DPAD 焦点, 空格/回车, 快进快退及 MediaSession 媒体键.
- 可选后台听音: 默认关闭且仅在授予通知权限后启用; 画中画不可用时会把现有播放器移交给媒体播放前台服务并提供通知控制, 播完或关闭设置即停止. 画中画始终优先.
- 只读安全: 不申请存储权限, 绝不写入源视频; 联网仅用于检查更新.
- 设置页提供自适应亮色, 自适应暗色, 自适应自动 (默认)与透明背景四种启动器图标. 自动配色与透明效果取决于启动器, 部分系统可能缓存图标或添加背景. 切换后部分主屏幕快捷方式可能需要重新添加.

******

### 安装与使用

******

开始前请确认以下环境要求:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.three.amber.player
```

从安装到播放第一个视频共 4 步:

1. 下载并安装本插件 APK. 安装后桌面会出现 3-Amber Player 图标, 插件能力则由 AutoJs6 统一管理.
2. 打开 AutoJs6, 进入 `插件中心`, 找到 `3-Amber Player` 并启用.
3. 在 AutoJs6 文件管理器中定位任意视频文件 (如 `movie.mp4`).
4. 点击该文件, 视频随即全屏开始播放.

不依赖宿主的用法: 从桌面直接打开 3-Amber Player, 点按 `打开视频` 并通过系统文件选择器挑选视频即可播放; 其他应用发起的视频查看请求也可以选择本播放器承接. 上述环境要求中的宿主版本仅约束文件管理器入口, 独立播放不受影响.

播放页手势速查:

- 单击画面: 显示或隐藏控制栏.
- 双击中央: 播放/暂停; 双击左右两侧: 后退/前进 10 秒 (可在设置中改为 5/10/30 秒).
- 左半屏上下滑动: 调节亮度; 右半屏上下滑动: 调节音量.
- 横向滑动: 预览快进/快退目标位置, 松手生效.
- 长按画面: 临时 2 倍速播放, 松手恢复原倍速.
- 双指捏合: 缩放画面 (0.25× 至 4×); 缩放状态下双击可复位.
- 锁定按钮: 屏蔽全部手势与控件防误触; 锁定后单击屏幕浮现解锁按钮.

******

### 支持的格式

******

文件管理器中的播放动作精确匹配以下 23 种扩展名:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

以上是面向旧版宿主保留的精确扩展名白名单; 新版宿主识别为视频的任何文件均可通过 `video/*` 交给本插件. 命中列表不代表在每台设备上都能解码, 实际播放能力取决于 Media3 与设备平台解码器. 独立入口与外部应用调用同样按 `video/*` MIME 类型接收请求.

******

### 常见问题

******

**点击视频文件后没有用本播放器打开?**

请依次检查: AutoJs6 版本代码是否不低于 5276 (对应 6.8.0 及以上版本); 插件是否已在 `插件中心` 启用; 该文件是否被宿主识别为视频. 三者任一不满足, 点击都不会由本插件承接.

**没有安装或已停用本插件时, 点击视频会怎样?**

兼容宿主会显示恢复引导, 提示安装或启用插件; 只有当用户明确选择 `用其他应用打开` 时, 才会弹出系统应用候选菜单, 交给设备上的其他播放器.

**播放时只有声音没有画面, 或提示无法播放?**

视频能否解码取决于设备平台与 Media3 的解码能力, 命中扩展名列表不代表一定可以播放. 对常见的 XVID-in-MKV 旧格式, 插件已内置兼容层交给系统 MPEG-4 Part 2 解码器处理; 仍无法解码时会显示明确提示, 可点按 `使用其他应用打开` 转交其他播放器.

**怎么连续播放同一文件夹里的视频?**

在满足宿主版本要求的前提下, 从文件管理器打开任意视频即自动生成同目录队列: 按自然文件名排序, 从当前视频开始, 播完自动接续下一个, 并提供顺序, 随机与单项循环模式. 宿主版本过旧或从独立入口打开时, 保持单文件播放.

**外挂字幕怎么加载?**

把 `.srt` 或 `.ass` 字幕文件与视频放在同一目录并保持同名 (允许 `movie.zh.srt` 这类语言后缀), 从文件管理器打开视频后即可在字幕菜单中选择加载. 字幕默认关闭, 不会自动开启.

**续播功能记录了什么? 会上传吗?**

只记录最近一个未播完视频的位置, 内容仅为文件的 SHA-256 摘要与时间数值, 不含文件名与路径; 打开其他视频或播放完毕都会立即清除. 所有数据仅保存在本机, 不会上传.

**插件需要哪些权限?**

不申请存储, 相机, 麦克风等任何敏感运行时权限. 仅声明网络权限用于检查插件更新 (用户手动触发或每日至多一次), 以及受宿主签名保护的插件权限用于文件管理器入口.

**能不能脱离 AutoJs6 独立使用?**

可以. 自 v2.0.0 起插件拥有桌面入口: 打开后通过系统文件选择器挑选视频即可播放, 也可以在其他应用的 `打开方式` 中选择本播放器. 同目录连播, 外挂字幕发现与跟随宿主设置等能力则需要配合 AutoJs6 使用.

******

### 安全

******

插件按默认拒绝原则构建, 以下措施全部默认开启且无法关闭:

- 零敏感权限: 不申请存储或其他运行时权限; 网络访问仅用于用户触发或每日一次的 GitHub 发行版检查.
- 绝不写入: 播放, 截图, 缩略图提取全程只读, 任何情况下不修改, 不移动, 不删除源视频.
- 入口逐项校验: 文件管理器入口受签名级插件权限保护, 每个请求的协议版本, 目标 URI, ClipData, 元数据, 宿主构建版本与只读授权都会逐项核验, 任一不符即拒绝.
- 有界的同目录访问: 队列与字幕发现经由宿主管理的短期会话完成, 仅能枚举选中文件的直接同级, 禁止递归目录, 写入与持久授权; 播放器内部只接收经校验的有界队列, 不接触文件系统路径.
- 双入口相互隔离: 面向系统的 `ACTION_VIEW` 入口仅接受只读 content URI 的 `video/*` 请求, 拒绝写入, 持久与前缀授权, 与文件管理器入口彼此独立.
- 续播最小化: 续播历史仅保留最近一条未播完记录, 内容为 SHA-256 摘要与时间数值, 播完即清.
- 安全转交: `使用其他应用打开` 会重新构建只读 Intent 并自动排除本插件, 避免授权扩散与自我循环.

******

### 插件接口 (面向开发者)

******

宿主通过以下标识发现并调用插件:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: three-amber-player
source namespace: io.github.supermonster003.autojs6.plugin.three.amber.player
stable application id: io.github.supermonster003.autojs6.plugin.three.amber.player
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

当前实现基于 explorer-action 协议 v12: 注册只读主动作 `play-video` 与选择工具栏动作 `play-video-selection`, 通过 `video/*` 及面向旧宿主的 23 种精确扩展名接收视频. 主动作可使用有界同级读取构建同目录队列和发现字幕; 选择动作严格保留宿主给出的 1 至 128 个目标且永不启用同级读取. 兼容会话还可携带播放进度, 缺少任何可选能力时均安全降级. 音频与图像仍是独立插件, 不在本 APK 中.

完整宿主协同能力需要 AutoJs6 6.8.0 (build 5276) 或更高版本及 Explorer Action v12; 后续插件更新不会提高此要求.

******

### 开发路线图

******

已完成能力与后续计划以可勾选清单维护在 Roadmap.md 中. 未勾选条目表示规划意向, 不代表当前版本已具备的能力.

- [查看可勾选的 Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/Roadmap.md)

******

### 版本记录

******

#### v4.0.0

###### 2026/09/30

* `提示` 3-Amber Player 4.0 使用新应用 ID io.github.supermonster003.autojs6.plugin.three.amber.player. Android 将其作为独立应用安装, 旧播放器设置不会自动迁移, 旧应用可以保留并存
* `新增` 设置页提供自适应亮色, 自适应暗色, 自适应自动 (默认)与透明背景四种启动器图标. 自动配色与透明效果取决于启动器, 部分系统可能缓存图标或添加背景. 切换后部分主屏幕快捷方式可能需要重新添加
* `新增` 统一语言, 夜间模式, 主题色与启动器图标设置, 选择后须确认保存, 支持 16 种预设色与 HEX/RGB 局部预览. 应用外观默认跟随 AutoJs6, 宿主不可用时安全回退
* `修复` 校正 Amber 图案视觉偏左, 对启动器及透明图标应用光学居中, 并将偏移纳入所有安全区校验
* `优化` 采用中性灰阶底色与清晰可读的主题色 Material 3 控件和对话框, 统一间距, 线性图标与分割线. 启动器图标默认改为自动, 升级保留明确选择并修复重复入口
* `优化` 启动器与插件中心图标按统一视觉尺寸标准调整, 插件中心采用透明背景和黑白或中性灰阶图案

#### v3.2.0

###### 2026/09/29

* `新增` 设置页提供自适应亮色, 自适应暗色 (默认), 自适应自动与透明背景四种启动器图标. 自动配色与透明效果取决于启动器, 部分系统可能缓存图标或添加背景. 切换后部分主屏幕快捷方式可能需要重新添加
* `修复` 校正 Ember 图案视觉偏左, 对启动器及透明图标应用光学居中, 并将偏移纳入所有安全区校验

#### v3.1.2

###### 2026/09/29

* `修复` Android 17 后台音频交接时, 播放界面取消共用通知导致前台服务状态丢失的问题
* `修复` 媒体控制器停止播放后, 后台播放服务仍保持运行的问题
* `修复` AGP 9.1 构建时的 SDK XML v4 解析警告, 以及 JVM 单元测试误触发 APK 原生库对齐检查的问题 (共享构建插件 1.8.3)
* `优化` 适配 Android 17 (SDK 37) 目标版本及后台音频播放前台服务要求
* `优化` 统一 Three 系列启动器图标为浅色图案配深色背景, 插件中心与应用内图案随应用主题切换并保持透明背景, 避免部分设备出现启动器背景套环

##### 完整记录

* [CHANGELOG-zh-Hans.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 构建:

```powershell
.\gradlew.bat :app:assembleRelease
```

构建参数来自 `version.properties`, 当前最低 SDK 为 24, 目标 SDK 为 36.

******

### 资源结构

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 提供插件信息与播放器界面的本地化, `plugin_instruction.md` 提供宿主侧展示的使用说明. 全部 README 与 CHANGELOG 由 `.python/generate_markdown.py` 依据 JSON 源生成: 修改文档时请编辑 `.readme` 与 `.changelog` 下的 `lang_*.json` 并重新运行脚本, 不要直接编辑生成的 Markdown 文件.

******

### 相关链接

******

- AutoJs6 文档: https://docs.autojs6.com
- AndroidX Media3 ExoPlayer (播放引擎): https://developer.android.com/media/media3/exoplayer
- Android 安全文件共享: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Amber-Player/blob/master/docs/16kb.md)
