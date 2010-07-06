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
package org.kuali.kra.timeandmoney.web.struts.action;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kra.award.AwardAmountInfoService;
import org.kuali.kra.award.awardhierarchy.AwardHierarchy;
import org.kuali.kra.award.awardhierarchy.AwardHierarchyService;
import org.kuali.kra.award.document.AwardDocument;
import org.kuali.kra.award.home.Award;
import org.kuali.kra.award.home.AwardAmountInfo;
import org.kuali.kra.award.timeandmoney.AwardDirectFandADistributionBean;
import org.kuali.kra.bo.versioning.VersionHistory;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.KraServiceLocator;
import org.kuali.kra.service.AwardDirectFandADistributionService;
import org.kuali.kra.service.VersionHistoryService;
import org.kuali.kra.timeandmoney.AwardHierarchyNode;
import org.kuali.kra.timeandmoney.TimeAndMoneyForm;
import org.kuali.kra.timeandmoney.document.TimeAndMoneyDocument;
import org.kuali.kra.timeandmoney.history.TransactionDetail;
import org.kuali.kra.timeandmoney.service.ActivePendingTransactionsService;
import org.kuali.kra.timeandmoney.service.TimeAndMoneyActionSummaryService;
import org.kuali.kra.timeandmoney.service.TimeAndMoneyHistoryService;
import org.kuali.kra.timeandmoney.transactions.AwardAmountTransaction;
import org.kuali.kra.web.struts.action.KraTransactionalDocumentActionBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

public class TimeAndMoneyAction extends KraTransactionalDocumentActionBase {
    
    private static final String PENDING_VIEW = "1";
    private static final String ACTIVE_VIEW = "0";
    private static final String PENDING_TRANSACTIONS_ATTRIBUTE_NAME = "pendingTransactions";
    private static final String OBLIGATED_START_COMMENT = "Obligated Start";
    private static final String OBLIGATED_END_COMMENT = "Obligated End";
    private static final String PROJECT_END_COMMENT = "Project End";
    private static final String ZERO = "0";
    private static final Integer TEN = 10;
    private BusinessObjectService businessObjectService;
    
    //FIXME wth - this cannot be here since struts actions are singletons
    //private AwardDirectFandADistributionBean awardDirectFandADistributionBean;
    
    //public TimeAndMoneyAction(){
    //    awardDirectFandADistributionBean = new AwardDirectFandADistributionBean();
   // }
    
