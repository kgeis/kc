<%--
 Copyright 2006-2008 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.osedu.org/licenses/ECL-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/WEB-INF/jsp/kraTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Awards" />
<div class="body">
  <ul class="chan">
    <li>Account Type</li>
    <li>Award Status</li>
    <li>Award Type</li>
    <li>Basis of Payment</li>
    <li>Calculated Cost Elements</li>
    <li><portal:portalLink displayTitle="true" title="Contact Type" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kra.award.bo.ContactType&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="Distribution" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kra.award.bo.Distribution&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li>Equipment Approval</li>
    <li><portal:portalLink displayTitle="true" title="Frequency" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kra.award.bo.Frequency&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li><portal:portalLink displayTitle="true" title="Frequency Base" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kra.award.bo.FrequencyBase&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li>Invention</li>
    <li>Method of Payment</li>
    <li>Prior Approval</li>
    <li>Property</li>
    <li>Publication</li>
    <li>Referenced Document</li>
    <li><portal:portalLink displayTitle="true" title="Report" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kra.award.bo.Report&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>    
    <li><portal:portalLink displayTitle="true" title="Report Class" url="kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kra.award.bo.ReportClass&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
    <li>Report Status</li>
    <li>Rights to Data</li>
    <li>Sub-Contract Approval</li>
    <li>Travel Restriction</li>
  </ul>
</div>
<channel:portalChannelBottom />