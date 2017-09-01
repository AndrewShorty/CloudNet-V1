package de.dytanic.cloudnet.lib.document;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import de.dytanic.cloudnet.network.NetworkUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by Tareko on 21.05.2017.
 */
public class Document
        implements DocumentAbstract {

    protected static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().disableHtmlEscaping().create();
    protected static final JsonParser PARSER = new JsonParser();

    @Getter
    @Setter
    protected String name;
    @Getter
    @Setter
    private File file;

    protected JsonObject dataCatcher;

    public Document(String name)
    {
        this.name = name;
        this.dataCatcher = new JsonObject();
    }

    public Document(String name, JsonObject source)
    {
        this.name = name;
        this.dataCatcher = source;
    }

    public Document(File file, JsonObject jsonObject)
    {
        this.file = file;
        this.dataCatcher = jsonObject;
    }

    public Document(Document defaults)
    {
        this.dataCatcher = defaults.dataCatcher;
    }

    public Document(Document defaults, String name)
    {
        this.dataCatcher = defaults.dataCatcher;
        this.name = name;
    }

    public Document()
    {
        this.dataCatcher = new JsonObject();
    }

    public Document(JsonObject source)
    {
        this.dataCatcher = source;
    }

    public JsonObject obj()
    {
        return dataCatcher;
    }

    public boolean contains(String key)
    {
        return this.dataCatcher.has(key);
    }

    public Document append(String key, String value)
    {
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(String key, Number value)
    {
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    public Document append(String key, Boolean value)
    {
        this.dataCatcher.addProperty(key, value);
        return this;
    }

    @Override
    public Document append(String key, JsonElement value)
    {
        this.dataCatcher.add(key, value);
        return this;
    }

    public Document append(String key, List<String> value)
    {
        JsonArray jsonElements = new JsonArray();

        for (String b : value)
        {
            jsonElements.add(b);
        }

        this.dataCatcher.add(key, jsonElements);
        return this;
    }

    public Document append(String key, Document value)
    {
        this.dataCatcher.add(key, value.dataCatcher);
        return this;
    }

    @Deprecated
    public Document append(String key, Object value)
    {
        if (value == null) return this;
        this.dataCatcher.add(key, GSON.toJsonTree(value));
        return this;
    }

    public Document appendValues(java.util.Map<String, Object> values)
    {
        for(java.util.Map.Entry<String, Object> valuess : values.entrySet())
        {
            append(valuess.getKey(), valuess.getValue());
        }
        return this;
    }

    @Override
    public Document remove(String key)
    {
        this.dataCatcher.remove(key);
        return this;
    }

    public Set<String> keys()
    {
        Set<String> c = new HashSet<>();

        for (Map.Entry<String, JsonElement> x : dataCatcher.entrySet())
        {
            c.add(x.getKey());
        }

        return c;
    }

    public JsonElement get(String key)
    {
        if(!dataCatcher.has(key)) return null;
        return dataCatcher.get(key);
    }

    public String getString(String key)
    {
        if (!dataCatcher.has(key)) return null;
        return dataCatcher.get(key).getAsString();
    }

    public int getInt(String key)
    {
        if (!dataCatcher.has(key)) return 0;
        return dataCatcher.get(key).getAsInt();
    }

    public long getLong(String key)
    {
        if (!dataCatcher.has(key)) return 0L;
        return dataCatcher.get(key).getAsLong();
    }

    public double getDouble(String key)
    {
        if (!dataCatcher.has(key)) return 0D;
        return dataCatcher.get(key).getAsDouble();
    }

    public boolean getBoolean(String key)
    {
        if (!dataCatcher.has(key)) return false;
        return dataCatcher.get(key).getAsBoolean();
    }

    public float getFloat(String key)
    {
        if (!dataCatcher.has(key)) return 0F;
        return dataCatcher.get(key).getAsFloat();
    }

    public short getShort(String key)
    {
        if (!dataCatcher.has(key)) return 0;
        return dataCatcher.get(key).getAsShort();
    }

    public <T> T getObject(String key, Class<T> class_)
    {
        if (!dataCatcher.has(key)) return null;
        JsonElement element = dataCatcher.get(key);

        return GSON.fromJson(element, class_);
    }

    public Document getDocument(String key)
    {
        Document document = new Document(dataCatcher.get(key).getAsJsonObject());
        return document;
    }

    public JsonArray getArray(String key)
    {
        return dataCatcher.get(key).getAsJsonArray();
    }

    public String convertToJson()
    {
        return GSON.toJson(dataCatcher);
    }

    public String convertToJsonString()
    {
        return NetworkUtils.GSON.toJson(dataCatcher);
    }

    public boolean saveAsConfig(File backend)
    {

        if (backend == null) return false;

        if (backend.exists())
        {
            backend.delete();
        }

        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(backend), "UTF-8"))
        {
            GSON.toJson(dataCatcher, (writer));
            return true;
        } catch (IOException ex)
        {
            ex.getStackTrace();
        }
        return false;
    }

    public boolean saveAsConfig(String path)
    {
        return saveAsConfig(new File(path));
    }

    public static Document loadDocument(File backend)
    {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(backend), "UTF-8"))
        {
            JsonObject object = PARSER.parse(new BufferedReader(reader)).getAsJsonObject();
            return new Document(object);
        } catch (Exception ex)
        {
            ex.getStackTrace();
        }
        return new Document();
    }

    public Document loadToExistingDocument(File backend)
    {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(backend), "UTF-8"))
        {

            this.dataCatcher = PARSER.parse(reader).getAsJsonObject();
            this.file = backend;
            return this;
        } catch (Exception ex)
        {
            ex.getStackTrace();
        }
        return new Document();
    }

    public static Document load(String input)
    {
        try (InputStreamReader reader = new InputStreamReader(new StringBufferInputStream(input), "UTF-8"))
        {
            return new Document(PARSER.parse(new BufferedReader(reader)).getAsJsonObject());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return new Document();
    }

    public static Document load(JsonObject input)
    {
        return new Document(input);
    }

    public <T> T getObject(String key, Type type)
    {
        return GSON.fromJson(dataCatcher.get(key), type);
    }
}