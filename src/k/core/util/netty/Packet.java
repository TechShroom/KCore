package k.core.util.netty;

import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import k.core.util.classes.StackTraceInfo;
import k.core.util.reflect.Reflect;

/**
 * A packet for connections. Implements {@link Comparable} so that the most
 * important packets go through first.
 * 
 * @author Kenzie Togami
 * 
 */
public abstract class Packet implements Comparable<Packet> {

    public static final int TERMINATION_PACKET_ID = Integer.MIN_VALUE;
    private static HashMap<Class<? extends Packet>, Integer> packetToId = new HashMap<Class<? extends Packet>, Integer>();
    private static HashMap<Integer, Class<? extends Packet>> idToPacket = new HashMap<Integer, Class<? extends Packet>>();

    public static final void registerPacket(Class<? extends Packet> type, int id) {
        if (type == null) {
            throw new NullPointerException();
        }
        if (packetToId.get(type) != null) {
            throw new IllegalArgumentException(type.getName()
                    + " already registered under id " + packetToId.get(type));
        }
        if (idToPacket.get(id) != null) {
            throw new IllegalArgumentException("id " + id
                    + " already linked to " + idToPacket.get(id).getName());
        }
        packetToId.put(type, id);
        idToPacket.put(id, type);
    }

    static {
        registerPacket(PacketFinished.class, TERMINATION_PACKET_ID);
    }
    private final String PACKET_CLASS_NAME = getClass().getName();

    /**
     * Uses an empty constructor for a packet.
     * 
     * @param id
     *            - the id of the packet
     * @return
     */
    public static Packet newPacket(int id) {
        return newPacket(id, new Object[0]);
    }

    /**
     * Uses a constructor that takes a {@link DataStruct} with the given data.
     * 
     * @param id
     *            - the id of the packet
     * @param data
     *            - the data for the packet
     * @return
     */
    public static Packet newPacket(int id, DataStruct data) {
        return newPacket(id, new Object[] { data });
    }

    /**
     * Uses an empty constructor for a packet.
     * 
     * @param id
     *            - the id of the packet
     * @param constrClasses
     *            - find a constructor using these classes
     * @param constrObjects
     *            - use these objects during construction
     * @return
     */
    public static Packet newPacket(int id, Object[] constrObjects) {
        Class<? extends Packet> pClass = idToPacket.get(id);
        if (pClass == null) {
            throw new IllegalStateException("Packet not registered.");
        } else {
            try {
                pClass.getDeclaredConstructor(new Class[] { DataStruct.class });
            } catch (Exception e) {
                throw new RuntimeException(
                        new IllegalClassFormatException(pClass.getName()
                                + " requires a DataStruct constructor"));
            }
            try {
                return Reflect.construct(pClass, constrObjects);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int idFor(Packet p) {
        return packetToId.get(p.getClass());
    }

    protected DataStruct data = new DataStruct(new Object[0]);

    /**
     * Do not attempt construction of packets. Use {@link Packet#newPacket(int)}
     * , {@link Packet#newPacket(int, DataStruct)}, or
     * {@link Packet#newPacket(int, Class[], Object[])}
     */
    protected Packet(DataStruct ds) {
        if (!StackTraceInfo.getInvokingClassName().equals(PACKET_CLASS_NAME)) {
            throw new IllegalAccessError(
                    "cannot create Packet class via constructor");
        }
        data = ds;
    }

    /**
     * Our method for compareTo involves using the registered id for the
     * packets. A lower id = higher priority.
     * 
     * This is <i>inconsistent</i> with equals, because this is used for
     * comparing priority, not equality.
     * 
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo(Packet o) {
        if (o == null) {
            throw new NullPointerException(
                    "implementation detail for compareTo: null");
        }
        return this.delCompareTo(o);
    }

    /**
     * Delegated method, o is guaranteed to be non-null.
     * 
     * @param o
     *            - the packet to compare to
     * @return see {@link #compareTo(Object)}
     * @see #compareTo(Object)
     */
    protected int delCompareTo(Packet o) {
        int ourId = packetToId.get(getClass()), theirId = packetToId.get(o
                .getClass());
        if (ourId == theirId) {
            return 0;
        } else if (ourId < theirId) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "Packet#" + packetToId.get(getClass()) + "[" + data.toString()
                + "]";
    }

    public static Packet fromData(String request) {
        DataStruct ds = new DataStruct(request);
        return newPacket((Integer) ds.get(0),
                new DataStruct((String) ds.get(1)));
    }

    public static String toData(Packet p) {
        String req = new DataStruct(new Object[] {
                packetToId.get(p.getClass()), p.data.toString() }).toString();
        return req;
    }
}
