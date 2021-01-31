package com.zk.utools.utils;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class FiltrateFileUtil {
    public static void main(String[] args) {
        int splitSize = 100;
        File[] str = filtrateFile("data/hzyh_eids.txt", splitSize);
        deleteFile(str, "data//out/result.txt", splitSize);
    }

    /**
     * 对拆分之后保存相同字符的小文件进行整理
     *
     * @param smallFiile  拆分相同字符的文件
     * @param deleteFiile 需要去除重复的文件
     * @param spliteSize  自定义拆分多少个文件
     */
    public static void deleteFile(File[] smallFiile, String deleteFiile, int spliteSize) {
        File repeatFile = new File("data//out//repeat.txt");
        File file = new File(deleteFiile);
        FileReader[] readers = new FileReader[spliteSize];
        BufferedReader[] str = new BufferedReader[spliteSize];
        PrintWriter id = null;
        PrintWriter rid = null;
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            id = new PrintWriter(file);
            rid = new PrintWriter(repeatFile);
            Set<String> unFile = new HashSet<String>();
            for (int i = 0; i < spliteSize; i++) {
                if (smallFiile[i].exists()) {
                    System.out.println("开始去除重复行:" + smallFiile[i].getName());
                    readers[i] = new FileReader(smallFiile[i]);
                    str[i] = new BufferedReader(readers[i]);
                    String line = null;
                    while ((line = str[i].readLine()) != null) {
                        if (unFile.contains(line)) {
                            rid.println(line);
                        }
                        if (line != "") {
                            unFile.add(line);
                        }
                    }
                    for (String c : unFile) {
                        id.println(c);
                    }
                    unFile.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < spliteSize; i++) {
                if (str[i] != null) {
                    try {
                        str[i].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (readers[i] != null) {
                    try {
                        readers[i].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (smallFiile[i].exists()) {
                    smallFiile[i].delete();
                }
            }
            if (id != null) {
                id.close();
            }
        }
    }


    /**
     * 拆分大文件 防止爱jvm内存溢出
     *
     * @param anyFile   需要去除重复的文件
     * @param splitFile 拆分之后保存字符相同的文件
     * @return 返回拆分之后的文件
     */
    public static File[] filtrateFile(String anyFile, int splitFile) {
        BufferedReader reader = null;
        File file = new File(anyFile);
        PrintWriter[] id = new PrintWriter[splitFile];
        File[] smallFiile = new File[splitFile];
        //获取文件路径的父级路径
        String parentPath = file.getParent();
        //创建拆分后的临时文件
        File temporaryFile = new File(parentPath + File.separator + "temporary");
        if (!temporaryFile.exists()) {
            temporaryFile.mkdir();
        }
        for (int i = 0; i < splitFile; i++) {
            //获取拆分后小文件的绝对路径
            smallFiile[i] = new File(temporaryFile.getAbsolutePath() + File.separator + i + "." + "txt");
            if (smallFiile[i].exists()) {
                smallFiile[i].delete();
            }
            try {
                //写入相同字符到 小文件中
                id[i] = new PrintWriter(smallFiile[i]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            reader = new BufferedReader(new FileReader(file));
            String temporary = null;
            try {
                while ((temporary = reader.readLine()) != null) {
                    temporary = temporary.trim();
                    if (temporary != "") {
                        //将读取hashCode字符相同的字符都保存到同一个文件中
                        int readLine = Math.abs(temporary.hashCode() % splitFile);
                        id[readLine].println(temporary);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < splitFile; i++) {
                if (id[i] != null) {
                    id[i].close();
                }
            }
        }
        return smallFiile;
    }

}
