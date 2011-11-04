/*
 * Copyright 2005-2010 The Kuali Foundation
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
package org.kuali.kra.coi.disclosure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kra.award.contacts.AwardPerson;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.bo.KcPerson;
import org.kuali.kra.bo.KraPersistableBusinessObjectBase;
import org.kuali.kra.coi.CoiDiscDetail;
import org.kuali.kra.coi.CoiDisclProject;
import org.kuali.kra.coi.CoiDisclosure;
import org.kuali.kra.coi.DisclosureReporter;
import org.kuali.kra.coi.DisclosureReporterUnit;
import org.kuali.kra.coi.personfinancialentity.FinancialEntityService;
import org.kuali.kra.coi.personfinancialentity.PersonFinIntDisclosure;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.irb.Protocol;
import org.kuali.kra.irb.ProtocolFinderDao;
import org.kuali.kra.irb.personnel.ProtocolPerson;
import org.kuali.kra.proposaldevelopment.bo.DevelopmentProposal;
import org.kuali.kra.proposaldevelopment.bo.ProposalPerson;
import org.kuali.kra.service.KcPersonService;
import org.kuali.kra.service.VersionException;
import org.kuali.kra.service.VersioningService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;

public class CoiDisclosureServiceImpl implements CoiDisclosureService {

    private BusinessObjectService businessObjectService;
    private KcPersonService kcPersonService;
    private FinancialEntityService financialEntityService;
    private ProtocolFinderDao protocolFinderDao;
    private VersioningService versioningService;

    @SuppressWarnings("rawtypes")
    public DisclosurePerson getDisclosureReporter(String personId, Long coiDisclosureId) {
        List<DisclosurePerson> reporters = new ArrayList<DisclosurePerson>();
        if (coiDisclosureId != null) {
            Map fieldValues = new HashMap();
            fieldValues.put("personId", personId);
            fieldValues.put("personRoleId", "COIR");
            fieldValues.put("coiDisclosureId", coiDisclosureId);

            reporters = (List<DisclosurePerson>) businessObjectService.findMatching(DisclosurePerson.class, fieldValues);
        }
        if (reporters.isEmpty()) {
            DisclosurePerson reporter = new DisclosurePerson();
            reporter.setDisclosurePersonUnits(new ArrayList<DisclosurePersonUnit>());
            reporter.setPersonId(personId);
            reporter.setPersonRoleId("COIR");
            DisclosurePersonUnit leadUnit = createLeadUnit(personId);
            if (leadUnit != null) {
                reporter.getDisclosurePersonUnits().add(leadUnit);
            }
            return reporter;
        }
        else {
            int i = 0;
            for (DisclosurePersonUnit disclosurePersonUnit : reporters.get(0).getDisclosurePersonUnits()) {
                if (disclosurePersonUnit.isLeadUnitFlag()) {
                    reporters.get(0).setSelectedUnit(i);
                    break;
                }
                i++;
            }
        }
        return reporters.get(0);
    }

    public void addDisclosureReporterUnit(DisclosureReporter disclosureReporter , DisclosureReporterUnit newDisclosureReporterUnit) {
        
        List<DisclosureReporterUnit> disclosureReporterUnits = (List<DisclosureReporterUnit>)disclosureReporter.getDisclosureReporterUnits();
        if (newDisclosureReporterUnit.isLeadUnitFlag()) {
            resetLeadUnit(disclosureReporterUnits);
            disclosureReporter.setSelectedUnit(disclosureReporterUnits.size());
        }
        disclosureReporterUnits.add(newDisclosureReporterUnit);
    }

    public void deleteDisclosureReporterUnit(DisclosureReporter disclosureReporter,List<? extends DisclosureReporterUnit> deletedUnits, int unitIndex) {
        
        List<DisclosureReporterUnit> disclosureReporterUnits = (List<DisclosureReporterUnit>)disclosureReporter.getDisclosureReporterUnits();
        DisclosureReporterUnit deletedUnit = disclosureReporterUnits.get(unitIndex);
        if (deletedUnit.getReporterUnitId() != null) {
            ((List<DisclosureReporterUnit>)deletedUnits).add(deletedUnit);
        }
        disclosureReporterUnits.remove(unitIndex);
        if (deletedUnit.isLeadUnitFlag() && !disclosureReporterUnits.isEmpty()) {
            disclosureReporterUnits.get(0).setLeadUnitFlag(true);
            disclosureReporter.setSelectedUnit(0);
        }
    }

    public void resetLeadUnit(DisclosureReporter disclosureReporter) {
        List<? extends DisclosureReporterUnit> disclosureReporterUnits = disclosureReporter.getDisclosureReporterUnits();
        if (CollectionUtils.isNotEmpty(disclosureReporterUnits)) {
            resetLeadUnit(disclosureReporterUnits);
            disclosureReporterUnits.get(disclosureReporter.getSelectedUnit()).setLeadUnitFlag(true);
        }
    }

    public void setDisclDetailsForSave(CoiDisclosure coiDisclosure) {
        if (coiDisclosure.isManualEvent()) {
            coiDisclosure.setCoiDiscDetails(new ArrayList<CoiDiscDetail>());
            for (CoiDisclProject coiDisclProject : coiDisclosure.getCoiDisclProjects()) {
                coiDisclosure.getCoiDiscDetails().addAll(coiDisclProject.getCoiDiscDetails());
            }
        }
        else {
   //     if (coiDisclosure.isProtocolEvent() || coiDisclosure.isProposalEvent() || coiDisclosure.isAwardEvent()) {
          if (coiDisclosure.isAnnualEvent()) {
              // TODO this is temp for moving to one project at a time
            coiDisclosure.setCoiDiscDetails(new ArrayList<CoiDiscDetail>());
            for (CoiDisclEventProject coiDisclEventProject : coiDisclosure.getCoiDisclEventProjects()) {
               // if (coiDisclEventProject.isDisclosureFlag()) {
                    coiDisclosure.getCoiDiscDetails().addAll(coiDisclEventProject.getCoiDiscDetails());
               // }
            }
          }
        }
    }


    private void resetLeadUnit(List<? extends DisclosureReporterUnit> disclosureReporterUnits) {
        for (DisclosureReporterUnit  disclosureReporterUnit : disclosureReporterUnits) {
            disclosureReporterUnit.setLeadUnitFlag(false);
        }
        
    }

    private DisclosurePersonUnit createLeadUnit(String personId) {

        DisclosurePersonUnit leadUnit = null;
        KcPerson kcPerson = kcPersonService.getKcPersonByPersonId(personId);
        if (kcPerson != null && kcPerson.getUnit() != null) {
            leadUnit = new DisclosurePersonUnit();
            leadUnit.setLeadUnitFlag(true);
            leadUnit.setUnitNumber(kcPerson.getUnit().getUnitNumber());
            leadUnit.setUnitName(kcPerson.getUnit().getUnitName());
            leadUnit.setPersonId(personId);
        }
        return leadUnit;
    }
    
    public void initializeDisclosureDetails(CoiDisclosure coiDisclosure) {
        // When creating a disclosure. the detail will be created at first
        // TODO : method too long need refactor
        List<CoiDiscDetail> disclosureDetails = new ArrayList<CoiDiscDetail>();
        List<CoiDisclEventProject> disclEventProjects = new ArrayList<CoiDisclEventProject>();
        List<PersonFinIntDisclosure> financialEntities = financialEntityService.getFinancialEntities(GlobalVariables
                .getUserSession().getPrincipalId(), true);
        if (coiDisclosure.isProtocolEvent() || coiDisclosure.isAnnualEvent()) {
            List<Protocol> protocols = getProtocols(GlobalVariables.getUserSession().getPrincipalId());
            for (Protocol protocol : protocols) {
                CoiDisclEventProject coiDisclEventProject = new CoiDisclEventProject("3", protocol,
                    new ArrayList<CoiDiscDetail>());
                for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
                    CoiDiscDetail disclosureDetail = createNewCoiDiscDetail(coiDisclosure, personFinIntDisclosure,
                            protocol.getProtocolNumber());
                    disclosureDetail.setProtocol(protocol);
                    disclosureDetails.add(disclosureDetail);
                    coiDisclEventProject.getCoiDiscDetails().add(disclosureDetail);
                }
                disclEventProjects.add(coiDisclEventProject);
            }
        } 
        if (coiDisclosure.isProposalEvent() || coiDisclosure.isAnnualEvent()) {
            // TODO : also need to add Institutional proposal
            List<DevelopmentProposal> proposals = getProposals(GlobalVariables.getUserSession().getPrincipalId());
            for (DevelopmentProposal proposal : proposals) {
                CoiDisclEventProject coiDisclEventProject = new CoiDisclEventProject("1", proposal,
                    new ArrayList<CoiDiscDetail>());
                for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
                    CoiDiscDetail disclosureDetail = createNewCoiDiscDetail(coiDisclosure, personFinIntDisclosure,
                            proposal.getProposalNumber());
//                    disclosureDetail.setProtocol(proposal);
                    disclosureDetails.add(disclosureDetail);
                    coiDisclEventProject.getCoiDiscDetails().add(disclosureDetail);
                }
                disclEventProjects.add(coiDisclEventProject);
            }
        } 
        if (coiDisclosure.isAwardEvent() || coiDisclosure.isAnnualEvent()) {
            List<Award> awards = getAwards(GlobalVariables.getUserSession().getPrincipalId());
            for (Award award : awards) {
                CoiDisclEventProject coiDisclEventProject = new CoiDisclEventProject("2", award,
                    new ArrayList<CoiDiscDetail>());
                for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
                    CoiDiscDetail disclosureDetail = createNewCoiDiscDetail(coiDisclosure, personFinIntDisclosure,
                            award.getAwardNumber());
//                    disclosureDetail.setProtocol(proposal);
                    disclosureDetails.add(disclosureDetail);
                    coiDisclEventProject.getCoiDiscDetails().add(disclosureDetail);
                }
                disclEventProjects.add(coiDisclEventProject);
            }
        } 
//        else if (coiDisclosure.isAnnualEvent()) {
//            // TODO : use protocol for now.  need to add all projects here
//            List<Protocol> protocols = getProtocols(GlobalVariables.getUserSession().getPrincipalId());
//            for (Protocol protocol : protocols) {
//                for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
//                    CoiDiscDetail disclosureDetail = createNewCoiDiscDetail(coiDisclosure, personFinIntDisclosure,
//                            protocol.getProtocolNumber());
//                    disclosureDetail.setProtocol(protocol);
//                    disclosureDetails.add(disclosureDetail);
//                }
//            }
//        } 

        coiDisclosure.setCoiDiscDetails(disclosureDetails);
        coiDisclosure.setCoiDisclEventProjects(disclEventProjects);

    }
    
    public void initializeDisclosureDetails(CoiDisclosure coiDisclosure, String projectId) {
        // When creating a disclosure. the detail will be created at first
        List<CoiDiscDetail> disclosureDetails = new ArrayList<CoiDiscDetail>();
        List<PersonFinIntDisclosure> financialEntities = financialEntityService.getFinancialEntities(GlobalVariables
                .getUserSession().getPrincipalId(), true);
        coiDisclosure.setEventBo(getEventBo(coiDisclosure, projectId));
        String moduleItemKey = getModuleItemKey(coiDisclosure, coiDisclosure.getEventBo());
        for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
            CoiDiscDetail disclosureDetail = createNewCoiDiscDetail(coiDisclosure, personFinIntDisclosure,
                    moduleItemKey);
            disclosureDetails.add(disclosureDetail);
        }
        coiDisclosure.setCoiDiscDetails(disclosureDetails);
    }
    
    private String getModuleItemKey(CoiDisclosure coiDisclosure, KraPersistableBusinessObjectBase eventBo) {
    // TODO : this is a temp method, should add interface and 'getmoduleitemkey' in the disclosurable bos    
        String moduleItemKey = null;
        if (coiDisclosure.isProtocolEvent()) {
            moduleItemKey = ((Protocol)eventBo).getProtocolNumber();
        }
        else if (coiDisclosure.isProposalEvent()) {
            moduleItemKey = ((DevelopmentProposal)eventBo).getProposalNumber();
        } 
        else if (coiDisclosure.isAwardEvent()) {
            moduleItemKey = ((Award)eventBo).getAwardNumber();
       }
        return moduleItemKey;
    }
    
    private KraPersistableBusinessObjectBase getEventBo(CoiDisclosure coiDisclosure, String projectId) {
        KraPersistableBusinessObjectBase eventBo = null;
        if (coiDisclosure.isProtocolEvent()) {
            eventBo = getProtocol(Long.valueOf(projectId));
        }
        else if (coiDisclosure.isProposalEvent()) {
            eventBo = getDevelopmentProposal(projectId);
        } 
        else if (coiDisclosure.isAwardEvent()) {
            // TODO : for award
            eventBo = getAwardById(projectId);
       }
        return eventBo;

    }

    private Protocol getProtocol(Long protocolId) {
        HashMap<String, Object> pkMap = new HashMap<String, Object>();
        pkMap.put("protocolId", protocolId);
        return (Protocol) this.businessObjectService.findByPrimaryKey(Protocol.class, pkMap);
    
    }
    
    public void initializeDisclosureDetails(CoiDisclProject coiDisclProject) {
        // When creating a disclosure. the detail will be created at first
        List<CoiDiscDetail> disclosureDetails = new ArrayList<CoiDiscDetail>();
        List<PersonFinIntDisclosure> financialEntities = financialEntityService.getFinancialEntities(GlobalVariables
                .getUserSession().getPrincipalId(), true);
        for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
            disclosureDetails.add(createNewCoiDiscDetail(coiDisclProject.getCoiDisclosure(), personFinIntDisclosure, coiDisclProject.getCoiProjectId()));
        }
        coiDisclProject.setCoiDiscDetails(disclosureDetails);

    }
    
    
    public void updateDisclosureDetails(CoiDisclosure coiDisclosure) {
        // When creating a disclosure. the detail will be created at first
        // TODO : this is for protocol now
        Collections.sort(coiDisclosure.getCoiDiscDetails());
        String moduleItemKey = Constants.EMPTY_STRING;
        List<CoiDisclEventProject> disclEventProjects = new ArrayList<CoiDisclEventProject>();
        CoiDisclEventProject coiDisclEventProject = new CoiDisclEventProject();
        List<PersonFinIntDisclosure> financialEntities = financialEntityService.getFinancialEntities(GlobalVariables
                .getUserSession().getPrincipalId(), true);

        List<String> disclEntityNumbers = new ArrayList<String>();
        if (!coiDisclosure.isManualEvent()) {
            for (CoiDiscDetail coiDiscDetail : coiDisclosure.getCoiDiscDetails()) {
                if (!StringUtils.equals(moduleItemKey, coiDiscDetail.getModuleItemKey())) {
                    if (StringUtils.isNotBlank(moduleItemKey) && coiDisclEventProject.getEventProjectBo() != null) {
                        // event bo is found in table. this is especially for PD to check null bo
                        checkToAddNewFinancialEntity(financialEntities, coiDisclEventProject.getCoiDiscDetails(), disclEntityNumbers, coiDisclEventProject.getProjectId(), coiDisclosure);
                        disclEventProjects.add(coiDisclEventProject);
                    }
                    moduleItemKey = coiDiscDetail.getModuleItemKey();
                    if (!coiDisclosure.isAnnualEvent()) {
                        coiDisclEventProject = getEventBo(coiDisclosure, coiDiscDetail);
                    } else {
                        coiDisclEventProject = getEventBoForAnnualDiscl(coiDisclosure, coiDiscDetail);
                    }
                }
                getCurrentFinancialEntity(coiDiscDetail);
                if (coiDiscDetail.getPersonFinIntDisclosure().isStatusActive()) {
                    if (!disclEntityNumbers.contains(coiDiscDetail.getPersonFinIntDisclosure().getEntityNumber())) {
                        disclEntityNumbers.add(coiDiscDetail.getPersonFinIntDisclosure().getEntityNumber());
                    }
                    coiDisclEventProject.getCoiDiscDetails().add(coiDiscDetail);
                    coiDisclEventProject.setDisclosureFlag(true);
                }
            }
            if (coiDisclEventProject.getEventProjectBo() != null) {
                checkToAddNewFinancialEntity(financialEntities, coiDisclEventProject.getCoiDiscDetails(), disclEntityNumbers, coiDisclEventProject.getProjectId(), coiDisclosure);
                disclEventProjects.add(coiDisclEventProject); // the last project
            }

            coiDisclosure.setCoiDisclEventProjects(disclEventProjects);
            // TODO : single project
            if (!coiDisclosure.isAnnualEvent()) {
                coiDisclosure.setCoiDiscDetails(disclEventProjects.get(0).getCoiDiscDetails());
            }
        }
    }


    private void checkToAddNewFinancialEntity(List<PersonFinIntDisclosure> financialEntities, List<CoiDiscDetail> coiDiscDetails, List<String> disclEntityNumbers, String projectId, CoiDisclosure coiDisclosure) {
        for (PersonFinIntDisclosure personFinIntDisclosure : financialEntities) {
            if (!disclEntityNumbers.contains(personFinIntDisclosure.getEntityNumber())) {
                coiDiscDetails.add(createNewCoiDiscDetail(coiDisclosure, personFinIntDisclosure, projectId));
            }
        }
        
        
    }

    private void getCurrentFinancialEntity(CoiDiscDetail coiDiscDetail) {
        if (!coiDiscDetail.getPersonFinIntDisclosure().isCurrentFlag()) {
            PersonFinIntDisclosure financialEntity = financialEntityService.getCurrentFinancialEntities(coiDiscDetail.getPersonFinIntDisclosure().getEntityNumber());
            coiDiscDetail.setEntitySequenceNumber(financialEntity.getSequenceNumber());
            coiDiscDetail.setPersonFinIntDisclosureId(financialEntity.getPersonFinIntDisclosureId());
            coiDiscDetail.setPersonFinIntDisclosure(financialEntity);
        }
        
    }
    
    private CoiDisclEventProject getEventBo(CoiDisclosure coiDisclosure, CoiDiscDetail coiDiscDetail) {
        CoiDisclEventProject coiDisclEventProject = new CoiDisclEventProject();
        if (coiDisclosure.isProtocolEvent()) {
            Protocol protocol = protocolFinderDao.findCurrentProtocolByNumber(coiDiscDetail.getModuleItemKey());
            coiDisclEventProject = new CoiDisclEventProject("3", protocol, new ArrayList<CoiDiscDetail>());
            coiDisclosure.setEventBo(protocol);
        }
        else if (coiDisclosure.isProposalEvent()) {
            DevelopmentProposal proposal = getDevelopmentProposal(coiDiscDetail.getModuleItemKey());
            coiDisclEventProject = new CoiDisclEventProject("1", proposal, new ArrayList<CoiDiscDetail>());
            coiDisclosure.setEventBo(proposal);
        } 
        else if (coiDisclosure.isAwardEvent()) {
            // TODO : for award
            Award award = getAward(coiDiscDetail.getModuleItemKey());
            coiDisclEventProject = new CoiDisclEventProject("2", award, new ArrayList<CoiDiscDetail>());
            coiDisclosure.setEventBo(award);
       }
        return coiDisclEventProject;

    }
    
    // temporary solution until coeus schema is finalized
    private CoiDisclEventProject getEventBoForAnnualDiscl(CoiDisclosure coiDisclosure, CoiDiscDetail coiDiscDetail) {
        CoiDisclEventProject coiDisclEventProject = new CoiDisclEventProject();
        if (isInBo(Protocol.class, "protocolNumber", coiDiscDetail.getModuleItemKey())) {
            Protocol protocol = protocolFinderDao.findCurrentProtocolByNumber(coiDiscDetail.getModuleItemKey());
            coiDisclEventProject = new CoiDisclEventProject("3", protocol, new ArrayList<CoiDiscDetail>());
        }
        else if (isInBo(DevelopmentProposal.class, "proposalNumber", coiDiscDetail.getModuleItemKey())) {
            DevelopmentProposal proposal = getDevelopmentProposal(coiDiscDetail.getModuleItemKey());
            coiDisclEventProject = new CoiDisclEventProject("1", proposal, new ArrayList<CoiDiscDetail>());
        } else  {
            // TODO : for award
            Award award = getAward(coiDiscDetail.getModuleItemKey());
            coiDisclEventProject = new CoiDisclEventProject("2", award, new ArrayList<CoiDiscDetail>());
       }
        return coiDisclEventProject;

    }
    
    private boolean isInBo(Class clazz, String fieldName, String filedValue) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(fieldName, filedValue);
        return businessObjectService.countMatching(clazz, fieldValues) > 0;

    }
    
    private Award getAwardById(String awardId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("awardId", awardId);
        return (Award)businessObjectService.findByPrimaryKey(Award.class, values);
    }
    
    private Award getAward(String awardNumber) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("awardNumber", awardNumber);
        List<Award> awards = (List<Award>)businessObjectService.findMatchingOrderBy(Award.class, values, "sequenceNumber", false);
        return awards.get(0);
    }

    private DevelopmentProposal getDevelopmentProposal(String proposalNumber) {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put("proposalNumber", proposalNumber);
        DevelopmentProposal currentProposal = (DevelopmentProposal) businessObjectService.findByPrimaryKey(DevelopmentProposal.class, primaryKeys);
        return currentProposal;
    }

    public void updateDisclosureDetails(CoiDisclProject coiDisclProject) {
        // When creating a disclosure. the detail will be created at first
        // TODO : what if FE is deactivate
        Map <String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("coiDisclosureId", coiDisclProject.getCoiDisclosureId());
        fieldValues.put("moduleCode", CoiDisclosure.MANUAL_DISCL_MODULE_CODE);
        fieldValues.put("moduleItemKey", coiDisclProject.getCoiProjectId());
        List<String> disclEntityNumbers = new ArrayList<String>();
        List<CoiDiscDetail> activeDetails = new ArrayList<CoiDiscDetail>();
        List<CoiDiscDetail> coiDiscDetails = (List<CoiDiscDetail>) businessObjectService.findMatching(CoiDiscDetail.class, fieldValues);
        List<PersonFinIntDisclosure> financialEntities = financialEntityService.getFinancialEntities(GlobalVariables
                .getUserSession().getPrincipalId(), true);
        for (CoiDiscDetail coiDiscDetail : coiDiscDetails) {
            getCurrentFinancialEntity(coiDiscDetail);
            if (coiDiscDetail.getPersonFinIntDisclosure().isStatusActive()) {
                if (!disclEntityNumbers.contains(coiDiscDetail.getPersonFinIntDisclosure().getEntityNumber())) {
                    disclEntityNumbers.add(coiDiscDetail.getPersonFinIntDisclosure().getEntityNumber());
                }
                activeDetails.add(coiDiscDetail);
            }            
        }
        checkToAddNewFinancialEntity(financialEntities, activeDetails, disclEntityNumbers, coiDisclProject.getCoiProjectId().toString(), coiDisclProject.getCoiDisclosure());

        coiDisclProject.setCoiDiscDetails(activeDetails);

//        if (CollectionUtils.isEmpty(coiDisclProject.getCoiDiscDetails())) {
//            initializeDisclosureDetails(coiDisclProject);
//        }
    }

    private CoiDiscDetail createNewCoiDiscDetail(CoiDisclosure coiDisclosure,PersonFinIntDisclosure personFinIntDisclosure, String moduleItemKey) {
        CoiDiscDetail disclosureDetail = new CoiDiscDetail(personFinIntDisclosure);
        disclosureDetail.setModuleItemKey(moduleItemKey);
        // TODO : this is how coeus set. not sure ?
        disclosureDetail.setModuleCode(coiDisclosure.getModuleCode());
        coiDisclosure.initCoiDisclosureNumber();
        disclosureDetail.setCoiDisclosureNumber(coiDisclosure.getCoiDisclosureNumber());
        disclosureDetail.setSequenceNumber(coiDisclosure.getSequenceNumber());
        disclosureDetail.setDescription("Sample Description"); // this is from coeus.
        return disclosureDetail;
        
    }
    
    public List<Protocol> getProtocols(String personId) {
        
        List<Protocol> protocols = new ArrayList<Protocol>();
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("personId", personId);
        fieldValues.put("protocolPersonRoleId", "PI");
        List<ProtocolPerson> protocolPersons = (List<ProtocolPerson>) businessObjectService.findMatching(ProtocolPerson.class, fieldValues);
        for (ProtocolPerson protocolPerson : protocolPersons) {
            if (protocolPerson.getProtocol().isActive()) {
                protocols.add(protocolPerson.getProtocol());
            }
        }
        return protocols;
        
    }
    
    public List<DevelopmentProposal> getProposals(String personId) {
        
        List<DevelopmentProposal> proposals = new ArrayList<DevelopmentProposal>();
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("personId", personId);
        fieldValues.put("proposalPersonRoleId", "PI");
        List<ProposalPerson> proposalPersons = (List<ProposalPerson>) businessObjectService.findMatching(ProposalPerson.class, fieldValues);
        for (ProposalPerson proposalPerson : proposalPersons) {
//            if (proposalPerson.getDevelopmentProposal().) {
            // TODO : condition to be implemented
                proposals.add(proposalPerson.getDevelopmentProposal());
//            }
        }
        return proposals;
        
    }
 
    public List<Award> getAwards(String personId) {
        
        List<Award> awards = new ArrayList<Award>();
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put("personId", personId);
        fieldValues.put("roleCode", "PI");
        List<AwardPerson> awardPersons = (List<AwardPerson>) businessObjectService.findMatching(AwardPerson.class, fieldValues);
        for (AwardPerson awardPerson : awardPersons) {
//            if () {
            // TODO : condition to be implemented
                awards.add(awardPerson.getAward());
//            }
        }
        return awards;
        
    }
 
    public CoiDisclosure versionCoiDisclosure() throws VersionException {
        Map fieldValues = new HashMap();
        fieldValues.put("personId", GlobalVariables.getUserSession().getPrincipalId());

        List<CoiDisclosure> disclosures = (List<CoiDisclosure>) businessObjectService.findMatchingOrderBy(CoiDisclosure.class, fieldValues, "sequenceNumber", false);
        CoiDisclosure newDisclosure = null;
        if (CollectionUtils.isNotEmpty(disclosures)) {
            newDisclosure = versioningService.createNewVersion(disclosures.get(0));
            newDisclosure.setCoiDisclProjects(null);
            newDisclosure.setCoiDiscDetails(null);
        }
        return newDisclosure;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }

    public void setFinancialEntityService(FinancialEntityService financialEntityService) {
        this.financialEntityService = financialEntityService;
    }

    public void setProtocolFinderDao(ProtocolFinderDao protocolFinderDao) {
        this.protocolFinderDao = protocolFinderDao;
    }

    public void setVersioningService(VersioningService versioningService) {
        this.versioningService = versioningService;
    }

}
