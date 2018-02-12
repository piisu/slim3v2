package slim3.demo.model;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.InverseModelListRef;
import org.slim3.datastore.Model;
import org.slim3.datastore.Sort;

import com.google.appengine.api.datastore.Key;

@Data
@Model
public class UploadedData implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version = 0L;

    private String fileName;

    private int length;

    @Setter(AccessLevel.NONE)
    @Attribute(persistent = false)
    private org.slim3.datastore.InverseModelListRef<slim3.demo.model.UploadedDataFragment, slim3.demo.model.UploadedData> fragmentListRef =
        new org.slim3.datastore.InverseModelListRef<slim3.demo.model.UploadedDataFragment, slim3.demo.model.UploadedData>(
            slim3.demo.model.UploadedDataFragment.class,
            "uploadDataRef",
            this,
            new Sort("index"));

}