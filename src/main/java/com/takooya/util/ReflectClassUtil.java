package com.takooya.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ReflectClassUtil {
    /**
     * 获取同一路径下所有子类或接口实现类(未使用)
     *
     * @param cal 目的类
     * @return 类类型集合
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public List<Class<?>> getAllAssignedClass(Class<?> cal)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> c : getClasses(cal)) {
            if (cal.isAssignableFrom(c) && !cal.equals(c)) {
                classes.add(c);
            }
        }
        return classes;
    }

    /**
     * 取得目的类路径下的所有类
     *
     * @param cls 目的类
     * @return 类类型集合
     * @throws ClassNotFoundException ClassNotFoundException
     */
    public static List<Class<?>> getClasses(Class<?> cls) throws ClassNotFoundException {
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        ClassLoader classloader = Thread.currentThread()
                .getContextClassLoader();
        URL url = classloader.getResource(path);
        if (url != null) {
            return getClasses(new File(url.getFile()), pk);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 迭代查找类
     *
     * @param dir 目标文件夹
     * @param pk  文件名 不包含后缀名
     * @return 类类型集合
     * @throws ClassNotFoundException ClassNotFoundException
     */
    private static List<Class<?>> getClasses(File dir, String pk)
            throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!dir.exists()) {
            return classes;
        }
        for (File f : Objects.requireNonNull(dir.listFiles())) {
            if (f.isDirectory()) {
                classes.addAll(getClasses(f, pk + "." + f.getName()));
            }
            String name = f.getName();
            if (name.endsWith(".class")) {
                classes.add(Class.forName(pk + "."
                        + name.substring(0, name.length() - 6)));
            }
        }
        return classes;
    }

}