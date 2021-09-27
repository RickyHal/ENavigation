package com.ricky.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.PathConfig
import com.ricky.enavigation.api.anno.HostAndPathAnno

@HostAndPathAnno(PathConfig.App.Test1.PATH)
class TestActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ricky.base.R.layout.empty_activity)
    }
}