package com.ricky.module1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ricky.base.PathConfig
import com.ricky.enavigation.api.anno.HostAndPathAnno

@HostAndPathAnno(PathConfig.Module1.Test5.PATH)
class TestActivity5 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.ricky.base.R.layout.empty_activity)
    }

    override fun finish() {
        val intent = Intent()
        intent.putExtra(PathConfig.KEY_RESULT, "Hello world")
        setResult(Activity.RESULT_OK, intent)
        super.finish()
    }
}