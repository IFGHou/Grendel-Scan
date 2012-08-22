/*************************************************************************
 *
 * ADOBE CONFIDENTIAL
 * __________________
 *
 *  Copyright 2002 - 2007 Adobe Systems Incorporated
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.config;

/**
 * @author neville
 * @exclude
 */
public class ClusterSettings
{
    public static final String CLUSTER_ELEMENT = "cluster";
    public static final String REF_ATTR = "ref";
    public static final String SHARED_BACKEND_ATTR = "shared-backend";
    public static final String DEFAULT_ELEMENT = "default";
    public static final String URL_LOAD_BALANCING = "url-load-balancing";
    public static final String IMPLEMENTATION_CLASS = "class";

    public static final String JGROUPS_CLUSTER = "flex.messaging.cluster.JGroupsCluster";

    private String clusterName;
    private String propsFileName;
    private String implementationClass;
    private boolean def;
    private boolean urlLoadBalancing;

    /**
     * Creates a new <code>ClusterSettings</code> with default settings.
     */
    public ClusterSettings()
    {
        def = false;
        urlLoadBalancing = true;
        implementationClass = JGROUPS_CLUSTER;
    }

    /**
     * Returns the name of the cluster.
     *
     * @return The name of the cluster.
     */
    public String getClusterName()
    {
        return clusterName;
    }

    /**
     * Sets the name of the cluster.
     *
     * @param clusterName The name of the cluster.
     */
    public void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    /**
     * Returns whether the cluster is default or not.
     *
     * @return <code>true</code> is the cluster is default; otherwise <code>false</code>.
     */
    public boolean isDefault()
    {
        return def;
    }

    /**
     * Sets whether the cluster is default or not.
     *
     * @param def <code>true</code> is the cluster is default; otherwise <code>false</code>.
     */
    public void setDefault(boolean def)
    {
        this.def = def;
    }

    /**
     * Returns the properties file of the cluster.
     *
     * @return The properties file of the cluster.
     */
    public String getPropsFileName()
    {
        return propsFileName;
    }

    /**
     * Sets the properties file of the cluster.
     *
     * @param propsFileName The properties file of the cluster.
     */
    public void setPropsFileName(String propsFileName)
    {
        this.propsFileName = propsFileName;
    }

    /**
     * Returns whether url load balancing is enabled or not.
     *
     * @return <code>true</code> if the url load balancing is enabled; otherwise <code>false</code>.
     */
    public boolean getURLLoadBalancing()
    {
        return urlLoadBalancing;
    }

    /**
     * Sets whether url load balancing is enabled or not.
     *
     * @param ulb <code>true</code> if the url load balancing is enabled; otherwise <code>false</code>.
     */
    public void setURLLoadBalancing(boolean ulb)
    {
        urlLoadBalancing = ulb;
    }

    /**
     * Sets the name of the cluster implementation class.
     * The default is 'flex.messaging.cluster.JGroupsCluster'.
     *
     * @param className
     * @exclude
     */
    public void setImplementationClass(String className)
    {
        this.implementationClass = className;
    }

    /**
     * Get the name of the cluster implementation class.
     * The class must support the flex.messaging.cluster.Cluster interface.
     *
     * @return The implementation class name.
     * @exclude
     */
    public String getImplementationClass()
    {
        return implementationClass;
    }
}
