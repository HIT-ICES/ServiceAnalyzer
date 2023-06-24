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

    private static Logger logger = LogManager.getLogger(GetSourceCode.class);

    public static Service getMservice(String version, MPathInfo pathInfo) {
        Service mService = new Service();
        mService.setRepo(pathInfo.getGitUrl());
        Version mSvcVersion = new Version();
        String[] versions = version.replaceAll("[a-zA-Z]", "").split("\\.");
        mSvcVersion.setMajor(versions[0]);
        mSvcVersion.setMinor(versions[1]);
        mSvcVersion.setPatch(version);
        mService.setVersion(mSvcVersion);
        Map<String, Interface> map = new HashMap<>();
        for (String s : pathInfo.getControllerListPath()) {
            map.putAll(getServiceInfo(s,mService.getRepo()));
        }
        mService.setInterfaces(map);
        return mService;

    }

    /**
     * 根据路径得到接口信息
     *
     * @return Service类，返回该类的信息
     */
    public static Map<String, Interface> getServiceInfo(String codepath, String contextPath) {
        Map<String, Interface> map = new HashMap<>();
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
//                mSvcInterface.setReturnType(m.getType().toString());
                List<String> pathurl = new ArrayList<>();
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
                        logger.info(childNodes.toString());
                        logger.info(childNodes.size());
                        for (Node node: childNodes){
                            if (node.getChildNodes().size()>0){
                                logger.info(node.getChildNodes().get(0)+"\t"+node.getChildNodes().get(1));
                            }

                        }
                        continue;
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
                    map.put(string, mSvcInterface);
                }
            }
        }
        return map;
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
}

