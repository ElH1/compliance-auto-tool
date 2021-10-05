package compliance.rules;

import org.json.JSONObject;

interface complianceRule {

    /* detect whether rule applies to instanceModel */
    public boolean detectRule(JSONObject instanceModel);

    /* detect whether rule is fulfilled in instanceModel */
    public JSONObject evaluateRule(JSONObject instanceModel);

}
