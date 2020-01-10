package com.example.javass;

import com.alibaba.fastjson.JSON;
import javassist.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

public class SS {
    public static void main(String[] args) throws Exception{
        List<HashMap<String,Object>> maps = new ArrayList<>();
        HashMap<String,Object> map1 = new HashMap<>();
        map1.put("a",1);
        map1.put("b",2.2);
        map1.put("c","a");
        maps.add(map1);
        HashMap<String,Object> map2 = new HashMap<>();
        map2.put("a",5);
        map2.put("b",2.6);
        map2.put("c","g");
        maps.add(map2);
        Class aClass = parseMap(maps.get(0));
        List<Object> collect = parseList(aClass,maps);
        System.out.println("在这停顿");
    }





    public static Class parseMap(HashMap<String,Object> map) throws Exception{
        ClassPool pool = ClassPool.getDefault();
        String beanName = "javassist" + System.currentTimeMillis();
        CtClass cclz = pool.makeClass("com.example.javass." + beanName);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object obj = entry.getValue();
            String fieldTypeAllName = "";
            if (obj instanceof Integer) {
                fieldTypeAllName=Integer.class.getName();
            } else if (obj instanceof String) {
                fieldTypeAllName=String.class.getName();
            } else if (obj instanceof Double) {
                fieldTypeAllName=Double.class.getName();
            } else if (obj instanceof Long) {
                fieldTypeAllName=Long.class.getName();
            } else if (obj instanceof Boolean) {
                fieldTypeAllName=Boolean.class.getName();
            } else if (obj instanceof Date) {
                fieldTypeAllName=Date.class.getName();
            } else {
            }
            //sql列名貌似暂时不用处理

            CtField f2 = new CtField(pool.get(fieldTypeAllName), key, cclz);
            //设置修饰符
            f2.setModifiers(Modifier.PRIVATE);
            cclz.addField(f2);

            //如果是某个属性的get和set方法还可以通过CtNewMethod类来创建
            String s = key.substring(0, 1).toUpperCase() + key.substring(1);
            CtMethod m3 = CtNewMethod.getter("get" + s, f2);
            CtMethod m4 = CtNewMethod.setter("set" + s, f2);
            cclz.addMethod(m3);
            cclz.addMethod(m4);
        }
        CtConstructor constructor2 = CtNewConstructor.make("public " + beanName +"(){}", cclz);
        cclz.addConstructor(constructor2);

        Class clz = cclz.toClass(SS.class.getClassLoader());
        return clz;
    }

    public static  List parseList(Class type,List<HashMap<String,Object>> maps){
        List list = new ArrayList();
        for (HashMap<String, Object> map : maps) {
            String s = JSON.toJSONString(map);
            Object o = JSON.parseObject(s, type);
            list.add(o);
        }
        return list;
    }
}
