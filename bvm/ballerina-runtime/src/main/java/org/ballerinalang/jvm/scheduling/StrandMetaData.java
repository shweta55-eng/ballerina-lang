/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.ballerinalang.jvm.scheduling;

/**
 * Holds meta data of the @{@link Strand}.
 *
 * @since 2.0.0
 */

public class StrandMetaData {

    private String moduleOrg;

    private String moduleName;

    private String moduleVersion;

    private String parentFunctionName;

    public StrandMetaData(String moduleOrg, String moduleName, String moduleVersion, String parentFunctionName) {
        this.moduleOrg = moduleOrg;
        this.moduleName = moduleName;
        this.moduleVersion = moduleVersion;
        this.parentFunctionName = parentFunctionName;
    }

    public String getModuleOrg() {
        return moduleOrg;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public String getParentFunctionName() {
        return parentFunctionName;
    }
}
