/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

/*  TODO: integration test(s)
 *  TODO: allow loading of class file from a given path, compile + load at runtime
 *  TODO: readme
 *  TODO: modify execution args! allow selection of whether to input paths directly, or file with rule description
 */

package compliance.framework.core;

import javassist.tools.rmi.ObjectNotFoundException;
import org.json.JSONObject;

import java.io.IOException;

public class Main {
    // String instanceModelPath = "instanceModels/motivating-scenario-1-noncompliant.json";
    // String iedmmPath = "instanceModels/iedmm";

    /* Arguments when running program:
     * 0. path to instance model file
     * 1. input path or file?
     * 2.-x. path to rule(s) on local filesystem
     */
    public static void main(String[] args) throws ObjectNotFoundException, IOException, ClassNotFoundException {
        String instanceModelPath = args[0];
        JSONObject[] issues = new JSONObject[args.length - 2];
        int ctrForIssues = 0;

        try {

            /* get the instance model */
            InstanceModelRetriever getModel = new InstanceModelRetriever();
            JSONObject instanceModel = getModel.getInstance(instanceModelPath);
            RuleDetector detector = new RuleDetector();
            RuleEvaluator evaluator = new RuleEvaluator();

            /* for all rules that were passed as args, execute their detector and evaluator */
            for (int i = 2; i < args.length; i++) {
                String ruleClassPath = args[i];
                /* check if rule applies to instance model */
                boolean applicable = detector.detectRule(instanceModel, ruleClassPath);

                /* if the rule applies, evaluate the rule and annotate the instance model */
                if (applicable) {
                    JSONObject result = (JSONObject) evaluator.evaluateRule(instanceModel, ruleClassPath);
                    if (result != null) {
                        issues[ctrForIssues] = result;
                    } else {
                        issues[ctrForIssues] = null;
                        System.out.println("rule is fulfilled with path " + args[i]);
                    }
                } else {
                    System.out.println("rule not applicable at position: " + args[i]);
                    issues[ctrForIssues] = null;
                }
                System.out.println("issue at position " + ctrForIssues + " is " + issues[ctrForIssues]);
                ctrForIssues++;
            }

            /* after all rules have been evaluated + their results collected, annotate the instance model with this information */
            InstanceModelAnnotator annotator = new InstanceModelAnnotator();
            JSONObject iedmm = annotator.annotateModel(issues, instanceModel);
            if (iedmm != null) {
                annotator.saveToFile(iedmm, "instanceModels/iedmm", "motivating-scenario-1-iedmm", ".json");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
