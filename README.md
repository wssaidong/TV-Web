# TV-Web

Android TV 浏览器应用，支持 D-pad 遥控器导航，可浏览网页。

## 功能特性

- URL 直接跳转或搜索
- 前进/后退/刷新导航
- D-pad 遥控器适配
- 竖屏横屏自适应

## 构建

```bash
./gradlew assembleDebug
```

## 安装

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 技术栈

- Kotlin + Android SDK 34
- WebView (AndroidX WebKit)
- Material Design 3
