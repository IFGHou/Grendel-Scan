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
package flex.messaging.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import flex.messaging.MessageException;
import flex.messaging.io.TypeMarshaller;
import flex.messaging.io.TypeMarshallingContext;

/**
 * A utility class used to find a suitable method based on matching
 * signatures to the types of set of arguments. Since the arguments
 * may be from more loosely typed environments such as ActionScript,
 * a translator can be employed to handle type conversion. Note that
 * there isn't a great guarantee for which method will be selected
 * when several overloaded methods match very closely through the use
 * of various combinations of generic types.
 *
 * @exclude
 */
public class MethodMatcher
{
    private final Map methodCache = new HashMap();
    private static final int ARGUMENT_CONVERSION_ERROR = 10006;
    private static final int CANNOT_INVOKE_METHOD = 10007;

    /**
     * Default constructor.
     */
    public MethodMatcher()
    {
    }

    /**
     * Utility method that searches a class for a given method, taking
     * into account the supplied parameters when evaluating overloaded
     * method signatures.
     *
     * @param c          the class
     * @param methodName desired method to search for
     * @param parameters required to distinguish between overloaded methods of the same name
     * @return The best-match <tt>Method</tt>.
     */
    public Method getMethod(Class c, String methodName, List parameters)
    {
        Method method = null;

        // Keep track of the best method match found
        Match bestMatch = new Match(methodName);

        // Determine supplied parameter types.
        Class[] suppliedParamTypes = paramTypes(parameters);

        // Create a key to search our method cache
        MethodKey methodKey = new MethodKey(c, methodName, suppliedParamTypes);

        if (methodCache.containsKey(methodKey))
        {
            method = (Method) methodCache.get(methodKey);

            String thisMethodName = method.getName();
            bestMatch.matchedMethodName = thisMethodName;

            // Despite the method being cached, we still have to convert
            // input params to the desired types each invocation...
            Class[] desiredParamTypes = method.getParameterTypes();
            bestMatch.methodParamTypes = desiredParamTypes;
            convertParams(parameters, desiredParamTypes, bestMatch);
        }
        else
        {
            // Search for the method by name
            Method[] methods = c.getMethods();
            for (int i = 0; i < methods.length; i++)
            {
                Method thisMethod = methods[i];

                String thisMethodName = thisMethod.getName();

                // FIXME: Do we want to do this case-insensitively in Flex 2.0?
                // First, search by name; for backwards compatibility
                // we continue to check case-insensitively
                if (thisMethodName.equalsIgnoreCase(methodName))
                {
                    // Next, search on params
                    Match currentMatch = new Match(methodName);
                    currentMatch.matchedMethodName = thisMethodName;

                    // If we've not yet had a match, this is our
                    // best match so far...
                    if (bestMatch.matchedMethodName == null)
                    {
                        bestMatch = currentMatch;
                    }

                    // Number of parameters must match
                    Class[] desiredParamTypes = thisMethod.getParameterTypes();
                    currentMatch.methodParamTypes = desiredParamTypes;

                    if (desiredParamTypes.length == suppliedParamTypes.length)
                    {
                        currentMatch.matchedByNumberOfParams = true;

                        // If we've not yet matched any params
                        // this is our best match so far...
                        if (!bestMatch.matchedByNumberOfParams && bestMatch.matchedParamCount == 0)
                        {
                            bestMatch = currentMatch;
                        }

                        // Parameter types must also be compatible
                        convertParams(parameters, desiredParamTypes, currentMatch);

                        // If we've not yet had this many params
                        // match, this is our best match so far...
                        if (currentMatch.matchedParamCount >= bestMatch.matchedParamCount)
                        {
                            bestMatch = currentMatch;
                        }

                        // If all types were compatible, we have a match
                        if (currentMatch.matchedParamCount == desiredParamTypes.length)
                        {
                            method = thisMethod;
                            bestMatch = currentMatch;
                            synchronized(methodCache)
                            {
                                Method method2 = (Method)methodCache.get(methodKey);
                                if (method2 == null)
                                    methodCache.put(methodKey, method);
                                else
                                    method = method2;
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (method == null)
        {
            methodNotFound(methodName, suppliedParamTypes, bestMatch);
        }
        else if (bestMatch.paramTypeConversionFailure != null)
        {
            //Error occurred while attempting to convert an input argument's type.
            MessageException me = new MessageException();
            me.setMessage(ARGUMENT_CONVERSION_ERROR);
            me.setCode("Server.Processing");
            me.setRootCause(bestMatch.paramTypeConversionFailure);
            throw me;
        }

        return method;
    }


    /**
     * Utility method to convert a collection of parameters to desired types. We keep track
     * of the progress of the conversion to allow callers to gauge the success of the conversion.
     * This is important for ranking overloaded-methods and debugging purposes.
     *
     * @param parameters actual parameters for an invocation
     * @param desiredParamTypes classes in the signature of a potential match for the invocation
     * @param currentMatch the currently best known match
     */
    public static void convertParams(List parameters, Class[] desiredParamTypes, Match currentMatch)
    {
        int matchCount = 0;

        currentMatch.matchedParamCount = 0;
        currentMatch.convertedSuppliedTypes = new Class[desiredParamTypes.length];

        TypeMarshaller marshaller = TypeMarshallingContext.getTypeMarshaller();
        
        for (int i = 0; i < desiredParamTypes.length; i++)
        {
            Object param = parameters.get(i);

            // consider null param to match
            if (param != null)
            {
                Object obj = null;
                Class objClass = null;

                if (marshaller != null)
                {
                    try
                    {
                        obj = marshaller.convert(param, desiredParamTypes[i]);
                    }
                    catch (MessageException ex)
                    {
                        currentMatch.paramTypeConversionFailure = ex;
                        break;
                    }
                }
                else
                {
                    obj = param;
                }

                currentMatch.convertedSuppliedTypes[i] = (obj != null ? (objClass = obj.getClass()) : null);

                // things match if we now have an object which is assignable from the
                // method param class type or if we have an Object which corresponds to
                // a primitive
                if (obj == null || 
                        (desiredParamTypes[i].isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Integer.TYPE && Integer.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Double.TYPE && Double.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Long.TYPE && Long.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Boolean.TYPE && Boolean.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Character.TYPE && Character.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Float.TYPE && Float.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Short.TYPE && Short.class.isAssignableFrom(objClass)) ||
                        (desiredParamTypes[i] == Byte.TYPE && Byte.class.isAssignableFrom(objClass)))
                {
                    parameters.set(i, obj);
                    matchCount++;
                }
                else
                {
                    break;
                }
            }
            else
            {
                matchCount++;
            }
        }

        currentMatch.matchedParamCount = matchCount;
    }

    /**
     * Utility method that iterates over a collection of input
     * parameters to determine their types while logging
     * the class names to create a unique identifier for a
     * method signature.
     *
     * @param parameters - A list of supplied parameters.
     * @return An array of <tt>Class</tt> instances indicating the class of each corresponding parameter.
     */
    public static Class[] paramTypes(List parameters)
    {
        Class[] paramTypes = new Class[parameters.size()];
        for (int i = 0; i < paramTypes.length; i++)
        {
            Object p = parameters.get(i);
            paramTypes[i] = p == null ? Object.class : p.getClass();
        }
        return paramTypes;
    }

    /**
     * Utility method to provide more detailed information in the event that a search
     * for a specific method failed for the service class.
     *
     * @param methodName         the name of the missing method
     * @param suppliedParamTypes the types of parameters supplied for the search
     * @param bestMatch          the best match found during the search
     */
    public static void methodNotFound(String methodName, Class[] suppliedParamTypes, Match bestMatch)
    {
        // Set default error message...
        // Cannot invoke method '{methodName}'.
        int errorCode = CANNOT_INVOKE_METHOD;
        Object[] errorParams = new Object[]{methodName};
        String errorDetailVariant = "0";
        // Method '{methodName}' not found.
        Object[] errorDetailParams = new Object[]{methodName};

        if (bestMatch.matchedMethodName != null)
        {
            // Cannot invoke method '{bestMatch.matchedMethodName}'.
            errorCode = CANNOT_INVOKE_METHOD;
            errorParams = new Object[]{bestMatch.matchedMethodName};

            int suppliedParamCount = suppliedParamTypes.length;
            int expectedParamCount = bestMatch.methodParamTypes != null ? bestMatch.methodParamTypes.length : 0;

            if (suppliedParamCount != expectedParamCount)
            {
                // {suppliedParamCount} arguments were sent but {expectedParamCount} were expected.
                errorDetailVariant = "1";
                errorDetailParams = new Object[]{new Integer(suppliedParamCount), new Integer(expectedParamCount)};

            }
            else
            {
                String suppliedTypes = bestMatch.listTypes(suppliedParamTypes);
                String convertedTypes = bestMatch.listConvertedTypes();
                String expectedTypes = bestMatch.listExpectedTypes();

                if (expectedTypes != null)
                {
                    if (suppliedTypes != null)
                    {
                        if (convertedTypes != null)
                        {
                            // The expected argument types are ({expectedTypes})
                            // but the supplied types were ({suppliedTypes})
                            // and converted to ({convertedTypes}).
                            errorDetailVariant = "2";
                            errorDetailParams = new Object[]{expectedTypes, suppliedTypes, convertedTypes};
                        }
                        else
                        {
                            // The expected argument types are ({expectedTypes})
                            // but the supplied types were ({suppliedTypes})
                            // with none successfully converted.
                            errorDetailVariant = "3";
                            errorDetailParams = new Object[]{expectedTypes, suppliedTypes};
                        }
                    }
                    else
                    {
                        // The expected argument types are ({expectedTypes})
                        // but no arguments were provided.
                        errorDetailVariant = "4";
                        errorDetailParams = new Object[]{expectedTypes};
                    }
                }
                else
                {
                    // No arguments were expected but the following types were supplied (suppliedTypes)
                    errorDetailVariant = "5";
                    errorDetailParams = new Object[]{suppliedTypes};
                }
            }
        }

        MessageException ex = new MessageException();
        ex.setMessage(errorCode, errorParams);
        ex.setCode(MessageException.CODE_SERVER_RESOURCE_UNAVAILABLE);
        if (errorDetailVariant != null)
            ex.setDetails(errorCode, errorDetailVariant, errorDetailParams);

        if (bestMatch.paramTypeConversionFailure != null)
            ex.setRootCause(bestMatch.paramTypeConversionFailure);

        throw ex;
    }

    /**
     * A utility class to help rank methods in the search
     * for a best match, given a name and collection of
     * input parameters.
     */
    public static class Match
    {
        /**
         * Constructor.         
         * @param name the name of the method to match
         */
        public Match(String name)
        {
            this.methodName = name;
        }

        /**
         * Returns true if desired and found method names match.         
         * @return true if desired and found method names match
         */
        public boolean matchedExactlyByName()
        {
            if (matchedMethodName != null)
                return matchedMethodName.equals(methodName);
            else
                return false;
        }

        /**
         * Returns true if desired and found method names match only when case is ignored.         
         * @return true if desired and found method names match only when case is ignored  
         */
        public boolean matchedLooselyByName()
        {
            if (matchedMethodName != null)
                return (!matchedExactlyByName() && matchedMethodName.equalsIgnoreCase(methodName));
            else
                return false;
        }

        
        /**
         * Lists the classes in the signature of the method matched.
         * @return the classes in the signature of the method matched
         */
        public String listExpectedTypes()
        {
            return listTypes(methodParamTypes);
        }

        
        /**
         * Lists the classes corresponding to actual invocation parameters once they have been
         * converted as best they could to match the classes in the invoked method's signature.
         * 
         * @return the classes corresponding to actual invocation parameters once they have been
         * converted as best they could to match the classes in the invoked method's signature
         */
        public String listConvertedTypes()
        {
            return listTypes(convertedSuppliedTypes);
        }

        /**
         * Creates a string representation of the class names in the array of types passed into
         * this method.
         * 
         * @param types an array of types whose names are to be listed
         * @return a string representation of the class names in the array of types
         */
        public String listTypes(Class[] types)
        {
            if (types != null && types.length > 0)
            {
                StringBuffer sb = new StringBuffer();

                for (int i = 0; i < types.length; i++)
                {
                    if (i > 0)
                        sb.append(", ");

                    Class c = types[i];

                    if (c != null)
                    {
                        if (c.isArray())
                        {
                            c = c.getComponentType();
                            sb.append(c.getName()).append("[]");
                        }
                        else
                        {
                            sb.append(c.getName());
                        }
                    }
                    else
                        sb.append("null");
                }

                return sb.toString();
            }

            return null;
        }

        final String methodName;
        String matchedMethodName;

        boolean matchedByNumberOfParams;
        int matchedParamCount;
        Class[] methodParamTypes;
        Class[] convertedSuppliedTypes;
        Exception paramTypeConversionFailure;
    }
}
