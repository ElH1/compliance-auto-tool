/* ADD COPYRIGHT DISCLAIMER */

package compliance.rules;

// EDMM MODEL ENTITIES --> stored for future use, not relevant to current implementation

import io.github.edmm.model.DeploymentModel;
import io.github.edmm.model.component.Compute;
import io.github.edmm.model.component.Mom;
import io.github.edmm.model.component.Database;
import io.github.edmm.model.component.Dbaas;
import io.github.edmm.model.component.Paas;
import io.github.edmm.model.component.RootComponent;
import io.github.edmm.model.component.SoftwareComponent;
import io.github.edmm.model.component.WebApplication;
import io.github.edmm.model.component.WebServer;
import io.github.edmm.model.relation.RootRelation;
import io.github.edmm.model.relation.ConnectsTo;
import io.github.edmm.model.relation.HostedOn;

// PARSER
import io.github.edmm.core.parser.Entity;
import io.github.edmm.core.parser.EntityGraph;
import io.github.edmm.core.parser.EntityId;
import io.github.edmm.core.parser.MappingEntity;
import io.github.edmm.core.parser.ScalarEntity;
import io.github.edmm.core.parser.SequenceEntity;
// import io.github.edmm.core.yaml;

import io.kubernetes.client.JSON;
import javassist.tools.rmi.ObjectNotFoundException;
import org.json.*;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

// yaml example instance model:
// https://github.com/UST-EDMM/edmm/blob/master/edmm-core/src/test/resources/templates/scenario_iaas.yml


// this rule detects applicability and evaluates CR 1
public class ruleOne {
    String instanceModelPath = "src/main/java/compliance/instanceModel/motivating-scenario-1.json";
    String iedmmPath = "src/main/java/compliance/instanceModel/";
    String dbKey;
    String webAppKey;

    ruleOne() {
        this.instanceModelPath = instanceModelPath;
        this.iedmmPath = iedmmPath;
    }

    public static void main(String[] args) throws ObjectNotFoundException, IOException {
        ruleOne item = new ruleOne();
        JSONObject currentInstanceModel = item.getInstance(item.instanceModelPath);
        // System.out.println(currentInstanceModel);

        System.out.println(item.detectRule(currentInstanceModel));
        JSONObject evaluationResult = item.evaluateRule(currentInstanceModel);
        System.out.println(evaluationResult);
        JSONObject iedmm = item.annotateModel(evaluationResult, currentInstanceModel);
        item.saveToFile(iedmm, item.iedmmPath, "motivating-scenario-1-iedmm", ".json");

    }

    /* gets an instance model from a JSON file at a provided path */
    JSONObject getInstance(String Path) {
        try {
            String stringModel = new String(Files.readAllBytes(Paths.get(Path)));
            return new JSONObject(stringModel);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject("Error", "Couldn't get instance model");
        }
    }

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

        }
        return components.getJSONObject(componentKey);
    }


    /* detects whether evaluation for CR 1 has to be executed, i.e. if rule applies to instance model */
    boolean detectRule(JSONObject instanceModel) throws ObjectNotFoundException {
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
                        if (connectedComponentType.equals("RelationalDB") || connectedComponentType.equals("Database")) {
                            dbKey = connectedComponent;
                            webAppKey = currentKey;
                            status = true;
                        }
                    }
                }
            }
        }
        if (status == true) {
            return true;
        } else {
            return false;
        }
    }

    /* evaluates whether CR 1 (WebApp and DB have same region) is fulfilled or not */
    JSONObject evaluateRule(JSONObject instanceModel) {
        JSONObject components = instanceModel.getJSONObject("components");
        String regionWebApplication = "";
        String regionDatabase = "";
        /* get the bottom-most components in each stack */
        JSONObject bottomMostDBObject = findLastComponent(dbKey, components);
        JSONObject bottomMostWebAppObject = findLastComponent(webAppKey, components);
        /* get the region of bottom-most components */
        regionWebApplication = bottomMostWebAppObject.getJSONObject("properties").getString("region");
        regionDatabase = bottomMostDBObject.getJSONObject("properties").getString("region");
        /* compare the regions */
        boolean equality = regionDatabase.equals(regionWebApplication);
        if (equality == true) {
            JSONObject result = new JSONObject();
            result.put("rule OK with ID", "CR 1");
            return result;
        } else {
            /* construct and return issue */
            JSONObject issue = new JSONObject();
            JSONArray affects_component = new JSONArray();
            JSONObject compositeIssue = new JSONObject();
            affects_component.put("UserDataTable");
            affects_component.put("AmazonRDS");
            affects_component.put("WebApp");
            affects_component.put("Apache2");
            affects_component.put("WebAppServer");
            affects_component.put("Instance2-WebApp");
            compositeIssue.put("ruleId", "CR 1");
            compositeIssue.put("issueTitle", "unequalRegion");
            compositeIssue.put("message", "Database and WebApplication not in the same region");
            compositeIssue.put("relations", affects_component);
            issue.put("unequalRegion", compositeIssue);
            return issue;
        }
    }

    /* annotates a provided instance model with provided issue */
    JSONObject annotateModel(JSONObject issue, JSONObject instanceModel) {
        instanceModel.put("issues", issue);
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

    /* saves a JSON object to a file at a provided location*/
    void saveToFile(JSONObject contents, String path, String filename, String fileEnding) throws IOException {
        try {
            String destination = path + filename + fileEnding;
            if (Files.notExists(Paths.get(destination))) {
                Files.createFile(Paths.get(destination));
                Files.write(Paths.get(destination), contents.toString(4).getBytes());
            } else {
                String newDestination = path + filename + fileEnding;
                int ctr = 0;
                while (!Files.notExists(Paths.get(newDestination))) {
                    ctr = ++ctr;
                    newDestination = path + filename + ctr + fileEnding;
                }
                Files.createFile(Paths.get(newDestination));
                Files.write(Paths.get(newDestination), contents.toString(4).getBytes());
                System.out.println("File already existed! Written to " + newDestination);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
