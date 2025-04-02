package org.cdc.framework.utils;

import java.io.File;

public class FileUtils {
    public static void deleteNonEmptyDirector(File directory) {
        try {
            //�г������е������ļ�
            File[] files = directory.listFiles();

            //��Ŀ¼��ɾ��ÿ���ļ�
            for (File file : files) {
                System.out.println(file + " deleted.");
                file.delete();
            }

            //ɾ��Ŀ¼
            if (directory.delete()) {
                System.out.println("Ŀ¼��ɾ��");
            } else {
                System.out.println("Directory not Found");
            }

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
