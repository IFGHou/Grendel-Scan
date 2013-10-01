/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * [2002] - [2007] Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.config;

// TODO UCdetector: Remove unused code:
// /**
// * The factory configuration defines a single factory in the flex
// * configuration file.
// *
// * @author Jeff Vroom
// * @exclude
// */
// public class FactorySettings extends PropertiesSettings
// {
// protected String id;
// protected String className;
//
// /* TODO UCdetector: Remove unused code:
// public FactorySettings(String id, String className)
// {
// this.id = id;
// this.className = className;
// }
// */
//
// public String getId()
// {
// return id;
// }
//
// public String getClassName()
// {
// return className;
// }
//
// public FlexFactory createFactory()
// {
// try
// {
// Class c = ClassUtil.createClass(className);
// Object f = ClassUtil.createDefaultInstance(c, FlexFactory.class);
// if (f instanceof FlexFactory)
// {
// FlexFactory ff = (FlexFactory) f;
// ff.initialize(getId(), getProperties());
// return ff;
// }
// else
// {
// ConfigurationException cx = new ConfigurationException();
// cx.setMessage(11101, new Object[] { className });
// throw cx;
// }
// }
// catch (Throwable th)
// {
// ConfigurationException cx = new ConfigurationException();
// cx.setMessage(11102, new Object[] { className, th.toString() });
// cx.setRootCause(th);
// throw cx;
// }
// }
// }
