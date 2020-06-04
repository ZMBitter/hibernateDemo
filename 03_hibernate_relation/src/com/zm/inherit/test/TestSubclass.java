package com.zm.inherit.test;

import com.zm.inherit.subclass.Person;
import com.zm.inherit.subclass.Student;
import org.hibernate.PersistentObjectException;
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

public class TestSubclass {

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

    /*插入操作：
    * 1.对于子类对象只需把记录插入到一张数据表中
    * 2.辨别者列有Hibernate自动维护
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


    /*缺点：
    * 1.使用了辨别者列
    * 2.子类独有的字段不能添加非空约束
    * 3.若继承层次较深，则数据表的字段也会较多
    * */

    /*查询：
    * 1.查询父类记录，只需要查询一张数据表
    * 2.对于子类记录,也只需要查询一张数据表（根据type）
    * */
    @Test
    public void testGet(){
        List<Person> persons = session.createQuery("from Person").list();
        System.out.println(persons.size());

        List<Student> students = session.createQuery("from Student").list();
        System.out.println(students.size());
    }
}
