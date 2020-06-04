package com.zm.inherit.test;

import com.zm.inherit.union.subclass.Person;
import com.zm.inherit.union.subclass.Student;
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

public class TestUnionSubclass {
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

    /*插入：
    * 1.对于子类对象只需把记录插入到一张数据表中
    * */
    @Test
    public void testSave(){
        Person person = new Person();
        person.setName("AA");
        person.setAge(21);
        session.save(person);

        Student student = new Student();
        student.setName("Stu_AA");
        student.setAge(12);
        student.setSchool("超星学院");
        session.save(student);
    }


    /*查询：
    * 1.查询父类记录，需把父表和子表汇总到一起再做查询，性能较差
    * 2.对于子类记录，只需要查询一张数据表
    * */
    @Test
    public void testGet(){
        List<Person> persons = session.createQuery("from Person").list();
        System.out.println(persons.size());

        List<Student> students = session.createQuery("from Student").list();
        System.out.println(students.size());
    }

    @Test
    public void testUpdate(){
        String sql = "update Person p set p.age = 10";
        session.createQuery(sql).executeUpdate();
    }

    /*优点：
     * 1.无需使用辨别者列
     * 2.子类独有的字段能添加非空约束
     * 缺点：
     * 1.存在冗余字段
     * 2.若更新父表的字段，则更新的效率较低
     * */
}
