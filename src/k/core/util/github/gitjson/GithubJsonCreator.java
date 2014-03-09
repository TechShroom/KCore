package k.core.util.github.gitjson;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import com.google.gson.*;

public abstract class GithubJsonCreator<T extends JsonElement> {
    private static final class JArray extends GithubJsonCreator<JsonArray> {
        private ArrayList<JsonElement> elems = new ArrayList<JsonElement>();

        @Override
        public JsonArray result() {
            JsonArray array = new JsonArray();
            for (JsonElement je : elems) {
                array.add(je);
            }
            return array;
        }

        @Override
        public JArray add(JsonElement je) {
            return add(je, -1);
        }

        @Override
        public JArray add(JsonElement je, int index) {
            if (index == -1) {
                elems.add(je);
                return this;
            }
            elems.add(index, je);
            return this;
        }

        @Override
        public int size() {
            return elems.size();
        }

        @Override
        protected Class<JsonArray> type() {
            return JsonArray.class;
        }
    }

    private static final class JObject extends GithubJsonCreator<JsonObject> {
        JsonObject obj = new JsonObject();

        @Override
        public JsonObject result() {
            return obj;
        }

        @Override
        public JObject add(JsonElement je, int index) {
            JsonObject toAdd = je.getAsJsonObject();
            Set<Entry<String, JsonElement>> map = toAdd.entrySet();
            for (Entry<String, JsonElement> entry : map) {
                obj.add(entry.getKey(), entry.getValue());
            }
            return this;
        }

        @Override
        public int size() {
            return obj.entrySet().size();
        }

        @Override
        protected Class<JsonObject> type() {
            return JsonObject.class;
        }

    }

    private static final class JPrimitive extends
            GithubJsonCreator<JsonPrimitive> {
        private JsonPrimitive obj = null;

        @Override
        public JsonPrimitive result() {
            return obj;
        }

        @Override
        public JPrimitive add(JsonElement je, int index) {
            obj = je.getAsJsonPrimitive();
            return this;
        }

        @Override
        public int size() {
            return obj == null ? 0 : 1;
        }

        @Override
        protected Class<JsonPrimitive> type() {
            return JsonPrimitive.class;
        }
    }

    public static GithubJsonCreator<JsonObject> getForObjectCreation() {
        return new JObject();
    }

    public static GithubJsonCreator<JsonArray> getForArrayCreation() {
        return new JArray();
    }

    public static GithubJsonCreator<JsonPrimitive> getForPrimitiveCreation() {
        return new JPrimitive();
    }

    public static JsonObject createKeyValuePair(String key, JsonElement object) {
        JsonObject obj = new JsonObject();
        obj.add(key, object);
        return obj;
    }

    public static JsonArray arrayOf(String... strings) {
        JsonArray array = new JsonArray();
        if (strings != null) {
            for (String s : strings) {
                array.add(new JsonPrimitive(s));
            }
        }
        return array;
    }

    public abstract T result();

    public abstract GithubJsonCreator<T> add(JsonElement je, int index);

    public abstract int size();

    protected abstract Class<T> type();

    public GithubJsonCreator<T> add(JsonElement je) {
        return add(je, size() - 1);
    }

    public GithubJsonCreator<T> add(String key, String value) {
        return add(key, new JsonPrimitive(value));
    }

    public GithubJsonCreator<T> add(String key, JsonElement value) {
        if (type() == JsonObject.class) {
            return add(createKeyValuePair(key, value));
        } else {
            throw new IllegalStateException(
                    "T is not JsonObject, cannot map key -> values");
        }
    }

    @Override
    public String toString() {
        return result().toString();
    }
}
