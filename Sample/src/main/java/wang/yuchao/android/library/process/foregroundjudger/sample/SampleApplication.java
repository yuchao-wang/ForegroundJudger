package wang.yuchao.android.library.process.foregroundjudger.sample;

import android.app.Application;

import wang.yuchao.android.library.process.foregroundjudger.ForegroundJudgerLibrary;

/**
 * Created by wangyuchao on 16/7/25.
 */
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ForegroundJudgerLibrary.init(this);
    }
}
