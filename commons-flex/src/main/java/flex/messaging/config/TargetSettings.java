/*************************************************************************
 * 
 * ADOBE CONFIDENTIAL __________________
 * 
 * Copyright 2002 - 2007 Adobe Systems Incorporated All Rights Reserved.
 * 
 * NOTICE: All information contained herein is, and remains the property of Adobe Systems Incorporated and its suppliers, if any. The intellectual and technical concepts contained herein are
 * proprietary to Adobe Systems Incorporated and its suppliers and may be covered by U.S. and Foreign Patents, patents in process, and are protected by trade secret or copyright law. Dissemination of
 * this information or reproduction of this material is strictly forbidden unless prior written permission is obtained from Adobe Systems Incorporated.
 **************************************************************************/
package flex.messaging.config;

// TODO UCdetector: Remove unused code:
// /**
// * A logging target must specify the class name
// * of the implementation, the level of logging events
// * it will accept, a list of filters for logging
// * categories it is interested in, and a collection of
// * properties required to initialize the target.
// *
// * @author Peter Farland
// * @exclude
// */
// public class TargetSettings extends PropertiesSettings
// {
// private String className;
// private String level;
// private List filters;
//
// /* TODO UCdetector: Remove unused code:
// public TargetSettings(String className)
// {
// this.className = className;
// }
// */
//
// public String getClassName()
// {
// return className;
// }
//
// public String getLevel()
// {
// return level;
// }
//
// public void setLevel(String level)
// {
// this.level = level;
// }
//
// public List getFilters()
// {
// return filters;
// }
//
// public void addFilter(String filter)
// {
// if (filters == null)
// filters = new ArrayList();
//
// // Replace DataService with Service.Data for backwards compatibility,
// // excluding DataService.coldfusion.
// if (filter.startsWith("DataService") && !filter.equals("DataService.coldfusion"))
// filter = filter.replaceFirst("DataService", LogCategories.SERVICE_DATA);
//
// filters.add(filter);
// }
//
//
// }
