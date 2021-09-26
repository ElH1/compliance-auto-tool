/* Copyright IBM 2021 under Apache 2.0 license */
/* Author: Elena Heldwein */

package compliance.framework.core;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.github.edmm.core.*;

public class InstanceModelRetriever {

    /**
     * @param path
     * @return
     */
    /* gets an instance model from a JSON file at a provided path */
    public JSONObject getInstance(String path) {
        try {
            String stringModel = new String(Files.readAllBytes(Paths.get(path)));
            return new JSONObject(stringModel);
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject("Error", "Couldn't get instance model");
        }
    }


}
