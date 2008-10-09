/*
 * Copyright 2006-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.proposaldevelopment.bo;

import javax.persistence.Version;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Table;
import javax.persistence.Entity;

import java.util.LinkedHashMap;


import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;

/**
 * Business Object for narratives added to a proposal. Narratives are the same as Proposal Attachments and vice-versa.
 * 
 * 
 * @version $Revision: 1.4 $
 */
@Entity
@Table(name="NARRATIVE_TYPE")
public class NarrativeType extends KraPersistableBusinessObjectBase {
    
    @Id
	@Column(name="NARRATIVE_TYPE_CODE")
	private String narrativeTypeCode;
    @Column(name="DESCRIPTION")
	private String description;
    @Column(name="SYSTEM_GENERATED")
	private String systemGenerated;
    @Column(name="ALLOW_MULTIPLE")
	private String allowMultiple;
    @Column(name="NARRATIVE_TYPE_GROUP")
	private String narrativeTypeGroup;
    
    public String getAllowMultiple() {
        return allowMultiple;
    }
    public void setAllowMultiple(String allowMultiple) {
        this.allowMultiple = allowMultiple;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getNarrativeTypeCode() {
        return narrativeTypeCode;
    }
    public void setNarrativeTypeCode(String narrativeTypeCode) {
        this.narrativeTypeCode = narrativeTypeCode;
    }
    public String getNarrativeTypeGroup() {
        return narrativeTypeGroup;
    }
    public void setNarrativeTypeGroup(String narrativeTypeGroup) {
        this.narrativeTypeGroup = narrativeTypeGroup;
    }
    public String getSystemGenerated() {
        return systemGenerated;
    }
    public void setSystemGenerated(String systemGenerated) {
        this.systemGenerated = systemGenerated;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap propMap = new LinkedHashMap();
        propMap.put("narrativeTypeCode", this.getNarrativeTypeCode());
        propMap.put("description", this.getDescription());
        propMap.put("systemGenerated", this.getSystemGenerated());
        propMap.put("allowMultiple", this.getAllowMultiple());
        propMap.put("narrativeTypeGroup", this.getNarrativeTypeGroup());
        propMap.put("updateTimestamp", this.getUpdateTimestamp());
        propMap.put("updateUser", this.getUpdateUser());
        return propMap;
    }
    
    /**
     * Determine if two NarrativeTypes have the same values.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof NarrativeType) {
            NarrativeType other = (NarrativeType) obj;
            return StringUtils.equals(this.narrativeTypeCode, other.narrativeTypeCode) &&
                   StringUtils.equals(this.description, other.description) &&
                   StringUtils.equals(this.systemGenerated, other.systemGenerated) &&
                   StringUtils.equals(this.allowMultiple, other.allowMultiple) &&
                   StringUtils.equals(this.narrativeTypeGroup, other.narrativeTypeGroup);
        }
        return false;
    }
}

