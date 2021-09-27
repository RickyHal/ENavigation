package com.ricky.enavigation.complier

import com.squareup.kotlinpoet.FileSpec
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 *
 * @author RickyHal
 * @date 2021/9/17
 */
abstract class BaseProcessor : AbstractProcessor() {
    protected val packageName = "com.ricky.enavigation"
    private var filer: Filer? = null
    private var messager: Messager? = null
    protected var moduleName: String = ""
    private var processed = false

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        filer = processingEnv?.filer
        processingEnv?.options?.get("host")?.let { moduleName = it.capitalize() }
        messager = processingEnv?.messager
        if (moduleName.isEmpty()) {
            messager?.printMessage(Diagnostic.Kind.ERROR, "ENavigation: Host is empty, please setup the host name in build.gradle.")
        }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(annotations: MutableSet<out TypeElement>?, env: RoundEnvironment?): Boolean {
        if (annotations == null || env == null) return false
        if (!processed) {
            onProcess(env)
            filer?.let { generateFile().writeTo(it) }
            processed = true
        }
        return true
    }

    protected fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    abstract fun onProcess(env: RoundEnvironment)
    abstract fun getClassName(): String
    abstract fun generateFile(): FileSpec
}