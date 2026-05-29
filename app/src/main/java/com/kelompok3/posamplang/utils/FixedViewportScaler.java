package com.kelompok3.posamplang.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ScrollView;

public final class FixedViewportScaler {

    private static final int DESIGN_WIDTH_DP = 1280;
    private static final int DESIGN_HEIGHT_DP = 800;

    private FixedViewportScaler() { }

    public static void apply(Activity activity) {
        FrameLayout content = activity.findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return;
        }

        View original = content.getChildAt(0);
        if (original instanceof FrameLayout && "fixed_viewport_wrapper".equals(original.getTag())) {
            return;
        }

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int designWidth = dp(metrics, DESIGN_WIDTH_DP);
        int designHeight = dp(metrics, DESIGN_HEIGHT_DP);
        float scale = Math.min(metrics.widthPixels / (float) designWidth,
                metrics.heightPixels / (float) designHeight);

        if (scale >= 1f) {
            return;
        }

        content.removeView(original);
        int scaledWidth = Math.round(designWidth * scale);
        int scaledHeight = Math.round(designHeight * scale);
        int left = Math.max(0, (metrics.widthPixels - scaledWidth) / 2);
        int top = Math.max(0, (metrics.heightPixels - scaledHeight) / 2);

        FrameLayout wrapper = new FrameLayout(activity);
        wrapper.setTag("fixed_viewport_wrapper");
        wrapper.setClipChildren(false);
        wrapper.setClipToPadding(false);

        FrameLayout viewport = new FrameLayout(activity);
        viewport.setPivotX(0f);
        viewport.setPivotY(0f);
        viewport.setScaleX(scale);
        viewport.setScaleY(scale);
        viewport.setClipChildren(false);
        viewport.setClipToPadding(false);

        viewport.addView(original, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        FrameLayout.LayoutParams viewportParams = new FrameLayout.LayoutParams(
                designWidth, designHeight, Gravity.TOP | Gravity.START);
        viewportParams.leftMargin = left;
        viewportParams.topMargin = top;
        wrapper.addView(viewport, viewportParams);
        content.addView(wrapper, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public static int responsiveDialogWidth(Activity activity, int preferredWidthDp) {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int preferredWidth = dp(metrics, preferredWidthDp);
        int horizontalMargin = dp(metrics, 24) * 2;
        return Math.min(preferredWidth, Math.max(0, metrics.widthPixels - horizontalMargin));
    }

    public static int responsiveDialogHeight(Activity activity, int preferredHeightDp) {
        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int preferredHeight = dp(metrics, preferredHeightDp);
        int verticalMargin = dp(metrics, 24) * 2;
        return Math.min(preferredHeight, Math.max(0, metrics.heightPixels - verticalMargin));
    }

    public static void showResponsiveDialog(Activity activity, Dialog dialog,
                                            int preferredWidthDp, int preferredHeightDp) {
        dialog.show();
        makeDialogScrollable(dialog, responsiveDialogHeight(activity, preferredHeightDp));
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    responsiveDialogWidth(activity, preferredWidthDp),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public static void makeDialogScrollable(Dialog dialog) {
        makeDialogScrollable(dialog, 0);
    }

    public static void makeDialogScrollable(Dialog dialog, int maxHeightPx) {
        FrameLayout content = dialog.findViewById(android.R.id.content);
        if (content == null || content.getChildCount() == 0) {
            return;
        }

        View original = content.getChildAt(0);
        if (original instanceof ScrollView || "dialog_scroll_wrapper".equals(original.getTag())) {
            return;
        }

        content.removeView(original);
        ScrollView scrollView = new MaxHeightScrollView(dialog.getContext(), maxHeightPx);
        scrollView.setTag("dialog_scroll_wrapper");
        scrollView.setFillViewport(false);
        scrollView.setClipToPadding(false);
        scrollView.addView(original, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        content.addView(scrollView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private static int dp(DisplayMetrics metrics, int value) {
        return Math.round(value * metrics.density);
    }

    private static class MaxHeightScrollView extends ScrollView {
        private final int maxHeightPx;

        MaxHeightScrollView(Context context, int maxHeightPx) {
            super(context);
            this.maxHeightPx = maxHeightPx;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (maxHeightPx > 0) {
                int heightMode = MeasureSpec.getMode(heightMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                int cappedHeight = heightMode == MeasureSpec.UNSPECIFIED || heightSize == 0
                        ? maxHeightPx
                        : Math.min(heightSize, maxHeightPx);
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(cappedHeight, MeasureSpec.AT_MOST);
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
