package org.nhindirect.config.ui;
/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
   in the documentation and/or other materials provided with the distribution.  
3. Neither the name of the The NHIN Direct Project (nhindirect.org) nor the names of its contributors may be used to endorse or promote 
   products derived from this software without specific prior written permission.
   
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nhind.config.rest.CertPolicyService;
import org.nhindirect.common.rest.exceptions.ServiceException;
import org.nhindirect.config.model.CertPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

import java.net.MalformedURLException;
import java.net.URL;
import org.nhindirect.config.ui.form.PolicyForm;
import org.nhindirect.policy.PolicyLexicon;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.nhindirect.policy.PolicyLexiconParser;
import org.nhindirect.policy.PolicyLexiconParserFactory;
import org.nhindirect.policy.PolicyParseException;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@Controller
@RequestMapping("/policies")
@Slf4j
public class PoliciesController {

	private CertPolicyService policyService;

	@Autowired
	public void setCertPolicyService(CertPolicyService policyService)
    {
        this.policyService = policyService;
    }

    public PoliciesController() {
	if (log.isDebugEnabled()) {
            log.error("PoliciesController initialized");
        }
    }
	
    /*********************************
     *
     * Add Policy Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/addPolicy", method = RequestMethod.POST)
    @ResponseBody
    public String addPolicy (
            @RequestParam("id") String id,
            @RequestParam("policyName") String policyName,
            @RequestParam("policyContent") String policyContent,
            @RequestParam("policyLexicon") String policyLexicon            
    )  
    { 		
        
        // Debug Statement
        if (log.isDebugEnabled()) {
            log.debug("Adding New Policy");
        }

        /*if(actionPath.equalsIgnoreCase("cancel"))
        {
                if (log.isDebugEnabled()) 
                {
                        log.debug("trying to cancel from saveupdate");
                }

                // If cancel then clear form	
                SearchDomainForm form2 = (SearchDomainForm) session.getAttribute("searchDomainForm");
                model.addAttribute(form2 != null ? form2 : new SearchDomainForm());
                model.addAttribute("ajaxRequest", AjaxUtils
                                .isAjaxRequest(requestedWith));

                mav.setViewName("main");
                mav.addObject("statusList", EntityStatus.getEntityStatusList());
                return mav;
        }*/

        

            if (log.isDebugEnabled()) 
            {
                    log.debug("Beginning to add new policy");		
            }

            try {
                URL configURL = new URL("http://localhost:8081/config-service/ConfigurationService");
            } catch (MalformedURLException ue) {

            }

            
            final CertPolicy newPolicy = new CertPolicy();
            
            newPolicy.setPolicyName(policyName);                         
            
            // Set policy lexicon type           
            final String lexiconName = policyLexicon;
            
            if(lexiconName.equalsIgnoreCase("XML")) {
                newPolicy.setLexicon(PolicyLexicon.XML);
            } else if (lexiconName.equalsIgnoreCase("JAVA_SER")) {
                newPolicy.setLexicon(PolicyLexicon.JAVA_SER);
            } else if (lexiconName.equalsIgnoreCase("SIMPLE_TEXT_V1")) {
                newPolicy.setLexicon(PolicyLexicon.SIMPLE_TEXT_V1);
            }                   
            
            
            
            // Get lexicon file from form
            //CommonsMultipartFile lexiconFile = policyForm.getFileData();            
            newPolicy.setPolicyData(policyContent.getBytes());
           
            try {
                policyService.addPolicy(newPolicy);
            } catch (ServiceException cse) {
                cse.printStackTrace();
            }
            
            
           
       
            
