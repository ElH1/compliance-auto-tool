package compliance.framework.core;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class InstanceModelAnnotator {

    /**
     * @param issue
     * @param instanceModel
     * @return
     */
    /* annotates a provided instance model with provided issue */
    public JSONObject annotateModel(JSONObject issue, JSONObject instanceModel) {
        instanceModel.put("issues", issue);
        JSONObject affectsRelationType = new JSONObject();
        affectsRelationType.put("extends", "null");
        affectsRelationType.put("properties", "");
        instanceModel.getJSONObject("relation_types").put("affects", affectsRelationType);
        JSONObject issueTypes = new JSONObject();
        JSONObject baseIssue = new JSONObject();
        JSONObject complianceIssue = new JSONObject();
        JSONObject baseProperties = new JSONObject();
        complianceIssue.put("extends", "base");
        baseProperties.put("ruleId", "string");
        baseProperties.put("message", "string");
        baseIssue.put("extends", "null");
        baseIssue.put("properties", baseProperties);
        issueTypes.put("base_issue", baseIssue);
        issueTypes.put("compliance_issue", complianceIssue);
        instanceModel.put("issue_types", issueTypes);
        return instanceModel;
    }

    /**
     * @param contents
     * @param path
     * @param filename
     * @param fileEnding
     * @throws IOException
     */
    /* saves a JSON object to a file at a provided location*/
    public void saveToFile(JSONObject contents, String path, String filename, String fileEnding) throws IOException {
        try {
            String destination = path + filename + fileEnding;
            if (Files.notExists(Paths.get(destination))) {
                Files.createFile(Paths.get(destination));
                Files.write(Paths.get(destination), contents.toString(4).getBytes());
            } else {
                String newDestination = path + filename + fileEnding;
                int ctr = 0;
                while (!Files.notExists(Paths.get(newDestination))) {
                    ++ctr;
                    newDestination = path + filename + ctr + fileEnding;
                }
                Files.createFile(Paths.get(newDestination));
                Files.write(Paths.get(newDestination), contents.toString(4).getBytes());
                System.out.println("File already existed! Written to " + newDestination);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Something unexpected went wrong.");
        }

    }

}
