package com.zm.test;

import com.zm.bean.Pay;
import com.zm.bean.Worker;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ComponentTest {

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
    public void testComponent(){
        Worker worker = new Worker();
        Pay pay = new Pay();

        pay.setMonthPay(2000);
        pay.setYearPay(10000);
        pay.setVocationWithPay(10);

        //worker.setId(1);
        worker.setName("AA");
        worker.setPay(pay);

        session.save(worker);


    }
}
