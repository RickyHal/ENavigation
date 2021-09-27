package com.ricky.module1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.PathConfig
import com.ricky.enavigation.api.anno.HostAndPathAnno

@HostAndPathAnno(PathConfig.Module1.Test2.PATH)
class TestActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ricky.base.R.layout.empty_activity)
    }
}