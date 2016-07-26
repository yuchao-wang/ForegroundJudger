

最近在做一个小东西用到了这个功能，因此整理了一下。

## Tags
Android Foreground Background Judger Process

## Android Foreground Judger

- min sdk 15(Android 4.0.3)

### Demo

[Download](https://codeload.github.com/yuchao-wang/ForegroundJudger/zip/master)

![pic is here](https://github.com/yuchao-wang/ForegroundJudger/blob/master/image/screenshot.png)

### Update
#### 1.0.0 (2016/07/26)
- Basic Function

### How To Use
**dependence**
```
compile 'wang.yuchao.android.library.view.circleprogressbar:CircleProgressBarLibrary:1.0.0'
```

**Application init**
```
ForegroundJudgerLibrary.init(this);
```

**use it in java**
```
        boolean[] result = new boolean[6];
        result[0] = ForegroundJudgerLibrary.isForegroundFromRunningTaskInfo(this, getPackageName());
        result[1] = ForegroundJudgerLibrary.isForegroundFromRunningAppProcessInfo(this, getPackageName());
        result[2] = ForegroundJudgerLibrary.isForegroundFromApplication();
        result[3] = ForegroundJudgerLibrary.isForegroundFromUsageState(this, getPackageName());
        result[4] = ForegroundJudgerLibrary.isForegroundFromAccessibilityService(this, getPackageName());
        // result[5] 请看Sample
```

### Proguard

```
-keep class wang.yuchao.android.library.** { *; }
-dontwarn wang.yuchao.android.library.**
```

### 说明

#### 方法一:RunningTask
- 优点
	+ 不需要权限
	+ 可以判断其他应用
- 缺点：
	+ 5.0此方法被废弃

#### 方法二：RunningProcess
- 优点
	+ 不需要权限
	+ 可以判断其他应用
- 缺点：
	+ 当App存在后台常驻的Service时失效

#### 方法三:ActivityLifecycleCallbacks
- 优点
	+ 不需要权限
- 缺点：
	+ 不能判断其他应用

#### 方法四:UsageStatsManager
- 优点
	+ 可以判断其他应用
- 缺点：
	+ 需要用户手动授权
	+ 只针对5.0以上，5.0以下无效

#### 方法五：AccessibilityService
- 优点
	+ 无需权限
	+ 可以判断其他应用
- 缺点：
	+ 需要用户手动授权

#### 方法六：读取Linux日志信息（hacker）
- 优点
	+ 无需权限
	+ 可以判断其他应用
- 缺点：
	+ 耗时

#### 我的选择
- 判断个人应用是否位于前台，推荐方法三
- 判断其他应用是否位于前台，推荐方法五	
- 未来趋势：谷歌推荐方法四

### Thanks

- [wenmingvs](https://github.com/wenmingvs/AndroidProcess)
- [jaredrummler](https://github.com/jaredrummler/AndroidProcesses)
- [hatewx Blog](http://effmx.com/articles/tong-guo-android-fu-zhu-gong-neng-accessibility-service-jian-ce-ren-yi-qian-tai-jie-mian/)
- [hatewx Github](https://github.com/hatewx/AndroidProcess)

### [About Me](http://yuchao.wang)

### License

```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```