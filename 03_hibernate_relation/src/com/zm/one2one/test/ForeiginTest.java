package com.zm.one2one.test;

import com.zm.one2one.foreignkey.Department;
import com.zm.one2one.foreignkey.Manager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//基于外键的1-1关联关系映射
public class ForeiginTest {

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
       // dept.setDeptName("Dept-AA");
        dept.setDeptName("Dept-BB");

        Manager manager = new Manager();
       // manager.setManagerName("Manager-AA");
        manager.setManagerName("Manager-BB");

        //设定关联关系
        manager.setDept(dept);
        dept.setManager(manager);
      //先保存的是没有外键列的对象，再保存有外键列的对象。2条INSERT语句，没有UPDATE语句
       // Hibernate: insert into MANAGERS (manager_name) values (?)
       // Hibernate: insert into DEPARTMENTS (dept_name, manger_id) values (?, ?)
//        session.save(manager);
//        session.save(dept);

        //先保存有外键列对象，在保存无外键列对象。2条INSERT语句，1条UPDATE语句
        //Hibernate: insert into DEPARTMENTS (dept_name, manger_id) values (?, ?)
        //Hibernate: insert into MANAGERS (manager_name) values (?)
        //Hibernate: update DEPARTMENTS set dept_name=?, manger_id=? where dept_id=?
        session.save(dept);
        session.save(manager);
    }

    @Test
    public void testGet(){
        //1.默认情况下对关联属性使用懒加载
        //Hibernate: select department0_.dept_id as dept_id1_0_0_, department0_.dept_name as dept_nam2_0_0_,
        // department0_.manger_id as manger_i3_0_0_ from DEPARTMENTS department0_ where department0_.dept_id=?
        //Dept-AA
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
        //Hibernate: select manager0_.manger_id as manger_i1_1_0_, manager0_.manager_name
        // as manager_2_1_0_, department1_.dept_id as dept_id1_0_1_, department1_.dept_name
        // as dept_nam2_0_1_, department1_.manger_id as manger_i3_0_1_ from MANAGERS
        // manager0_ left outer join DEPARTMENTS department1_ on
        // manager0_.manger_id=department1_.manger_id where manager0_.manger_id=?
        Manager manager = (Manager) session.get(Manager.class,1);
        System.out.println(manager.getManagerName());
        System.out.println(manager.getDept().getDeptName());
    }
}