            return "test";
            
    }			
	
    
    /*********************************
     *
     * Remove Policies Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/removePolicies", method = RequestMethod.POST)
    public ModelAndView removePolicies (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter remove policies method");
        }
        
        if(policyForm.getPoliciesSelected() != null)
        {
            if (log.isDebugEnabled()) 
            {
                log.debug("Policies marked for removal: {}", policyForm.getPoliciesSelected().toString());
            }
        }

        if (policyService != null 
                && policyForm != null 
                && policyForm.getPoliciesSelected() != null) 
        {
            
            int policyCount = policyForm.getPoliciesSelected().size();

            if (log.isDebugEnabled()) 
            {
                log.debug("Removing Policies");
            }
            
            for(int i=0; i<policyCount; i++) 
            {
                final String policyName = policyForm.getPoliciesSelected().get(i);
 
                
                // Delete Trust Bundle(s)
                try 
                {
                	policyService.deletePolicy(policyName);
                } catch (ServiceException cse) 
                {
                    log.error("Problem removing policies");
                }
                
            }
          
            
        }        
        
        // Just redirect to Manage Policies page for now
        return new ModelAndView("redirect:/main/search?domainName=&submitType=ManagePolicies");
    }		

    
    /*********************************
     *
     * Update Policy Form Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/updatePolicyForm", method = RequestMethod.GET)        
    public ModelAndView updatePolicyForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith, 
                                            @RequestParam("policyName") String policyName,
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		
        CertPolicy policy = null;
        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter policies update form for policy #{}", policyName);
        }
        
        try {
            policy = policyService.getPolicyByName(policyName);
        } catch (ServiceException cse){
            cse.printStackTrace();
            return new ModelAndView("redirect:/");                    
        }
        
        PolicyForm pform = new PolicyForm();
                
        pform.setPolicyName(policy.getPolicyName());
        pform.setPolicyLexicon(policy.getLexicon());
        try {
            pform.setPolicyContent(new String(policy.getPolicyData(), "UTF-8"));
        } catch (UnsupportedEncodingException ie) {
            ie.printStackTrace();
        }
        
        
        model.addAttribute("sessionId", session.getId());
        
        
        model.addAttribute("policyForm", pform);
        mav.setViewName("updatePolicyForm"); 
        
        
        /*
        if (configSvc != null 
                && simpleForm != null 
                && simpleForm.getBundlesSelected() != null) 
        {
            
            int bundleCount = simpleForm.getBundlesSelected().size();            

            if (log.isDebugEnabled()) 
            {
                log.debug("Refreshing Bundles");
            }
            
            for(int i=0; i<bundleCount; i++) 
            {
                String bundleId = simpleForm.getBundlesSelected().get(i);
                log.debug("Refreshing Bundle #"+bundleId);
                                
                // Refresh Trust Bundle(s)
                try 
                {
                    configSvc.refreshTrustBundle(Long.parseLong(bundleId));
                } catch (ConfigurationServiceException cse) {
                    log.error("Could not refresh bundle: #"+bundleId);
                }
                
            }
                                    
        }
        
        model.addAttribute("ajaxRequest", AjaxUtils.isAjaxRequest(requestedWith));
        
        BundleForm bform = new BundleForm();
        bform.setId(0);
        model.addAttribute("bundleForm", bform);
        mav.setViewName("bundles"); 
        
        // Process data for Trust Bundle View
        try {

            // Get Trust Bundles
            Collection<TrustBundle> trustBundles = configSvc.getTrustBundles(false);

            if(trustBundles != null) {
                model.addAttribute("trustBundles", trustBundles);
            }


        } catch (ConfigurationServiceException e1) {

        }                            
        return new ModelAndView("redirect:/config/main/search?domainName=&submitType=ManageTrustBundles");        
        */
        return mav;     
    }		

    
    /*********************************
     *
     * Update Policy Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/updatePolicy", method = RequestMethod.POST)    
    @ResponseBody
    public String updatePolicy (@RequestParam("id") String id,
        @RequestParam("policyContent") String policyContent,
        @RequestParam("policyLexicon") String policyLexicon,
        @RequestParam("policyName") String policyName)  { 		
        
        String jsonResponse = "";
        final org.nhindirect.policy.PolicyLexicon parseLexicon;
        
        if (log.isDebugEnabled()) 
        {
            log.debug("Enter update policy #{}", id);
        }    
        
        log.error(policyName);
        
        PolicyLexicon lex = null;
        
        // Check the file for three types of policies
        if (policyLexicon.isEmpty()) {
            lex = PolicyLexicon.SIMPLE_TEXT_V1;
        }
        else
        {
            try {
                // Convert string of file contents to lexicon object
                lex = PolicyLexicon.valueOf(policyLexicon);
            }
            catch (Exception e)
            {
                log.error("Invalid lexicon name.");				
            }
        }
        
        // Determine lexicon type
        parseLexicon = lex;
        
        // Convert policy content string to byte array
        byte[] policyContentByteArray = policyContent.getBytes(); 
        
        
        /*
         * Need to complete this
        try {
            policyService.updatePolicy(policyName, policyAttributes)(Long.parseLong(id), policyName, parseLexicon, policyContentByteArray);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        */  
        
        return jsonResponse;
    }
    
    
    /*********************************
     *
     * Check Lexicon File Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/checkLexiconFile", method = {RequestMethod.GET, RequestMethod.POST } )    
    @ResponseBody
    public String checkLexiconFile (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,
        HttpServletResponse response, 
        Object command, 
        @RequestHeader(value="lexicon", required=false) String lexicon,
        MultipartHttpServletRequest request) throws FileUploadException, IOException, Exception 
    { 		        

        
        
        final org.nhindirect.policy.PolicyLexicon parseLexicon;
        String jsonResponse = "";
        String uploadToString = "";
        
        
        if (log.isDebugEnabled()) 
        {
            log.debug("Checking uploaded lexicon file for format and validation");
        }                  
        
        // Grab uploaded file from the post submission
        UploadedFile ufile = new UploadedFile();
        Iterator<String> itr =  request.getFileNames();
        MultipartFile mpf = request.getFile(itr.next());        

        try {                   
            ufile.length = mpf.getBytes().length;
            ufile.bytes= mpf.getBytes();
            ufile.type = mpf.getContentType();
            ufile.name = mpf.getOriginalFilename();
        } catch (IOException e) {            
        }
        
        // Convert upload content to string
        uploadToString = new String(ufile.bytes);
              
        lexicon = request.getParameter("lexicon");
        PolicyLexicon lex = null;
        
        // Check the file for three types of policies
        if (lexicon.isEmpty()) {
            lex = PolicyLexicon.SIMPLE_TEXT_V1;
        }
        else
        {
            try {
                // Convert string of file contents to lexicon object
                lex = PolicyLexicon.valueOf(lexicon);
            }
            catch (Exception e)
            {
                log.error("Invalid lexicon name.");				
            }
        }		                

        // Determine lexicon type
        parseLexicon = lex;
        
        InputStream inStr = null;
        try
        {			
            // Convert policy file upload to byte stream
            inStr = new ByteArrayInputStream(ufile.bytes);

            // Initialize parser engine
            final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(parseLexicon);
            
            // Attempt to parse the lexicon file for validity
            parser.parse(inStr);                        
            
            
        }                                
        catch (PolicyParseException e)
        {
            log.error("Syntax error in policy file " + " : " + e.getMessage());			
            jsonResponse = "{\"Status\":\"File was not a valid file.\",\"Content\":\""+ uploadToString +"\"}";
        }
        finally
        {
            IOUtils.closeQuietly(inStr);
        }		                                          
        
        if(jsonResponse.isEmpty()) {
            jsonResponse = "{\"Status\":\"Success\",\"Content\":\""+ uploadToString  +"\"}";                     
        }
        
        return jsonResponse;
    }
    
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/checkPolicyContent", method = {RequestMethod.GET, RequestMethod.POST } )    
    @ResponseBody
    public String checkPolicyContent (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,
        HttpServletResponse response, 
        HttpServletRequest request,
        Object command)
            throws Exception 
    { 		        
                
        final org.nhindirect.policy.PolicyLexicon parseLexicon;
        String jsonResponse = "";
        String content = request.getParameter("content");
        String lexicon = "";
        
        if (log.isDebugEnabled()) 
        {
            log.debug("Checking policy content for format and validation");
        }                                  
              
        lexicon = request.getParameter("lexicon");
        PolicyLexicon lex = null;
        
        // Check the file for three types of policies
        if (lexicon.isEmpty()) {
            lex = PolicyLexicon.SIMPLE_TEXT_V1;
        }
        else
        {
            try {
                // Convert string of file contents to lexicon object
                lex = PolicyLexicon.valueOf(lexicon);
            }
            catch (Exception e)
            {
                log.error("Invalid lexicon name.");				
            }
        }		                

        // Determine lexicon type
        parseLexicon = lex;

        InputStream inStr = null;
        try
        {			
            // Convert policy file upload to byte stream
            inStr = new ByteArrayInputStream(content.getBytes());

            // Initialize parser engine
            final PolicyLexiconParser parser = PolicyLexiconParserFactory.getInstance(parseLexicon);
            
            // Attempt to parse the lexicon file for validity
            parser.parse(inStr);                        
            
            
        }                                
        catch (PolicyParseException e)
        {
            log.error("Syntax error in policy content " + " : " + e.getMessage());			
            jsonResponse = "{\"Status\":\"Policy content was not valid.\",\"Error\":\""+e.getMessage()+"\"}";
        }
        finally
        {
            IOUtils.closeQuietly(inStr);
        }		                                          
        
        if(jsonResponse.isEmpty()) {
            jsonResponse = "{\"Status\":\"Success\"}";
        }
        
        return jsonResponse;
    }
    
		    
    /*********************************
     *
     * New Policy Form Method
     *
     *********************************/
    @PreAuthorize("hasRole('ROLE_ADMIN')") 
    @RequestMapping(value="/newPolicyForm", method = RequestMethod.GET)
    public ModelAndView newPolicyForm (@RequestHeader(value="X-Requested-With", required=false) String requestedWith,                                                     
                                                    HttpSession session,
                                                    @ModelAttribute PolicyForm policyForm,
                                                    Model model)  { 		

        ModelAndView mav = new ModelAndView(); 

        if (log.isDebugEnabled()) 
        {
            log.debug("Enter policies");
        }    
                
        PolicyForm pform = new PolicyForm();
        pform.setId(0);
        model.addAttribute("policyForm", pform);                        
        model.addAttribute("lexiconNames", pform.getLexiconNames());
        
        mav.setViewName("newPolicyForm");
        
        return mav;
    }
    
    
    
	
    /**
     * Handle exceptions as gracefully as possible
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(IOException.class) 
    public String handleIOException(IOException ex, HttpServletRequest request) {
            return ClassUtils.getShortName(ex.getClass() + ":" + ex.getMessage());
    }
}
