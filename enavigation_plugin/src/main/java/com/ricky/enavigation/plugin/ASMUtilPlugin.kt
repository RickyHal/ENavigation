package com.ricky.enavigation.plugin

import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * 自动注册插件
 * @author RickyHal
 * @date 2021/9/22
 */
class ASMUtilPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val android = project.extensions.findByType(BaseExtension::class.java)
        android?.registerTransform(ASMUtilTransform())
    }
}