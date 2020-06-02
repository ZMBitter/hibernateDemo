package com.zm.test;

import com.zm.n21.Customer;
import com.zm.n21.Orders;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleStateException;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Many2OneTest {

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
    public void many2One(){
        Customer customer = new Customer();
        //customer.setCustomerName("AA");
        customer.setCustomerName("CC");

        Orders orders1 = new Orders();
        //orders1.setOrderName("Orders-1");
        orders1.setOrderName("Order-5");

        Orders orders2 = new Orders();
        //orders2.setOrderName("Orders-2");
        orders2.setOrderName("Order-6");

        //设定关联关系
       orders1.setCustomer(customer);
       orders2.setCustomer(customer);
        //执行save操作
        session.save(customer);

       /*先插入Customer，再插入Orders，3条INSERT
       * 即：先插入1的一端，再插入n的一端，只有INSERT语句
       * */
        session.save(orders1);
        session.save(orders2);

        /*先插入Orders，再插入Customer*/
//       session.save(orders1);
//       session.save(orders2);
//
//       session.save(customer);
    }


    @Test
    public void many2OneGet(){
        /*1.若查询多的一端的一个对象，默认情况下，只查询了多的一端的对象，而没有查询关联的1的那端的对象*/
        Orders  order = (Orders) session.get(Orders.class, 6);
        System.out.println(order);
       // session.close();
        /*2.在需要使用到关联的对象时，才发送对应的SQL语句*/
        Customer customer = order.getCustomer();
        System.out.println(customer);

        /*3.在查询Customer对象时，由多的一端导航到1的一端时，若此时session已经被关闭，则在默认情
        * 况下可能会发生懒加载异常(LazyInitializationException)
        * */

        /*4.获取Order对象时，默认情况下，其关联的Customer对象是一个代理对象*/
    }

    @Test
    public void testUpdate(){
        Orders order = (Orders) session.get(Orders.class, 6);
        Customer customer = order.getCustomer();
        customer.setCustomerName("aaa");
    }

    @Test
    public void testDelete(){
        //在不设定级联关系的情况下，且1的这一端的对象有n的对象在引用，不能直接删除1这一端的对象

        /*报错情况
          ERROR: Cannot delete or update a parent row: a foreign key constraint fails
         (`hibernate_db`.`orders`, CONSTRAINT `FK_astys1dv61mdlp0n0wx0574r2` FOREIGN
         KEY (`customer_id`) REFERENCES `customer` (`customer_id`))六月 01, 2020 2:41:51
          下午 org.hibernate.engine.jdbc.batch.internal.AbstractBatchImpl release
          */
        Customer customer = (Customer) session.get(Customer.class, 6);
        session.delete(customer);
    }

}
