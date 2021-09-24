package com.ricky.enavigation.complier

import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.ricky.enavigation.api.anno.HostAndPathAnno
import com.ricky.enavigation.complier.bean.NavigationAnnoBean
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.util.*
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException
import kotlin.collections.HashMap
import kotlin.reflect.KClass

@AutoService(Processor::class)
class PathAnnoProcessor : BaseProcessor() {
    private val pathMap = mutableMapOf<String, NavigationAnnoBean>()

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(HostAndPathAnno::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun onProcess(env: RoundEnvironment) {
        env.getElementsAnnotatedWith(HostAndPathAnno::class.java)?.filterIsInstance(TypeElement::class.java)?.let {
            parsePathAnno(it)
        }
    }

    override fun getClassName(): String {
        return "${moduleName.capitalize()}PathGenerated"
    }

    override fun generateFile(): FileSpec {
        return FileSpec.builder("$packageName.impl.path", getClassName())
            .addImport("kotlin.reflect", "KClass")
            .addImport("$packageName.bean", "NavigationBean")
            .addImport("$packageName.api", "INavigationInterceptor")
            .addType(
                TypeSpec.classBuilder(getClassName())
                    .addSuperinterface(ClassName("$packageName.api", "IPathGenerated"))
                    .addAnnotation(Keep::class)
                    .addProperty(
                        PropertySpec.builder("host", String::class, KModifier.OVERRIDE)
                            .initializer("\"$moduleName\"")
                            .build()
                    )
                    .addFunction(buildGetPathFunc())
                    .build()
            )
            .build()
    }

    private fun parsePathAnno(elements: List<TypeElement>) {
        elements.forEach {
            val annotation: HostAndPathAnno = it.getAnnotation(HostAndPathAnno::class.java)
            val interceptors = getInterceptorClassName(annotation) ?: emptyList()
            val navigationAnnoBean = NavigationAnnoBean(annotation.hostAndPath, it.toString(), interceptors, annotation.interceptorNames.toList(), annotation.desc)
            pathMap[annotation.hostAndPath] = navigationAnnoBean
        }
    }

    private fun getInterceptorClassName(anno: HostAndPathAnno): List<String>? {
        val implClassNames: MutableList<String> = ArrayList()
        try {
            implClassNames.clear()
            val interceptors: Array<KClass<*>> = anno.interceptors
            for (interceptor in interceptors) {
                implClassNames.add(interceptor.java.name)
            }
        } catch (e: MirroredTypesException) {
            implClassNames.clear()
            val typeMirrors = e.typeMirrors
            for (typeMirror in typeMirrors) {
                implClassNames.add(typeMirror.toString())
            }
        }
        return implClassNames
    }

    private fun buildGetPathFunc(): FunSpec {
        return FunSpec.builder("getPathMap")
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("val pathMap = HashMap<String, NavigationBean>()")
            .apply {
                val codeStr = StringBuilder()
                var index = 0
                for ((key, value) in pathMap) {
                    val interceptorNamesCode = StringBuilder("val stringInterceptors$index = mutableListOf<String>()\n")
                    for (name in value.interceptorNames) {
                        interceptorNamesCode.append("stringInterceptors$index.add(\"${name.capitalize()}\")\n")
                    }

                    val interceptorCode = StringBuilder("val classInterceptors$index = mutableListOf<INavigationInterceptor>()\n")
                    for (interceptor in value.interceptors) {
                        interceptorCode.append("classInterceptors$index.add($interceptor())\n")
                    }
                    val beanCode = """
                        val navigationBean$index = NavigationBean(
                             "${value.path.capitalize()}",
                             ${value.target}::class,
                             stringInterceptors$index,
                             classInterceptors$index,
                             "${value.desc}"
                        )
                        
                    """.trimIndent()
                    codeStr.append("\n/**-----------------path: ${key.capitalize()} -> [${value.target}]------------------ */\n")
                    codeStr.append(interceptorCode)
                    codeStr.append(interceptorNamesCode)
                    codeStr.append(beanCode)
                    codeStr.append("pathMap.put(\"${key.capitalize()}\", navigationBean$index)\n")
                    index++
                }
                addCode(buildCodeBlock {
                    add(codeStr.toString())
                })
            }
            .addStatement("return pathMap")
            .returns(
                HashMap::class.asClassName().parameterizedBy(String::class.asClassName(), ClassName("$packageName.bean", "NavigationBean"))
            )
            .build()
    }
}