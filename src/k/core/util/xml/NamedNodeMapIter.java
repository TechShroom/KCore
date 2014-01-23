package k.core.util.xml;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapIter implements Iterable<Node>, Iterator<Node> {
    private NamedNodeMap backing = null;
    private int index = 0;
    private boolean removed;

    public NamedNodeMapIter(NamedNodeMap map) {
        backing = map;
    }

    @Override
    public boolean hasNext() {
        return index >= backing.getLength();
    }

    @Override
    public Node next() {
        if (hasNext()) {
            return backing.item(index++);
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void remove() {
        if (!removed) {
            backing.removeNamedItem(backing.item(index).getNodeName());
            removed = true;
        }
    }

    @Override
    public Iterator<Node> iterator() {
        return this;
    }

}
