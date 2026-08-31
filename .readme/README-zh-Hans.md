<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Ember Player</h1>

  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="3-Ember Player icon" border="0" width="128" />
  </p>

  <p>文件管理器插件. 直接播放视频文件</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Video-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Video-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Video-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/.readme/README-ar.md)

******

### 简介

******

3-Ember Player 是 AutoJs6 文件管理器的视频播放插件, 同时也是一个可独立使用的本地视频播放器. 在文件管理器中点击视频文件即可全屏播放; 手势控制, 倍速, 外挂字幕, 同目录连播, 画中画等主流播放器的核心体验一应俱全. 播放能力基于 AndroidX Media3 ExoPlayer 构建.

插件坚持只读安全模型: 视频通过临时只读授权进入播放器, 全程不申请存储权限, 不修改也不移动任何源文件; 网络访问仅用于检查插件更新.

******

### 功能亮点

******

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
- 主题随心: 由一个主题色生成亮暗两套界面配色, 默认跟随 AutoJs6, 另有 19 个预置色与带实时预览的自定义 RGB 颜色.
- 独立可用: 自带桌面入口与设置页, 可通过系统文件选择器直接打开视频, 也可作为系统 "打开方式" 中的视频播放器.
- 显示与输出工具: 查看 HDR10/HLG/SDR 与可用色彩信息并一键复制, 可选择把字幕合入可分享截图, 在会话内镜像画面, 或在风险提示后使用最高 +15 dB 音量增强.
- 无需手势也能完整操作: TalkBack 下具名控件保持可用, 每种手势都有按钮或菜单等价入口, 布局适配 200% 字体与显示缩放, 并支持键盘/DPAD 焦点, 空格/回车, 快进快退及 MediaSession 媒体键.
- 可选后台听音: 默认关闭且仅在授予通知权限后启用; 画中画不可用时会把现有播放器移交给媒体播放前台服务并提供通知控制, 播完或关闭设置即停止. 画中画始终优先.
- 只读安全: 不申请存储权限, 绝不写入源视频; 联网仅用于检查更新.

******

### 安装与使用

******

开始前请确认以下环境要求:

```text
host app: AutoJs6 (org.autojs.autojs6)
minimum host build: 5276 (AutoJs6 6.8.0+)
minimum android: 7.0 (API 24)
plugin package: io.github.supermonster003.autojs6.plugin.videoplayer
```

从安装到播放第一个视频共 4 步:

1. 下载并安装本插件 APK. 安装后桌面会出现 3-Ember Player 图标, 插件能力则由 AutoJs6 统一管理.
2. 打开 AutoJs6, 进入 `插件中心`, 找到 `3-Ember Player` 并启用.
3. 在 AutoJs6 文件管理器中定位任意视频文件 (如 `movie.mp4`).
4. 点击该文件, 视频随即全屏开始播放.

不依赖宿主的用法: 从桌面直接打开 3-Ember Player, 点按 `打开视频` 并通过系统文件选择器挑选视频即可播放; 其他应用发起的视频查看请求也可以选择本播放器承接. 上述环境要求中的宿主版本仅约束文件管理器入口, 独立播放不受影响.

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

当前实现基于 explorer-action 协议 v12: 注册只读主动作 `play-video` 与选择工具栏动作 `play-video-selection`, 通过 `video/*` 及面向旧宿主的 23 种精确扩展名接收视频. 主动作可使用有界同级读取构建同目录队列和发现字幕; 选择动作严格保留宿主给出的 1 至 128 个目标且永不启用同级读取. 兼容会话还可携带播放进度, 缺少任何可选能力时均安全降级. 音频与图像仍是独立插件, 不在本 APK 中.

完整宿主协同能力需要 AutoJs6 6.8.0 (build 5276) 或更高版本及 Explorer Action v12; 后续插件更新不会提高此要求.

******

### 开发路线图

******

已完成能力与后续计划以可勾选清单维护在 Roadmap.md 中. 未勾选条目表示规划意向, 不代表当前版本已具备的能力.

- [查看可勾选的 Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/Roadmap.md)

******

### 版本记录

******

#### v3.0.0

###### 2026/08/31

* `新增` 完成播放页可访问性审计: 控件具备名称与动态状态, TalkBack 触摸探索时不自动隐藏, 每种手势都有按钮或菜单等价入口, 焦点顺序确定, 并通过 200% 字体与显示缩放验证
* `新增` 支持键盘与遥控器: 清晰焦点环, DPAD 导航, 空格/回车播放暂停, 左右键按已配置的 5/10/30 秒步长跳转, 媒体键交由 MediaSession 处理
* `新增` 新增默认关闭的可选后台听音: 授予通知权限后, 画中画不可用时可将当前播放无缝移交给媒体播放前台服务并通过通知控制
* `优化` 画中画始终优先于后台听音; 关闭设置, 播放完成, 发生错误或主动退出播放都会停止服务, 点击通知则以相同播放器和连续进度返回播放页
* `优化` 耳机行为遵循 Media3: 单击切换播放暂停, 外接设备双击在队列存在下一项时前进, 显式 Previous 媒体命令返回上一项; 不强加非标准三击计时

#### v2.3.0

###### 2026/08/31

* `新增` 视频信息现在可标注 HDR10,HLG 或 SDR,并在可得时显示色彩空间,范围与位深;支持一键复制全部字段,显示设备未报告支持片源 HDR 格式时会给出一次性提示
* `新增` 新增默认关闭的`截图包含字幕`设置,可将当前可见字幕绘制到截图;保存 PNG 后可直接通过`分享`发送到其他应用
* `新增` 新增仅当前会话生效的画面镜像,支持左右,上下或双轴翻转,并与双指缩放及屏幕旋转保持一致组合
* `新增` 新增仅当前会话生效的 +3 dB 至 +15 dB 音量增强,默认关闭,首次启用前提示失真与听力风险,设备不支持时自动退回关闭
* `优化` 截图继续作为独立 PNG 通过 MediaStore 保存,无需存储权限;分享时只向目标应用授予所选图片的只读访问
* `优化` 评估后采用 LoudnessEnhancer:它能在不修改媒体文件的前提下提供有上限的播放增益;设备效果创建失败时会立即释放并安全回退

#### v2.2.0

###### 2026/08/31

* `新增` 新增 Host Session 播放队列面板:展示文件名,已知时长或占位,高亮当前项,点按跳转,并实时联动顺序,随机与单项循环
* `新增` 新增只读文件管理器动作`播放所选`:按宿主选择顺序播放同一父目录内 1 至 128 个受支持视频,且不发现同级文件
* `新增` 队列自动连播前保留当前末帧并显示可取消的 3 秒下一项提示,控制锁定状态不会被解除
* `新增` 新增默认关闭的`记住播放模式`设置,可跨播放会话保留顺序,随机或单项循环
* `优化` 每个所选目标都会分别核对 content URI,ClipData 位置,MIME,元数据,大小与父目录;ID 或 URI 重复时整组拒绝
* `优化` 多选仅通过有界 Host Session 路由读取宿主明确授权的目标,不扫描同级视频或字幕

##### 完整记录

* [CHANGELOG-zh-Hans.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

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
