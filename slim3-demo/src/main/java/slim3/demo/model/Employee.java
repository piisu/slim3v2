package slim3.demo.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.InverseModelListRef;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

@Data
@Model
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    @Sync(true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    private Integer schemaVersion = 1;

    @Setter(AccessLevel.NONE)
    private org.slim3.datastore.ModelRef<slim3.demo.model.Address> addressRef =
        new org.slim3.datastore.ModelRef<slim3.demo.model.Address>(
            slim3.demo.model.Address.class);

    @Setter(AccessLevel.NONE)
    private org.slim3.datastore.ModelRef<slim3.demo.model.Department> departmentRef =
        new org.slim3.datastore.ModelRef<slim3.demo.model.Department>(
            slim3.demo.model.Department.class);

    @Setter(AccessLevel.NONE)
    @Attribute(persistent = false)
    private org.slim3.datastore.InverseModelListRef<slim3.demo.model.EmployeeProject, slim3.demo.model.Employee> employeeProjectListRef =
        new org.slim3.datastore.InverseModelListRef<slim3.demo.model.EmployeeProject, slim3.demo.model.Employee>(
            slim3.demo.model.EmployeeProject.class,
            "employeeRef",
            this);
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Employee other = (Employee) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }

}
