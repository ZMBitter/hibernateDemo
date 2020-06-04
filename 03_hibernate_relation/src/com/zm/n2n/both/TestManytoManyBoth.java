package com.zm.n2n.both;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class TestManytoManyBoth {

        private Transaction transaction;
        private SessionFactory sessionFactory;
        private Session session;

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
        public void testSave(){
            Category category1 = new Category();
            category1.setCategoryName("C-AA");
            Category category2 = new Category();
            category2.setCategoryName("C-BB");

            Item item1 = new Item();
            item1.setName("T-AA");
            Item item2 = new Item();
            item2.setName("T-BB");

            //设定关联关系
            category1.getItems().add(item1);
            category1.getItems().add(item2);
            category2.getItems().add(item1);
            category2.getItems().add(item2);
            item1.getCategories().add(category1);
            item1.getCategories().add(category2);
            item2.getCategories().add(category2);
            item2.getCategories().add(category1);
//            session.save(category1);
//            session.save(category2);
//            session.save(item1);
//            session.save(item2);
            session.save(item1);
            session.save(item2);
            session.save(category1);
            session.save(category2);
        }

        @Test
        public void testGet(){
            Category category = (Category) session.get(Category.class,1);
            System.out.println(category.getCategoryName());
            //需要连接中间表
            Set<Item> items = category.getItems();
            System.out.println(items.size());
        }
}
