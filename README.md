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

- 通过设备内置 MPEG-4 Part 2 解码器提供轻量 XVID-in-MKV 兼容; 系统解码器不可用或失败时明确提示并可转交其他播放器.
- 通过共享 `org.autojs.plugin.EXPLORER_ACTION` 协议 v12 注册只读文件浏览器主动作, 可选提供有界的直接同级读取与播放进度能力.
- 使用 AndroidX Media3 ExoPlayer 和 PlayerView 提供自动播放, 标准控件, 音频焦点, 音频输出变为嘈杂环境时的处理及设备编解码器集成.
- 在界面重建后恢复播放位置和播放/暂停意图, 且仅在视频实际播放时保持屏幕常亮.
- 为带有 `video/*` MIME 类型的只读 `content://` URI 提供独立导出的 `android.intent.action.VIEW` 入口.
- 播放失败后提供安全的使用其他应用打开动作, 重新构建只读查看 Intent 并从候选列表排除本插件.
- 全屏沉浸式播放, 支持手势调节亮度, 音量与播放进度, 双击跳转, 长按倍速与控制锁定.
- 0.25× 至 3× 倍速播放, 画面缩放模式与屏幕方向一键切换.
- 基于 content URI 摘要的本地播放进度记忆, 播完自动清除.
- 支持内嵌音轨与字幕选择, 字幕默认关闭, 不支持的轨道会清晰标记.
- 提供单曲循环, 详细媒体信息, 基于画面比例的方向建议及 API 26+ 画中画远程播放/暂停.
- 通过 MediaSession 支持耳机, 蓝牙与系统媒体控制, 并以媒体通知显示标题和播放进度.
- 提供 15/30/45/60 分钟与播完停止睡眠定时, 并持久保存手势灵敏度及 5/10/30 秒双击跳转设置.
- 支持 0.25× 至 4× 双指捏合缩放与双击复位, 拖动进度条时显示目标时间气泡及尽力提取的内存缩略图.
- Android 10+ 可免权限将当前画面通过 MediaStore 保存为独立 PNG, 且不修改源视频.
- 通过 Explorer Action v12 构建同目录视频自然排序队列, 支持上一个 / 下一个, 顺序, 随机, 单项循环及自动连播下一项.
- 发现同名 .srt / .ass 外挂字幕及语言后缀变体, 字幕默认关闭, 仅在用户显式选择后加载.
- 由宿主管理的可选续播历史默认关闭, 可在 AutoJs6 设置中关闭或清除, 且文件列表不显示已看标记.

******

### 宿主集成

******

宿主将本插件用于视频主打开路径和播放动作.

安装插件并保持启用, 可信且兼容后, 打开宿主识别为视频的任意文件都将以文件浏览器主查看器方式启动动作 `play-video`.

如果插件缺失, 已禁用, 未授权, 不可用, 不兼容或无法启动, 兼容宿主将显示恢复引导. 仅当用户明确选择 "用其他应用打开" 后才会显示系统应用候选菜单.

本插件仅匹配视频文件. 音频播放与图像查看仍是独立的插件能力, 不包含在此 APK 中.

******

### 支持的格式

******

