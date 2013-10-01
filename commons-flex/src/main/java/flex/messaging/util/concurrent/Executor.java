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
package flex.messaging.util.concurrent;

// TODO UCdetector: Remove unused code:
// /**
// * This interface allows different Executor implementations to be chosen and used
// * without creating a direct dependency upon <code>java.util.concurrent.Executor</code>
// * added in Java 1.5, the Java 1.4.x-friendly backport of the <code>java.util.concurrent</code> APIs
// * which has a different package structure, or alternative work execution frameworks such as
// * IBM WebSphere 5's <code>com.ibm.websphere.asynchbeans.WorkManager</code> or the
// * <code>commonj.work.WorkManager</code> available in IBM WebSphere 6, BEA WebLogic 9 or
// * other application servers that support the <code>commonj</code> API.
// * Implementations should notify clients of any failure with executing a command by invoking
// * the callback on the <code>FailedExecutionHandler</code> if one has been set.
// *
// * @see java.util.concurrent.Executor
// * @exclude
// */
// public interface Executor
// {
//
// // TODO UCdetector: Remove unused code:
// // /**
// // * Executes the given command at some time in the future.
// // * The command may execute in a new thread, in a pooled thread, or in the calling thread, at the
// // * discretion of the <code>Executor</code> implementation.
// // * Implementation classes are free to throw a <code>RuntimeException</code> if the command can not
// // * be executed.
// // */
// // void execute(Runnable command);
//
// /**
// * Returns the current handler for failed executions.
// *
// * @return The current handler.
// */
// FailedExecutionHandler getFailedExecutionHandler();
//
// /**
// * Sets the handler for failed executions.
// *
// * @param handler The new handler.
// */
// void setFailedExecutionHandler(FailedExecutionHandler handler);
// }
