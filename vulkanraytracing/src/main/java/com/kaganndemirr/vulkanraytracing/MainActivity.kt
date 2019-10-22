package com.kaganndemirr.vulkanraytracing

import android.app.NativeActivity
import android.os.Bundle

class MainActivity: NativeActivity(){

    init {
        System.loadLibrary("native-lib")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}