文件浏览器主动作通过 `video/*` 接受宿主识别的所有视频类型, 并保留以下 23 种精确扩展名匹配器以兼容旧版宿主:

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
Explorer MIME types: video/*
external view action: android.intent.action.VIEW
external MIME type: video/*
required host build: 5276
```

版本 1.4 提供协议 v12 只读文件浏览器主动作. 兼容宿主可附加请求级直接同级读取与播放进度能力; 不支持这些可选扩展的宿主保留单文件播放. 独立外部入口仍仅接受带有效 `video/*` MIME 子类型的只读 content URI.

完整宿主协同能力需要 AutoJs6 6.8.0 build 5276 或更高版本及 Explorer Action v12; 后续插件能力不会提高此要求.

******

### 安全性

******

插件不请求存储或 INTERNET 权限. 受签名权限保护的文件浏览器边界会验证完整协议 v12 信封, 精确的单个选中目标, 父子关系, ClipData, 元数据, 宿主构建版本和只读授权. 可选 Host Session 由宿主绑定到插件 UID, 仅允许列出选中文件的直接父目录以及打开选中文件或可读直接同级文件. 私有播放器只接收经验证的有界不透明队列, 不接收文件系统路径. 公共 ACTION_VIEW 边界保持独立且仅支持单文件.

******

### 安全限制

******

- 文件浏览器始终从 1 个选中的 content URI 开始; 可选 Host Session 仅暴露可读直接同级文件, 绝不允许递归目录访问.
- 文件浏览器执行需要签名级 `org.autojs.permission.PLUGIN` 权限.
- 始终拒绝写入和持久授权. 用于验证文件浏览器父级 URI 的前缀访问权绝不会转发到播放器.
- 公共 ACTION_VIEW 边界拒绝写入, 持久和前缀授权.
- 外部回退候选应用会先被解析, 再筛选为其他软件包, 最后通过新建的只读 Intent 启动.
- 宿主播放历史默认关闭, 仅在用户显式启用后保存规范路径摘要与时间数值, 并可在 AutoJs6 设置中关闭或清除.
- 被识别为视频或命中列出的旧版扩展名均不保证在每台设备上都能解码. 实际播放支持取决于 Media3 和已安装的平台编解码器.

******

### 版本历史

******

# v1.4.0

###### 2026/08/28

* `新增` 通过 Explorer Action v12 请求级且绑定 UID 的 Host Session 构建同目录视频自然排序队列, 支持上一个 / 下一个, 顺序, 随机, 单项循环及自动连播下一项
* `新增` 发现同名 .srt / .ass 外挂字幕及语言后缀变体, 字幕默认关闭, 仅在用户显式选择后加载
* `新增` 新增由宿主管理的可选续播历史: 默认不记录, 可在 AutoJs6 设置中关闭或清除, 文件列表不显示已看标记
* `修复` 宿主已识别为视频的 Explorer 请求仍会因扩展名不在旧版 23 项白名单中而被拒绝的问题; 现在统一接受可信的 `video/*` 请求
* `优化` 同级访问仅限选中文件及可读的直接同级文件, 禁止递归目录, 写入, 持久授权, 且插件不保存明文路径
* `优化` 序列化队列限制为 128 个视频, 每个视频 8 个外挂字幕且字幕关联总数 128, 同时始终保留用户选中项
* `优化` 兼容要求固定为 AutoJs6 6.8.0 build 5276 与 Explorer Action v12; 不支持可选扩展的宿主安全保留单文件播放
* `依赖` 将内置 Explorer Action API 从协议 v2 升级到向后兼容的 v12 媒体会话扩展

# v1.3.1

###### 2026/08/27

* `修复` 新增轻量 XVID-in-MKV 兼容层: 将严格验证的 VFW/FourCC XVID 轨道交给设备内置 MPEG-4 Part 2 解码器, 不转码且不修改源文件
* `修复` 设备没有兼容系统解码器或解码失败时停止仅音频播放, 显示专项说明并提供使用其他应用打开

# v1.3.0

###### 2026/08/27

* `新增` 睡眠定时器: 可在 15, 30, 45 或 60 分钟后, 或当前视频播放结束时暂停, 并处理与单曲循环的模式冲突
* `新增` 画面交互: 0.25× 至 4× 双指捏合缩放, 缩放态双击复位, 并与现有画面缩放模式联动
* `新增` 拖动预览: 显示目标时间气泡及尽力提取的内存缩略图, 提取失败时静默回退为仅显示时间
* `新增` Android 10+ 当前帧截图通过 MediaStore 保存为独立 PNG, 无需存储权限且不修改源视频
* `新增` 持久化手势设置: 低/标准/高三档灵敏度及 5/10/30 秒双击跳转
* `优化` 睡眠截止时间使用系统运行时长计算, 不受墙上时钟变化影响, 并可随播放页状态重建恢复
* `优化` 缩略图提取以单一工作线程合并快速拖动请求, 过期的临时位图会被及时释放

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