    /**
     * @see org.kuali.kra.web.struts.action.KraTransactionalDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = mapping.findForward(Constants.MAPPING_BASIC);
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) form;
        TimeAndMoneyDocument timeAndMoneyDocument = timeAndMoneyForm.getTimeAndMoneyDocument();
        //forward = super.save(mapping, form, request, response);
        ActivePendingTransactionsService aptService = getActivePendingTransactionsService();
        AwardAmountInfoService awardAmountInfoService = KraServiceLocator.getService(AwardAmountInfoService.class);
        List<AwardAmountInfo> awardAmountInfoObjects = new ArrayList<AwardAmountInfo>();
        //save rules have not been applied yet so there needs to be a null check on transaction type code before testing the value.
        boolean isNoCostExtension;
        if (timeAndMoneyDocument.getAwardAmountTransactions().get(0).getTransactionTypeCode() == null) {
            isNoCostExtension = false;
        }else {
            isNoCostExtension = timeAndMoneyDocument.getAwardAmountTransactions().get(0).getTransactionTypeCode().equals(TEN);//Transaction type code for No Cost Extension
        }
        
        //if Dates have changed in a node in hierarchy view and the Transaction Type is a No Cost Extension,
        //we need to record this as a transaction in history.
        //build the transaction and add to this list for persistence later.
        List<TransactionDetail> dateChangeTransactionDetailItems = new ArrayList<TransactionDetail>();
        
        updateDocumentFromSession(timeAndMoneyDocument);//not sure if I need to do this.
        updateAwardAmountTransactions(timeAndMoneyDocument);
        for(Entry<String, AwardHierarchyNode> awardHierarchyNode : timeAndMoneyDocument.getAwardHierarchyNodes().entrySet()){
            Award award = aptService.getWorkingAwardVersion(awardHierarchyNode.getValue().getAwardNumber()); 
            //capture any changes of DirectFandADistributions, and add them to the Award working version for persistence.
            if(award.getAwardNumber().equals(timeAndMoneyDocument.getAward().getAwardNumber())) {
                award.setAwardDirectFandADistributions(timeAndMoneyDocument.getAward().getAwardDirectFandADistributions());
            }
            AwardAmountInfo aai = awardAmountInfoService.fetchAwardAmountInfoWithHighestTransactionId(award.getAwardAmountInfos());
            boolean addToList = false;
            int index = findAwardHierarchyNodeIndex(awardHierarchyNode);
            //Date changes in hierarchy view are captured here.  If the transaction is a No Cost Extension, we report the transaction
            //details for display in history tab.
            if(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate()!=null &&
                    !timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate().equals(aai.getCurrentFundEffectiveDate())){
                if (isNoCostExtension && 
                        timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate().before(aai.getCurrentFundEffectiveDate())) {
                            AwardAmountInfo tempAai = getNewAwardAmountInfoForDateChangeTransaction(aai, award, timeAndMoneyDocument.getDocumentNumber());
                            aai = tempAai;
                            aai.setCurrentFundEffectiveDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate());
                            awardHierarchyNode.getValue().setCurrentFundEffectiveDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate());
                            award.getAwardAmountInfos().add(aai);
                            addTransactionDetails(aai.getAwardNumber(), aai.getAwardNumber(), aai.getSequenceNumber(), timeAndMoneyDocument.getAwardNumber(),
                                                    timeAndMoneyDocument.getDocumentNumber(), OBLIGATED_START_COMMENT, dateChangeTransactionDetailItems);
                }else {AwardAmountInfo tempAai = getNewAwardAmountInfoForDateChangeTransaction(aai, award, timeAndMoneyDocument.getDocumentNumber());
                        aai = tempAai;
                        aai.setCurrentFundEffectiveDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate());
                        awardHierarchyNode.getValue().setCurrentFundEffectiveDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate());
                        award.getAwardAmountInfos().add(aai);
                }
            }
            //special case where a user can enter an invalid date that will throw a hard error.  If the user tries to change that date back
            //to the original date, we need to capture that and change the value on the document which is the date value that gets validated
            //in save rules.
            if(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate()!=null && 
                    timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate().equals(aai.getCurrentFundEffectiveDate()) &&
                    !timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate().equals(awardHierarchyNode.getValue().getCurrentFundEffectiveDate())) {
                awardHierarchyNode.getValue().setCurrentFundEffectiveDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate());
            }
            if(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate()!=null &&
                    !timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate().equals(aai.getObligationExpirationDate())){
                if (isNoCostExtension && 
                        timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate().after(aai.getObligationExpirationDate())) {
                            AwardAmountInfo tempAai = getNewAwardAmountInfoForDateChangeTransaction(aai, award, timeAndMoneyDocument.getDocumentNumber());
                            aai = tempAai;
                            aai.setObligationExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate());
                            awardHierarchyNode.getValue().setObligationExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate());
                            award.getAwardAmountInfos().add(aai);
                            addTransactionDetails(aai.getAwardNumber(), aai.getAwardNumber(), aai.getSequenceNumber(), timeAndMoneyDocument.getAwardNumber(),
                                                        timeAndMoneyDocument.getDocumentNumber(), OBLIGATED_END_COMMENT, dateChangeTransactionDetailItems);
                }else {AwardAmountInfo tempAai = getNewAwardAmountInfoForDateChangeTransaction(aai, award, timeAndMoneyDocument.getDocumentNumber());
                        aai = tempAai;
                        aai.setObligationExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate());
                        awardHierarchyNode.getValue().setObligationExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate());
                        award.getAwardAmountInfos().add(aai);
                }
            }
            //special case where a user can enter an invalid date that will throw a hard error.  If the user tries to change that date back
            //to the original date, we need to capture that and change the value on the document which is the date value that gets validated
            //in save rules.
            if(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate()!=null && 
                    timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate().equals(aai.getObligationExpirationDate()) &&
                    !timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate().equals(awardHierarchyNode.getValue().getObligationExpirationDate())) {
                awardHierarchyNode.getValue().setObligationExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getObligationExpirationDate());
            }
            if(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate()!=null &&
                    !timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate().equals(aai.getFinalExpirationDate())){ 
                if (isNoCostExtension && 
                        timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate().after(aai.getFinalExpirationDate())) {
                            AwardAmountInfo tempAai = getNewAwardAmountInfoForDateChangeTransaction(aai, award, timeAndMoneyDocument.getDocumentNumber());
                            aai = tempAai;
                            aai.setFinalExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate());
                            awardHierarchyNode.getValue().setFinalExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate());
                            award.getAwardAmountInfos().add(aai);
                            addTransactionDetails(aai.getAwardNumber(), aai.getAwardNumber(), aai.getSequenceNumber(), timeAndMoneyDocument.getAwardNumber(),
                                                    timeAndMoneyDocument.getDocumentNumber(), PROJECT_END_COMMENT, dateChangeTransactionDetailItems);
                }else {AwardAmountInfo tempAai = getNewAwardAmountInfoForDateChangeTransaction(aai, award, timeAndMoneyDocument.getDocumentNumber());
                        aai = tempAai;
                        aai.setFinalExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate());
                        awardHierarchyNode.getValue().setFinalExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate());
                        award.getAwardAmountInfos().add(aai);
                }
            }
            //special case where a user can enter an invalid date that will throw a hard error.  If the user tries to change that date back
            //to the original date, we need to capture that and change the value on the document which is the date value that gets validated
            //in save rules.
            if(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getCurrentFundEffectiveDate()!=null && 
                    timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate().equals(aai.getFinalExpirationDate()) &&
                    !timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate().equals(awardHierarchyNode.getValue().getFinalExpirationDate())) {
                awardHierarchyNode.getValue().setFinalExpirationDate(timeAndMoneyForm.getAwardHierarchyNodeItems().get(index).getFinalExpirationDate());
            }
            getBusinessObjectService().save(award);
        }
        forward = super.save(mapping, form, request, response);
        //we want to apply save rules to doc before we save any captured changes.
        //The save on awardAmountInfoObjects should always be after the save on entire award object otherwise awardAmountInfoObjects changes get overwritten.
        getBusinessObjectService().save(awardAmountInfoObjects);
        getBusinessObjectService().save(timeAndMoneyDocument.getAwardAmountTransactions());
        //save all transaction details from No Cost extension date changes.
        getBusinessObjectService().save(dateChangeTransactionDetailItems);
        timeAndMoneyDocument.getAward().refreshReferenceObject("awardAmountInfos");//don't think I need to do this.

        return forward;
    }
    
    /*
     * 
     * This method creates a transactionDetail object and adds it to the list for persistence later.
     * 
     * @param sourceAwardNumber
     * @param destinationAwardNumber
     * @param sequenceNumber
     * @param pendingTransaction
     * @param currentAwardNumber
     * @param documentNumber
     * @param transactionDetailItems
     */
    protected void addTransactionDetails(String sourceAwardNumber, String destinationAwardNumber, Integer sequenceNumber, String currentAwardNumber, String documentNumber, 
            String commentsString, List<TransactionDetail> transactionDetailItems){
        TransactionDetail transactionDetail = new TransactionDetail();
        transactionDetail.setSourceAwardNumber(sourceAwardNumber);
        transactionDetail.setSequenceNumber(sequenceNumber);
        transactionDetail.setDestinationAwardNumber(destinationAwardNumber);
        //transactionDetail.setAnticipatedAmount(pendingTransaction.getAnticipatedAmount());
        //transactionDetail.setObligatedAmount(pendingTransaction.getObligatedAmount());
        transactionDetail.setAwardNumber(currentAwardNumber);
        transactionDetail.setTransactionId(new Long(-1));
        transactionDetail.setTimeAndMoneyDocumentNumber(documentNumber);
        transactionDetail.setComments(commentsString);
        transactionDetailItems.add(transactionDetail);
    }
    
