/*
 * Copyright 2005-2013 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.iacuc.committee.bo;

import org.kuali.coeus.common.committee.bo.CommitteeBatchCorrespondenceBase;

import java.sql.Date;

public class IacucCommitteeBatchCorrespondence extends CommitteeBatchCorrespondenceBase {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = -7537703994487982310L;
    
    public IacucCommitteeBatchCorrespondence(String batchCorrespondenceTypeCode, String committeeId, Date startDate, Date endDate) {
        super(batchCorrespondenceTypeCode, committeeId, startDate, endDate);
    }


}
