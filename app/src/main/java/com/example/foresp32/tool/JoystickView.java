package com.example.foresp32.tool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.foresp32.R;

public class JoystickView extends View {
    private Drawable baseDrawable;
    private int baseProportion;
    private Drawable capDrawable;
    private int capProportion;
    private float centerX, centerY;
    private float baseRadius;
    private float hatRadius;
    private float joystickX, joystickY;

    public interface OnJoystickMoveListener {
        void onValueChanged(float xPercent, float yPercent);
    }
    private OnJoystickMoveListener moveListener;

    // 获取外部的反馈逻辑
    public void setOnJoystickMoveListener(OnJoystickMoveListener listener) {
        this.moveListener = listener;
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    // 从外界获取自定义
    private void init(Context context, AttributeSet attrs) {
        // 自定义样式
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.JoystickView);
            baseDrawable = a.getDrawable(R.styleable.JoystickView_joystickBase);
            capDrawable = a.getDrawable(R.styleable.JoystickView_joystickCap);
            baseProportion = a.getInteger(R.styleable.JoystickView_base_proportionOf_back, 90);
            capProportion =  a.getInteger(R.styleable.JoystickView_cap_proportionOf_base, 20);
            a.recycle(); // 规定动作：用完必须回收
        }

        // 错误检查
        if (baseProportion > 100) {baseProportion = 100;}
        if (capProportion > 100) {capProportion = 100;}

        // 默认样式
        if (baseDrawable == null) {
            GradientDrawable defaultBase = new GradientDrawable();
            defaultBase.setShape(GradientDrawable.OVAL); // 椭圆/圆
            defaultBase.setColor(Color.parseColor("#44000000")); // 半透明黑
            baseDrawable = defaultBase;
        }
        if (capDrawable == null) {
            GradientDrawable defaultCap = new GradientDrawable();
            defaultCap.setShape(GradientDrawable.OVAL);
            defaultCap.setColor(Color.parseColor("#3F51B5")); // 蓝色
            capDrawable = defaultCap;
        }
    }

    // 从外界获取长宽
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2f;
        centerY = h / 2f;
        baseRadius = Math.min(w, h) / 2f * ((float)baseProportion /100);
        hatRadius = baseRadius * ((float)capProportion /100);

        joystickX = centerX;
        joystickY = centerY;
    }

    // 画出来（指定边界）
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (baseDrawable != null) {
            baseDrawable.setBounds(
                    (int) (centerX - baseRadius),
                    (int) (centerY - baseRadius),
                    (int) (centerX + baseRadius),
                    (int) (centerY + baseRadius)
            );
            baseDrawable.draw(canvas);
        }

        if (capDrawable != null) {
            capDrawable.setBounds(
                    (int) (joystickX - hatRadius),
                    (int) (joystickY - hatRadius),
                    (int) (joystickX + hatRadius),
                    (int) (joystickY + hatRadius)
            );
            capDrawable.draw(canvas);
        }
    }

    // 动作逻辑
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        // 更新位置信息
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
        // 按下或者移动 -》 更新
            // 获取位置信息
            float touchX = event.getX();
            float touchY = event.getY();

            // 偏移量
            float dx = touchX - centerX;
            float dy = touchY - centerY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            // 越界检查
            if (distance > baseRadius) {
                float ratio = baseRadius / distance;
                joystickX = centerX + dx * ratio;
                joystickY = centerY + dy * ratio;
            } else {
                joystickX = touchX;
                joystickY = touchY;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
        // 抬手 -》 回收
            joystickX = centerX;
            joystickY = centerY;
        }

        // 回调传输位置信息
        if (moveListener != null) {
            float xPercent = (joystickX - centerX) / baseRadius;
            float yPercent = (joystickY - centerY) / baseRadius;
            moveListener.onValueChanged(xPercent, -yPercent);
        }

        // 通知系统重绘（这句极其重要！它会迫使系统再次调用onDraw，画面才会动）
        invalidate();
        return true; // 返回true表示这个View消费了触摸事件
    }
}