    /*
     * add money to amount info Totals, and Distributables.
     * 
     */
    private AwardAmountInfo getNewAwardAmountInfoForDateChangeTransaction(AwardAmountInfo awardAmountInfo,  Award award, String documentNumber) {
        
        AwardAmountInfo newAwardAmountInfo = new AwardAmountInfo();
        newAwardAmountInfo.setAwardNumber(awardAmountInfo.getAwardNumber());
        newAwardAmountInfo.setSequenceNumber(award.getSequenceNumber());
        newAwardAmountInfo.setFinalExpirationDate(awardAmountInfo.getFinalExpirationDate());
        newAwardAmountInfo.setCurrentFundEffectiveDate(awardAmountInfo.getCurrentFundEffectiveDate());
        newAwardAmountInfo.setObligationExpirationDate(awardAmountInfo.getObligationExpirationDate());
        newAwardAmountInfo.setTimeAndMoneyDocumentNumber(documentNumber);
        newAwardAmountInfo.setTransactionId(null);
        newAwardAmountInfo.setAward(award);
       //add transaction amounts to the AmountInfo
        newAwardAmountInfo.setObliDistributableAmount(awardAmountInfo.getObliDistributableAmount());
        newAwardAmountInfo.setAmountObligatedToDate(awardAmountInfo.getAmountObligatedToDate());
        newAwardAmountInfo.setAntDistributableAmount(awardAmountInfo.getAntDistributableAmount());
        newAwardAmountInfo.setAnticipatedTotalAmount(awardAmountInfo.getAnticipatedTotalAmount());
        newAwardAmountInfo.setOriginatingAwardVersion(award.getSequenceNumber());

        
        //updateAmountFields(updateAmounts, addOrSubtract, pendingTransaction, awardAmountInfo, newAwardAmountInfo);
        
        //addAwardAmountTransaction(newAwardAmountInfo.getAwardNumber(), awardAmountTransactionItems, newAwardAmountTransaction, documentNumber);

        return newAwardAmountInfo;
    }
    
