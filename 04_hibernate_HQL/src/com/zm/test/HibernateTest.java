package com.zm.test;

import com.zm.bean.Department;
import com.zm.bean.Employee;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.*;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

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
//            transaction.commit();
//            session.close();
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


        //基于命名参数
        @Test
        public void testHQLNamedParameter(){
            //1.创建Query对象
            String hql = "from Employee e where e.salary > :sal and e.email like :email";
            Query query = session.createQuery(hql);
            //2.绑定参数
            query.setFloat("sal",1500).setString("email","%qq%");
            //3.执行查询
            List<Employee> list = query.list();
            System.out.println(list.size());
        }

        //1HQL的步骤
        //2.占位符：① 问好；② 参数名
        //参数还可以是实体类类型
        //4.可以在hql对象后面添加order by语句

        @Test //分页查询
        public void testPageQuery(){
            String hql = "from Employee";
            Query query = session.createQuery(hql);

            int pageNo = 1; //起始页
            int pageSize = 4; //每页最多显示的数据数

            List<Employee> emps = query
                    .setFirstResult((pageNo-1)*pageSize)
                    .setMaxResults(pageSize)
                    .list();
            System.out.println(emps);
        }

        @Test
        public void testNamedQuery(){
            Query query = session.getNamedQuery("salaryEmps");
            List<Employee> emps = query.setFloat("minSal",1000).setFloat("maxSal",3000).list();
            System.out.println(emps.size());
        }

        /*投影查询：
         *  1.通过testFieldQuery()中的方式，返回的是一个对象数组的集合
         *  2.通过testFieldQuery2()中的返回，返回的是一个实体类对象的集合。前替条件是：在
         *  实体类对象中要有相对应的有参构造函数和无参构造函数
         * */
        @Test
        public void testFieldQuery(){
            String hql = "select e.email,e.salary,e.dept from Employee e where e.dept=:dept";
            Query query = session.createQuery(hql);
            Department dept = new Department();
            dept.setId(1);
            List<Object[]> result = query.setEntity("dept",dept).list();
            for (Object[] objects:result){
                System.out.println(Arrays.asList(objects));
            }
        }
        @Test
        public void testFieldQuery2(){
            String hql = "select new Employee(e.salary,e.email,e.dept) from Employee e where e.dept=:dept";
            Query query = session.createQuery(hql);
            Department dept = new Department();
            dept.setId(1);
            List<Employee> result = query.setEntity("dept",dept).list();
            for (Employee emp:result){
                System.out.println(emp.getEmail()+","+emp.getSalary()+","+emp.getDept());
            }
        }

        @Test
        public void testGroupBy(){
            String hql = "select min(e.salary),max(e.salary) from Employee e " +
                    "group by e.dept having min(salary) > :minSal";
            Query query = session.createQuery(hql);
            query.setFloat("minSal",1000);
            List<Object[]> list = query.list();
            for (Object[] objs:list){
                System.out.println(Arrays.asList(objs));
            }
        }

        //迫切左外连接
        @Test
        public void testLeftJoinFetch(){
            // String hql = "from Department d LEFT JOIN FETCH d.emps";
            //方法1：直接在HQL语句中去重
            String hql  = "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.emps";
            Query query = session.createQuery(hql);
            List<Department> depts = query.list();
            //方法2：使用xxxSet()去重
            // depts = new ArrayList<>(new LinkedHashSet<>(depts));
            System.out.println(depts.size());

            for (Department dept:depts){
                System.out.println(dept.getDeptName()+dept.getEmps().size());
            }
        }


        //左外连接
        @Test
        public void testLeftJoin(){
            String hql = "select distinct d from Department d LEFT JOIN d.emps";
            Query query = session.createQuery(hql);
            //返回的是一个数组的集合
//        List<Object[]> list = query.list();
//        System.out.println(list);
//
//        for (Object[] objs:list){
//            System.out.println(Arrays.asList(objs));
//        }

            List<Department> depts = query.list();
            System.out.println(depts.size());

            for (Department dept:depts){
                System.out.println(dept.getDeptName()+","+dept.getEmps().size());
            }
        }


        //1.HelloWorld
        @Test
        public void testQBC(){
            //1.创建一个Criteria对象
            Criteria criteria = session.createCriteria(Employee.class);

            //2.添加查询条件：在QBC中查询条件使用Criterion来表示。Criterion可以通
            // 过Restrictions的静态方法得到
            criteria.add(Restrictions.eq("email","bb@163.com"));
            criteria.add(Restrictions.eq("salary",1250F));

            //3.执行查询
            Employee employee = (Employee) criteria.uniqueResult();
            System.out.println(employee);
        }

        //and/or
        @Test
        public void testQBC2(){
            Criteria criteria = session.createCriteria(Employee.class);
            //1.AND 使用Conjunction表示，Conjunction本身就是一个Criteria对象，且其中
            // 还可以添加Criteria对象
            Conjunction conjunction = Restrictions.conjunction();
            conjunction.add(Restrictions.like("email","qq", MatchMode.ANYWHERE));
            Department dept = new Department();
            dept.setId(2);
            conjunction.add(Restrictions.eq("dept",dept));
            System.out.println(conjunction);

            //2.OR
            Disjunction disjunction = Restrictions.disjunction();
            disjunction.add(Restrictions.ge("salary",1000F));
            disjunction.add(Restrictions.isNull("email"));

            criteria.add(disjunction);
            criteria.add(conjunction);

            criteria.list();

        }


        //统计查询
        @Test
        public void testQBC3(){
            Criteria criteria = session.createCriteria(Employee.class);
            //统计查询：使用Projections的静态方法得到
            criteria.setProjection(Projections.max("salary"));
            System.out.println(criteria.uniqueResult());
        }

        @Test
        public void testQBC4(){
            Criteria criteria = session.createCriteria(Employee.class);
            //1.添加排序：
            criteria.addOrder(Order.asc("salary")).addOrder(Order.desc("email"));

            //添加翻页方法
            int pageSize = 4;
            int pageNo = 1;
            criteria.setFirstResult((pageNo - 1) * pageSize).setMaxResults(pageSize).list();
        }

        //本地SQL插入操作
        @Test
        public void testNativeSQL(){
            String sql = "INSERT INTO department values(?,?)";
            SQLQuery query = session.createSQLQuery(sql);

            query.setInteger(0,10).setString(1,"策划部").executeUpdate();
        }

        @Test
        public void testHQLUpdate(){
            String hql = "delete from Department d where d.id=?";
            session.createQuery(hql).setInteger(0,10).executeUpdate();
        }


        //==============================Hibernate二级缓存==================================
        @Test
        public void testSecondLevelCache(){
            Employee emp01 = (Employee) session.get(Employee.class, 1);
            System.out.println(emp01.getName());
            transaction.commit();
            session.close();
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Employee emp02 = (Employee) session.get(Employee.class, 1);
            System.out.println(emp02.getName());
            transaction.commit();
            session.close();
        }
}
