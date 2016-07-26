package wang.yuchao.android.library.process.foregroundjudger;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by wangyuchao on 16/7/25.
 */
public class ForegroundJudgerLibrary {

    private static int appCount = 0;

    public static void init(Application application) {
        appCount = 0;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                appCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
                appCount--;
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }


    /**
     * 注意：5.0此方法被废弃
     * 需要权限：android.permission.GET_TASKS
     *
     * @return 应用是否在前台
     */
    public static boolean isForegroundFromRunningTaskInfo(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
            if (tasks != null && (!tasks.isEmpty())) {
                ComponentName topActivity = tasks.get(0).topActivity;
                if (topActivity != null && (TextUtils.equals(topActivity.getPackageName(), packageName))) {
                    return true;//此时一定在前台
                }
            }
        }
        return false;
    }


    /**
     * 注意：当service常驻后台时候，此方法失效【在小米 Note上此方法无效，在Nexus上正常】
     *
     * @return 应用是否在前台
     */
    public static boolean isForegroundFromRunningAppProcessInfo(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && TextUtils.equals(appProcess.processName, packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 注意：不能判断其他应用是否位于前台
     *
     * @return 应用是否在前台
     */
    public static boolean isForegroundFromApplication() {
        return appCount > 0;
    }

    /**
     * 注意：5.0以上有效
     * 1. AndroidManifest中加入此权限
     * 2. 打开手机设置，点击安全-高级，在有权查看使用情况的应用中，为这个App打上勾
     *
     * @return 应用是否在前台
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static boolean isForegroundFromUsageState(Context context, String packageName) {

        if (Build.VERSION.SDK_INT < 21) {
            return false;
        }

        class RecentUseComparator implements Comparator<UsageStats> {
            @Override
            public int compare(UsageStats lhs, UsageStats rhs) {
                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
            }
        }
        RecentUseComparator mRecentComp = new RecentUseComparator();
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 10, ts);
        if (usageStats == null || usageStats.size() == 0) {
            if (!hasPermission(context)) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Toast.makeText(context, "请打开权限！", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        Collections.sort(usageStats, mRecentComp);
        String currentTopPackage = usageStats.get(0).getPackageName();
        return TextUtils.equals(currentTopPackage, packageName);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean hasPermission(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }


    /**
     * 注意：由于AccessibilityService回调会在Activity onResume执行完了以后才会执行。因此在onResume中使用这个方法无效。
     * 1. 创建 ACCESSIBILITY SERVICE INFO 属性文件
     * 2. 注册 SERVICE 到 AndroidManifest.xml
     *
     * @return 应用是否在前台
     */
    public static boolean isForegroundFromAccessibilityService(Context context, String packageName) {
        if (isAccessibilitySettingsOn(context)) {
            return TextUtils.equals(packageName, ForegroundAccessibilityService.getForegroundPackageName());
        } else {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "请打开辅助功能开关！", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    /**
     * 此方法用来判断当前应用的辅助功能服务是否开启
     */
    private static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }
        return false;
    }
}