    /*
     * This is a helper method to add awardAmountTransaction information.
     * 
     * AwardAmountTransacion table is going to have one entry per document, per affected award.
     * Affected award here means an award that is part of any of the Pending Transactions.
     * 
     * That's why we will maintain a map of AwardAmountTransaction Objects with awardNumber as the key. If the key is present, we won't
     * add a new entry here, otherwise we will. We will persist all awardAmountTransaction objects later.
     */
    private void addAwardAmountTransaction(String awardNumber, Map<String, AwardAmountTransaction> awardAmountTransactionItems, AwardAmountTransaction newAwardAmountTransaction, String documentNumber) {
        if(!awardAmountTransactionItems.containsKey(awardNumber)){
            AwardAmountTransaction newAwardAmountTransaction1 = new AwardAmountTransaction(); 
            newAwardAmountTransaction1.setAwardNumber(awardNumber);
            newAwardAmountTransaction1.setDocumentNumber(documentNumber);
            newAwardAmountTransaction1.setTransactionTypeCode(newAwardAmountTransaction.getTransactionTypeCode());
            newAwardAmountTransaction1.setComments(newAwardAmountTransaction.getComments());
            newAwardAmountTransaction1.setNoticeDate(newAwardAmountTransaction.getNoticeDate());
            awardAmountTransactionItems.put(awardNumber, newAwardAmountTransaction1);    
        }       
    }
    
    private void updateAwardAmountTransactions(TimeAndMoneyDocument timeAndMoneyDocument) {
        AwardAmountTransaction aat = timeAndMoneyDocument.getNewAwardAmountTransaction();
        if(!(aat == null)) {
            if (timeAndMoneyDocument.getAwardAmountTransactions().size() == 0 ||
                    (timeAndMoneyDocument.getAwardAmountTransactions().size() == 1 && 
                            timeAndMoneyDocument.getAwardAmountTransactions().get(0).getTransactionTypeCode() == null)) {
                aat.setAwardNumber(timeAndMoneyDocument.getAwardNumber());
                aat.setDocumentNumber(timeAndMoneyDocument.getDocumentNumber());
                timeAndMoneyDocument.getAwardAmountTransactions().set(0, aat);
            }else {
                AwardAmountTransaction firstAatInList = timeAndMoneyDocument.getAwardAmountTransactions().get(0);
                for(AwardAmountTransaction awardAmountTransaction : timeAndMoneyDocument.getAwardAmountTransactions()) {
                    awardAmountTransaction.setTransactionTypeCode(firstAatInList.getTransactionTypeCode());
                    awardAmountTransaction.setNoticeDate(firstAatInList.getNoticeDate());
                    awardAmountTransaction.setComments(firstAatInList.getComments());
                }
            }
        }
    }

    /*
     * This method...
     */
    private int findAwardHierarchyNodeIndex(Entry<String, AwardHierarchyNode> awardHierarchyNode) {
        String reverseAwardNumber = StringUtils.reverse(awardHierarchyNode.getValue().getAwardNumber());
        String i= StringUtils.substring(reverseAwardNumber, 0, StringUtils.indexOf(reverseAwardNumber, ZERO));
        int index = Integer.parseInt(StringUtils.reverse(i));
        return index;
    }
    
