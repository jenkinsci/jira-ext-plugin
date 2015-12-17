/***************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **************************************************************************/
package org.jenkinsci.plugins.jiraext;

import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.Serializable;

/**
 * @author dalvizu
 */
public class GuiceSingleton
    implements Serializable
{
    private static final long serialVersionUID = -1584374153143256161L;
    private static GuiceSingleton self;
    private Injector injector;

    public GuiceSingleton()
    {
        injector = Guice.createInjector(new PluginGuiceModule());
    }

    public synchronized static GuiceSingleton get()
    {
        if (self == null)
        {
            self = new GuiceSingleton();
        }
        return self;
    }

    public Injector getInjector()
    {
        return injector;
    }


}
