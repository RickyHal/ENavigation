package com.ricky.enavigation.support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ricky.enavigation.api.IInterceptorGenerated;
import com.ricky.enavigation.api.IPathGenerated;

import java.util.ArrayList;
import java.util.List;

/**
 * ASM字节码插入生成的类，用于自动注册
 *
 * @author haiyanghou
 * @date 2021/9/24
 */
public class ASMUtil {
    @NonNull
    public static List<String> getModuleNameList() {
        List<String> result = new ArrayList<>();
        return result;
    }

    @Nullable
    public static IPathGenerated getPathClassByHost(@NonNull String host) {
        return null;
    }

    @Nullable
    public static IInterceptorGenerated getInterceptorClassByHost(@NonNull String host) {
        return null;
    }
}