    /*
     * override route to call save before we route.
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward actionForward;
        save(mapping, form, request, response);
        actionForward = super.route(mapping, form, request, response);            
        return actionForward;
    }
    
    
        
    /**
     * override to call save before we blanket approve.
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#blanketApprove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ActionForward actionForward;
        save(mapping, form, request, response);
        actionForward = super.blanketApprove(mapping, form, request, response);            
        return actionForward;
    }
    
    
    

    /**
     * must remove all award amount infos corresponding to this document.  Date changes create and add new Award Amount Info.  Pending Transactions
     * do not create new Award Amount Info until the document is routed or blanket approved.
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @SuppressWarnings("unchecked")
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionForward actionForward;
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) form;
        TimeAndMoneyDocument timeAndMoneyDocument = timeAndMoneyForm.getTimeAndMoneyDocument();
        //remove all Award Amount Infos from all affected awards.
        Map<String, AwardHierarchy> awardHierarchyItems = timeAndMoneyDocument.getAwardHierarchyItems();
        for (Map.Entry<String, AwardHierarchy> awardHierarchyEntry : awardHierarchyItems.entrySet()) {
            AwardHierarchy awardHierarchy = awardHierarchyEntry.getValue();
            Award award = getWorkingAwardVersion(awardHierarchy.getAwardNumber());
            List<AwardAmountInfo> deleteCollection = new ArrayList<AwardAmountInfo>();
            for (AwardAmountInfo awardAmountInfo : award.getAwardAmountInfos()) {
                if(!(awardAmountInfo.getTimeAndMoneyDocumentNumber() == null)) {
                    if(awardAmountInfo.getTimeAndMoneyDocumentNumber().equals(timeAndMoneyDocument.getDocumentNumber())) {
                        deleteCollection.add(awardAmountInfo);
                    }
                }
            }
            getBusinessObjectService().delete(deleteCollection);
            deleteCollection.clear();
        }
        actionForward = super.cancel(mapping, form, request, response);   
        
        return actionForward;
    }

    /**
     * 
     * This method refreshes the view depending on various view optins like either active or pending view or dates only, totals and 
     * distributed/distributable. 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) form;
        TimeAndMoneyDocument doc = timeAndMoneyForm.getTimeAndMoneyDocument();
        //perform this logic if pending view
        if(StringUtils.equalsIgnoreCase(timeAndMoneyForm.getCurrentOrPendingView(),PENDING_VIEW)){
            
            Map<String, AwardAmountTransaction> awardAmountTransactionItems = new HashMap<String, AwardAmountTransaction>();
            List<Award> awardItems = new ArrayList<Award>();
            List<TransactionDetail> transactionDetailItems = new ArrayList<TransactionDetail>();
            
            updateDocumentFromSession(doc);

            ActivePendingTransactionsService service = KraServiceLocator.getService(ActivePendingTransactionsService.class);
            //service.processTransactions(doc, doc.getNewAwardAmountTransaction(), awardAmountTransactionItems, awardItems, transactionDetailItems);
            service.processTransactions(doc, doc.getAwardAmountTransactions().get(0), awardAmountTransactionItems, awardItems, transactionDetailItems);
            GlobalVariables.getUserSession().addObject(GlobalVariables.getUserSession().getKualiSessionId()+Constants.TIME_AND_MONEY_DOCUMENT_STRING_FOR_SESSION, doc);
            //doc.refreshReferenceObject(PENDING_TRANSACTIONS_ATTRIBUTE_NAME);
        //perform this logic if active view
        }else if(StringUtils.equalsIgnoreCase(timeAndMoneyForm.getCurrentOrPendingView(),ACTIVE_VIEW)){
            timeAndMoneyForm.setOrder(new ArrayList<String>());
            
            Award tmpAward = getCurrentAward(doc);
            doc.setAwardHierarchyItems(getAwardHierarchyService().getAwardHierarchy(doc.getRootAwardNumber(), timeAndMoneyForm.getOrder()));
            if(tmpAward != null) {
                getAwardHierarchyService().populateAwardHierarchyNodes(doc.getAwardHierarchyItems(), doc.getAwardHierarchyNodes(), tmpAward.getAwardNumber(), tmpAward.getSequenceNumber().toString());
            } else {
                getAwardHierarchyService().populateAwardHierarchyNodes(doc.getAwardHierarchyItems(), doc.getAwardHierarchyNodes(), null, null);
            }
            GlobalVariables.getUserSession().addObject(GlobalVariables.getUserSession().getKualiSessionId()+Constants.TIME_AND_MONEY_DOCUMENT_STRING_FOR_SESSION, doc);
        }
        return mapping.findForward(Constants.MAPPING_AWARD_BASIC);
    }

    /*
     * This method...
     * @param doc
     */
    private void updateDocumentFromSession(TimeAndMoneyDocument doc) {
        if(doc.getAwardHierarchyNodes()==null || doc.getAwardHierarchyNodes().size()==0){
            if(GlobalVariables.getUserSession().retrieveObject(GlobalVariables.getUserSession().getKualiSessionId()+Constants.TIME_AND_MONEY_DOCUMENT_STRING_FOR_SESSION)!=null){
                TimeAndMoneyDocument document = (TimeAndMoneyDocument)GlobalVariables.getUserSession().retrieveObject(GlobalVariables.getUserSession().getKualiSessionId()+Constants.TIME_AND_MONEY_DOCUMENT_STRING_FOR_SESSION);
                doc.setAwardHierarchyItems(document.getAwardHierarchyItems());
                doc.setAwardHierarchyNodes(document.getAwardHierarchyNodes());
            }else {
                throw new RuntimeException("Can't Retrieve Time And Money Document from Session");
            }
        }
    }
    
//    private Award getCurrentAward(TimeAndMoneyDocument timeAndMoneyDocument) {
//        Award tmpAward = timeAndMoneyDocument.getAward();
//        if(tmpAward == null) {
//            AwardDocument awardDocument = (AwardDocument) GlobalVariables.getUserSession().retrieveObject(Constants.DOCUMENT_NUMBER_FOR_RETURN_TO_AWARD);
//            if(awardDocument != null) {
//                tmpAward = awardDocument.getAward();
//            }
//        }
//        
//        return tmpAward;
//    }
    
