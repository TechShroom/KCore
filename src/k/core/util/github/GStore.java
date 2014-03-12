package k.core.util.github;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import k.core.util.github.GData.GDataError;
import k.core.util.github.gitjson.GithubJsonCreator;
import k.core.util.github.gitjson.GitHubJsonParser;
import k.core.util.netty.DataStruct;

import com.google.gson.*;

/**
 * Handles all storage, NOTHING gives this data, it pulls all. IT COMMANDS ALL.
 * and it gets one chance to do it.
 * 
 * @author Kenzie Togami
 *
 */
final class GStore {
    private static final JsonParser parser = GitHubJsonParser.parser;
    static final int AUTH_INDEX = 0, LAST_MOD_INDEX = 1, LAST_DATA_INDEX = 2,
            SCOPE_INDEX = 3;

    private static JsonObject buildGDataJSON(GData value) {
        GithubJsonCreator<JsonObject> gdata = GithubJsonCreator
                .getForObjectCreation(), headers = GithubJsonCreator
                .getForObjectCreation();
        gdata.add("rate", value.rate().json());
        gdata.add("rawdata", value.getData());
        Map<String, List<String>> gheads = value.getHeaders();
        for (Entry<String, List<String>> e : gheads.entrySet()) {
            headers.add(
                    e.getKey(),
                    GithubJsonCreator.arrayOf(e.getValue().toArray(
                            new String[0])));
        }
        gdata.add("headers", headers.result());
        gdata.add("errstate", value.getErrorState().toString());
        gdata.add("code", new JsonPrimitive(value.responseCode()));
        return gdata.result();
    }

    private static GData createGData(JsonElement value) {
        JsonObject obj = value.getAsJsonObject();
        GData out = new GData(GDataError.valueOf(obj.get("errstate")
                .getAsString()));
        RateLimit rate = RateLimit.fromJSON(obj.get("rate").getAsJsonObject());
        String raw = obj.get("rawdata").getAsString();
        Map<String, List<String>> gheads = new HashMap<String, List<String>>();
        for (Entry<String, JsonElement> e : obj.get("headers")
                .getAsJsonObject().entrySet()) {
            String key = e.getKey();
            JsonArray array = e.getValue().getAsJsonArray();
            ArrayList<String> v = new ArrayList<String>(array.size());
            Iterator<JsonElement> it = array.iterator();
            while (it.hasNext()) {
                v.add(it.next().getAsString());
            }
            gheads.put(key, v);
        }
        try {
            int code = obj.get("code").getAsInt();
            out.contentloaded(raw, rate.getRemaining(), rate.getLimit(),
                    rate.getResetTime(), gheads, code);
            return out;
        } catch (Exception e) {
            return null;
        }
    }

    public static void loadGitData() {
        File config = new File("./config/config.datastruct").getAbsoluteFile();
        try {
            config.getParentFile().mkdirs();
            if (config.createNewFile()) {
                return;
            }
        } catch (IOException e) {
            throw new IllegalStateException("path: " + config, e);
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(config);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = "", data = "";
            try {
                while ((line = br.readLine()) != null) {
                    data += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if (data.length() == 0) {
                // nothing to read here
                return;
            }
            DataStruct dataStruct = new DataStruct(data);
            GNet.authorization = (GAuth) dataStruct.get(AUTH_INDEX, null);
            try {
                Map<String, Long> lm = GNet.lastModsForUrls;
                JsonObject savedMap = parser.parse(
                        (String) dataStruct.get(LAST_MOD_INDEX, "{}"))
                        .getAsJsonObject();
                for (Entry<String, JsonElement> e : GitHubJsonParser
                        .getAsMapWithNullKeys(savedMap).entrySet()) {
                    lm.put(e.getKey(), e.getValue().getAsLong());
                }
                Map<String, GData> ld = GNet.lastDataForUrls;
                savedMap = parser.parse(
                        (String) dataStruct.get(LAST_DATA_INDEX, "{}"))
                        .getAsJsonObject();
                for (Entry<String, JsonElement> e : GitHubJsonParser
                        .getAsMapWithNullKeys(savedMap).entrySet()) {
                    GData gd = createGData(e.getValue());
                    if (gd != null) {
                        ld.put(e.getKey(), gd);
                    } else {
                        lm.remove(e.getKey());
                    }
                }
                GitHub.scope = parser.parse(
                        (String) dataStruct.get(SCOPE_INDEX, "[]"))
                        .getAsJsonArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static void storeGitData() {
        DataStruct dataStruct = new DataStruct();
        dataStruct.add(GNet.authorization);
        GithubJsonCreator<JsonObject> lastMod = GithubJsonCreator
                .getForObjectCreation();
        Map<String, Long> lm = GNet.lastModsForUrls;
        for (Entry<String, Long> e : lm.entrySet()) {
            lastMod.add(e.getKey(), new JsonPrimitive(e.getValue()));
        }
        dataStruct.add(lastMod.toString());
        GithubJsonCreator<JsonObject> lastData = GithubJsonCreator
                .getForObjectCreation();
        Map<String, GData> ld = GNet.lastDataForUrls;
        for (Entry<String, GData> e : ld.entrySet()) {
            lastData.add(e.getKey(), buildGDataJSON(e.getValue()));
        }
        dataStruct.add(lastData.toString());
        if (GitHub.scope != null) {
            dataStruct.add(GitHub.scope.toString());
        } else {
            dataStruct.add("[]");
        }
        File config = new File("./config/config.datastruct").getAbsoluteFile();
        try {
            config.getParentFile().mkdirs();
            config.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("path: " + config, e);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(config);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            fos.write(dataStruct.toString().getBytes());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                // ignore closing problems
            }
        }
    }
}
