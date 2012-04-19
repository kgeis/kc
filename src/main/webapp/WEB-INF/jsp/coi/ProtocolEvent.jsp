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

<c:choose>
    <c:when test="${fn:length(KualiForm.disclosureHelper.newProtocols) == 0}" >
        <c:set var="notfound" value=" (No new protocol found)" />
    </c:when>
    <c:otherwise>
        <c:set var="notfound" value="" />
    </c:otherwise>
</c:choose>

<kul:page lookup="false" 
          docTitle="New Protocol For Disclosure" 
          transactionalDocument="false"
          renderMultipart="true" 
          htmlFormAction="protocolEventDisclosure">
          
    <script language="javascript" src="scripts/kuali_application.js"></script>
    
    <div id="workarea">

<c:set var="protocolAttributes" value="${DataDictionary.Protocol.attributes}" />

<kul:tab tabTitle="New Protocols for disclosure${notfound}"
         defaultOpen="true"
         alwaysOpen="true"
         transparentBackground="true" 
         useCurrentTabIndexAsKey="true"
         tabErrorKey="notificationTemplates[*">
         
    <div class="tab-container" align="center" id="G100">
        <h3>
            <span class="subhead-left">Protocol List</span>
            <span class="subhead-right"><kul:help businessObjectClassName="org.kuali.kra.coi.CoiDisclosure" altText="help" /></span>
        </h3>
        
            <table style="border-top-width:1px; border-top-style:solid; border-top-color:#999999;" cellpAdding="0" cellspacing="0" width="50%" align="center" >
                <tr>
                                    <th><div align="center">Protocol Number</div></th> 
                                    <th><div align="center">Protocol Name</div></th> 
                                    <th><div align="center">Protocol Type</div></th> 
                                    <th><div align="center">Action</div></th> 
               </tr>
        <c:forEach items="${KualiForm.disclosureHelper.newProtocols}" var="protocol" varStatus="status">
               <tr>
                  <td align="left" valign="middle">
					<div align="left">
                		${protocol.protocolNumber}
					</div>
				  </td>
                  <td align="left" valign="middle">
					<div align="left">
                		${protocol.title}
					</div>
				  </td>
                  <td align="left" valign="middle">
					<div align="left">
                		${protocol.protocolType.description} 
					</div>
				  </td>
                    <td>
                      <div align="center">
                        <a title="New Protocol Disclosure" href="coiDisclosure.do?methodToCall=newProjectDisclosure&disclosureHelper.eventTypeCode=3&disclosureHelper.newProjectId=${protocol.protocolId}&disclosureHelper.newModuleItemKey=${protocol.protocolNumber}&docTypeName=CoiDisclosureDocument">Report Coi</a>                        
                      </div>
                    </td>
               </tr>
        </c:forEach>
            </table>   
    </div> 
</kul:tab>

<kul:panelFooter />

<div id="globalbuttons" class="globalbuttons">
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" styleClass="globalbuttons" property="methodToCall.close" title="close" alt="close"/>
</div>

<script type="text/javascript">
   var $j = jQuery.noConflict();
   $j(document).ready(function () {
        $j('#horz-links').hide();
   }); // end document.ready
</script>

</kul:page>
