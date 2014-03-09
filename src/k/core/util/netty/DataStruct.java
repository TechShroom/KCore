package k.core.util.netty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class DataStruct {
    protected static final String SPLIT_PAIRS = ";", SPLIT_KEYVALUE = "|",
            SPLIT_CLASS = "`";
    protected static final char KEY_STRING = 's', KEY_LONG = 'l',
            KEY_INT = 'i', KEY_CHAR = 'c', KEY_BYTE = 'b', KEY_FLOAT = 'f',
            KEY_DOUBLE = 'd', KEY_BOOL = 'B', KEY_OTHER = 'o';
    private List<Object> dataValues = new ArrayList<Object>();

    /**
     * The 'decode' constructor. Used for decoding the data to values.
     * 
     * @param data
     *            - the data string
     */
    public DataStruct(String data) {
        dataValues = decodeData(data);
    }

    private List<Object> decodeData(String s) {
        String[] splitToCopy = s.split(SPLIT_PAIRS);
        Object[] out = new Object[splitToCopy.length];
        System.arraycopy(splitToCopy, 0, out, 0, splitToCopy.length);
        for (int i = 0; i < out.length; i++) {
            String pair = (String) out[i];
            String[] key_val = pair.split("\\Q" + SPLIT_KEYVALUE + "\\E");
            try {
                out[i] = decode(key_val[0].toCharArray()[0], key_val[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Broken pair " + pair);
                out[i] = "<invalid>";
            }
        }
        return Arrays.asList(out);
    }

    private Object decode(char key, String val) {
        switch (key) {
        case KEY_BOOL:
            return Boolean.valueOf(val);
        case KEY_BYTE:
            return Byte.valueOf(val);
        case KEY_CHAR:
            return new String(DatatypeConverter.parseBase64Binary(val));
        case KEY_DOUBLE:
            return Double.valueOf(val);
        case KEY_FLOAT:
            return Float.valueOf(val);
        case KEY_INT:
            return Integer.valueOf(val);
        case KEY_LONG:
            return Long.parseLong(val);
        case KEY_OTHER:
            return decodeOther(new String(
                    DatatypeConverter.parseBase64Binary(val)));
        case KEY_STRING:
            return new String(DatatypeConverter.parseBase64Binary(val));
        default:
            System.err.println("Broken key " + key);
        }
        return null;
    }

    private Object decodeOther(String val) {
        String[] classAndJson = val.split(SPLIT_CLASS);
        try {
            Object o = new Gson().fromJson(classAndJson[1],
                    Class.forName(classAndJson[0]));
            return o;
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The 'encode' constructor. Used for encoding the values to a data String.
     * 
     * @param values
     *            - the data values
     */
    public DataStruct(Object[] values) {
        dataValues = Arrays.asList(values);
    }

    /**
     * A second encode constructor that passes nothing to start with.
     */
    public DataStruct() {
        this(new Object[0]);
    }

    private String encodeData(List<Object> enc) {
        String out = "";
        for (Object o : enc) {
            try {
                out += encode(o);
            } catch (NullPointerException e) {
                System.err.println("npe on " + enc + " at " + o);
            }
            out += SPLIT_PAIRS;
        }
        if (out.length() < 2) {
            return out;
        }
        return out.substring(0, out.length() - 1);
    }

    private String encode(Object o) throws NullPointerException {
        Class<?> oc = o.getClass();
        // Doing the toString() here allows for us to skip it in the primitive
        // if's
        String val = "" + o;
        char key = 0;
        if (oc == Boolean.class || oc == boolean.class) {
            key = KEY_BOOL;
        } else if (oc == Byte.class || oc == byte.class) {
            key = KEY_BYTE;
        } else if (oc == Character.class || oc == char.class) {
            key = KEY_CHAR;
            val = DatatypeConverter.printBase64Binary(o.toString().getBytes());
        } else if (oc == Double.class || oc == double.class) {
            key = KEY_DOUBLE;
        } else if (oc == Float.class || oc == float.class) {
            key = KEY_FLOAT;
        } else if (oc == Integer.class || oc == int.class) {
            key = KEY_INT;
        } else if (oc == Long.class || oc == long.class) {
            key = KEY_LONG;
        } else if (oc == String.class) {
            key = KEY_STRING;
            val = DatatypeConverter.printBase64Binary(o.toString().getBytes());
        } else {
            key = KEY_OTHER;
            val = DatatypeConverter.printBase64Binary((o.getClass().getName()
                    + SPLIT_CLASS + new Gson().toJson(o)).getBytes());
        }
        return key + SPLIT_KEYVALUE + val;
    }

    /**
     * Gets an object from the data values.
     * 
     * @param index
     *            - the index to retrieve from
     * @return the object found, or null if out of bounds.
     */
    public Object get(int index) {
        return get(index, null);
    }

    /**
     * Gets an object from the data values.
     * 
     * @param index
     *            - the index to retrieve from
     * @param def
     *            - the default value
     * @return the object found, or the default value if out of bounds.
     */
    public Object get(int index, Object def) {
        if (dataValues == null) {
            return def;
        }
        return dataValues.size() <= index ? def
                : dataValues.get(index) == null ? def : dataValues.get(index);
    }

    public void add(Object o) {
        dataValues.add(o);
    }

    public void remove(Object o) {
        dataValues.remove(o);
    }

    public void remove(int index) {
        dataValues.remove(index);
    }

    /**
     * Gets the entire object array.
     * 
     * @return the object values
     */
    public Object[] getAll() {
        return dataValues.toArray(new Object[0]);
    }

    @Override
    public String toString() {
        return encodeData(dataValues);
    }
}
