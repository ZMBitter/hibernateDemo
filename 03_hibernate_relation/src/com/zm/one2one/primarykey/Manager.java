package com.zm.one2one.primarykey;

/**
 * @ClassName Manager
 * @Description TODO
 * @Version $
 **/
public class Manager {
    private Integer mangerId;
    private String managerName;
    private Department dept;

    public Integer getMangerId() {
        return mangerId;
    }

    public void setMangerId(Integer mangerId) {
        this.mangerId = mangerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Department getDept() {
        return dept;
    }

    public void setDept(Department dept) {
        this.dept = dept;
    }
}