    private Award getCurrentAward(TimeAndMoneyDocument timeAndMoneyDocument) {
        Award tmpAward = timeAndMoneyDocument.getAward();
        if(tmpAward == null) {
            tmpAward = getActivePendingTransactionsService().getWorkingAwardVersion(timeAndMoneyDocument.getAwardNumber());
        }
        
        return tmpAward;
    }
    
   
    
    /**
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) form;
        TimeAndMoneyDocument timeAndMoneyDocument = timeAndMoneyForm.getTimeAndMoneyDocument();
        
        ActionForward forward = handleDocument(mapping, form, request, response, timeAndMoneyForm);
        timeAndMoneyForm.initializeFormOrDocumentBasedOnCommand();        
        String rootAwardNumber = timeAndMoneyForm.getTimeAndMoneyDocument().getRootAwardNumber();
                
        timeAndMoneyDocument.setAwardHierarchyItems(getAwardHierarchyService().getAwardHierarchy(rootAwardNumber, timeAndMoneyForm.getOrder()));
        timeAndMoneyDocument.setAwardNumber(rootAwardNumber);  
        
        Award tmpAward = getCurrentAward(timeAndMoneyDocument);
        if(tmpAward != null) {
            getAwardHierarchyService().populateAwardHierarchyNodes(timeAndMoneyDocument.getAwardHierarchyItems(), timeAndMoneyDocument.getAwardHierarchyNodes(), tmpAward.getAwardNumber(), tmpAward.getSequenceNumber().toString());
        } else {
            getAwardHierarchyService().populateAwardHierarchyNodes(timeAndMoneyDocument.getAwardHierarchyItems(), timeAndMoneyDocument.getAwardHierarchyNodes(), null, null);
        }

        GlobalVariables.getUserSession().addObject(GlobalVariables.getUserSession().getKualiSessionId()+Constants.TIME_AND_MONEY_DOCUMENT_STRING_FOR_SESSION, timeAndMoneyDocument);
        
        populateOtherPanels(timeAndMoneyForm.getTransactionBean().getNewAwardAmountTransaction(), timeAndMoneyForm, rootAwardNumber);
        
        return forward;
    }
         
    
    /*
     * This method retrieves AwardHierarchyService
     */
    protected AwardHierarchyService getAwardHierarchyService(){        
        return (AwardHierarchyService) KraServiceLocator.getService(AwardHierarchyService.class);
    }
    
