package com.hitices.analyzer.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.hitices.analyzer.base.Interface;
import com.hitices.analyzer.base.Service;
import com.hitices.analyzer.base.Version;
import com.hitices.analyzer.bean.MPathInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;


import java.io.*;
import java.util.*;


public class GetServiceInfo {

    private static Logger logger = LogManager.getLogger(GetServiceInfo.class);

    public static Service getMservice(String version, MPathInfo pathInfo) {
        Service mService = new Service();
        mService.setRepo(pathInfo.getGitUrl());
        Version mSvcVersion = new Version();
        String[] versions = version.replaceAll("[a-zA-Z]", "").split("\\.");
        mSvcVersion.setMajor(versions[0]);
        mSvcVersion.setMinor(versions[1]);
        mSvcVersion.setPatch(version);
        mService.setVersion(mSvcVersion);
        List<Interface> list = new ArrayList<>();
        for (String s : pathInfo.getControllerListPath()) {
            list.addAll(getServiceInfo(s,mService.getRepo()));
        }
        mService.setInterfaces(list);
        return mService;

    }

    /**
     * 根据路径得到接口信息
     *
     * @return Service类，返回该类的信息
     */
    public static List<Interface> getServiceInfo(String codepath, String contextPath) {
        List<Interface> list = new ArrayList<>();
        CompilationUnit compilationUnit = null;
        File file;
        try {
            file = new File(codepath);
            if (!file.exists()) {
                logger.debug ("源码路径错误");
            } else {
                compilationUnit = JavaParser.parse(file);
            }
        } catch (Exception e) {
            logger.error(e);
            return null;
        }

        if (compilationUnit == null) {
            throw new RuntimeException("Failed to parse java file " + file.getAbsolutePath());
        }

        String[] strings = codepath.split("/");
        String className = strings[strings.length - 1].split("\\.")[0];

        Optional<ClassOrInterfaceDeclaration> cOptional = compilationUnit.getClassByName(className);
        if (cOptional.isPresent()) {
            ClassOrInterfaceDeclaration c = cOptional.get();
            NodeList<AnnotationExpr> annotations = c.getAnnotations();
            // 所有的基础路径
            List<String> pathContexts = getContextPath(contextPath, annotations);
            if (pathContexts.size() == 0) {
                pathContexts.add("/");
            }
            /*得到interface方面*/
            List<MethodDeclaration> methodDeclarationList = c.getMethods();
            // 遍历每一个方法
            for (MethodDeclaration m : methodDeclarationList) {
                Interface mSvcInterface = new Interface();
                mSvcInterface.setId(m.getName().toString());
                Set<String> pathurl = new HashSet<>();
                NodeList<AnnotationExpr> anno = m.getAnnotations();
                List<String> pathContextsFunction = new ArrayList<>();
                for (AnnotationExpr annotationExpr : anno) {
                    List<Node> childNodes = annotationExpr.getChildNodes();
                    String annoName = childNodes.get(0).toString();
                    if ("RequestMapping".equals(annoName) || "GetMapping".equals(annoName) || "PostMapping".equals(annoName) || "DeleteMapping".equals(annoName) || "PutMapping".equals(annoName)) {
                        Node s = childNodes.get(1);
                        List<Node> sUrl = s.getChildNodes();
                        if (sUrl.size() == 0) {
                            String h = s.toString();
                            pathContextsFunction.add(h.substring(1, h.length() - 1));
                        } else {
                            Node node1 = sUrl.get(1);
                            if (node1.getChildNodes().size() == 0) {
                                String h = node1.toString();
                                pathContextsFunction.add(h.substring(1, h.length() - 1));
                            } else {
                                for (Node node : node1.getChildNodes()) {
                                    String h = node.toString();
                                    pathContextsFunction.add(h.substring(1, h.length() - 1));
                                }
                            }
                        }
                    } else if("ApiOperation".equals(annoName)){
                        for (Node node: childNodes){
                            if (node.getChildNodes().size()>0){
                                if (node.getChildNodes().get(0).toString().equals("httpMethod")){
                                    mSvcInterface.setMethod(node.getChildNodes().get(1).toString().replace("\"", ""));
                                }else {
                                    mSvcInterface.setInfo(node.getChildNodes().get(1).toString().replace("\"", ""));
                                }
                            }
                        }
                    }
                    for (String string1 : pathContexts) {
                        for (String string2 : pathContextsFunction) {
                            String p = string1 + string2;
                            pathurl.add(p.replaceAll("/+", "/"));
                        }
                    }
                }
                if (pathurl.size() == 0) {
                    continue;
                }
                for (String string : pathurl) {
                    mSvcInterface.setPath(string);
                    // todo
                    mSvcInterface.setInputSize(0.0);
                    mSvcInterface.setOutputSize(0.0);
                    list.add(mSvcInterface);
                }
            }
        }

        return list;
    }

    public static List<String> getContextPath(String contextPath,NodeList<AnnotationExpr> annotations){
        List<String> pathContexts = new ArrayList<>();
        for (AnnotationExpr annotationExpr : annotations) {
            List<Node> childNodes = annotationExpr.getChildNodes();
            String annoName = childNodes.get(0).toString();
            if ("RequestMapping".equals(annoName)) {
                // 在此得到路径信息
                Node s = childNodes.get(1);
                if (s.getChildNodes().size() == 0) {
                    String h = s.toString();
                    pathContexts.add(contextPath+h.substring(1, h.length() - 1));
                } else {
                    Node node1 = s.getChildNodes().get(1);
                    if (node1.getChildNodes().size() == 0) {
                        String h = node1.toString();
                        pathContexts.add(contextPath+h.substring(1, h.length() - 1));
                    } else {
                        for (Node node : node1.getChildNodes()) {
                            String h = node.toString();
                            pathContexts.add(contextPath+h.substring(1, h.length() - 1));
                        }
                    }
                }
            }
        }
        return pathContexts;
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
                    logger.debug("Failed to delete " + name);
                }
            }
        }
        return true;
    }
}

