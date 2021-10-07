import compliance.framework.core.InstanceModelRetriever;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class InstanceModelRetrieverTest {

    String path;

    @Test(expected = IOException.class)
    public void nonexistentPathTest() throws IOException {
        InstanceModelRetriever retriever = new InstanceModelRetriever();
        path = "nonexistent/path";
        retriever.retrieveInstance(path);
    }

    @Test
    public void existentPathTest() throws IOException {
        InstanceModelRetriever retriever = new InstanceModelRetriever();
        path = "instanceModels/motivating-scenario-1-compliant.json";
        JSONObject result = retriever.retrieveInstance(path);
        assertTrue(result != null);
    }

    @Test
    public void returnedObjectTest() throws IOException {
        InstanceModelRetriever retriever = new InstanceModelRetriever();
        path = "instanceModels/motivating-scenario-1-compliant.json";
        JSONObject result = retriever.retrieveInstance(path);
        String stringModel = new String(Files.readAllBytes(Paths.get(path)));
        JSONObject expected = new JSONObject(stringModel);
        assertEquals(expected.toString(), result.toString());
    }
}
