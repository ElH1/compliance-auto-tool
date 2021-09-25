package compliance.rules;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class ruleOneDetector {

    String dbKey;
    String webAppKey;

    /**
     * @param instanceModel
     * @return
     * @throws ObjectNotFoundException
     */
    /* detects whether evaluation for CR 1 has to be executed, i.e. if rule applies to instance model */
    public boolean detectRule(JSONObject instanceModel) throws ObjectNotFoundException {
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
                                dbKey = connectedComponent;
                                webAppKey = currentKey;
                                status = true;
                            }
                        }
                    }
                }
            }
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ObjectNotFoundException("Object wasn't found");
        }
    }

}
