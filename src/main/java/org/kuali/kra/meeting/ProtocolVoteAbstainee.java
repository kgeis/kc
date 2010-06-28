/*
 * Copyright 2005-2010 The Kuali Foundation
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
package org.kuali.kra.meeting;

import java.util.LinkedHashMap;
/**
 * 
 * This class is for member abstained from vote.
 */
public class ProtocolVoteAbstainee extends ProtocolMeetingVoter { 
    
    private static final long serialVersionUID = 6207540592702779528L;
    private Long protocolVoteAbstaineesId;    
    
    public ProtocolVoteAbstainee() { 

    } 
    
    public Long getProtocolVoteAbstaineesId() {
        return protocolVoteAbstaineesId;
    }

    public void setProtocolVoteAbstaineesId(Long protocolVoteAbstaineesId) {
        this.protocolVoteAbstaineesId = protocolVoteAbstaineesId;
    }


    /** {@inheritDoc} */
    @Override 
    protected LinkedHashMap<String, Object> toStringMapper() {
        LinkedHashMap<String, Object> hashMap = super.toStringMapper();
        hashMap.put("protocolVoteAbstaineesId", this.getProtocolVoteAbstaineesId());
        return hashMap;
    }    
}