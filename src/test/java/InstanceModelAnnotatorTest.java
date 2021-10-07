import compliance.framework.core.InstanceModelAnnotator;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InstanceModelAnnotatorTest {
    JSONObject instanceModel;
    JSONObject expected;
    JSONObject newIssue;
    JSONObject[] issues;

    @Before
    public void init() {
        instanceModel = new JSONObject();
        expected = new JSONObject();
        newIssue = new JSONObject();
        issues = new JSONObject[2];
    }

    @Test
    public void annotateWithIssues() {
        newIssue.put("testIssue", "Test");
        expected.put("issues", newIssue);
        for (int i = 0; i < issues.length; i++) {
            issues[i] = new JSONObject();
        }
        issues[0].put("testIssue", "Test");
        issues[1] = null;
        instanceModel.put("components", "placeholder");
        InstanceModelAnnotator annotator = new InstanceModelAnnotator();
        JSONObject result = annotator.annotateModel(issues, instanceModel);
        assertTrue(result.has("issues"));
    }

    @Test
    public void annotateWithIssues2() {
        newIssue.put("testIssue", "Test");
        for (int i = 0; i < issues.length; i++) {
            issues[i] = new JSONObject();
        }
        issues[0].put("testIssue", "Test");
        issues[1] = null;
        instanceModel.put("components", "placeholder");
        expected.put("components", "placeholder");
        expected.put("issues", newIssue);
        InstanceModelAnnotator annotator = new InstanceModelAnnotator();
        JSONObject result = annotator.annotateModel(issues, instanceModel);
        assertEquals(expected.toString(), result.toString());
    }

    @Test
    public void annotateWithoutIssues() {
        newIssue = null;
        expected.put("components", "placeholder");
        for (int i = 0; i < issues.length; i++) {
            issues[i] = new JSONObject();
        }
        issues[0] = null;
        issues[1] = newIssue;
        instanceModel.put("components", "placeholder");
        InstanceModelAnnotator annotator = new InstanceModelAnnotator();
        JSONObject result = annotator.annotateModel(issues, instanceModel);
        assertEquals(expected.toString(), result.toString());
    }
}
