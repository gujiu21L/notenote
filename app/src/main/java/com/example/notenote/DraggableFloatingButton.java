package com.example.notenote;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DraggableFloatingButton extends View implements View.OnTouchListener {

    private int lastX, lastY;
    private boolean isDragging = false;

    public DraggableFloatingButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                isDragging = true;
                break;

            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    int deltaX = (int) event.getRawX() - lastX;
                    int deltaY = (int) event.getRawY() - lastY;

                    // 动态更新按钮位置
                    int newX = view.getLeft() + deltaX;
                    int newY = view.getTop() + deltaY;

                    // 更新布局参数
                    ViewGroup.LayoutParams params = view.getLayoutParams();
                    params.width = newX;
                    params.height = newY;
                    view.setLayoutParams(params);

                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                }
                break;

            case MotionEvent.ACTION_UP:
                isDragging = false;
                break;
        }

        return true;
    }
}
