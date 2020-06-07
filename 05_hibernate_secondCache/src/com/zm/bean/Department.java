package com.zm.bean;

import java.util.HashSet;
import java.util.Set;

public class Department {
   private Integer id;
   private String deptName;
   private Set<Employee> emps = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Set<Employee> getEmps() {
        return emps;
    }

    public void setEmps(Set<Employee> emps) {
        this.emps = emps;
    }

    /*@Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", deptName='" + deptName + '\'' +
                ", emps=" + emps +
                '}';
    }*/
}
