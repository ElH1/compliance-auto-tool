import compliance.framework.core.InstanceModelAnnotator;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceModelAnnotatorTest {
    JSONObject instanceModel = new JSONObject();
    JSONObject expected = new JSONObject();
    JSONObject newIssue = new JSONObject();
    JSONObject[] issues = new JSONObject[2];

    @Before
    public void init() {
        newIssue.put("testIssue", "Test");
        expected.put("issues", newIssue);
        for (int i = 0; i < issues.length; i++) {
            issues[i] = new JSONObject();
        }
        issues[0].put("testIssue", "Test");
        issues[1] = null;
        instanceModel.put("components", "placeholder");
    }

    @Test
    public void testAnnotateModel() {
        InstanceModelAnnotator annotator = new InstanceModelAnnotator();
        JSONObject result = annotator.annotateModel(issues, instanceModel);
        assertTrue(result.has("issues"));
    }
}
