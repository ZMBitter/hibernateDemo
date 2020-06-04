package com.zm.strategy;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestStrategy {

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

    @Test  //类级别的检索策略测试
    public void testClassLevelStrategy(){
        Customer customer = (Customer) session.load(Customer.class, 1);
        //得出的是一个代理对象
        System.out.println(customer.getClass());
    }

    @Test
    public void testOne2ManyLevelStrategy(){
        Customer customer = (Customer) session.get(Customer.class, 1);
        System.out.println(customer.getCustomerName());
        System.out.println(customer.getOrders().size());

        Order order = new Order();
        order.setOrderId(1);
        System.out.println(customer.getOrders().contains(order));
        //使代理的对象初始化
        Hibernate.initialize(customer.getOrders());

        //--------------------set的lazy属性------------------------
       //1. 1-n或者n-n的集合属性默认使用懒加载的加载策略
        //2.可以通过设置set的lazy属性来修改默认的检索策略，默认为true。并不建议设置为false
        //3.lazy还可以设置为extra增强的延迟检索，该取值会尽可能延迟初始化的时机
    }

    @Test
    public void testBatchSize(){
        List<Customer> customers = session.createQuery("from Customer").list();
        System.out.println(customers.size());
        for(Customer customer:customers){
            if(customer.getOrders()!=null){
                System.out.println(customer.getOrders().size());
            }
        }
    }

    @Test
    public void testFetch(){
        List<Customer> customers = session.createQuery("from Customer").list();
        System.out.println(customers.size());
        for(Customer customer:customers){
            if(customer.getOrders()!=null){
                System.out.println(customer.getOrders().size());
            }
        }

        //set集合的fetch属性：
        //1.默认值为select，通过正常的方式来初始化set元素
        //2.可以取值为subselect，通过子查询的方式来初始化所有的set集合，子查
        // 询作为where子句的in的条件出现，子查询查询所有1的一端的ID，此时lazy有效，
        //但batch-size失效
        //3.若取值为join，则
        //3.1在加载1的一端的对象时，使用迫切左外连接（使用左外连接进行查询，且把集合属性进行初始化）的方式检索
        // n的一端的集合属性
        //3.2忽略lazy属性
        //3.3HQL查询忽略fetch=join的取值
    }

    @Test
    public void testMany2OneStrtegy(){
//        Order order = (Order) session.get(Order.class, 1);
//        System.out.println(order.getCustomer().getCustomerName());

        List<Order> orders = session.createQuery("from Order o").list();
        for (Order order:orders){
            if (order.getCustomer()!=null){
                System.out.println(order.getCustomer().getCustomerName());
            }
        }

        //1.lazy取值为proxy和false分别代表对应的属性采用延迟检索和立即检索
        //2.fetch取值为join，表示使用迫切左外连接的方式初始化n关联的1的一端的属性
        //3.batch-size：该属性需要设置在1的那一端的class元素中
        //作用：一次初始化1的这一端代理对象的个数
    }
}
