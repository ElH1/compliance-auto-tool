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

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.*;

import java.io.IOException;
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
    String iedmmPath = "src/main/java/compliance/instanceModel/motivating-scenario-1-iedmm.json";
    String model;

    ruleOne() {
        this.instanceModelPath = instanceModelPath;
        this.iedmmPath = iedmmPath;
    }

    public static void printJSON(JSONObject jsonObj) {
        //Iterating Key Set
        for (Object keyObj : jsonObj.keySet()) {
            String key = (String) keyObj;
            Object valObj = jsonObj.get(key);
            //If next entry is Object
            if (valObj instanceof JSONObject) {
                // call printJSON on nested object
                printJSON((JSONObject) valObj);
            }
            //iff next entry is nested Array
            else if (valObj instanceof JSONArray) {
                System.out.println("NestedArraykey : " + key);
                printJSONArray((JSONArray) valObj);

            } else {
                System.out.println("key : " + key);
                System.out.println("value : " + valObj.toString());
            }
        }
    }

    public static void printJSONArray(JSONArray jarray) {
        //Get Object from Array
        for (int i = 0; i < jarray.length(); i++) {
            printJSON(jarray.getJSONObject(i));
        }

    }

    public static void main(String[] args) throws ObjectNotFoundException {
        ruleOne item = new ruleOne();
        String currentInstanceModel = item.getInstance(item.instanceModelPath);
        System.out.println(item.detectRule(currentInstanceModel));
        // System.out.println(item.evaluateRule(currentInstanceModel));
    }

    String getInstance(String Path) {
        try {
            this.model = new String(Files.readAllBytes(Paths.get(Path)));
        } catch (IOException e) {
            e.printStackTrace();
            this.model = "Error";
        }
        return this.model;
    }

    /* ASSUMPTION: region property is always stored in the BOTTOM-MOST component */
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


    boolean detectRule(String instanceModel) throws ObjectNotFoundException {
        boolean status = false;
        JSONObject obj = new JSONObject(instanceModel);
        JSONObject components = obj.getJSONObject("components");
        JSONObject componentTypes = obj.getJSONObject("component_types");
        JSONObject relationTypes = obj.getJSONObject("relation_types");

        // JSONObject bottomMostObject = findLastComponent("UserDataTable", components);
        // System.out.println(bottomMostObject.toString());
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
                            String regionWebApplication = "";
                            String regionDatabase = "";
                            JSONObject bottomMostDBObject = findLastComponent(connectedComponent, components);
                            JSONObject bottomMostWebAppObject = findLastComponent(currentKey, components);
                            regionWebApplication = bottomMostWebAppObject.getJSONObject("properties").getString("region");
                            regionDatabase = bottomMostDBObject.getJSONObject("properties").getString("region");
                            return regionDatabase.equals(regionWebApplication);
                        }
                        // System.out.println("WebApplication name: " + currentKey + "and connected component name: " + connectedComponent + " and type: " + connectedComponentType);
                    }

                }
            }

        }
        throw new ObjectNotFoundException("Couldn't decide rule applicability");

    }

    void evaluateRule(String instanceModel) {
        JSONObject obj = new JSONObject(instanceModel);
        JSONArray relations = obj.getJSONObject("components").getJSONObject("PrivateInternalApp").getJSONArray("relations");
        for (int i = 0; i < relations.length(); i++) {
            String relation = relations.getJSONObject(i).getString("hosted_on");
            System.out.println(relation);
        }
        // System.out.println(obj);
        String privateApp = obj.getJSONObject("components").getJSONObject("PrivateInternalApp").getJSONObject("properties").getString("isPrivate");
        System.out.println(privateApp);


    }
}
