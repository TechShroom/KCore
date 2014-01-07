package k.core.util.arrays;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import k.core.util.Helper.BetterArrays;
import k.core.util.reflect.Reflect;

/**
 * An array that resizes when needed. Can be forced down as well. Is like an
 * {@link ArrayList}, but allows the use of primitive values as the array type.
 * 
 * @author kenzietogami
 * 
 */
public class ResizableArray<T> extends AbstractList<Object> implements
        List<Object>, RandomAccess, Cloneable, java.io.Serializable {
    private static final long serialVersionUID = 8683452581122892189L;

    /**
     * The array buffer into which the elements of the ResizableArray are
     * stored. The capacity of the ResizableArray is the length of this array
     * buffer.
     */
    private transient T elementData;

    /**
     * The size of the ResizableArray (the number of elements it contains).
     * 
     * @serial
     */
    private int size;

    /**
     * The type of the internal array. This is NOT the component type.
     */
    private Class<T> arrayType;

    /**
     * If this <tt>ResiableArray</tt> permits undefined values, eg. null, 0.
     */
    private boolean permitUndef = true;

    /**
     * Constructs an empty list with the specified initial capacity.
     * 
     * @param initialCapacity
     *            the initial capacity of the list
     * @throws IllegalArgumentException
     *             if the specified initial capacity is negative
     */
    @SuppressWarnings("unchecked")
    public ResizableArray(Class<T> type, int initialCapacity) {
        super();
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "
                    + initialCapacity);
        this.elementData = (T) Array.newInstance(type.getComponentType(),
                initialCapacity);
        arrayType = type;
    }

    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public ResizableArray(Class<T> type) {
        this(type, 10);
    }

    /**
     * Constructs a list containing the elements of the specified collection, in
     * the order they are returned by the collection's iterator.
     * 
     * @param c
     *            the collection whose elements are to be placed into this list
     * @throws NullPointerException
     *             if the specified collection is null
     */
    @SuppressWarnings("unchecked")
    public ResizableArray(Class<T> type, Collection<?> c) {
        elementData = (T) c.toArray();
        size = length();
        // c.toArray might (incorrectly) not return Object[] (see 6260652)
        if (elementData.getClass() != type) {
            refCopyOf(size);
        }
        arrayType = type;
    }

    /**
     * Uses the fact that we use an array internally to allow creation via array
     * 
     * @param array
     *            - the array to create from
     * @throws IllegalArgumentException
     *             if <tt>array</tt> is not an array (
     *             <tt>array.getClass().getComponentType()</tt> is <tt>null</tt>
     *             )
     */
    @SuppressWarnings("unchecked")
    public ResizableArray(T array) {
        arrayType = (Class<T>) array.getClass();
        if (arrayType == null) {
            throw new IllegalArgumentException(String.valueOf(array));
        }
        elementData = array;
        size = length();
    }

    /**
     * Trims the capacity of this <tt>ResizableArray</tt> instance to be the
     * list's current size. An application can use this operation to minimize
     * the storage of an <tt>ResizableArray</tt> instance.
     */
    public void trimToSize() {
        modCount++;
        int oldCapacity = length();
        if (size < oldCapacity) {
            refCopyOf(size);
        }
    }

    /**
     * Increases the capacity of this <tt>ResizableArray</tt> instance, if
     * necessary, to ensure that it can hold at least the number of elements
     * specified by the minimum capacity argument.
     * 
     * @param minCapacity
     *            the desired minimum capacity
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > 0)
            ensureCapacityInternal(minCapacity);
    }

    /**
     * Changes the <tt>permitUndef</tt> boolean to <tt>value</tt>. If
     * <tt>true</tt>, prevents all default initialization values like 0 and
     * <tt>null</tt>
     * 
     * @param value
     *            - true to allow undefined values, false to not allow
     */
    public void permitUndefined(boolean value) {
        permitUndef = value;
        if (!permitUndef)
            invalidCheck();
    }

    private void invalidCheck() {
        // create with size() so that it doesn't create a huge array from
        // doubling size.
        ArrayList<Object> rem = new ArrayList<Object>(size());
        for (Object o : this) {
            if (o == null || primitveInvalids(o)) {
                rem.add(o);
            }
        }
        removeAll(rem);
    }

    private void invalidCheck(Object o) {
        if (o == null || primitveInvalids(o)) {
            throw new IllegalArgumentException();
        }
    }

    private boolean primitveInvalids(Object o) {
        Class<?> primCheck = o.getClass();
        if (primCheck == Integer.class) {
            return ((Integer) o) == 0;
        }
        if (primCheck == Byte.class) {
            return ((Byte) o) == 0;
        }
        if (primCheck == Character.class) {
            return ((Character) o) == 0;
        }
        if (primCheck == Float.class) {
            return ((Float) o) == 0.0f;
        }
        if (primCheck == Double.class) {
            return ((Double) o) == 0.0d;
        }
        if (primCheck == Short.class) {
            return ((Short) o) == 0;
        }
        if (primCheck == Long.class) {
            return ((Long) o) == 0;
        }
        if (primCheck == Boolean.class) {
            return ((Boolean) o) == false;
        }
        return false;
    }

    private void ensureCapacityInternal(int minCapacity) {
        modCount++;
        // overflow-conscious code
        if (minCapacity - length() > 0)
            grow(minCapacity);
    }

    /**
     * The maximum size of array to allocate. Some VMs reserve some header words
     * in an array. Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Increases the capacity to ensure that it can hold at least the number of
     * elements specified by the minimum capacity argument.
     * 
     * @param minCapacity
     *            the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = length();
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        refCopyOf(newCapacity);
    }

    private void refCopyOf(int size) {
        elementData = copyOf(size);
    }

    @SuppressWarnings("unchecked")
    private T copyOf(int size) {
        T temp = (T) Array.newInstance(arrayType.getComponentType(), size);
        System.arraycopy(elementData, 0, temp, 0, Math.min(size, length()));
        return temp;
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE
                : MAX_ARRAY_SIZE;
    }

    /**
     * Returns the number of elements in this list.
     * 
     * @return the number of elements in this list
     */
    @Override
    public int size() {
        return size;
    }

    private int length() {
        return Array.getLength(elementData);
    }

    /**
     * Returns <tt>true</tt> if this list contains no elements.
     * 
     * @return <tt>true</tt> if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns <tt>true</tt> if this list contains the specified element. More
     * formally, returns <tt>true</tt> if and only if this list contains at
     * least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     * 
     * @param o
     *            element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    @Override
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    @Override
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData(i) == null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData(i)))
                    return i;
        }
        return -1;
    }

    /**
     * Returns the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element. More formally,
     * returns the highest index <tt>i</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>,
     * or -1 if there is no such index.
     */
    @Override
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size - 1; i >= 0; i--)
                if (elementData(i) == null)
                    return i;
        } else {
            for (int i = size - 1; i >= 0; i--)
                if (o.equals(elementData(i)))
                    return i;
        }
        return -1;
    }

    /**
     * Returns a shallow copy of this <tt>ResizableArray</tt> instance. (The
     * elements themselves are not copied.)
     * 
     * @return a clone of this <tt>ResizableArray</tt> instance
     */
    @SuppressWarnings("unchecked")
    @Override
    public ResizableArray<T> clone() {
        try {
            ResizableArray<T> v = (ResizableArray<T>) super.clone();
            v.elementData = copyOf(size);
            v.modCount = 0;
            v.arrayType = arrayType;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean equals(Object o) {
        if (o instanceof ResizableArray
                && (((ResizableArray<?>) o).arrayType == arrayType)) {
            ResizableArray<T> r = (ResizableArray<T>) o;
            Object data = elementData, data2 = r.elementData;
            if (data instanceof Object[] && data2 instanceof Object[]) {
                return Arrays.deepEquals((Object[]) data, (Object[]) data2);
            }
            try {
                return Reflect.invokeMethodStatic(boolean.class, Arrays.class,
                        "equals", data, data2);
            } catch (Exception e) {
                throw new RuntimeException("problem during equals", e);
            }
        }
        return super.equals(o);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element).
     * 
     * <p>
     * The returned array will be "safe" in that no references to it are
     * maintained by this list. (In other words, this method must allocate a new
     * array). The caller is thus free to modify the returned array.
     * 
     * <p>
     * This method acts as bridge between array-based and collection-based APIs.
     * 
     * @return an array containing all of the elements in this list in proper
     *         sequence
     */
    @Override
    public Object[] toArray() {
        return (Object[]) copyOf(size);
    }

    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element); the runtime type of the returned
     * array is that of the specified array. If the list fits in the specified
     * array, it is returned therein. Otherwise, a new array is allocated with
     * the runtime type of the specified array and the size of this list.
     * 
     * <p>
     * If the list fits in the specified array with room to spare (i.e., the
     * array has more elements than the list), the element in the array
     * immediately following the end of the collection is set to <tt>null</tt>.
     * (This is useful in determining the length of the list <i>only</i> if the
     * caller knows that the list does not contain any null elements.)
     * 
     * @param a
     *            the array into which the elements of the list are to be
     *            stored, if it is big enough; otherwise, a new array of the
     *            same runtime type is allocated for this purpose.
     * @return an array containing the elements of the list
     * @throws ArrayStoreException
     *             if the runtime type of the specified array is not a supertype
     *             of the runtime type of every element in this list
     * @throws NullPointerException
     *             if the specified array is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (E[]) copyOf(size);
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    public T getArray() {
        return copyOf(size);
    }

    /**
     * Gets the array that backs this <tt>ResizableArray</tt>. Only use this if
     * you know what you are doing!
     * 
     * @return the array that backs this instance.
     */
    public T getUnderlyingArray() {
        return elementData;
    }

    // Positional Access Operations

    Object elementData(int index) {
        return Array.get(elementData, index);
    }

    /**
     * Returns the element at the specified position in this list.
     * 
     * @param index
     *            index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public Object get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     * 
     * @param index
     *            index of the element to replace
     * @param element
     *            element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public Object set(int index, Object element) {
        rangeCheck(index);

        Object oldValue = elementData(index);
        fastSet(index, element);
        return oldValue;
    }

    /**
     * Does a set with a resize to the index if required.
     * 
     * @param index
     * @param element
     */
    public void setResize(int index, Object element) {
        try {
            elementData(index);
        } catch (ArrayIndexOutOfBoundsException out) {
            size++;
        }

        ensureCapacityInternal(index + 1);

        fastSet(index, element);
    }

    /**
     * A quick set that ignores range checking. Checks against invalid adding,
     * but does not throw exceptions
     */
    private void fastSet(int index, Object o) {
        if (!permitUndef) {
            try {
                invalidCheck(o);
            } catch (IllegalArgumentException iae) {
                return;
            }
        }
        Array.set(elementData, index, o);
    }

    /**
     * Appends the specified element to the end of this list.
     * 
     * @param e
     *            element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    @Override
    public boolean add(Object e) {
        ensureCapacityInternal(size + 1); // Increments modCount!!
        fastSet(size++, e);
        return true;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     * 
     * @param index
     *            index at which the specified element is to be inserted
     * @param element
     *            element to be inserted
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public void add(int index, Object element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1); // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1, size
                - index);
        fastSet(index, element);
        size++;
    }

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * 
     * @param index
     *            the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public Object remove(int index) {
        rangeCheck(index);

        modCount++;
        Object oldValue = elementData(index);

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index,
                    numMoved);
        fastSet(size--, null); // Let gc do its work

        return oldValue;
    }

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present. If the list does not contain the element, it is unchanged.
     * More formally, removes the element with the lowest index <tt>i</tt> such
     * that
     * <tt>(o==null&nbsp;?&nbsp;get(i)==null&nbsp;:&nbsp;o.equals(get(i)))</tt>
     * (if such an element exists). Returns <tt>true</tt> if this list contained
     * the specified element (or equivalently, if this list changed as a result
     * of the call).
     * 
     * @param o
     *            element to be removed from this list, if present
     * @return <tt>true</tt> if this list contained the specified element
     */
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData(index) == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData(index))) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }

    /*
     * Private remove method that skips bounds checking and does not return the
     * value removed.
     */
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index + 1, elementData, index,
                    numMoved);
        fastSet(size--, null); // Let gc do its work
    }

    /**
     * Removes all of the elements from this list. The list will be empty after
     * this call returns.
     */
    @Override
    public void clear() {
        modCount++;

        // Let gc do its work
        for (int i = 0; i < size; i++)
            fastSet(i, null);

        size = 0;
    }

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list, in the order that they are returned by the specified
     * collection's Iterator. The behavior of this operation is undefined if the
     * specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this list, and this list is nonempty.)
     * 
     * @param c
     *            collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException
     *             if the specified collection is null
     */
    @Override
    public boolean addAll(Collection<?> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew); // Increments modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    public boolean addAll(Object a) {
        int numNew = Array.getLength(a);
        ensureCapacityInternal(size + numNew); // Increments modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Inserts all of the elements in the specified collection into this list,
     * starting at the specified position. Shifts the element currently at that
     * position (if any) and any subsequent elements to the right (increases
     * their indices). The new elements will appear in the list in the order
     * that they are returned by the specified collection's iterator.
     * 
     * @param index
     *            index at which to insert the first element from the specified
     *            collection
     * @param c
     *            collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     * @throws NullPointerException
     *             if the specified collection is null
     */
    @Override
    public boolean addAll(int index, Collection<?> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew); // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    public boolean addAll(int index, Object a) {
        rangeCheckForAdd(index);

        int numNew = Array.getLength(a);
        ensureCapacityInternal(size + numNew); // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }

    /**
     * Removes from this list all of the elements whose index is between
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. Shifts any
     * succeeding elements to the left (reduces their index). This call shortens
     * the list by {@code (toIndex - fromIndex)} elements. (If
     * {@code toIndex==fromIndex}, this operation has no effect.)
     * 
     * @throws IndexOutOfBoundsException
     *             if {@code fromIndex} or {@code toIndex} is out of range (
     *             {@code fromIndex < 0 ||
     *          fromIndex >= size() ||
     *          toIndex > size() ||
     *          toIndex < fromIndex})
     */
    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);

        // Let gc do its work
        int newSize = size - (toIndex - fromIndex);
        while (size != newSize)
            fastSet(--size, null);
    }

    /**
     * Checks if the given index is in range. If not, throws an appropriate
     * runtime exception. This method does *not* check if the index is negative:
     * It is always used immediately prior to an array access, which throws an
     * ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * A version of rangeCheck used by add and addAll.
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message. Of the many
     * possible refactorings of the error handling code, this "outlining"
     * performs best with both server and client VMs.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: " + index + ", Size: " + size;
    }

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     * 
     * @param c
     *            collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException
     *             if the class of an element of this list is incompatible with
     *             the specified collection (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException
     *             if this list contains a null element and the specified
     *             collection does not permit null elements (<a
     *             href="Collection.html#optional-restrictions">optional</a>),
     *             or if the specified collection is null
     * @see Collection#contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return batchRemove(c, false);
    }

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection. In other words, removes from this list all of its
     * elements that are not contained in the specified collection.
     * 
     * @param c
     *            collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException
     *             if the class of an element of this list is incompatible with
     *             the specified collection (<a
     *             href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException
     *             if this list contains a null element and the specified
     *             collection does not permit null elements (<a
     *             href="Collection.html#optional-restrictions">optional</a>),
     *             or if the specified collection is null
     * @see Collection#contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return batchRemove(c, true);
    }

    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++)
                if (c.contains(Array.get(elementData, r)) == complement)
                    Array.set(elementData, w++, Array.get(elementData, r));
        } finally {
            // Preserve behavioral compatibility with AbstractCollection,
            // even if c.contains() throws.
            if (r != size) {
                System.arraycopy(elementData, r, elementData, w, size - r);
                w += size - r;
            }
            if (w != size) {
                for (int i = w; i < size; i++)
                    Array.set(elementData, i, null);
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    /**
     * Reverses this list.
     */
    public void reverse() {
        elementData = BetterArrays.reverseNonGeneric(elementData);
    }

    /**
     * Save the state of the <tt>ResizableArray</tt> instance to a stream (that
     * is, serialize it).
     * 
     * @serialData The length of the array backing the <tt>ResizableArray</tt>
     *             instance is emitted (int), followed by all of its elements
     *             (each an <tt>Object</tt>) in the proper order.
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out element count, and any hidden stuff
        int expectedModCount = modCount;
        s.defaultWriteObject();

        // Write out array length
        s.writeInt(length());
        s.writeObject(arrayType);

        // Write out all elements in the proper order.
        for (int i = 0; i < size; i++)
            s.writeObject(elementData(i));

        s.writeBoolean(permitUndef);
        ;

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }

    }

    /**
     * Reconstitute the <tt>ResizableArray</tt> instance from a stream (that is,
     * deserialize it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in array length and allocate array
        int arrayLength = s.readInt();
        arrayType = (Class<T>) s.readObject();
        T a = elementData = (T) Array.newInstance(arrayType.getComponentType(),
                arrayLength);

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            Array.set(a, i, s.readObject());

        permitUndef = s.readBoolean();
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence), starting at the specified position in the list. The specified
     * index indicates the first element that would be returned by an initial
     * call to {@link ListIterator#next next}. An initial call to
     * {@link ListIterator#previous previous} would return the element with the
     * specified index minus one.
     * 
     * <p>
     * The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     * 
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     */
    @Override
    public ListIterator<Object> listIterator(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("Index: " + index);
        return new ListItr(index);
    }

    /**
     * Returns a list iterator over the elements in this list (in proper
     * sequence).
     * 
     * <p>
     * The returned list iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     * 
     * @see #listIterator(int)
     */
    @Override
    public ListIterator<Object> listIterator() {
        return new ListItr(0);
    }

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * 
     * <p>
     * The returned iterator is <a href="#fail-fast"><i>fail-fast</i></a>.
     * 
     * @return an iterator over the elements in this list in proper sequence
     */
    @Override
    public Iterator<Object> iterator() {
        return new Itr();
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private class Itr implements Iterator<Object> {
        int cursor; // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such
        int expectedModCount = modCount;

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public Object next() {
            checkForComodification();
            int i = cursor;
            if (i >= size)
                throw new NoSuchElementException();
            Object elementData = ResizableArray.this.elementData;
            if (i >= Array.getLength(elementData))
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return Array.get(elementData, lastRet = i);
        }

        @Override
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ResizableArray.this.remove(lastRet);
                cursor = lastRet;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * An optimized version of AbstractList.ListItr
     */
    private class ListItr extends Itr implements ListIterator<Object> {
        ListItr(int index) {
            super();
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public Object previous() {
            checkForComodification();
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            Object elementData = ResizableArray.this.elementData;
            if (i >= Array.getLength(elementData))
                throw new ConcurrentModificationException();
            cursor = i;
            return Array.get(elementData, lastRet = i);
        }

        @Override
        public void set(Object e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                ResizableArray.this.set(lastRet, e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(Object e) {
            checkForComodification();

            try {
                int i = cursor;
                ResizableArray.this.add(i, e);
                cursor = i + 1;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive. (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.) The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     * 
     * <p>
     * This method eliminates the need for explicit range operations (of the
     * sort that commonly exist for arrays). Any operation that expects a list
     * can be used as a range operation by passing a subList view instead of a
     * whole list. For example, the following idiom removes a range of elements
     * from a list:
     * 
     * <pre>
     * list.subList(from, to).clear();
     * </pre>
     * 
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     * 
     * <p>
     * The semantics of the list returned by this method become undefined if the
     * backing list (i.e., this list) is <i>structurally modified</i> in any way
     * other than via the returned list. (Structural modifications are those
     * that change the size of this list, or otherwise perturb it in such a
     * fashion that iterations in progress may yield incorrect results.)
     * 
     * @throws IndexOutOfBoundsException
     *             {@inheritDoc}
     * @throws IllegalArgumentException
     *             {@inheritDoc}
     */
    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        subListRangeCheck(fromIndex, toIndex, size);
        return new SubList(this, 0, fromIndex, toIndex);
    }

    static void subListRangeCheck(int fromIndex, int toIndex, int size) {
        if (fromIndex < 0)
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        if (toIndex > size)
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        if (fromIndex > toIndex)
            throw new IllegalArgumentException("fromIndex(" + fromIndex
                    + ") > toIndex(" + toIndex + ")");
    }

    private class SubList extends AbstractList<Object> implements RandomAccess {
        private final AbstractList<Object> parent;
        private final int parentOffset;
        private final int offset;
        int size;

        SubList(AbstractList<Object> parent, int offset, int fromIndex,
                int toIndex) {
            this.parent = parent;
            this.parentOffset = fromIndex;
            this.offset = offset + fromIndex;
            this.size = toIndex - fromIndex;
            this.modCount = ResizableArray.this.modCount;
        }

        @Override
        public Object set(int index, Object e) {
            rangeCheck(index);
            checkForComodification();
            Object oldValue = ResizableArray.this.elementData(offset + index);
            ResizableArray.this.fastSet(offset + index, e);
            return oldValue;
        }

        @Override
        public Object get(int index) {
            rangeCheck(index);
            checkForComodification();
            return ResizableArray.this.elementData(offset + index);
        }

        @Override
        public int size() {
            checkForComodification();
            return this.size;
        }

        @Override
        public void add(int index, Object e) {
            rangeCheckForAdd(index);
            checkForComodification();
            parent.add(parentOffset + index, e);
            try {
                this.modCount = Reflect.getField(int.class, "modCount", parent);
            } catch (Exception e1) {
            }
            this.size++;
        }

        @Override
        public Object remove(int index) {
            rangeCheck(index);
            checkForComodification();
            Object result = parent.remove(parentOffset + index);
            try {
                this.modCount = Reflect.getField(int.class, "modCount", parent);
            } catch (Exception e1) {
            }
            this.size--;
            return result;
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            checkForComodification();
            try {
                Reflect.invokeMethod(Void.TYPE, "removeRange", parent,
                        parentOffset + fromIndex, parentOffset + toIndex);
            } catch (Exception e1) {
            }
            try {
                this.modCount = Reflect.getField(int.class, "modCount", parent);
            } catch (Exception e1) {
            }
            this.size -= toIndex - fromIndex;
        }

        @Override
        public boolean addAll(Collection<? extends Object> c) {
            return addAll(this.size, c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends Object> c) {
            rangeCheckForAdd(index);
            int cSize = c.size();
            if (cSize == 0)
                return false;

            checkForComodification();
            parent.addAll(parentOffset + index, c);
            try {
                this.modCount = Reflect.getField(int.class, "modCount", parent);
            } catch (Exception e1) {
            }
            this.size += cSize;
            return true;
        }

        @Override
        public Iterator<Object> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<Object> listIterator(final int index) {
            checkForComodification();
            rangeCheckForAdd(index);
            final int offset = this.offset;

            return new ListIterator<Object>() {
                int cursor = index;
                int lastRet = -1;
                int expectedModCount = ResizableArray.this.modCount;

                @Override
                public boolean hasNext() {
                    return cursor != SubList.this.size;
                }

                @Override
                public Object next() {
                    checkForComodification();
                    int i = cursor;
                    if (i >= SubList.this.size)
                        throw new NoSuchElementException();
                    Object elementData = ResizableArray.this.elementData;
                    if (offset + i >= Array.getLength(elementData))
                        throw new ConcurrentModificationException();
                    cursor = i + 1;
                    return Array.get(elementData, offset + (lastRet = i));
                }

                @Override
                public boolean hasPrevious() {
                    return cursor != 0;
                }

                @Override
                public Object previous() {
                    checkForComodification();
                    int i = cursor - 1;
                    if (i < 0)
                        throw new NoSuchElementException();
                    Object elementData = ResizableArray.this.elementData;
                    if (offset + i >= Array.getLength(elementData))
                        throw new ConcurrentModificationException();
                    cursor = i;
                    return Array.get(elementData, offset + (lastRet = i));
                }

                @Override
                public int nextIndex() {
                    return cursor;
                }

                @Override
                public int previousIndex() {
                    return cursor - 1;
                }

                @Override
                public void remove() {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        SubList.this.remove(lastRet);
                        cursor = lastRet;
                        lastRet = -1;
                        expectedModCount = ResizableArray.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                @Override
                public void set(Object e) {
                    if (lastRet < 0)
                        throw new IllegalStateException();
                    checkForComodification();

                    try {
                        ResizableArray.this.set(offset + lastRet, e);
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                @Override
                public void add(Object e) {
                    checkForComodification();

                    try {
                        int i = cursor;
                        SubList.this.add(i, e);
                        cursor = i + 1;
                        lastRet = -1;
                        expectedModCount = ResizableArray.this.modCount;
                    } catch (IndexOutOfBoundsException ex) {
                        throw new ConcurrentModificationException();
                    }
                }

                final void checkForComodification() {
                    if (expectedModCount != ResizableArray.this.modCount)
                        throw new ConcurrentModificationException();
                }
            };
        }

        @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            subListRangeCheck(fromIndex, toIndex, size);
            return new SubList(this, offset, fromIndex, toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
        }

        private String outOfBoundsMsg(int index) {
            return "Index: " + index + ", Size: " + this.size;
        }

        private void checkForComodification() {
            if (ResizableArray.this.modCount != this.modCount)
                throw new ConcurrentModificationException();
        }
    }
}