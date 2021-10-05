import compliance.framework.core.InstanceModelRetriever;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertThrows;

public class InstanceModelRetrieverTest {

    String path;

    // @Test
    // public void testRetrieveInstance() throws IOException {
    //     InstanceModelRetriever retriever = new InstanceModelRetriever();
    //     path = "nonexistent/path";
    //     assertThrows(IOException.class, () -> {
    //         retriever.retrieveInstance(path);
    //     });
    //
    // }
}
