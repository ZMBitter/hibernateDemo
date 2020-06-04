package com.zm.inherit.test;

import com.zm.inherit.joined.subclass.Person;
import com.zm.inherit.joined.subclass.Student;
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

public class TestJoinedSubclass {
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
     * 1.对于子类对象至少需要插入到两张数据表中
     *
     * Hibernate: insert into PERSON (NAME, AGE) values (?, ?)
     * Hibernate: insert into PERSON (NAME, AGE) values (?, ?)
     * Hibernate: insert into STUDENTS (SCHOOL, student_id) values (?, ?)
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


    /*优点：
     * 1.不需要使用辨别者列
     * 2.子类独有的字段能添加非空约束
     * 3.没有冗余字段
     * */

    /*查询：
     * 1.查询父类记录，做一个左外连接
     * select person0_.ID as ID1_0_, person0_.NAME as NAME2_0_, person0_.AGE as
     * AGE3_0_, person0_1_.SCHOOL as SCHOOL2_1_, case when person0_1_.student_id
      * is not null then 1 when person0_.ID is not null then 0 end as clazz_ from
      * PERSON person0_ left outer join STUDENTS person0_1_ on person0_.ID=person0_1_.
      * student_id
      *
     * 2.对于子类记录,做一个内连接
     * select student0_.student_id as ID1_0_, student0_1_.NAME as NAME2_0_,
     * student0_1_.AGE as AGE3_0_, student0_.SCHOOL as SCHO
     * */
    @Test
    public void testGet(){
        List<Person> persons = session.createQuery("from Person").list();
        System.out.println(persons.size());

        List<Student> students = session.createQuery("from Student").list();
        System.out.println(students.size());
    }
}
