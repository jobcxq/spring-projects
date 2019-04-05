package com.study.spring.framework.v1;

import com.study.spring.framework.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cnxqin
 * @desc
 * @date 2019/03/30 22:28
 */
public class DispatcherServlet extends HttpServlet {
    private final static Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    //保存配置文件
    private Properties contextConfig = new Properties();

    //保存类全路径
    private List<String> classNames = new ArrayList<>();

    //ICO容器
    private Map<String,Object> ioc = new HashMap<>();

    //请求地址映射
//    private Map<String,Method> handlerMapping = new HashMap<>();
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //调用，运行阶段
        try {
            doDispatch(req, resp);
        }catch (Exception e){
            log.error("request error.",e);
            resp.getWriter().write("Sorry ! Internal server error[500] has found: " + Arrays.toString(e.getStackTrace()));
        }

    }

    @Override
    public void destroy() {
        super.destroy();
        log.info("\n**********My Spring framework is destroy.");
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        HandlerMapping handlerMapping = getHandlerMapping(getRequestUrl(req));
        
//        if(!this.handlerMapping.containsKey(url)){
        if(handlerMapping == null){
            resp.getWriter().write("404 Not Found!!!");
            return;
        }

//        Method method = this.handlerMapping.get(url);

        //构造参数
//        Object [] paramValues = constructParameter(req,resp,method);
        Object [] paramValues = constructParameter(req,resp,handlerMapping);
        //反射调用
//        Object result = method.invoke(ioc.get(getBeanName(method.getDeclaringClass())),paramValues);
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(),paramValues);

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-type", "text/html;charset=UTF-8");
        resp.getWriter().write(result != null ? result.toString() : "NO result return!");
    }

    /**
     * 获取请求的相对路径
     * @param req
     * @return
     */
    private String getRequestUrl(HttpServletRequest req){
        //获取绝对路径
        String url = req.getRequestURI();
        //获取相对路径
        String contextPath = req.getContextPath();
        url = url.replaceAll(contextPath,"").replaceAll("/+","/");
        return url;
    }

    private HandlerMapping getHandlerMapping(String url) {
        if(handlerMappings.isEmpty()){return null;}

        for (HandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(matcher.matches()){
                return handler;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        log.info("My Spring framework is initing...");
        //1、加载配置文件
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        //2、扫描相关的类
        doScanner(contextConfig.getProperty("scanPackage"));

        //3、初始化扫描到的类，并且将它们放入到ICO容器之中
        doInstance();

        //4、完成依赖注入
        doAutowired();

        //5、初始化HandlerMapping
        initHandlerMapping();

        log.info("\n***********************************************************" +
                "\n**********My Spring framework started successful.**********" +
                "\n***********************************************************");
    }

    private Object[] constructParameter(HttpServletRequest req, HttpServletResponse resp,
                                        HandlerMapping handlerMapping) {
        //获取方法的形参列表
        Class<?> [] parameterTypes = handlerMapping.getParameterTypes();
        Object [] paramValues = new Object[parameterTypes.length];  //方法的实参
        Map<String,String[]> requestMap =  req.getParameterMap(); //请求参数

        for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
            if(handlerMapping.getParamIndexMapping().containsKey(entry.getKey())){
                Integer index = handlerMapping.getParamIndexMapping().get(entry.getKey());
                String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]","")
                        .replaceAll("\\s",",");
                paramValues[index] = convert(parameterTypes[index],value);//装换为指定的类型
            }
        }
        if(handlerMapping.getParamIndexMapping().containsKey(req.getClass().getName())){
            Integer index = handlerMapping.getParamIndexMapping().get(req.getClass().getName());
            paramValues[index] = req;
        }
        if(handlerMapping.getParamIndexMapping().containsKey(resp.getClass().getName())){
            Integer index = handlerMapping.getParamIndexMapping().get(resp.getClass().getName());
            paramValues[index] = resp;
        }

        return paramValues;

    }

    @Deprecated
    private Object[] constructParameter(HttpServletRequest req, HttpServletResponse resp, Method method) {
        //获取方法的形参列表
        Class<?> [] parameterTypes = method.getParameterTypes();
        Object [] paramValues = new Object[parameterTypes.length];  //方法的实参
        Map<String,String[]> requestMap =  req.getParameterMap(); //请求参数

        for(int i = 0; i < parameterTypes.length; i ++){
            Class<?> parameterType = parameterTypes[i];
            //不能用instanceof，parameterType它不是实参，而是形参
            if(parameterType == HttpServletRequest.class){
                paramValues[i] = req;
            }else if(parameterType == HttpServletResponse.class){
                paramValues[i] = resp;
            }else{

                Annotation[][] annotations = method.getParameterAnnotations();
                for(int j = 0; j < annotations.length; j ++){
                    for(Annotation annotation : annotations[j]){
                        if(annotation instanceof RequestParam){
                            //获取注解
                            RequestParam requestParam = (RequestParam) annotation;
                            if(req.getParameterMap().containsKey(requestParam.value())){
                                for (Map.Entry<String,String[]> param : requestMap.entrySet()){
                                    String value = Arrays.toString(param.getValue())
                                            .replaceAll("\\[|\\]","")
                                            .replaceAll("\\s",",");
                                    paramValues[i] = value;
                                }
                            }
                        }
                    }

                }
            }
        }
        return paramValues;
    }

    /**
     * HTTP是基于字符串协议，需要把String类型的值转换为指定类型
     * @param type
     * @param value
     * @return
     */
    private Object convert(Class<?> type,String value){
        if(Integer.class == type){
            return Integer.valueOf(value);
        }else if(Double.class == type){
            return Double.valueOf(value);
        }else if(Long.class == type){
            return Long.valueOf(value);
        }
        //需要使用策略模式
        return value;
    }

    private void initHandlerMapping() {
        if(ioc.isEmpty()){ return; }

        for(Map.Entry<String, Object> entry : ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            //首先需要时一个Controller
            if(!clazz.isAnnotationPresent(Controller.class)){ return; }

            String baseUrl  = "/";
            //注解在类上的路径
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl += requestMapping.value();
            }

            //获取所有public方法，及其 RequestMapping注解
            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(RequestMapping.class)){ return;}
                RequestMapping requestMapping = method.getDeclaredAnnotation(RequestMapping.class);
                //组装相对的请求url地址，并去掉多余/
                String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+","/");

                Pattern pattern = Pattern.compile(url);
                if(null != getHandlerMapping(url)){
                    String message = "the request url [" + url + "] is existed.";
                    log.error(message);
                    throw new RuntimeException(message);
                }
                handlerMappings.add(new HandlerMapping(pattern,method,entry.getValue()));
