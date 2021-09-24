package com.ricky.module1

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.PathConfig
import com.ricky.enavigation.api.anno.HostAndPathAnno

@HostAndPathAnno(PathConfig.Module1.Test4.PATH)
class TestActivity4 : AppCompatActivity() {

    private val text: String? by lazy { intent.getStringExtra(PathConfig.Module1.Test4.KEY_TEXT) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test4)
        findViewById<TextView>(R.id.tv_text).text = text
    }
}