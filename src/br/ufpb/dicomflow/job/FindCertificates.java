/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package br.ufpb.dicomflow.job;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.ControllerProperty;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.service.CertificateServiceIF;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class FindCertificates {
	
	private static final String  DAILY_STRATEGY = "1";
	private static final String  INTERVAL_STRATEGY = "2";
	private static final String  CURRENT_STUDY_STRATEGY = "3";
	
	private String initialDate;
	private String finalDate;
	private String modalities;
	private String strategy;

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("STORE CERTIFICATES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		CertificateServiceIF certificateService =  ServiceLocator.singleton().getCertificateService();
		
		//load the domain certificate. It can't load throws exception and return.
		File domainCertificate;
		try {
			domainCertificate = certificateService.getCertificate();
		} catch (ServiceException e2) {
			e2.printStackTrace();
			return;
		}
		Access domain = CredentialUtil.getDomain();
		
		List<Access> accesses = new ArrayList<Access>();
		
		ControllerProperty currentDateProperty = (ControllerProperty) persistentService.select("property", ControllerProperty.CERTIFICATE_MAIL_CURRENT_DATE_PROPERTY, ControllerProperty.class);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			
			
			
			//verifies the retrieve straegy
			if(strategy.equals(DAILY_STRATEGY)){
				
				Date currentDate = Calendar.getInstance().getTime();
				
				accesses = messageService.getCertificates(currentDate, null, null);
				
			}
			if(strategy.equals(INTERVAL_STRATEGY)){
				
				try{
					
					if(initialDate == null || finalDate == null || initialDate.equals("") || finalDate.equals("")){
						throw new Exception("Formato inv�lido para initialDate ou finalDate. Formato: dd/MM/yyyy");
					}
					
					Date startDate = formatter.parse(initialDate);
					Date finishDate = formatter.parse(finalDate);
					if(currentDateProperty != null){
						Date currentDate = formatter.parse(currentDateProperty.getValue());
						
						if(currentDate.equals(startDate) || currentDate.after(startDate) ){
							startDate = currentDate;
						}
						if(currentDate.after(finishDate)){
							throw new Exception("CurrentDateProperty fora do intervalo.");
						}
					}
					accesses = messageService.getCertificates(startDate, finishDate, null);
					
				} catch (Exception e) {
					Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
					e.printStackTrace();
					return;
				}	
				
				
			}
			if(strategy.equals(CURRENT_STUDY_STRATEGY)){
				
					
				try{
					
					if(initialDate == null || initialDate.equals("")){
						throw new Exception("Formato inv�lido para initialDate. Formato: dd/MM/yyyy");
					}
					
					Date startDate = formatter.parse(initialDate);
					
					if(currentDateProperty != null){
						Date currentDate = formatter.parse(currentDateProperty.getValue());
						
						if(currentDate.equals(startDate) || currentDate.after(startDate) ){
							startDate = currentDate;
						}
						
					}
					
					accesses = messageService.getCertificates(startDate, null, null);
					
				} catch (Exception e) {
					Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
					e.printStackTrace();
					return;
				}
				
			}
			
			
		} catch (ServiceException e1) {
			e1.printStackTrace();
		}
		
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = (Access) it.next();
			Access bdAccess = (Access) persistentService.selectByParams(new Object[]{"host","port","mail"}, new Object[]{access.getHost(), access.getPort(), access.getMail()}, Access.class);
			
				byte[] accessCertificate = access.getCertificate();
				try {
					
					if(certificateService.storeCertificate(accessCertificate, access.getHost())){
						if(bdAccess == null){
							
							access.setCertificateStatus(Access.CREDENTIAL_PENDING);
							access.save();
							Credential credential = CredentialUtil.createCredential(access);
							credential.save();
							messageService.sendCertificateResult(access.getMail(), domainCertificate, domain, MessageServiceIF.CERTIFICATE_RESULT_CREATED, credential);
							
						}else{
							bdAccess.setCertificateStatus(Access.CREDENTIAL_PENDING);
							bdAccess.save();
							Credential credential = CredentialUtil.getCredential(bdAccess, domain);
							messageService.sendCertificateResult(access.getMail(),domainCertificate, domain, MessageServiceIF.CERTIFICATE_RESULT_UPDATED, credential);
						}
					}else{
//						messageService.sendCertificateError(access.getMail(), domain, MessageServiceIF.CERTIFICATE_RESULT_ERROR);
					}
					
				} catch (ServiceException e) {
					e.printStackTrace();
				}
			
			
			
		}
		
		//update currentDate Property
		try {
			if(currentDateProperty == null){
				currentDateProperty = new ControllerProperty();
				currentDateProperty.setProperty(ControllerProperty.CERTIFICATE_MAIL_CURRENT_DATE_PROPERTY);
			}
			Date currentDate = Calendar.getInstance().getTime();
			currentDateProperty.setValue(formatter.format(currentDate));
			currentDateProperty.save();
			
		} catch (ServiceException e) {
			Util.getLogger(this).error("Could not possible save currentDateProperty", e);
			e.printStackTrace();
		}
		
		Util.getLogger(this).debug("DONE!!");	
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:STORE_CERTIFICATE - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}
	
	public String getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	public String getModalities() {
		return modalities;
	}

	public void setModalities(String modalities) {
		this.modalities = modalities;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	

}