package com.winlator.inputcontrols;

import android.view.View;

import timber.log.Timber;

public class InputCaptureManager {
    private final View targetView;

    private boolean captureEnabled = false;
    private boolean pointerCaptureRequested = true;

    public InputCaptureManager(View targetView) {
        this.targetView = targetView;
    }

    public boolean isCaptureEnabled() {
        return captureEnabled;
    }

    public void setCaptureEnabled(boolean captureEnabled) {
        this.captureEnabled = captureEnabled;
    }

    public boolean isPointerCaptureRequested() {
        return pointerCaptureRequested;
    }

    public void setPointerCaptureRequested(boolean pointerCaptureRequested) {
        this.pointerCaptureRequested = pointerCaptureRequested;
    }

    public void onWindowFocusChanged(@SuppressWarnings("unused") boolean hasFocus) {
        refreshPointerCapture();
    }

    public void onAttachedToWindow() {
        enablePointerCapture();
    }

    public void onDetachedFromWindow() {
        disablePointerCapture();
    }

    public void refreshPointerCapture() {
        boolean shouldCapture = targetView.hasFocus() && pointerCaptureRequested;
        boolean hasCapture = targetView.hasPointerCapture();

        if (shouldCapture && !hasCapture) {
            enablePointerCapture();
            setPointerCaptureRequested(true);
        } else if (!shouldCapture && hasCapture) {
            disablePointerCapture();
            setPointerCaptureRequested(false);
        }
    }

    public void togglePointerCapture() {
        if (!captureEnabled) return;
        if (targetView.hasPointerCapture()) {
            disablePointerCapture();
            setPointerCaptureRequested(false);
        } else {
            enablePointerCapture();
            setPointerCaptureRequested(true);
        }
    }

    public void disablePointerCapture() {
        if (!captureEnabled) return;
        if (!targetView.hasPointerCapture() || !pointerCaptureRequested) {
            Timber.tag("TouchpadView").v("Pointer capture: Pointer capture not detected, skipped");
            return;
        }
        targetView.releasePointerCapture();
        Timber.tag("TouchpadView").v("Pointer capture: Pointer capture release (state=%s).", targetView.hasPointerCapture());
    }

    public void enablePointerCapture() {
        if (!captureEnabled) return;
        if (!targetView.hasFocus() && !targetView.requestFocus()) {
            Timber.tag("TouchpadView").w("Pointer capture: Unable to request pointer capture, view is unfocused and cannot regain focus!");
            return;
        }
        if (targetView.hasPointerCapture() && !pointerCaptureRequested) {
            Timber.v("Pointer capture: Pointer capture already requested, skipped");
            return;
        }
        targetView.requestPointerCapture();
        Timber.tag("TouchpadView").v("Pointer capture: Pointer capture request (state=%s).", targetView.hasPointerCapture());
    }
}
