package com.zm.one2one.foreignkey;

/**
 * @ClassName Department
 * @Description TODO
 * @Version $
 **/
public class Department {
    private Integer deptId;
    private String deptName;

    private Manager manager;

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }
}
