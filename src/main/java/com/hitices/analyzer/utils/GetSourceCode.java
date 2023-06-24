package com.hitices.analyzer.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.hitices.analyzer.base.Service;
import com.hitices.analyzer.bean.MPathInfo;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GetSourceCode {

    private static Logger logger = LogManager.getLogger(GetSourceCode.class);

    private final static String CODE_DIWNLOAD_PATH  = "D:\\Program Files\\JetBrains\\IntelliJ IDEA 2023.1.3\\jbr\\bin\\IdeaProjects\\serviceanalyzer\\tmp";


    /**
     * 下载源码 并得到路径信息
     */
    public static Map<String, MPathInfo> getCodeAndGetMPathInfo(String url) {
        logger.info(1);
        String[] urls = url.split("/");
        String projectName = urls[urls.length - 1].split("\\.")[0];
        List<String> allTags = getAllTags(url, projectName);
        logger.info(2);
        Map<String, MPathInfo> map = new HashMap<>();
        for (String tag : allTags) {
            try {
                logger.info(tag);
                Git git = Git.cloneRepository()
                        .setURI(url)
                        .setBranch(tag).setDirectory(new File(CODE_DIWNLOAD_PATH + "/" + projectName + "_" + tag)).call();
                MPathInfo mPathInfo = getMPathInfo(tag, projectName);
                mPathInfo.setGitUrl(url);
                map.put(tag, mPathInfo);
                git.close();
            } catch (GitAPIException g) {
                logger.error(g);
            }
        }
        return map;
    }

    /**
     * @param url
     * @param version
     * @return
     */
    public static MPathInfo getCodeByVersion(String url, String version) {
        String[] urls = url.split("/");
        String projectName = urls[urls.length - 1].split("\\.")[0];
        MPathInfo mPathInfo = null;
        File file = new File(CODE_DIWNLOAD_PATH + "/" + projectName + "_" + version);
        if (file.exists()) {
            mPathInfo = getMPathInfo(version, projectName);
        } else {
            try {
                Git git = Git.cloneRepository().setURI(url).setBranch(version).setDirectory(file).call();
                git.close();
                mPathInfo = getMPathInfo(version, projectName);
            } catch (GitAPIException g) {
                logger.error(g);
            }
        }
        mPathInfo.setGitUrl(url);
        return mPathInfo;
    }


    /**
     * get controller path info
     * @param version     version
     * @param projectName projectName
     * @return path info(contains controller)
     */
    public static MPathInfo getMPathInfo(String version, String projectName) {
        String path = CODE_DIWNLOAD_PATH + "/" + projectName + "_" + version + "/";
        MPathInfo MPathInfo = new MPathInfo();
        File file_findapplication = new File(path + "src/main/resources");
        String version1_ymlconfig = path + "src/main/resources/" + getYmlPath(file_findapplication);
        MPathInfo.setApplicationPath(version1_ymlconfig);
        List<File> pathList = getListFiles(new File(path + "src/main/java"));
        List<String> listPath = new ArrayList<>();
        for (File file : pathList) {
            if (ifController(file)) {
                listPath.add(file.toString().replace("\\", "/"));
            }
        }
        MPathInfo.setControllerListPath(listPath);
        return MPathInfo;
    }


    /**
     * 判断文件是否是由restcontroller注解的对外提供rest接口的类
     * @param file 文件
     * @return true or false
     */
    public static boolean ifController(File file) {
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = JavaParser.parse(file);
        } catch (Exception e) {
            logger.error(e);
        }
        String className = file.getName().split("\\.")[0];
        if (compilationUnit.getClassByName(className).isPresent()) {
            ClassOrInterfaceDeclaration c = compilationUnit.getClassByName(className).get();
            NodeList<AnnotationExpr> annotations = c.getAnnotations();
            for (Node node : annotations) {
                if ("RestController".equals(node.getChildNodes().get(0).toString()) || "Controller".equals(node.getChildNodes().get(0).toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 遍历得到配置文件
     * @param file
     * @return
     */
    public static String getYmlPath(File file) {
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isFile()) {
                if ("application.yml".equals(file1.getName())) {
                    return "application.yml";
                } else if("application.yaml".equals(file1.getName())){
                    return "application.yaml";
                }else if ("application.properties".equals(file1.getName())) {
                    return "application.properties";
                } else {
                    continue;
                }
            } else if (file1.isDirectory()) {
                return file1.getName() + "/" + getYmlPath(file1);
            } else {
                continue;
            }
        }
        return "";
    }

    /**
     * 得到一个文件夹下的所有文件
     * @param directory 目录
     * @return 文件
     */
    public static List<File> getListFiles(File directory) {
        List<File> files = new ArrayList<>();
        if (directory.isFile()) {
            files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getListFiles(fileOne));
            }
        }
        return files;
    }

    public static boolean deleteDir(String path){
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        String[] content = file.list();
        for(String name : content){
            File temp = new File(path, name);
            if(temp.isDirectory()){
                deleteDir(temp.getAbsolutePath());
                temp.delete();//删除空目录
            }else{
                System.gc();
                if(!temp.delete()){
                    System.err.println("Failed to delete " + name);
                }
            }
        }
        return true;
    }

    /**
     *
     * @param url 仓库地址
     * @param projectname 项目名称
     * @return
     */
    public static List<String> getAllTags(String url, String projectname) {
        logger.info("get tag");
        deleteDir(CODE_DIWNLOAD_PATH);
        logger.info("delete");
        String p = CODE_DIWNLOAD_PATH + "/" + projectname;
        Git git = null;
        try {
            git = Git.cloneRepository().setURI(url).setDirectory(new File(p)).call();
        } catch (Exception e) {
            logger.error(e);
        }
        git.close();
        return new ArrayList<>(git.getRepository().getTags().keySet());
    }

    public static void main(String[] args) {
        Map<String, MPathInfo> map = GetSourceCode.getCodeAndGetMPathInfo("http://192.168.1.104:12345/wangteng/service1.git");
        for (Map.Entry<String, MPathInfo> entry : map.entrySet()) {
            logger.info(entry.getValue());
            Service service = GetServiceInfo.getMservice(entry.getKey(), entry.getValue());
            logger.info(service.toString());
        }

    }
}

