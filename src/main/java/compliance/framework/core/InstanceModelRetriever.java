/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import io.github.edmm.core.parser.EntityGraph;
import org.jgrapht.GraphType;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import io.github.edmm.core.*;

public class InstanceModelRetriever {

    public static void main(String[] args) throws FileNotFoundException {
        InstanceModelRetriever test = new InstanceModelRetriever();
        EntityGraph instanceModel = test.getInstance("src/main/java/compliance/instanceModel/motivating-scenario-1.yml");
        String owner = instanceModel.getOwner();
        // System.out.println("getParticipants: " + instanceModel.getParticipants()); // has no participants
        Set edgeset = instanceModel.edgeSet();
        GraphType type = instanceModel.getType();
        Set vertices = instanceModel.vertexSet();
        String testComponent = instanceModel.getParticipantFromComponentName("PrivateInternalApp");
        
        // System.out.println("toString: " + instanceModel.toString());
    }

    /**
     * @param path
     * @return
     */
    /* gets an instance model from a JSON file at a provided path */
    public EntityGraph getInstance(String path) throws FileNotFoundException {
        InputStream is = new FileInputStream(path);
        EntityGraph instanceModel = new EntityGraph(is);
        System.out.println(instanceModel);
        return instanceModel;
        // try {
        //     String stringModel = new String(Files.readAllBytes(Paths.get(path)));
        //     return new JSONObject(stringModel);
        // } catch (IOException e) {
        //     e.printStackTrace();
        //     return new JSONObject("Error", "Couldn't get instance model");
        // }
    }


}
