/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.rules;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;


/**
 *
 */
/* this rule detects applicability of and evaluates CR 1 */
public class exampleRule implements complianceRule {
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
            // catches and ignores when a requested key doesn't exist, replace with if x.has(key) above.
        }
        return components.getJSONObject(componentKey);
    }

    /**
     * @param instanceModel
     * @return
     */
    /* detects whether evaluation for CR 1 has to be executed, i.e. if rule applies to instance model */
    public boolean detectRule(JSONObject instanceModel) {
        try {
            boolean status = false;
            JSONObject components = instanceModel.getJSONObject("components");
            Iterator<?> keys = components.keySet().iterator();
            /* iterate through all components in instance model */
            while (keys.hasNext()) {
                String currentKey = (String) keys.next();
                JSONObject currentComponent = components.getJSONObject(currentKey);
                String componentType = currentComponent.getString("type");
                /* check if the component is of type WebApplication */
                if (componentType.equals("WebApplication")) {
                    JSONArray relations = components.getJSONObject(currentKey).getJSONArray("relations");
                    for (int i = 0; i < relations.length(); i++) {
                        /* check if the component of type WebApplication connects to any other components */
                        if (relations.getJSONObject(i).toString().contains("connects_to")) {
                            String connectedComponent = relations.getJSONObject(i).getString("connects_to");
                            String connectedComponentType = components.getJSONObject(connectedComponent).getString("type");
                            /* check whether the component that the WebApplication connects to is a database type component */
                            if (connectedComponentType.equals("RelationalDB") || connectedComponentType.equals("database")) {
                                status = true;
                            }
                        }
                    }
                }
            }
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        }
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
            String dbKey = "";
            String webAppKey = "";

            /* Retrieve affected components + starting points for traversal */
            Iterator<?> keys = components.keySet().iterator();
            while (keys.hasNext()) {
                String currentKey = (String) keys.next();
                JSONObject currentComponent = components.getJSONObject(currentKey);
                String componentType = currentComponent.getString("type");
                /* check if the component is of type WebApplication */
                if (componentType.equals("WebApplication")) {
                    JSONArray relations = components.getJSONObject(currentKey).getJSONArray("relations");
                    for (int i = 0; i < relations.length(); i++) {
                        /* check if the component of type WebApplication connects to any other components */
                        if (relations.getJSONObject(i).toString().contains("connects_to")) {
                            String connectedComponent = relations.getJSONObject(i).getString("connects_to");
                            String connectedComponentType = components.getJSONObject(connectedComponent).getString("type");
                            /* check whether the component that the WebApplication connects to is a database type component */
                            if (connectedComponentType.equals("RelationalDB") || connectedComponentType.equals("database")) {
                                dbKey = connectedComponent;
                                webAppKey = currentKey;
                            }
                        }
                    }
                }
            }

            /* get the bottom-most components in each stack */
            JSONObject bottomMostDBObject = findLastComponent(dbKey, components);
            JSONObject bottomMostWebAppObject = findLastComponent(webAppKey, components);
            /* get the region of bottom-most components */
            regionWebApplication = bottomMostWebAppObject.getJSONObject("properties").getString("region");
            regionDatabase = bottomMostDBObject.getJSONObject("properties").getString("region");
            /* compare the regions */
            boolean equality = regionDatabase.equals(regionWebApplication);
            if (equality) {
                // rule OK
                return null;
            } else {
                /* construct and return issue */
                JSONObject issue = new JSONObject();
                JSONArray affects_component = new JSONArray();
                JSONObject compositeIssue = new JSONObject();
                JSONObject affects = new JSONObject();
                // the two affected components are currently hard-coded! would require tracking in recursive method --> storage of the value in PRIOR execution
                affects_component.put("AmazonRDS");
                affects_component.put("Instance2-WebApp");

                compositeIssue.put("ruleId", "CR 1");
                compositeIssue.put("type", "IncorrectPropertyValue");
                compositeIssue.put("message", "Database and WebApplication don't have the same value for property region");
                affects.put("affects", affects_component);
                compositeIssue.put("relations", affects);
                issue.put("incorrectProperty-2", compositeIssue);
                return issue;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
