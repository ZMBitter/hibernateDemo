package com.zm.one2one.test;

import com.zm.one2one.primarykey.Department;
import com.zm.one2one.primarykey.Manager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrimaryTest {
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
        Department dept = new Department();
         dept.setDeptName("Dept-AA");
        //dept.setDeptName("Dept-BB");

        Manager manager = new Manager();
        manager.setManagerName("Manager-AA");
        //manager.setManagerName("Manager-BB");

        //设定关联关系
        dept.setManager(manager);
        manager.setDept(dept);

        //先插入哪一个都一样，只有INSERT语句
        session.save(manager);
        session.save(dept);
//        session.save(dept);
//        session.save(manager);
    }

    @Test
    public void testGet(){
        //1.默认情况下对关联属性使用懒加载
        Department department = (Department) session.get(Department.class, 1);
        System.out.println(department.getDeptName());
        //2.关闭session后，再获取对象的属性值，会出现懒加载异常LazyInitializationException
//        session.close();
//        Manager manager = department.getManager();
//        System.out.println(manager.getClass());
//        System.out.println(manager.getManagerName());

        //在one-to-one中没有设置property-ref属性的值时，查询Manager对象的连接条件会出现错误，
        // 应该是dept.manager_id=manager.manager_id，而不是dept.dept_id=manager.manager_id
        //在添加了property-ref的属性设置时正确
        Manager manager = department.getManager();
        System.out.println(manager.getManagerName());
    }

    @Test
    public void testGet2(){
        //在查询没有外键的实体对象时，使用的是左外连接查询，一并查出其关联的对象，并且已经初始化
        Manager manager = (Manager) session.get(Manager.class,1);
        System.out.println(manager.getManagerName());
        System.out.println(manager.getDept().getDeptName());
    }
}
