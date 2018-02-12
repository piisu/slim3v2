package slim3.demo.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModelRef;

import com.google.appengine.api.datastore.Key;

@Data
@Model
public class EmployeeProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    private Integer schemaVersion = 1;

    @Setter(AccessLevel.NONE)
    private org.slim3.datastore.ModelRef<slim3.demo.model.Employee> employeeRef =
        new org.slim3.datastore.ModelRef<slim3.demo.model.Employee>(
            slim3.demo.model.Employee.class);

    @Setter(AccessLevel.NONE)
    private org.slim3.datastore.ModelRef<slim3.demo.model.Project> projectRef =
        new org.slim3.datastore.ModelRef<slim3.demo.model.Project>(
            slim3.demo.model.Project.class);

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
        EmployeeProject other = (EmployeeProject) obj;
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
