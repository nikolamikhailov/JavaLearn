package ru.l4gunner4l.javalearn.mainactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ru.l4gunner4l.javalearn.R;

/**
 * Activity with Bottom NavigationView (Profile, Lessons, Shop).
 * It is host for fragments
 *
 * Экран с нижней навигацией (профилем, уроками, магазином).
 * Является хостом для фрагментов
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
