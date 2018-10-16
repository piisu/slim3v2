package org.slim3plus.model.mr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.appengine.api.datastore.Key;
import lombok.Data;
import org.slim3.datastore.Attribute;
import org.slim3.datastore.Datastore;
import org.slim3.datastore.Model;

import java.io.Serializable;
import java.util.Map;

@Data
@Model(schemaVersion = 1)
public class MapWorker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    private Key key;

    @Attribute(version = true)
    private Long version;

    /**
     * Returns the key.
     *
     * @return the key
     */
    @JsonIgnore
    public Key getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

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
        MapWorker other = (MapWorker) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }


    public static Key createKey(Key mapReduceKey, int index) {
        return Datastore.createKey(MapWorker.class,
                String.format("/%d/%d", mapReduceKey.getId(), index));
    }

    @Attribute(lob = true)
    private Class<? extends MapReduceTask<?>> taskClass;

    private boolean complete;

    @Attribute(lob = true)
    private Map<String, Object> params;

    @Attribute(lob = true)
    private KeyRange keyRange;

    @Attribute(lob = true)
    private KeyRange processingKeyRange;

    @Attribute(lob = true)
    private MapperContext context = new MapperContext();
}