//                handlerMapping.put(url,method);

                log.info("add handler Mapping, url:{}, method：{}",url,method);
            }
        }
    }

    private void doAutowired() {
        if(ioc.isEmpty()){ return; }
        for(Map.Entry<String, Object> entry : ioc.entrySet()){
            Class<?> clazz = entry.getValue().getClass();
            Field[] fields = clazz.getDeclaredFields();
            for(Field field : fields){
                if(!field.isAnnotationPresent(Autowired.class)){ continue; }

                Autowired autowired = field.getAnnotation(Autowired.class);

                //若注解指定了BeanName,则直接使用，否则使用字段类型作为beanName从IOC容器中获取
                String beanName = !"".equals(autowired.value().trim()) ? autowired.value().trim() : getBeanName(field.getType());
                try {
                    field.setAccessible(true);
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    log.error("autowred error, field:{}",field.getName(),e);
                    e.printStackTrace();
                }
            }

        }

    }


    /**
     * 初始化Bean
     */
    private void doInstance(){
        if(classNames.isEmpty()){ return; }

        classNames.forEach((className) -> {
            try {
                Class<?> clazz = Class.forName(className);
                Object instance = null;

                //初始化带有类注解的类
                if(clazz.isAnnotationPresent(Controller.class) ||
                        clazz.isAnnotationPresent(Repository.class) ||
                        clazz.isAnnotationPresent(Service.class)){
                    instance = clazz.newInstance();
                    String beanName = getBeanName(clazz);
                    ioc.put(beanName,instance);

                    //将类实现的接口进行初始化
                    if(clazz.getInterfaces() != null){
                        for(Class<?> inter : clazz.getInterfaces()){
                            if(!ioc.containsKey(inter.getName())){
                                //将接口类型作为 key 保存，方便Autowried注入
                                beanName = getBeanName(inter);
                                ioc.put(beanName,instance);
                            }
                        }

                    }

                }
            } catch (Exception e) {
                log.error("init IOC error, className:{}",className,e);
            }
        });
    }

    /**
     * 扫描包路径scanPackage下的所有类
     * @param scanPackage
     */
    private void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader()
                .getResource("/" + scanPackage.replaceAll("\\.","/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()){
            if(file.isDirectory()){
                doScanner(scanPackage + "." + file.getName());
            }else{
                //是否为Class文件
                if(!file.getName().endsWith(".class")){
                    continue;
                }
                String classPackage = scanPackage + "." + file.getName().replace(".class","");
                classNames.add(classPackage);
            }
        }

    }

    /**
     * 加载配置文件
     * @param contextConfigLocation 配置文件名称
     */
    private void doLoadConfig(String contextConfigLocation) {
        log.info("contextConfigLocation is [{}]",contextConfigLocation);
        InputStream inputStream = null;
        try{
            //从类路径下找到Spring主配置文件所在的路径，并且将其读取出来放到Properties对象中
            inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
            contextConfig.load(inputStream);
        }catch (Exception e){
            log.error("load config error,",e);
        }finally {
            if(null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取指定类的 BeanName，默认首字母小写
     * @param clazz 类或接口
     * @return
     */
    private String getBeanName(Class<?> clazz){

        String beanName = null;
        if(!clazz.isInterface()) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                Controller controller = clazz.getAnnotation(Controller.class);
                beanName = controller.value();
            } else if (clazz.isAnnotationPresent(Service.class)) {
                Service service = clazz.getAnnotation(Service.class);
                beanName = service.value();
            } else if (clazz.isAnnotationPresent(Repository.class)) {
                Repository repository = clazz.getAnnotation(Repository.class);
                beanName = repository.value();
            }
        }
        if(beanName != null && !"".equals(beanName.trim())){
            return beanName;
        }

        //默认beanName为首字母小写
        char[] chars = clazz.getSimpleName().toCharArray();
        if(chars[0] > 65 && chars[0] < 90){
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

}
