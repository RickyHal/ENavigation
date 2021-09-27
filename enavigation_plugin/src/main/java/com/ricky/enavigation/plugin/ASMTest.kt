package com.ricky.enavigation.plugin

import org.objectweb.asm.util.ASMifier

/**
 * 生成ASM代码
 * @author RickyHal
 * @date 2021/9/24
 */
fun test() {
    ASMifier.main(arrayOf("./ASMUtil.class"))
}