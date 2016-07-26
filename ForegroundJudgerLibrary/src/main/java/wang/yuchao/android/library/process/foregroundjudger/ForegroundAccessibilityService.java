package wang.yuchao.android.library.process.foregroundjudger;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by wangyuchao on 16/7/26.
 */
public class ForegroundAccessibilityService extends AccessibilityService {

    private static String foregroundPackageName;

    public static String getForegroundPackageName() {
        return foregroundPackageName;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            foregroundPackageName = event.getPackageName().toString();
        }
    }

    @Override
    public void onInterrupt() {
    }
}
