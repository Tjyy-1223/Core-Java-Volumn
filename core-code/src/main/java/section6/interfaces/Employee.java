package section6.interfaces;

import java.util.Date;
import java.util.GregorianCalendar;

public class Employee implements Comparable<Employee>,Cloneable{
    private String name;
    private double salary;
    private Date hireDay;

    public Employee(String name, double salary) {
        this.name = name;
        this.salary = salary;
        hireDay = new Date();
    }

    public Employee clone() throws CloneNotSupportedException{
        // call Object,clone()
        Employee cloned = (Employee) super.clone();

        // clone mutable fields
        cloned.hireDay = (Date) hireDay.clone();
        return cloned;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public void setHireDay(int year,int month, int day){
        Date newHireDay = new GregorianCalendar(year,month - 1, day).getTime();
        hireDay.setTime(newHireDay.getTime() ) ;
    }

    public void raiseSalary(double byPercent){
        double raise = salary * byPercent/ 100;
        salary += raise;
    }

    /**
     * Compares employees by salary
     * @param other another Employee object
     * @return a negative value if this employee has a lower salary than
     * otherObject, 0 if the salaries are the same, a positive value otherwise
     */
    @Override
    public int compareTo(Employee other){
        return Double.compare(salary, other.salary);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", salary=" + salary +
                ", hireDay=" + hireDay +
                '}';
    }
}
