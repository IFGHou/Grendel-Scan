/**
 * 
 */
package com.grendelscan.commons.flex;

import java.util.ArrayList;
import java.util.List;

import com.grendelscan.commons.StringUtils;
import com.grendelscan.commons.collections.BidiMap;
import com.grendelscan.commons.flex.dataTypeDefinitions.AmfDataType;
import com.grendelscan.commons.flex.interfaces.ArbitraryChildren;
import com.grendelscan.commons.http.dataHandling.containers.ExpandableDataContainer;
import com.grendelscan.commons.http.dataHandling.data.Data;
import com.grendelscan.commons.http.dataHandling.data.DataUtils;
import com.grendelscan.commons.http.dataHandling.references.DataReference;
import com.grendelscan.commons.http.dataHandling.references.NamedDataContainerDataReference;

/**
 * @author david
 * 
 */
public abstract class AbstractAmfNamedDataContainer extends AbstractAmfDataContainer<NamedDataContainerDataReference> implements ArbitraryChildren, ExpandableDataContainer<NamedDataContainerDataReference>
{
    protected BidiMap<byte[], NamedAmfDataContainer> properties;
    private static final long serialVersionUID = 1L;

    /**
     * @param name
     * @param type
     * @param parent
     * @param forceAmf3Code
     * @param reference
     * @param transactionId
     */
    public AbstractAmfNamedDataContainer(final String name, final AmfDataType type, final AbstractAmfDataContainer<?> parent, final boolean forceAmf3Code, final int transactionId)
    {
        super(name, type, parent, forceAmf3Code, transactionId);
        properties = new BidiMap<byte[], NamedAmfDataContainer>(1);
        // properties = new BidiMap<byte[], NamedAmfDataContainer>(1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(final Data child)
    {
        if (!(child instanceof NamedAmfDataContainer))
        {
            throw new IllegalArgumentException("Children must be of type NamedAmfDataContainer");
        }
        NamedAmfDataContainer amfChild = (NamedAmfDataContainer) child;
        properties.put(DataUtils.getBytes(amfChild.getNameData()), amfChild);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#addChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void addChild(@SuppressWarnings("unused") final NamedDataContainerDataReference reference, final Data child)
    {
        addChild(child);
    }

    @Override
    public String childrenDebugString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("AMF properties:");
        for (byte[] key : properties.keySet())
        {
            sb.append("\n\tkey: ");
            sb.append(key);
            sb.append("\nvalue:\n");
            sb.append(StringUtils.indentLines(properties.get(key).debugString(), 2));
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChild(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChild(final NamedDataContainerDataReference reference)
    {
        return properties.get(reference.getName());
    }

    public AbstractAmfData getChild(final String name)
    {
        NamedAmfDataContainer param = properties.get(name.getBytes());
        if (param == null)
        {
            return null;
        }
        return param.getValueData();
    }

    public List<byte[]> getChildNames()
    {
        return properties.getSortedKeys();
    }

    @Override
    public ArrayList<AbstractAmfData> getChildren()
    {
        ArrayList<AbstractAmfData> children = new ArrayList<AbstractAmfData>(properties.size());
        for (NamedAmfDataContainer data : properties.getSortedValues())
        {
            children.add(data.getValueData());
        }
        return children;
    }

    @Override
    public NamedDataContainerDataReference getChildsReference(final Data child)
    {
        if (properties.containsValue(child))
        {
            return new NamedDataContainerDataReference(properties.getKey((NamedAmfDataContainer) child));
        }
        throw new IllegalArgumentException("The passed data object is not a child of this container");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getChildUnsafeType(com.grendelscan.commons.http.dataHandling.references.DataReference)
     */
    @Override
    public Data getChildUnsafeType(final DataReference reference)
    {
        return getChild((NamedDataContainerDataReference) reference);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#getDataChildren()
     */
    @Override
    public Data[] getDataChildren()
    {
        return properties.getSortedValues().toArray(new Data[properties.size()]);
    }

    @Override
    public int getSize()
    {
        return properties.size();
    }

    @Override
    public boolean isMutable()
    {
        return true;
    }

    public Data putChild(final String name, final AbstractAmfData child)
    {
        child.setName(name);
        NamedAmfDataContainer container = new NamedAmfDataContainer(this, getTransactionId(), name, child);
        NamedAmfDataContainer oldValue = properties.put(name.getBytes(), container);
        // Data oldValue = properties.put(name.getBytes(), child);
        if (oldValue == null)
        {
            return null;
        }
        return oldValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#removeChild(com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void removeChild(final Data child)
    {
        NamedAmfDataContainer amfChild = (NamedAmfDataContainer) child;
        if (amfChild.getValueData().isDeletable())
        {
            properties.removeValue(amfChild);
        }
    }

    public AbstractAmfData removeChild(final String name)
    {
        NamedAmfDataContainer oldValue = properties.remove(name.getBytes());
        if (oldValue == null || oldValue.getValueData().isDeletable())
        {
            return null;
        }
        return oldValue.getValueData();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.grendelscan.commons.http.dataHandling.containers.DataContainer#replaceChild(com.grendelscan.commons.http.dataHandling.references.DataReference,
     * com.grendelscan.commons.http.dataHandling.data.Data)
     */
    @Override
    public void replaceChild(@SuppressWarnings("unused") final NamedDataContainerDataReference reference, final Data child)
    {
        addChild(child);
    }
}
