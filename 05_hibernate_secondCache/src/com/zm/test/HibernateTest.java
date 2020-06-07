package com.zm.test;

import com.zm.bean.Department;
import com.zm.bean.Employee;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

/*hibernate的二级缓存*/
public class HibernateTest {
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

    //基于位置参数
    @Test
    public void testHQL(){
        //1.创建Query对象
        String hql = "from Employee e where e.salary > ? and e.email like ?";
        Query query = session.createQuery(hql);
        //2.绑定参数
        //Query对象调用setxxx方法支持方法链的编程风格
        query.setFloat(0,1500).setString(1,"%qq%");
        //3.执行查询
        List<Employee> list = query.list();
        System.out.println(list.size());

    }

    //类级别的二级缓存
    @Test
   public void testClassLevelSecondCache(){
        Employee emp01 = (Employee) session.get(Employee.class, 1);
        System.out.println(emp01.getName());
        transaction.commit();
        session.close();

        //新开一个session
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        Employee emp02 = (Employee) session.get(Employee.class, 1);
        System.out.println(emp02.getName());

        transaction.commit();
        session.close();
    }

    //集合级别的二级缓存:不但要缓存集合，还要缓存集合中的元素
    @Test
    public void testCollectionSecondLevelCache(){
        Department dept = (Department) session.get(Department.class, 1);
        System.out.println(dept.getDeptName());
        System.out.println(dept.getEmps().size());
        transaction.commit();
        session.close();

        //新开一个session
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();

        Department dept2 = (Department) session.get(Department.class, 1);
        System.out.println(dept2.getDeptName());
        System.out.println(dept2.getEmps().size());
        transaction.commit();
        session.close();
    }


    //查询缓存
    @Test
    public void testQueryCache(){
        Query query = session.createQuery("from Employee");
        query.setCacheable(true);
        List<Employee> emps = query.list();
        System.out.println(emps.size());

        emps = query.list();
        System.out.println(emps.size());

        Criteria criteria = session.createCriteria(Employee.class);
        criteria.setCacheable(true);

    }

    //更新时间戳缓存
    @Test
    public void testUpdateTimeStampCache(){
        Query query = session.createQuery("FROM Employee");
        query.setCacheable(true);
        List<Employee> emps = query.list();
        System.out.println(emps.size());

        Employee emp = (Employee) session.get(Employee.class, 1);
        emp.setSalary(5000);

        emps = query.list();
        System.out.println(emps.size());

    }

    @Test
    public void testQueryIterate(){
        Department dept = (Department) session.get(Department.class, 1);
        System.out.println(dept.getDeptName());
        System.out.println(dept.getEmps().size());

        Query query = session.createQuery("from Employee e where e.dept.id=1");
//        List<Employee> emps = query.list();
//        System.out.println(emps.size());

        Iterator<Employee> iterate = query.iterate();
        while (iterate.hasNext()){
            System.out.println(iterate.next().getName());
        }
    }
}
