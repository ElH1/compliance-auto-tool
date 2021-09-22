/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package com.company;

import compliance.rules.ruleOne;
import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

import java.io.IOException;

public class Main {
    String instanceModelPath = "src/main/java/compliance/instanceModel/motivating-scenario-1.json";
    String iedmmPath = "src/main/java/compliance/instanceModel/";

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
}
