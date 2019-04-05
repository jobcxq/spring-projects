package com.study.spring.framework.v1;

import com.study.spring.framework.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author cnxqin
 * @desc 定义处理请求方法与地址映射类
 * @date 2019/04/05 13:49
 */
public class HandlerMapping {

    private Pattern pattern;    //保存请求地址的正则表达式
    private Method method;
    private Object controller;  //请求地址对应的对象
    private Class<?> [] parameterTypes;     //参数类型列表
    private Map<String,Integer> paramIndexMapping;//形参列表，参数的名字作为key,参数的顺序作为值

    public HandlerMapping(Pattern pattern, Method method, Object controller) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;

        parameterTypes = method.getParameterTypes();
        paramIndexMapping = new HashMap<>();
        initParamIndexMapping(method);
    }

    /**
     * 初始化参数列表
     * @param method
     */
    private void initParamIndexMapping(Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();

        for(int i = 0; i < annotations.length; i ++){
            if(annotations[i] != null){
                for(Annotation annotation : annotations[i]){
                    if(annotation instanceof RequestParam){
                        String paramValue = ((RequestParam) annotation).value().trim();
                        if(!"".equals(paramValue)){
                            this.paramIndexMapping.put(paramValue,i);
                        }
                    }
                }
            }
        }

        //提取方法中的request和response参数
        if(parameterTypes != null){
            for(int i = 0; i < parameterTypes.length; i ++){
                if(parameterTypes[i] == HttpServletRequest.class ||
                        parameterTypes[i] == HttpServletResponse.class){
                    this.paramIndexMapping.put(parameterTypes[i].getName(),i);
                }
            }
        }

    }

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Object getController() {
        return controller;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Map<String, Integer> getParamIndexMapping() {
        return paramIndexMapping;
    }
}
