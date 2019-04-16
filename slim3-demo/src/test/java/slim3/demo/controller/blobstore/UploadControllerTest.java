package slim3.demo.controller.blobstore;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.tester.ControllerTestCase;

import slim3.demo.model.Blobstore;

import com.google.appengine.api.datastore.Key;

public class UploadControllerTest extends ControllerTestCase {

    @Test
    public void run() throws Exception {
        String keyString = "hoge";
        tester.addBlobKey("formFile", keyString);
        tester.start("/blobstore/upload");
        UploadController controller = tester.getController();
        assertThat(controller, is(notNullValue()));
        assertThat(tester.isRedirect(), is(true));
        assertThat(tester.getDestinationPath(), is("/blobstore/"));
        Key key = Datastore.createKey(Blobstore.class, keyString);
        Datastore.get(key);
    }
}
