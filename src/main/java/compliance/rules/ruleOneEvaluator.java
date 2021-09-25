package compliance.rules;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class ruleOneEvaluator {

    String dbKey = "placeholder";
    String webAppKey = "placeholder";

    public static void main(String[] args) throws ObjectNotFoundException, IOException {
        System.out.println("main evaluator executed.");
        ruleOneEvaluator item = new ruleOneEvaluator();
        // JSONObject currentInstanceModel = item.getInstance(item.instanceModelPath);
        // if (item.detectRule(currentInstanceModel)) {
        //     JSONObject evaluationResult = item.evaluateRule(currentInstanceModel);
        //     JSONObject iedmm = item.annotateModel(evaluationResult, currentInstanceModel);
        //     item.saveToFile(iedmm, item.iedmmPath, "motivating-scenario-1-iedmm", ".json");
        // }
    }

    /**
     * @param componentKey
     * @param components
     * @return
     */
    /* ASSUMPTION: region property is always stored in the BOTTOM-MOST component */
    /* Helper method to get bottom-most component of a provided stack */
    JSONObject findLastComponent(String componentKey, JSONObject components) {
        String nextKey = "";
        try {
            JSONArray relations = components.getJSONObject(componentKey).getJSONArray("relations");
            if (relations.toString().contains("hosted_on")) {
                for (int i = 0; i < relations.length(); i++) {
                    if (relations.getJSONObject(i).toString().contains("hosted_on")) {
                        nextKey = relations.getJSONObject(i).getString("hosted_on");
                    }
                }

            }
            return findLastComponent(nextKey, components);
        } catch (Exception e) {
            // catches and ignores when a requested key doesn't exist.
        }
        return components.getJSONObject(componentKey);
    }

    /**
     * @param instanceModel
     * @return
     */
    /* evaluates whether CR 1 (WebApp and DB have same region) is fulfilled or not */
    public JSONObject evaluateRule(JSONObject instanceModel) {
        try {
            JSONObject components = instanceModel.getJSONObject("components");
            String regionWebApplication;
            String regionDatabase;
            /* get the bottom-most components in each stack */
            JSONObject bottomMostDBObject = findLastComponent(dbKey, components);
            JSONObject bottomMostWebAppObject = findLastComponent(webAppKey, components);
            /* get the region of bottom-most components */
            regionWebApplication = bottomMostWebAppObject.getJSONObject("properties").getString("region");
            regionDatabase = bottomMostDBObject.getJSONObject("properties").getString("region");
            /* compare the regions */
            boolean equality = regionDatabase.equals(regionWebApplication);
            if (equality) {
                JSONObject result = new JSONObject();
                result.put("rule OK with ID", "CR 1");
                return result;
            } else {
                /* construct and return issue */
                JSONObject issue = new JSONObject();
                JSONArray affects_component = new JSONArray();
                JSONObject compositeIssue = new JSONObject();
                JSONObject affects = new JSONObject();
                affects_component.put("UserDataTable");
                affects_component.put("AmazonRDS");
                affects_component.put("WebApp");
                affects_component.put("Apache2");
                affects_component.put("WebAppServer");
                affects_component.put("Instance2-WebApp");
                compositeIssue.put("ruleId", "CR 1");
                compositeIssue.put("issueTitle", "unequalRegion");
                compositeIssue.put("message", "Database and WebApplication not in the same region");
                affects.put("affects", affects_component);
                compositeIssue.put("relations", affects);
                issue.put("unequalRegion", compositeIssue);
                return issue;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
