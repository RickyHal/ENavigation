package com.ricky.enavigation.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.CRC32
import java.util.zip.ZipEntry

/**
 *
 * @author RickyHal
 * @date 2021/9/22
 */
class ASMUtilTransform : Transform() {
    private val pathList = mutableListOf<String>()
    private val interceptorList = mutableListOf<String>()
    private val implOutputPath = "com/ricky/enavigation/impl"
    private val asmImplClassPath = "com/ricky/enavigation/support/ASMUtil.class"

    override fun getName(): String {
        return "ASMUtilTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        filterAllNames(transformInvocation)
        try {
            transformInvocation?.inputs?.forEach { input ->
                for (jarInput in input.jarInputs) {
                    transformJar(transformInvocation, jarInput)
                }
                for (directoryInput in input.directoryInputs) {
                    transformDirectory(transformInvocation, directoryInput)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace(System.out)
            throw e
        }
    }

    private fun transformDirectory(transformInvocation: TransformInvocation, directoryInput: DirectoryInput) {
        val dest: File = transformInvocation.outputProvider.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes, directoryInput.scopes,
            Format.DIRECTORY
        )
        directoryInput.file.copyRecursively(dest, true)
    }

    private fun transformJar(transformInvocation: TransformInvocation, jarInput: JarInput) {
        val dest: File = transformInvocation.outputProvider.getContentLocation(
            jarInput.file.absolutePath,
            jarInput.contentTypes,
            jarInput.scopes,
            Format.JAR
        )
        val destJarFile = File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "_" + jarInput.file.name)
        if (destJarFile.exists()) {
            destJarFile.delete()
        }
        val jarFile = JarFile(jarInput.file)
        val jarEntryEnumeration = jarFile.entries()
        val jarOutputStream = JarOutputStream(
            FileOutputStream(destJarFile)
        )
        while (jarEntryEnumeration.hasMoreElements()) {
            val jarEntry = jarEntryEnumeration.nextElement()
            val entryName = jarEntry.name
            if (asmImplClassPath == entryName) {
                val bytes: ByteArray = ASMGenHelper.getBytes(pathList, interceptorList)
                val asmUtiZipEntry = ZipEntry(jarEntry.name)
                asmUtiZipEntry.size = bytes.size.toLong()
                val crc = CRC32()
                crc.update(bytes)
                asmUtiZipEntry.crc = crc.value
                jarOutputStream.putNextEntry(asmUtiZipEntry)
                jarOutputStream.write(bytes)
            } else {
                val zipEntry = ZipEntry(jarEntry)
                zipEntry.compressedSize = -1
                jarOutputStream.putNextEntry(zipEntry)
                val inputStream = jarFile.getInputStream(zipEntry)
                readAndWrite(inputStream, jarOutputStream)
                inputStream.close()
            }
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        destJarFile.copyTo(dest, true)
        destJarFile.delete()
    }

    private fun readAndWrite(inputStream: InputStream, out: OutputStream) {
        val buff = ByteArray(1024)
        var len: Int
        while (inputStream.read(buff).also { len = it } != -1) {
            out.write(buff, 0, len)
        }
    }

    private fun filterAllNames(invocation: TransformInvocation?) {
        pathList.clear()
        interceptorList.clear()
        invocation?.inputs?.forEach { input ->
            input.directoryInputs.forEach { directoryInput ->
                filterDirectory(invocation, directoryInput.file)
            }
            input.jarInputs.forEach(::filterJar)
        }
    }

    private fun filterDirectory(invocation: TransformInvocation, dir: File) {
        dir.listFiles()?.iterator()?.forEach { file ->
            if (file.isDirectory) {
                filterDirectory(invocation, file)
            } else {
                val filePath = file.path.replace('\\', '/')
                if (filePath.isNotEmpty() && filePath.contains(implOutputPath)) {
                    val className: String = filePath.substring(filePath.indexOf(implOutputPath))
                    filterPathAndInterceptor(className)
                }
            }
        }
    }

    private fun filterJar(jarInput: JarInput) {
        val destJarFile = File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + "_" + jarInput.file.name)
        if (destJarFile.exists()) {
            destJarFile.delete()
        }
        val jarFile = JarFile(jarInput.file)
        val jarEntryEnumeration = jarFile.entries()
        while (jarEntryEnumeration.hasMoreElements()) {
            val jarEntry = jarEntryEnumeration.nextElement()
            val entryName = jarEntry.name
            val className = entryName.replace('\\', '/')
            filterPathAndInterceptor(className)
        }
    }

    private fun filterPathAndInterceptor(className: String) {
        when {
            isPathClass(className) -> {
                pathList.add(className.getHost())
            }
            isInterceptorClass(className) -> {
                interceptorList.add(className.getInterceptor())
            }
        }
    }

    private fun String.getHost(): String {
        val simpleName: String = substring(lastIndexOf("/") + 1)
        return simpleName.substring(0, simpleName.indexOf("PathGenerated"))
    }

    private fun String.getInterceptor(): String {
        val simpleName: String = substring(lastIndexOf("/") + 1)
        return simpleName.substring(0, simpleName.indexOf("InterceptorGenerated"))
    }

    private fun isPathClass(name: String): Boolean {
        return name.startsWith("$implOutputPath/path") && name.endsWith("PathGenerated.class")
    }

    private fun isInterceptorClass(name: String): Boolean {
        return name.startsWith("$implOutputPath/interceptor") && name.endsWith("InterceptorGenerated.class")
    }
}