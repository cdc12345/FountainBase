package org.cdc.framework.utils;

import java.io.File;

public class FileUtils {
    public static void deleteNonEmptyDirector(File directory) {
        try {
            //列出数组中的所有文件
            File[] files = directory.listFiles();

            //从目录中删除每个文件
            for (File file : files) {
                System.out.println(file + " deleted.");
                file.delete();
            }

            //删除目录
            if (directory.delete()) {
                System.out.println("目录已删除");
            } else {
                System.out.println("Directory not Found");
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
