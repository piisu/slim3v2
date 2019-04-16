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
import com.google.appengine.api.datastore.ShortBlob;

@Data
@Model
public class UploadedDataFragment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(lob = true)
    private byte[] bytes;

    private ShortBlob bytes2;

    /**
     * @return the bytes2
     */
    public ShortBlob getBytes2() {
        return bytes2;
    }

    /**
     * @param bytes2
     *            the bytes2 to set
     */
    public void setBytes2(ShortBlob bytes2) {
        this.bytes2 = bytes2;
    }


    @Setter(AccessLevel.NONE)
    private org.slim3.datastore.ModelRef<slim3.demo.model.UploadedData> uploadDataRef =
        new org.slim3.datastore.ModelRef<slim3.demo.model.UploadedData>(
            slim3.demo.model.UploadedData.class);

    private int index;
}
