package com.shipbattle.message;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 用来生成消息的工厂
 */
public class MessageFactory {
    //用来读取配置文件的类
    private static Properties properties;
    //用来保存所有消息的实例的容器
    private static Map<String, Message> messageMap;

    /**
     * 静态代码块，用来加载配置文件内容
     */
    static {
        messageMap =new HashMap<>();
        properties=new Properties();
        InputStream resource = MessageFactory.class.getClassLoader().getResourceAsStream("properties");
        try {
            properties.load(resource);
            for(Object key:properties.keySet()){
                String beanName=key.toString();//获取properties文件中的beanName
                String beanPath=properties.getProperty(beanName);//获取全限定类名
                //使用反射根据全限定类名创建生成实例
                Message message=(Message) Class.forName(beanPath).newInstance();
                messageMap.put(beanName,message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * 通过读取配置文件获取全限定类名生成的具体消息类的实例(单例的)
     * @param messageName
     * @return
     */
    public static Message getSingletonMessage(String messageName){
        return messageMap.get(messageName);
    }

    /**
     * 通过读取配置文件获取全限定类名生成的具体消息类的实例(多例的)
     * @param messageName
     * @return
     */
    public static Message getMultiInstanceMessage(String messageName){
        Message message=null;
        String classPath=properties.getProperty(messageName);
        try {
            message=(Message) Class.forName(classPath).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

}
