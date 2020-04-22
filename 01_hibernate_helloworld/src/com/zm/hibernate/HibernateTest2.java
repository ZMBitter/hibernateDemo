package com.zm.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HibernateTest2 {
    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @Before
    public void init(){
        Configuration configure = new Configuration().configure();
        ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configure.getProperties()).buildServiceRegistry();
        sessionFactory = configure.buildSessionFactory(serviceRegistry);
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
    }

    @After
    public void destroy(){
        transaction.commit();
        session.close();
        sessionFactory.close();
    }

    @Test
    public void testCache(){
        News news = (News) session.get(News.class, 1);
        System.out.println(news );

        News news2 = (News) session.get(News.class, 1);
        System.out.println(news2);

        System.out.println(news==news2);
    }

    //session缓存操作：flush()、refresh()、clear()清除

    /*flush：使数据表中的记录和session缓存中的对象的状态保持一致。为了保持一致，可能会发送对应的SQL语句
    * 1.调用Transaction的commit()方法中，先调用session的flush()方法，再提交事务
    * 2.flush()方法可能会发送SQL语句，但不会提交事务
    * 3.注意：在未提交事务或显式的调用session.flush()方法之前，也有可能会进行flush操作
    * 1）执行HQL或QBC查询，会先进行flush()操作，以得到数据表的最新记录
    * 2）若记录的ID是由底层数据库使用自增的方式生成的，则在调用save()方法时，就会立即发送insert语句。
    * 因为save方法后必须保证对象的ID是存在的。
    * */

    @Test
    public void testFlush02(){
        News news = new News("Oracle", "Jack");
        session.save(news);
    }
    @Test
    public void testFlush(){
        News news = (News) session.get(News.class, 1);
        System.out.println(news);
        news.setTitle("Java");
        session.flush();
        System.out.println("flush");
    }

    /*refresh：会强制发送SELECT语句，以使session缓存中对象的状态和数据表中对应的记录保持一致*/
    @Test
    public void testRefresh(){
        News news = (News) session.get(News.class, 1);
        System.out.println(news);
        session.refresh(news);
        System.out.println(news);
    }

    /*clear：清除缓存*/
    @Test
    public void testClear(){
        News news = (News) session.get(News.class, 1);
        System.out.println(news);
        //清除缓存
        session.clear();
        News news2 = (News) session.get(News.class, 1);
        System.out.println(news2);
    }


   /* save()方法：
   * 1）是一个临时对象变为持久化对象
   * 2）自动为对象分配ID
   * 3）在flush缓存时会发送一条insert语句
   * 4）在save之前，使用setxx()方法设置的ID是无效的
   * 5）在save之后不能修改ID/持久化对象的ID是不能被修改的
   * */
    @Test
    public void testSave(){
        News news = new News();
        news.setTitle("Php");
        news.setAuth("John");
        news.setId(100);
        System.out.println(news);
        session.save(news);
        System.out.println(news);
        news.setId(10);
    }

   /* persits()：也会执行insert操作
    *  persist()与save()的区别：
    *  在调用persist方法之前，若对象已经有ID了，则不会执行insert操作，
    *  而是会抛出异常
     * */
    @Test
    public void testPersist(){
        News news = new News();
        news.setAuth("Java");
        news.setTitle("Black");
        //news.setId(100);

        session.persist(news);
    }


    /*get()与load()的区别：
    *
    *  1、执行get()方法，会立即加载对象；执行load()方法，若不适用该对象，则不会立即执行查询操作，
    *  而是返回一个代理对象
    *    get是立即检索，load是延迟检索
    *
    *  2、在需要初始化代理对象之前关闭session，load()方法会抛出LazyInitializationException的异
    *  常。
    *
    *  3、若数据表中没有对应的记录，session也没有被关闭，get返回null，load抛异常。load若不使用
    *  该对象的任何属性，没问题；若需要初始化，则会抛出异常。
    * */
    @Test
    public void testGet(){
       News news = (News) session.get(News.class,1);
      // session.close();
       System.out.println(news);
    }

    @Test
    public void testLoad(){
        News news = (News)session.load(News.class, 1);
        System.out.println(news.getClass().getName());
        //session.close();
        //System.out.println(news);
    }

    /* update()方法：
    *  1.若更新一个持久化对象，不需要显式地调用update()方法，因为在调用Transaction的commit()
    *  方法时，会先执行session的flush()方法
    *  2.更新一个游离对象，需要显式地调用session的update()方法。隐藏作用：可以将一个游离对象变为
    *  持久化对象
    *  注意：
    *  1.无论要更新的游离对象和数据表的记录是否一致，都会发送update语句
    *  2.如何能让update方法不再盲目地发送update语句？
    *     在*.hbm.xml文件的class节点设置select-before-update=true(默认为false），通常不设置此属性
    *  3、若数据表中没有对应的记录，但是又调用了update方法，则会抛出异常
    *  4.当update()方法关联一个游离对象时，如果在session的缓存中已经存在相同的OID,则会抛出异常。因为
    *  在session缓存中，不存在相同OID的对象
    * */
    @Test
    public void testUpdate(){
        News news = (News)session.get(News.class, 5);
        transaction.commit();
        //关闭session对象
        session.close();
        //重新获取session
       session = sessionFactory.openSession();
       transaction = session.beginTransaction();
        //news.setTitle("Java");
        session.update(news);
    }

    /* saveOrUpdate()方法：
    *  1、如果OID不为null，但数据表中还没有其对应的记录，则会抛出异常。
    *  2.如果OID为null，调用session的saveOrUpdate()方法后执行的是insert语句。
    *  3.如果OID不为null，且数据表中有与之对应的记录，调用session的saveOrUpdate()
    *  方法后执行的是update语句。
    *  4、了解：OID值等于id的unsaved-value属性值的对象，也被认为是一个游离对象
    * */
    @Test
    public void saveOrUpdate(){
        News news = new News("FF", "FF");
        news.setId(1);
        session.saveOrUpdate(news);
    }

    /* delete()：
    *   删除对象。只要OID和数据表中的一条记录对应，则删除该记录；若OID在数据表中没有对应的记录，则会抛出异常。
    *   可以通过设置Hibernate配置文件的hibernate.use_identifier_rollback为true，使删除对象后，把OID置为null。
    *   其默认值是false。
    * */
    @Test
    public void testDelete(){
       // News news = (News) session.get(News.class,1);
       // session.delete(news);

        News news = (News) session.get(News.class, 7);
        session.delete(news);
        System.out.println(news);
    }

    /* evict()：从session缓存中移除指定的持久化对象 */
    @Test
    public void testEvict(){
        News news1 = (News) session.get(News.class, 9);
        News news2 = (News) session.get(News.class, 10);

        news1.setTitle("AA");
        news2.setTitle("BB");

        session.evict(news1);
    }
}
