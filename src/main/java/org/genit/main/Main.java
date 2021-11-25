package org.genit.main;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {

        //Get task collection
        List<Callable<String>> pathCallableList = new ArrayList<>();

        JSONObject pathsObject = (JSONObject)getJsonFile().get("paths");
        Iterator<String> keys = pathsObject.keySet().iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            if (pathsObject.get(key) != null) {
                pathCallableList.add(pathCallable(key, (JSONObject) pathsObject.get(key)));
            }
        }

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        try {
            List<Future<String>> resultFutures = executorService.invokeAll(pathCallableList);

            for (Future<String> future : resultFutures) {
                System.out.println(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //shutting down the executor service
        executorService.shutdown();

    }

    public static Callable pathCallable(String path, JSONObject pathObject) {
        return () -> "Create template with the path : " + path + " in thread - " + Thread.currentThread().getName() + " | from object " + pathObject;
    }

    public static JSONObject getJsonFile() {

        JSONParser jsonParser = new JSONParser();
        JSONObject pathObject = null;
        try (FileReader reader = new FileReader("./data.json")) {
            Object obj = jsonParser.parse(reader);
            pathObject = (JSONObject) obj;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return pathObject;
    }
}
