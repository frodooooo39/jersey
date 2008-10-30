/*
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://jersey.dev.java.net/CDDL+GPL.html
 * or jersey/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at jersey/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.jersey.core.reflection;

import com.sun.jersey.impl.ImplMessages;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for Java reflection.
 *
 * @author Paul.Sandoz@Sun.Com
 */
public final class ReflectionHelper {
    /**
     * Get the class that is the type argument of a parameterized type.
     * <p>
     * @param parameterizedType must be an instance of ParameterizedType
     *        and have exactly one type argument.
     * @return the class of the actual type argument. If the type argument
     *         is a class then the class is returned. If the type argument
     *         is a generic array type and the generic component type is a
     *         class then class of the array is returned. If the parameterizedType
     *         is not an instance of ParameterizedType or contains more than one
     *         type argument null is returned.
     * @throws IllegalArgumentException if the single type argument is not of
     *         a class or a generic array type, or the generic component type
     *         of the generic array type is not class.
     */
    public static Class getGenericClass(Type parameterizedType) throws IllegalArgumentException {
        if (!(parameterizedType instanceof ParameterizedType)) return null;
        
        ParameterizedType type = (ParameterizedType)parameterizedType;
        Type[] genericTypes = type.getActualTypeArguments();
        if (genericTypes.length != 1) return null;
        
        Type genericType = genericTypes[0];
        if (genericType instanceof Class) {
            return (Class)genericTypes[0];
        } else if (genericType instanceof GenericArrayType) {
            GenericArrayType arrayType = (GenericArrayType)genericType;
            Type t = arrayType.getGenericComponentType();
            if (t instanceof Class) {
                Class c = (Class)t;
                try {
                    // TODO is there a better way to get the Class object 
                    // representing an array
                    Object o = Array.newInstance(c, 0);
                    return o.getClass();
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                throw new IllegalArgumentException(ImplMessages.GENERIC_TYPE_NOT_SUPPORTED(genericType,
                                                                                              parameterizedType));
            }
        } else {
            throw new IllegalArgumentException(ImplMessages.GENERIC_TYPE_NOT_SUPPORTED(genericType,
                                                                                           parameterizedType));
        }
    }

    /**
     * Get the static valueOf(String ) method.
     * 
     * @param c The class to obtain the method.
     * @return the method, otherwise null if the method is not present.
     */
    @SuppressWarnings("unchecked")
    public static Method getValueOfStringMethod(Class c) {
        try {
            Method m = c.getDeclaredMethod("valueOf", String.class);
            if (!Modifier.isStatic(m.getModifiers())) {
                return null;
            }
            return m;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the constructor that has a single parameter of String.
     *
     * @param c The class to obtain the constructor.
     * @return the constructor, otherwise null if the constructor is not present.
     */
    @SuppressWarnings("unchecked")
    public static Constructor getStringConstructor(Class c) {
        try {
            return c.getConstructor(String.class);
        } catch (Exception e) {
            return null;
        }
    }    

    /**
     * A tuple consisting of a class and type of the class.
     */
    public static class ClassTypePair {
        /**
         * The class.
         */
        public final Class c;

        /**
         * The type of the class.
         */
        public final Type t;
        
        private ClassTypePair(Class c) {
            this(c, c);
        }
        
        private ClassTypePair(Class c, Type t) {
            this.c = c;
            this.t = t;
        }
    }
    
    /**
     * Given a type variable resolve the Java class of that variable.
     * 
     * @param c the concrete class from which all type variables are resolved
     * @param dc the declaring class where the type variable was defined
     * @param tv the type variable
     * @return the resolved Java class and type, otherwise null if the type variable
     *         could not be resolved
     */
    public static ClassTypePair resolveTypeVariable(Class c, Class dc, TypeVariable tv) {
        return resolveTypeVariable(c, dc, tv, new HashMap<TypeVariable, Type>());
    }
    
    private static ClassTypePair resolveTypeVariable(Class c, Class dc, TypeVariable tv, 
            Map<TypeVariable, Type> map) {
        ParameterizedType pt = (ParameterizedType)c.getGenericSuperclass();
        Type[] typeArguments = pt.getActualTypeArguments();
        
        Class sc = c.getSuperclass();        
        TypeVariable[] typeParameters = sc.getTypeParameters();
        
        Map<TypeVariable, Type> submap = new HashMap<TypeVariable, Type>();
        for (int i = 0; i < typeArguments.length; i++) {
            // Substitute a type variable with the Java class
            if (typeArguments[i] instanceof TypeVariable) {
                Type t = map.get(typeArguments[i]);
                submap.put(typeParameters[i], t);
            } else {
                submap.put(typeParameters[i], typeArguments[i]);
            }
        }
        
        if (sc == dc) {
            Type t = submap.get(tv);
            if (t instanceof Class) {
                return new ClassTypePair((Class)t);
            } else if (t instanceof GenericArrayType) {
                t = ((GenericArrayType)t).getGenericComponentType();
                if (t instanceof Class) {
                    c = (Class)t;
                    try {
                        // TODO is there a better way to get the Class object 
                        // representing an array
                        Object o = Array.newInstance(c, 0);
                        return new ClassTypePair(o.getClass());
                    } catch (Exception e) {
                    } 
                    return null;
                } else {
                    return null;
                }
            } else if (t instanceof ParameterizedType) {
                pt = (ParameterizedType)t;
                if (pt.getRawType() instanceof Class) {
                    return new ClassTypePair((Class)pt.getRawType(), pt);
                } else 
                    return null;
            } else {
                return null;
            }
        } else {    
            return resolveTypeVariable(sc, dc, tv, submap);
        }
    }    
}