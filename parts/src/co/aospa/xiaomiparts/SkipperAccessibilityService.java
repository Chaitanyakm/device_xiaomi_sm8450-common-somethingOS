package co.aospa.xiaomiparts;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class SkipperAccessibilityService extends AccessibilityService {
    private static final String TAG = SkipperAccessibilityService.class.getSimpleName();

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (shouldProcess(event)) {
            processEventForAdSkipping(event);
        }
    }

    private boolean shouldProcess(AccessibilityEvent event) {
        return "com.google.android.youtube".equals(event.getPackageName().toString());
    }

    private void processEventForAdSkipping(AccessibilityEvent event) {
        AccessibilityNodeInfo source = event.getSource();
        if (source == null) return;

        try {
            AccessibilityNodeInfo adNode = findAdNode(source);
            if (adNode != null) {
                skipAd(adNode);
            }
        } finally {
            source.recycle();
        }
    }

    private AccessibilityNodeInfo findAdNode(AccessibilityNodeInfo root) {
        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/skip_ad_button");
        if (nodes == null || nodes.isEmpty()) return null;

        for (AccessibilityNodeInfo node : nodes) {
            if (node.isClickable() && node.isEnabled()) {
                return node;  // Found the clickable skip ad button
            }
        }

        return null;
    }

    private void skipAd(AccessibilityNodeInfo adNode) {
        if (adNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
            Log.i(TAG, "Ad skipped successfully.");
        } else {
            Log.i(TAG, "Failed to click skip ad button.");
        }
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "Service interrupted.");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Service unbound.");
        return super.onUnbind(intent);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = getServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_VISUAL;
        info.flags = AccessibilityServiceInfo.DEFAULT;
        setServiceInfo(info);

        Log.i(TAG, "Service connected.");
    }
}
