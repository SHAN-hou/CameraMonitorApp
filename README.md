# Camera Monitor - 专业摄影监视器模拟APP

一款运行在Android系统上的手机APP，模拟/实现专业摄影监视器的核心界面与功能。

## 功能特性

### 主界面布局
- **左侧主菜单** - 垂直排列的三级菜单（显示/功能/设置）
- **中间显示区域** - 1920x1080模拟画面，支持测试图、相册图片
- **底部快捷工具栏** - 三个核心快捷功能

### 底部快捷键
1. **画幅框** - 叠加不同比例的参考框（16:9、4:3、1.85:1、2.35:1）
2. **单色模式** - 切换为红/绿/蓝单色画面，辅助判断曝光
3. **斑马纹** - 模拟专业监视器的过曝提示功能（80 IRE / 100 IRE）

### 显示菜单
- 亮度、对比度、饱和度、色温、音量调节

### 功能菜单
- **对焦辅助** - 边缘检测高亮显示对焦区域
- **直方图** - RGB三色直方图实时显示
- **网格线** - 三分线/十字线辅助构图
- **中心标记** - 画面中心十字标记
- **安全区** - Action Safe (90%) / Title Safe (80%)

### 设置菜单
- 从相册加载图片
- 恢复测试图案
- 恢复默认设置

## 技术栈

- **语言**: Kotlin
- **最低SDK**: Android 7.0 (API 24)
- **目标SDK**: Android 14 (API 34)
- **构建工具**: Gradle 8.5 + AGP 8.2.0
- **CI/CD**: GitHub Actions 自动编译

## 下载APK

前往 [Releases](../../releases) 页面下载最新APK。

每次推送到 `main` 分支会自动构建，推送 `v*` 标签会自动创建Release。

## 构建方法

```bash
# 克隆项目
git clone https://github.com/YOUR_USERNAME/CameraMonitorApp.git
cd CameraMonitorApp

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK
./gradlew assembleRelease
```

## 项目结构

```
app/src/main/
├── java/com/tianshuo/cameramonitor/
│   ├── MainActivity.kt          # 主Activity
│   └── view/
│       ├── AspectRatioOverlayView.kt  # 画幅框覆层
│       ├── ZebraOverlayView.kt        # 斑马纹覆层
│       ├── GridOverlayView.kt         # 网格线覆层
│       ├── CenterMarkerView.kt        # 中心标记
│       ├── SafeAreaOverlayView.kt     # 安全区覆层
│       ├── FocusPeakingView.kt        # 对焦辅助覆层
│       └── HistogramView.kt           # 直方图
└── res/
    ├── layout/activity_main.xml
    ├── drawable/
    └── values/
```

## License

MIT License
