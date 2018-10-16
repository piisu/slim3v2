package org.slim3plus.model.mr;

import com.google.appengine.api.datastore.Key;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KeyRange implements Serializable {
    private static final long serialVersionUID = 1L;

    private Key lowerBound;

    private Key upperBound;

}
