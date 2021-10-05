/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

public class InstanceModelAnnotator {

    /**
     * @param issues
     * @param instanceModel
     * @return
     */
    /* annotates a provided instance model with provided issue */
    public JSONObject annotateModel(JSONObject[] issues, JSONObject instanceModel) {
        if (issues.length != 0) {
            JSONObject affectsRelationType = new JSONObject();
            JSONObject issueTypes = new JSONObject();
            JSONObject baseIssue = new JSONObject();
            JSONObject complianceIssue = new JSONObject();
            JSONObject baseProperties = new JSONObject();
            if (issues[0] != null) {
                instanceModel.put("issues", issues[0]);
            }
            int keyctr = 1; // ensures unique issue ID in JSON
            for (int i = 1; i < issues.length; i++) {
                if (issues[i] != null) {
                    Iterator<?> keys = issues[i].keySet().iterator();
                    while (keys.hasNext()) {
                        String currentKey = (String) keys.next();
                        instanceModel.getJSONObject("issues").put(currentKey + "-" + keyctr, issues[i].getJSONObject(currentKey));
                        if (issues[i] != null) {
                            if (!(issueTypes.has(issues[i].getJSONObject(currentKey).getString("type")))) {
                                JSONObject extend = new JSONObject();
                                extend.put("extends", "compliance_issue");
                                issueTypes.put(issues[i].getJSONObject(currentKey).getString("type"), extend);
                            }
                        }

                        keyctr++;
                    }
                }
            }
            affectsRelationType.put("extends", "null");
            affectsRelationType.put("properties", "");
            if (instanceModel.has("relation_types")) {
                instanceModel.getJSONObject("relation_types").put("affects", affectsRelationType);
                complianceIssue.put("extends", "base");
                baseProperties.put("ruleId", "string");
                baseProperties.put("message", "string");
                baseIssue.put("extends", "null");
                baseIssue.put("properties", baseProperties);
                issueTypes.put("base_issue", baseIssue);
                issueTypes.put("compliance_issue", complianceIssue);
                // issueTypes.put("compliance_issue", complianceIssue);
                instanceModel.put("issue_types", issueTypes);
            }
        }
        return instanceModel;
    }

    /**
     * @param contents
     * @param fileEnding
     * @throws IOException
     */
    /* saves a JSON object to a file at a provided location*/
    public void saveToFile(JSONObject contents, String file, String fileEnding) throws IOException {
        try {
            String destination = file + fileEnding;
            if (Files.notExists(Paths.get(destination))) {
                Files.createFile(Paths.get(destination));
                Files.write(Paths.get(destination), contents.toString(4).getBytes());
            } else {
                String newDestination = file + fileEnding;
                int ctr = 0;
                while (!Files.notExists(Paths.get(newDestination))) {
                    ++ctr;
                    newDestination = file + ctr + fileEnding;
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
