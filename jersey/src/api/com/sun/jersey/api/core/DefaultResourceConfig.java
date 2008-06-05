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

package com.sun.jersey.api.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.MediaType;

/**
 * A mutable implementation of {@link ResourceConfig} that declares
 * default values for features.
 * <p>
 * The set of features and properties may be modified by modifying the instances 
 * returned from the methods {@link ResourceConfig#getFeatures} and 
 * {@link ResourceConfig#getProperties} respectively.
 */
public class DefaultResourceConfig extends ResourceConfig {
    
    private final Set<Class<?>> resources = new HashSet<Class<?>>();
    
    private final Set<Class<?>> providers = new HashSet<Class<?>>();
    
    private final Map<String, MediaType> mediaExtentions = new HashMap<String, MediaType>();
    
    private final Map<String, String> languageExtentions = new HashMap<String, String>();
    
    private final Map<String, Boolean> features = new HashMap<String, Boolean>();
    
    private final Map<String, Object> properties = new HashMap<String, Object>();
    
    /**
     */
    public DefaultResourceConfig() {
        this((Set<Class<?>>)null);
    }
    
    /**
     * @param resources the initial set of root resource classes
     */
    public DefaultResourceConfig(Class<?>... resources) {
        this(new HashSet<Class<?>>(Arrays.asList(resources)));
    }
    
    /**
     * @param resources the initial set of root resource classes
     */
    public DefaultResourceConfig(Set<Class<?>> resources) {
        this.features.put(ResourceConfig.FEATURE_CANONICALIZE_URI_PATH, false);
        this.features.put(ResourceConfig.FEATURE_MATCH_MATRIX_PARAMS, false);
        this.features.put(ResourceConfig.FEATURE_NORMALIZE_URI, false);
        this.features.put(ResourceConfig.FEATURE_REDIRECT, false);
        this.features.put(ResourceConfig.FEATURE_IMPLICIT_VIEWABLES, false);
        if (null != resources) {
            this.resources.addAll(resources);
        }
    }

    public Set<Class<?>> getResourceClasses() {
        return resources;
    }

    @Override
    public Set<Class<?>> getProviderClasses() {
        return providers;
    }
    
    @Override
    public Map<String, MediaType> getMediaTypeMappings() {
        return mediaExtentions;
    }

    @Override
    public Map<String, String> getLanguageMappings() {
        return languageExtentions;
    }
    
    public Map<String, Boolean> getFeatures() {
        return features;
    }
    
    public boolean getFeature(String featureName) {
        final Boolean v = features.get(featureName);
        return (v != null) ? v : false;
    }
    
    public Map<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }
}