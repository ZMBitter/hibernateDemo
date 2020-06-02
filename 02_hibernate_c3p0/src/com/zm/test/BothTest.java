package com.zm.test;

import com.zm.n21.both.Customer;
import com.zm.n21.both.Order;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*双向关联关系*/
public class BothTest {

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
    public void testManytoOneSave(){
        Customer customer = new Customer();
        customer.setCustomerName("CC");

        Order order1 = new Order();
        order1.setOrderName("Order-5");

        Order order2 = new Order();
        order2.setOrderName("Order-6");

        //设定关联关系
        order1.setCustomer(customer);
        order2.setCustomer(customer);

        customer.getOrders().add(order1);
        customer.getOrders().add(order2);

        //执行save操作：先插入Customer，再插入Order
//        session.save(customer);
//        session.save(order1);
//        session.save(order2);

        //执行save操作：先插入Order，再插入Customer
        session.save(order1);
        session.save(order2);
        session.save(customer);

    }

    /*双向多对1的查询操作：
    * 先查1的一端
    * */
    @Test
    public void testOneToMany(){
        //1. 对n的一端的集合使用延迟加载
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        //2.返回的多的一端的集合是Hibenrate内置的集合类型，该类型具有延迟加载和存放代理对象的功能
        //class org.hibernate.collection.internal.PersistentSet
        System.out.println(customer.getOrders().getClass());

        //session.close();
        //3.可能会抛出懒加载异常LazyInitializationException

        //4.需要使用集合中元素的时候进行初始化
    }

    @Test
    public void testUpdate(){
        Customer customer = (Customer) session.get(Customer.class, 1);
        customer.getOrders().iterator().next().setOrderName("changeOrder");
    }

    @Test
     public void testCascade(){
        Customer customer = (Customer) session.get(Customer.class, 3);
        customer.getOrders().clear();

    }

    @Test
    public void testInverse(){
        Customer customer = (Customer) session.get(Customer.class, 2);
        session.delete(customer);
    }
}
