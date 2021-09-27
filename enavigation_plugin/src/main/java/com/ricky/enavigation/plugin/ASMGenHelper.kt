package com.ricky.enavigation.plugin

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

/**
 *
 * @author RickyHal
 * @date 2021/9/22
 */
object ASMGenHelper {
    fun getBytes(moduleList: List<String>, interceptorList: List<String>): ByteArray {
        val cw = ClassWriter(ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
        cw.visit(
            Opcodes.V1_7, Opcodes.ACC_PUBLIC or Opcodes.ACC_SUPER,
            "com/ricky/enavigation/support/ASMUtil", null,
            "java/lang/Object", null
        )
        writeStructureMethod(cw)
        writeModuleNameMethod(cw, moduleList, interceptorList)
        writeApplicationMethod(cw, moduleList)
        writeInterceptorMethod(cw, interceptorList)
        cw.visitEnd()
        return cw.toByteArray()
    }

    private fun writeStructureMethod(cw: ClassVisitor) {
        val methodVisitor = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null)
        methodVisitor.visitCode()
        val label0 = Label()
        methodVisitor.visitLabel(label0)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        methodVisitor.visitInsn(Opcodes.RETURN)
        val label1 = Label()
        methodVisitor.visitLabel(label1)
        methodVisitor.visitLocalVariable("this", "Lcom/ricky/enavigation/support/ASMUtil;", null, label0, label1, 0)
        methodVisitor.visitMaxs(1, 1)
        methodVisitor.visitEnd()
    }

    private fun writeModuleNameMethod(cw: ClassVisitor, moduleList: List<String>, interceptorList: List<String>) {
        val methodVisitor = cw.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC,
            "getModuleNameList", "()Ljava/util/List;", "()Ljava/util/List<Ljava/lang/String;>;", null
        )
        methodVisitor.visitCode()
        // 开始的标记
        val labelStart = Label()
        // 标记开始
        methodVisitor.visitLabel(labelStart)
        // 创建 对象 : List<String> result = new ArrayList<>();
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/util/ArrayList")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false)
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 0)

        // 开始的标记
        val label1 = Label()
        for (name in (moduleList + interceptorList).toSet()) {
            // 拿到名称
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitLdcInsn(name)
            methodVisitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true)
            methodVisitor.visitInsn(Opcodes.POP)
        }
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitInsn(Opcodes.ARETURN)

        // 结束的标记
        val labelEnd = Label()
        methodVisitor.visitLabel(labelEnd)
        methodVisitor.visitLocalVariable("result", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/String;>;", label1, labelEnd, 0)
        methodVisitor.visitMaxs(2, 2)
        methodVisitor.visitEnd()
    }

    private fun writeApplicationMethod(cw: ClassVisitor, moduleList: List<String>) {
        val methodVisitor = cw.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, "getPathClassByHost",
            "(Ljava/lang/String;)Lcom/ricky/enavigation/api/IPathGenerated;", null, null
        )
        methodVisitor.visitCode()
        // 开始的标记
        val labelStart = Label()
        // 标记开始
        methodVisitor.visitLabel(labelStart)
        for (name in moduleList) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            val ifJumpLabel = Label()
            // 拿到名称
            val classPathName = "com/ricky/enavigation/impl/path/${name}PathGenerated"
            methodVisitor.visitLdcInsn(name)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false)
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, ifJumpLabel)
            methodVisitor.visitTypeInsn(Opcodes.NEW, classPathName)
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, classPathName, "<init>", "()V", false)
            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitLabel(ifJumpLabel)
        }
        methodVisitor.visitInsn(Opcodes.ACONST_NULL)
        methodVisitor.visitInsn(Opcodes.ARETURN)

        // 结束的标记
        val labelEnd = Label()
        methodVisitor.visitLabel(labelEnd)
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0)
        methodVisitor.visitEnd()
    }

    private fun writeInterceptorMethod(cw: ClassVisitor, interceptorList: List<String>) {
        val methodVisitor = cw.visitMethod(
            Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, "getInterceptorClassByHost",
            "(Ljava/lang/String;)Lcom/ricky/enavigation/api/IInterceptorGenerated;",
            null,
            null
        )
        methodVisitor.visitCode()
        // 开始的标记
        val labelStart = Label()
        // 标记开始
        methodVisitor.visitLabel(labelStart)
        for (name in interceptorList) {
            // 除了第一个后面的都有前一个 if 跳转过来的 label
            val ifJumpLabel = Label()
            // 拿到名称
            val classPathName = "com/ricky/enavigation/impl/interceptor/${name}InterceptorGenerated"
            methodVisitor.visitLdcInsn(name)
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
            methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equalsIgnoreCase", "(Ljava/lang/String;)Z", false)
            methodVisitor.visitJumpInsn(Opcodes.IFEQ, ifJumpLabel)
            methodVisitor.visitTypeInsn(Opcodes.NEW, classPathName)
            methodVisitor.visitInsn(Opcodes.DUP)
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, classPathName, "<init>", "()V", false)
            methodVisitor.visitInsn(Opcodes.ARETURN)
            methodVisitor.visitLabel(ifJumpLabel)
        }
        methodVisitor.visitInsn(Opcodes.ACONST_NULL)
        methodVisitor.visitInsn(Opcodes.ARETURN)

        // 结束的标记
        val labelEnd = Label()
        methodVisitor.visitLabel(labelEnd)
        methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, labelStart, labelEnd, 0)
        methodVisitor.visitEnd()
    }
}