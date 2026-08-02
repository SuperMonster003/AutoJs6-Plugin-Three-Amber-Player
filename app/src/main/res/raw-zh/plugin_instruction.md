# 视频播放器

视频播放器为 AutoJs6 文件浏览器提供视频只读主动作. 插件使用 AndroidX Media3 ExoPlayer 和 PlayerView, 自动开始播放, 处理音频焦点与嘈杂输出变化, 并恢复播放位置和播放状态.

插件需要 AutoJs6 build 5269+. 安装插件并保持启用, 可信且兼容后, 匹配的视频文件将在此播放器中打开. 如果插件缺失或不可用, AutoJs6 将回退到 Android ACTION_VIEW 路径. 音频播放与图像查看仍是独立的插件能力.

文件浏览器扩展名:

- MP4, MPEG4, MPG4, AVI, MKV, MOV, FLV, WEBM, M4V, 3GP, MPEG, 3G2, 3GP2, 3GPP, F4V, M2T, M2TS, MTS, TS, MPG, MPE, VOB, QT.

安全和隐私限制:

- 文件浏览器执行需要签名级 AutoJs6 插件权限.
- 插件接受具有临时只读访问权的 content URI, 且绝不写入源文件.
- 拒绝写入和持久授权. 前缀访问权绝不会转发到播放器.
- 独立 ACTION_VIEW 入口仅接受只读视频 content URI, 并丢弃传入 extras 与 ClipData.
- 播放失败后, 使用其他应用打开会重新构建只读 Intent 并排除本插件.
- 实际解码能力取决于 Media3 提取器和设备可用编解码器.
