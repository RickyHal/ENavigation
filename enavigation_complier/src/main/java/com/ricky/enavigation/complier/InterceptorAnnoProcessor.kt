package com.ricky.enavigation.complier

import androidx.annotation.Keep
import com.google.auto.service.AutoService
import com.ricky.enavigation.api.anno.GlobalInterceptorAnno
import com.ricky.enavigation.api.anno.InterceptorAnno
import com.ricky.enavigation.complier.bean.GlobalInterceptorAnnoBean
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

@AutoService(Processor::class)
class InterceptorAnnoProcessor : BaseProcessor() {
    private val globalInterceptors = mutableListOf<GlobalInterceptorAnnoBean>()
    private val interceptorMap: HashMap<String, String> = HashMap()
    private val navigationClassName = ClassName("${packageName}.api", "INavigationInterceptor")

    override fun getClassName(): String {
        return "${moduleName.capitalize()}InterceptorGenerated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(InterceptorAnno::class.java.canonicalName, GlobalInterceptorAnno::class.java.canonicalName)
    }

    override fun onProcess(env: RoundEnvironment) {
        env.getElementsAnnotatedWith(GlobalInterceptorAnno::class.java)?.let {
            parseGlobalInterceptorAnno(it)
        }
        env.getElementsAnnotatedWith(InterceptorAnno::class.java)?.let {
            parseInterceptorAnno(it)
        }
    }

    override fun generateFile(): FileSpec {
        return FileSpec.builder("$packageName.impl.interceptor", getClassName())
            .addType(
                TypeSpec.classBuilder(getClassName())
                    .addSuperinterface(ClassName("$packageName.api", "IInterceptorGenerated"))
                    .addAnnotation(Keep::class)
                    .addProperty(
                        PropertySpec.builder("host", String::class, KModifier.OVERRIDE)
                            .initializer("\"$moduleName\"")
                            .build()
                    )
                    .addFunction(buildGlobalInterceptorFunc())
                    .addFunction(buildInterceptorFunc())
                    .addFunction(buildFindInterceptorFunc())
                    .build()
            )
            .build()
    }

    private fun parseGlobalInterceptorAnno(elements: MutableSet<out Element>) {
        elements.forEach {
            val annotation: GlobalInterceptorAnno = it.getAnnotation(GlobalInterceptorAnno::class.java)
            globalInterceptors.add(GlobalInterceptorAnnoBean(it.toString(), annotation.priority))
        }
        globalInterceptors.sortByDescending { it.priority }
    }

    private fun parseInterceptorAnno(elements: MutableSet<out Element>) {
        elements.forEach {
            val annotation: InterceptorAnno = it.getAnnotation(InterceptorAnno::class.java)
            val name: String = annotation.name
            interceptorMap[name] = it.toString()
        }
    }

    private fun buildGlobalInterceptorFunc(): FunSpec {
        val returnType = MutableList::class.asClassName().parameterizedBy(navigationClassName)
        return FunSpec.builder("getGlobalInterceptors")
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("val globalInterceptors = mutableListOf<INavigationInterceptor>()")
            .apply {
                val codeStr = StringBuilder()
                for (interceptor in globalInterceptors) {
                    codeStr.append("globalInterceptors.add(${interceptor.className}())\n")
                }
                addCode(buildCodeBlock {
                    add(codeStr.toString())
                })
            }
            .addStatement("return globalInterceptors")
            .returns(returnType)
            .build()
    }

    private fun buildInterceptorFunc(): FunSpec {
        val returnType = HashMap::class.asClassName()
            .parameterizedBy(
                String::class.asClassName(),
                navigationClassName
            )
        return FunSpec.builder("getInterceptors")
            .addModifiers(KModifier.OVERRIDE)
            .addStatement("val interceptorMap = HashMap<String, INavigationInterceptor>()")
            .apply {
                val codeStr = StringBuilder()
                for ((key, value) in interceptorMap) {
                    codeStr.append("interceptorMap.put(\"${key.capitalize()}\",$value())\n")
                }
                addCode(buildCodeBlock {
                    add(codeStr.toString())
                })
            }
            .addStatement("return interceptorMap")
            .returns(returnType)
            .build()
    }

    private fun buildFindInterceptorFunc(): FunSpec {
        return FunSpec.builder("getInterceptorByName")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("name", String::class)
            .addStatement("val interceptors =  getInterceptors() ")
            .addStatement("val interceptor = interceptors.get(name.capitalize())")
            .addStatement("return interceptor")
            .returns(navigationClassName.copy(nullable = true))
            .build()
    }
}