    /**
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param awardForm
     * @return
     * @throws Exception
     */
    public ActionForward handleDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                  HttpServletResponse response, TimeAndMoneyForm timeAndMoneyForm) throws Exception {
        String command = timeAndMoneyForm.getCommand();
        ActionForward forward;        
        if (KEWConstants.ACTIONLIST_INLINE_COMMAND.equals(command)) {
            String docIdRequestParameter = request.getParameter(KNSConstants.PARAMETER_DOC_ID);
            Document retrievedDocument = getDocumentService().getByDocumentHeaderId(docIdRequestParameter);
            timeAndMoneyForm.setDocument(retrievedDocument);
            request.setAttribute(KNSConstants.PARAMETER_DOC_ID, docIdRequestParameter);
            ActionForward baseForward = mapping.findForward(Constants.MAPPING_COPY_PROPOSAL_PAGE);
            forward = new ActionForward(buildForwardStringForActionListCommand(
                    baseForward.getPath(),docIdRequestParameter));  
        } else {
        forward = super.docHandler(mapping, form, request, response);
        }
        
        return forward;
    }
    
    /**
     * 
     * This method adds a new transaction.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ((TimeAndMoneyForm) form).getTransactionBean().addPendingTransactionItem();
        return mapping.findForward(Constants.MAPPING_BASIC);        
    }
    
    /**
     * 
     * This method adds a deletes a transaction.
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteTransaction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        ((TimeAndMoneyForm) form).getTransactionBean().deletePendingTransactionItem(getLineToDelete(request));
        return mapping.findForward(Constants.MAPPING_BASIC);        
    }
    
    /**
     * 
     * This method switches the award for history, summary and Action Summary panels.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward switchAward(ActionMapping mapping, ActionForm form , HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm)form;
        String goToAwardNumber = timeAndMoneyForm.getGoToAwardNumber();
        
        populateOtherPanels(timeAndMoneyForm.getTransactionBean().getNewAwardAmountTransaction(), timeAndMoneyForm, goToAwardNumber);
        
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /*
     * This method populates Summary, Action Summary and History panels for selected award.
     *  
     * @param timeAndMoneyForm
     * @param timeAndMoneyForm
     * @param goToAwardNumber
     * @throws LookupException
     * @throws SQLException
     */
    private void populateOtherPanels(AwardAmountTransaction newAwardAmountTransaction, TimeAndMoneyForm timeAndMoneyForm, String goToAwardNumber)
            throws LookupException, SQLException, WorkflowException {
        Award award = getWorkingAwardVersion(goToAwardNumber);
        TimeAndMoneyDocument timeAndMoneyDocument = timeAndMoneyForm.getTimeAndMoneyDocument();
        timeAndMoneyDocument.setAwardNumber(award.getAwardNumber());
        timeAndMoneyDocument.setAward(award);
        if(isNewAward(timeAndMoneyForm) && !(timeAndMoneyDocument.getAward().getBeginDate() == null)){
            AwardDirectFandADistributionService awardDirectFandADistributionService = getAwardDirectFandADistributionService();
            timeAndMoneyForm.getTimeAndMoneyDocument().getAward().setAwardDirectFandADistributions
                                (awardDirectFandADistributionService.
                                        generateDefaultAwardDirectFandADistributionPeriods(timeAndMoneyForm.getTimeAndMoneyDocument().getAward()));
        }
        
        
        TimeAndMoneyHistoryService tamhs = KraServiceLocator.getService(TimeAndMoneyHistoryService.class);
        
        tamhs.getTimeAndMoneyHistory(timeAndMoneyDocument.getAwardNumber(), timeAndMoneyDocument.getTimeAndMoneyHistory(), timeAndMoneyForm.getColumnSpan());
        
        timeAndMoneyDocument.getAwardVersionHistoryList().clear();
        tamhs.buildTimeAndMoneyHistoryObjects(award.getAwardNumber(), timeAndMoneyDocument.getAwardVersionHistoryList());
        TimeAndMoneyActionSummaryService tamass = KraServiceLocator.getService(TimeAndMoneyActionSummaryService.class);
        timeAndMoneyDocument.getTimeAndMoneyActionSummaryItems().clear();
        tamass.populateActionSummary(timeAndMoneyDocument.getTimeAndMoneyActionSummaryItems(), goToAwardNumber);
        
        timeAndMoneyDocument.setNewAwardAmountTransaction(newAwardAmountTransaction);
    }
    
    /**
     * This method tests if the award is new by checking the size of AwardDirectFandADistributions on the Award.
     * @param awardForm
     * @return
     */
    public boolean isNewAward(TimeAndMoneyForm timeAndMoneyForm) {
        return timeAndMoneyForm.getTimeAndMoneyDocument().getAward().getAwardDirectFandADistributions().size() == 0;
    }
    
    /**
     * 
     * This method is a helper method to retrieve AwardSponsorTermService.
     * @return
     */
    protected AwardDirectFandADistributionService getAwardDirectFandADistributionService() {
        return KraServiceLocator.getService(AwardDirectFandADistributionService.class);
    }

    public Award getWorkingAwardVersion(String goToAwardNumber) {
        Award award = null;
        award = getPendingAwardVersion(goToAwardNumber);
        if (award == null) {
            award = getActiveAwardVersion(goToAwardNumber);
        }
        return award;
    }
    
    /*
     * This method retrieves the pending award version.
     * 
     * @param doc
     * @param goToAwardNumber
     */
    @SuppressWarnings("unchecked")
    public Award getPendingAwardVersion(String goToAwardNumber) {
        
        Award award = null;
        BusinessObjectService businessObjectService =  KraServiceLocator.getService(BusinessObjectService.class);
        List<Award> awards = (List<Award>)businessObjectService.findMatchingOrderBy(Award.class, getHashMapToFindActiveAward(goToAwardNumber), "sequenceNumber", true);
        if(!(awards.size() == 0)) {
            award = awards.get(awards.size() - 1);
        }
      
        return award;
    }
    
   
    private Award getActiveAwardVersion(String goToAwardNumber) {
        VersionHistoryService vhs = KraServiceLocator.getService(VersionHistoryService.class);  
        VersionHistory vh = vhs.findActiveVersion(Award.class, goToAwardNumber);
        Award award = null;
        
        if(vh!=null){
            award = (Award) vh.getSequenceOwner();
        }else{
            BusinessObjectService businessObjectService =  KraServiceLocator.getService(BusinessObjectService.class);
            award = ((List<Award>)businessObjectService.findMatching(Award.class, getHashMapToFindActiveAward(goToAwardNumber))).get(0);              
        }
        return award;
    }
    private Map<String, String> getHashMapToFindActiveAward(String goToAwardNumber) {
        Map<String, String> map = new HashMap<String,String>();
        map.put("awardNumber", goToAwardNumber);
        return map;
    }
    /*
     * Retrieves an ActivePendingTransactionsService.
     */
    protected ActivePendingTransactionsService getActivePendingTransactionsService(){
        return (ActivePendingTransactionsService) KraServiceLocator.getService(ActivePendingTransactionsService.class);
    }
    
    /**
     * 
     * This method builds the string for the ActionForward 
     * @param forwardPath
     * @param docIdRequestParameter
     * @return
     */
    public String buildForwardStringForActionListCommand(String forwardPath, String docIdRequestParameter){
        StringBuilder sb = new StringBuilder();
        sb.append(forwardPath);
        sb.append("?");
        sb.append(KNSConstants.PARAMETER_DOC_ID);
        sb.append("=");
        sb.append(docIdRequestParameter);
        return sb.toString();
    }    
    
    
    public ActionForward addTransaction(){
        return null;
    }
    
    public ActionForward deleteTransaction(){
        return null;
    }
    
    public ActionForward submit(){
        return null;
    }
    
    /**
     * 
     * This method adds a new AwardDirectFandADistribution to the list.  
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addAwardDirectFandADistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        //FIXME AwardDirectFandADistributionBean seems like the wrong place for these types of methods.
        new AwardDirectFandADistributionBean().addAwardDirectFandADistribution(((TimeAndMoneyForm) form).getAwardDirectFandADistributionBean());    
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * 
     * This method removes an AwardDirectFandADistribution from the list. 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteAwardDirectFandADistribution(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) form;
        timeAndMoneyForm.getTimeAndMoneyDocument().getAward().getAwardDirectFandADistributions().remove(getLineToDelete(request));
        //FIXME AwardDirectFandADistributionBean seems like the wrong place for these types of methods.
        new AwardDirectFandADistributionBean().updateBudgetPeriodsAfterDelete(timeAndMoneyForm.getTimeAndMoneyDocument().getAward().getAwardDirectFandADistributions());
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * This method is used to recalculate the Total amounts in the Direct F and A Distribution panel.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return mapping forward
     * @throws Exception
     */
    public ActionForward recalculateDirectFandADistributionTotals(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
       
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        businessObjectService = KraServiceLocator.getService(BusinessObjectService.class);
        return businessObjectService;
    }
    
    /**
     * 
     * This method retrieves the awardDocument from the session and redirects the user to the appropriate Award
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward returnToAward(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        TimeAndMoneyForm timeAndMoneyForm = (TimeAndMoneyForm) form;
        TimeAndMoneyDocument timeAndMoneyDocument = timeAndMoneyForm.getTimeAndMoneyDocument();
        getBusinessObjectService().save(timeAndMoneyDocument);
        
        AwardDocument awardDocument = (AwardDocument)GlobalVariables.getUserSession().retrieveObject(Constants.DOCUMENT_NUMBER_FOR_RETURN_TO_AWARD);
        
        Long routeHeaderId = awardDocument.getDocumentHeader().getWorkflowDocument().getRouteHeaderId();

        String forward = buildForwardUrl(routeHeaderId);
        return new ActionForward(forward, true);
    }

}
