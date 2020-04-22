package com.zm.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.Test;

public class HibernateTest {

    @Test
    public void test(){
        //1.创建一个SessionFactory对象
        SessionFactory sessionFactory = null;
        //1.1创建Configuration对象：对应Hibernate的基本配置信息和对象关系映射信息
        Configuration configuration = new Configuration().configure();
        //hibernate 3中sessionFactory的创建
       // sessionFactory = configuration.buildSessionFactory();

        //hibernate 4中sessionFactory的创建
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        //2.创建一个Session对象
        Session session = sessionFactory.openSession();
        //3.开启事务
        Transaction transaction = session.beginTransaction();
        //4.执行保存操作
        News news = new News("Java","Tom");
        session.save(news);
        //5.提交事务
        transaction.commit();
        //6.关闭Session
       session.close();
        //7.关闭SessionFactory对象
      sessionFactory.close();
    }
}
