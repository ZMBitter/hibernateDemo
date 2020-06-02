package com.zm.test;

import com.zm.c3p0.News;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;


public class HibernateC3p0Test {
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void init(){
        Configuration configuration = new Configuration().configure();
        //4.3.x  创建sessionFactory对象
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        //创建session对象
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @After
    public void destory(){
        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void testDoWork(){
       session.doWork(new Work() {
           @Override
           public void execute(Connection connection) throws SQLException {
               System.out.println(connection);
           }
       });
    }

    //String title, String auth, String desc, Date date
    @Test
    public void testPropertyUpdate(){
        News news = new News("AA","admin",new Date(new java.util.Date().getTime()));
        session.save(news);
    }

    @Test
    public void testBlob() throws Exception {
//       News news = new News();
//       news.setAuth("bb");
//       news.setTitle("BB");
//       news.setDate(new java.util.Date());
//       news.setContent("CONTENT");
//
//       FileInputStream stream = new FileInputStream("logo.png");
//       Blob image = Hibernate.getLobCreator(session).createBlob(stream, stream.available());
//       news.setImage(image);
//
//       session.save(news);

        //获取
        News news = (News) session.get(News.class, 1);
        Blob image = news.getImage();
        InputStream stream = image.getBinaryStream();
        System.out.println(stream.available());

    }


}
