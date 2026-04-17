package com.example.foresp32.ui;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foresp32.R;
import com.example.foresp32.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mianBing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 绑定
        mianBing = ActivityMainBinding.inflate(getLayoutInflater());

        //  设置屏幕适配
        this.initScreen();

        // 初始化
        this.setTipText("初始化看看");

        // 初始化摇杆
        this.setJoystick();

        // 初始化按键
        this.setMianBtn();
    }

    //  设置屏幕适配
    private void initScreen() {
        EdgeToEdge.enable(this);
        setContentView(mianBing.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // 设置提示文本
    private void setTipText(String tipString) {
        mianBing.tvTip.setText(tipString);
    }

    // 设置摇杆
    private void setJoystick() {
        mianBing.myJoystick.setOnJoystickMoveListener((x, y) -> {
            this.setTipText(String.format("X :%.2f Y:%.2f", x, y));
        });
    }

    // 设置按键
    private void setMianBtn() {
        mianBing.btnRotate.setOnClickListener( v -> {
            // 获取方向
                int curOrientation = getResources().getConfiguration().orientation;
                if(curOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    // 竖屏改横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                } else {
                    // 横屏改竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
        });
    }
}