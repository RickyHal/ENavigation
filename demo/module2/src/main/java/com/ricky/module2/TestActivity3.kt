package com.ricky.module2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.PathConfig
import com.ricky.enavigation.api.anno.HostAndPathAnno

@HostAndPathAnno(PathConfig.Module2.Test3.PATH)
class TestActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test3)
    }
}