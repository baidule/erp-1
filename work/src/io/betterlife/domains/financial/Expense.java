package io.betterlife.domains.financial;

import io.betterlife.domains.BaseObject;
import io.betterlife.domains.security.User;
import io.betterlife.rest.Form;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Author: Lawrence Liu(xqinliu@cn.ibm.com)
 * Date: 10/31/14
 * This is the expense Entity definition
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Expense.getById", query = "SELECT e FROM Expense e WHERE e.id = :id "),
    @NamedQuery(name = "Expense.getAll",  query = "SELECT e FROM Expense e")
})
public class Expense extends BaseObject {

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        setValue("expenseCategory", expenseCategory);
    }

    @ManyToOne
    @Form(RepresentField="name")
    public ExpenseCategory getExpenseCategory() {
        return getValue("expenseCategory");
    }

    public void setCostCenter(CostCenter costCenter) {
        setValue("costCenter", costCenter);
    }

    @ManyToOne
    @Form(RepresentField="name")
    public CostCenter getCostCenter() {
        return getValue("costCenter");
    }

    public void setUser(User user) {
        setValue("user", user);
    }

    @ManyToOne
    @Form(RepresentField="username")
    public User getUser() {
        return getValue("user");
    }

    public void setAmount(BigDecimal amount) {
        setValue("amount", amount);
    }

    public BigDecimal getAmount() {
        return getValue("amount");
    }

    public void setRemark(String remark) {
        setValue("remark", remark);
    }

    public String getRemark() {
        return getValue("remark");
    }

    public void setDate(Date date) {
        setValue("date", date);
    }

    public Date getDate() {
        return getValue("date");
    }
}
