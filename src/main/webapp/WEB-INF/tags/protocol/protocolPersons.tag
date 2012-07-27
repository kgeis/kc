 <%--
 Copyright 2005-2010 The Kuali Foundation

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
<%@ attribute name="protocolAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="protocolPersonAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="personAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="protocolUnitsAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="unitAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="protocolAttachmentPersonnelAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="attachmentFileAttributes" required="true" type="java.util.Map" %>
<%@ attribute name="optionListClass" required="true" %>
<%@ attribute name="protocolAttachmentTypeByGroupValuesFinder" required="true" %>

<div id="workarea">
<c:forEach items="${KualiForm.document.protocolList[0].protocolPersons}" var="person" varStatus="status">
    <c:set var="protocolPersonProperty" value="document.protocolList[0].protocolPersons[${status.index}]" />
    <c:if test="${not KualiForm.personnelHelper.protocolFinal}">
        <c:set var="leftSideHtmlProperty" value="${protocolPersonProperty}.delete" />
    </c:if>
    <c:set var="personUnitRequired" value="${person.protocolPersonRole.unitDetailsRequired}" />
    <c:set var="transparent" value="false" />

    <c:if test="${status.first}">
      <c:set var="transparent" value="true" />
    </c:if> 
    	<c:set var="descri" value="${person.protocolPersonRole.description}" />
	<c:set var="personIndex" value="${status.index}" />
	<kul:tab tabTitle="${fn:substring(person.personName, 0, 22)}"
			 tabErrorKey="document.protocolList[0].protocolPersons[${personIndex}]*"
			 auditCluster="personnelAuditErrors" 
			 tabAuditKey="document.protocolList[0].protocolPersons[${personIndex}].*" 
			 useRiceAuditMode="true"
	         tabDescription="${descri}"
	         leftSideHtmlProperty="${leftSideHtmlProperty}" 
	         leftSideHtmlAttribute="${protocolPersonAttributes.delete}" 
	     	 leftSideHtmlDisabled="false" 
	         defaultOpen="${hasErrors}" 
			 useCurrentTabIndexAsKey="true"
	         transparentBackground="${transparent}">
        <div class="tab-container" align="center">
		    <div id="workarea">
				<h3>
					<span class="subhead-left"><bean:write name="KualiForm" property="${protocolPersonProperty}.personName"/></span>
				    <span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.protocol.personnel.ProtocolPerson" altText="help"/></span>
				</h3>
				<kra-protocol:personDetailsSection personIndex="${status.index}" protocolPerson="${protocolPersonProperty}" protocolPersonAttributes="${protocolPersonAttributes}" optionListClass="${optionListClass}"/>
				<kra-protocol:personContactInformationSection personIndex="${status.index}" protocolPerson="${protocolPersonProperty}" personAttributes="${personAttributes}"/>
  				<kra-protocol:personAttachmentSection personIndex="${status.index}" protocolPerson="${protocolPersonProperty}" protocolAttachmentTypeByGroupValuesFinder="${protocolAttachmentTypeByGroupValuesFinder}" protocolAttachmentPersonnelAttributes="${protocolAttachmentPersonnelAttributes}"/>
  				<c:if test="${personUnitRequired}">
					<kra-protocol:personUnitsSection personIndex="${status.index}" protocolPerson="${protocolPersonProperty}" protocolUnitsAttributes="${protocolUnitsAttributes}" unitAttributes="${unitAttributes}"/>
  				</c:if>
			</div>
		 </div>
	</kul:tab>
 </c:forEach>

<c:if test="${fn:length(KualiForm.document.protocolList[0].protocolPersons) > 0}">
    <kul:panelFooter />
</c:if>

</div>
