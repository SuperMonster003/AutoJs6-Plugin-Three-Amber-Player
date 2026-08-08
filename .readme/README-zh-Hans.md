<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="video-player-ic-launcher" border="0" width="128" />
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

视频播放器通过临时只读 content URI 播放文件管理器或其他 Android 应用提供的视频内容. 插件使用 AndroidX Media3 ExoPlayer, 且绝不修改源视频.

******

### 功能

******

- 通过共享 `org.autojs.plugin.EXPLORER_ACTION` 协议的版本 2 注册单文件只读文件浏览器主动作.
- 使用 AndroidX Media3 ExoPlayer 和 PlayerView 提供自动播放, 标准控件, 音频焦点, 音频输出变为嘈杂环境时的处理及设备编解码器集成.
- 在界面重建后恢复播放位置和播放/暂停意图, 且仅在视频实际播放时保持屏幕常亮.
- 为带有 `video/*` MIME 类型的只读 `content://` URI 提供独立导出的 `android.intent.action.VIEW` 入口.
- 播放失败后提供安全的使用其他应用打开动作, 重新构建只读查看 Intent 并从候选列表排除本插件.

******

### 宿主集成

******

宿主将本插件用于视频主打开路径和播放动作.

安装插件并保持启用, 可信且兼容后, 打开匹配的视频将以文件浏览器主查看器方式启动动作 `play-video`.

如果插件缺失, 已禁用, 不可用, 不兼容或无法启动, 宿主将回退到系统 `android.intent.action.VIEW` 路径, 由其他已安装的视频应用处理文件.

本插件仅匹配视频文件. 音频播放与图像查看仍是独立的插件能力, 不包含在此 APK 中.

******

### 支持的格式

******

文件浏览器主动作的 MIME 匹配器为空, 且仅精确匹配以下 23 种宿主视频扩展名:

```text
MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT
```

******

### 插件接口

******

宿主通过以下标识发现并执行插件:

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
```

版本 1 为列出的视频扩展名提供只读文件浏览器主动作. 独立外部入口接受带有任意有效 `video/*` MIME 子类型的只读 content URI. 实际解码能力取决于 Media3 提取器和设备可用编解码器.

需要宿主构建版本 5269 或更高版本.

******

### 安全性

******

插件不请求存储或 INTERNET 权限. 受签名权限保护的文件浏览器边界会验证协议版本 2, 来源界面, 宿主构建版本, 目标与父级 content URI, 精确的 ClipData 顺序, 显示名称, 声明大小, MIME 类型, 扩展名和只读授权标志. 随后它为非导出播放器创建一个新的显式 Intent, 其中仅包含目标 URI, MIME 类型, 安全显示名称, 单个目标 ClipData 条目和读取授权. 公共 ACTION_VIEW 边界单独仅接受 content URI, 视频 MIME 类型和精确读取授权, 忽略所有传入 extras 与 ClipData, 并重新构建相同的最小内部请求.

******

### 安全限制

******

- 每个播放请求仅包含 1 个目标 content URI.
- 文件浏览器执行需要签名级 `org.autojs.permission.PLUGIN` 权限.
- 始终拒绝写入和持久授权. 用于验证文件浏览器父级 URI 的前缀访问权绝不会转发到播放器.
- 公共 ACTION_VIEW 边界拒绝写入, 持久和前缀授权.
- 外部回退候选应用会先被解析, 再筛选为其他软件包, 最后通过新建的只读 Intent 启动.
- 列出的扩展名不保证在每台设备上都能解码. 实际播放支持取决于 Media3 和已安装的平台编解码器.

******

### 版本历史

******

# v1.0.1

###### 2026/08/08

* `修复` 在插件中心启用时返回有效的 Explorer Action 服务绑定
* `优化` 精简插件名称和描述, 并使用户文档表述更自然

# v1.0.0

###### 2026/08/02

* `新增` 视频播放器插件, 插件 ID 为 `video-player`, 动作 ID 为 `play-video`, 引擎为 `explorer-action`, 变体为 `default`
* `新增` MIME 匹配器为空且仅按宿主当前 23 种视频扩展名精确匹配的协议 v2 只读文件浏览器主动作, 要求宿主构建版本 5269
* `新增` 受签名权限保护的文件浏览器入口, 严格验证目标与父级 content URI, ClipData, 来源, 显示名称, 大小, MIME 类型, 扩展名和授权, 随后以最小内容转发到非导出播放器
* `新增` 面向只读视频 content URI 的独立导出 ACTION_VIEW 入口, 丢弃不可信 extras 与 ClipData, 拒绝禁止的授权并防止自回环
* `新增` Media3 ExoPlayer 和 PlayerView 播放, 支持自动播放, 标准控件, 音频焦点, 音频输出变为嘈杂环境时的处理, 保存播放位置与播放状态, 以及仅在实际播放时保持屏幕常亮
* `新增` 播放失败后安全地使用其他应用打开, 通过新建只读 Intent 并明确排除本插件实现恢复
* `新增` 插件元数据, 界面文本, 使用说明, README 和 CHANGELOG 的多语言资源: 西班牙语/法语/俄语/阿拉伯语/日语/韩语/英语/简体中文/香港繁体/台湾繁体
* `依赖` 附加 AndroidX Media3 ExoPlayer 和 UI 版本 1.10.1
* `依赖` 附加 Kotlin Parcelize 运行时版本 2.2.21

##### 查看更多版本

* [CHANGELOG-zh-Hans.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Video-Player/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

```powershell
.\gradlew.bat :app:assembleDebug
```

发布构建:

```powershell
.\gradlew.bat :app:assembleRelease
```

构建参数来自 `version.properties`. 当前最低 SDK 为 24, 目标 SDK 为 36.

******

### 资源布局

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 为插件元数据和界面文本提供本地化. `plugin_instruction.md` 提供使用和安全说明. `.python/generate_markdown.py` 根据 JSON 源文件生成多语言 README 和更新日志.

******

### 链接

******

- AutoJs6 文档: https://docs.autojs6.com
- Android 安全文件共享: https://developer.android.com/training/secure-file-sharing
- AndroidX Media3 ExoPlayer: https://developer.android.com/media/media3/exoplayer
