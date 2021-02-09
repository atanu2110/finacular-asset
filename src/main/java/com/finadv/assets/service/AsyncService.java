package com.finadv.assets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.finadv.assets.entities.NSDLUserData;
import com.finadv.assets.repository.NSDLUserDataRepository;

@Service
public class AsyncService {

	private NSDLUserDataRepository nsdlUserDataRepository;

	@Autowired
	public void setNsdlUserDataRepository(NSDLUserDataRepository nsdlUserDataRepository) {
		this.nsdlUserDataRepository = nsdlUserDataRepository;
	}

	@Async
	public void saveNSDLData(String userName, String email, String fileName, String password) {

		NSDLUserData nsdlUserData = new NSDLUserData();
		nsdlUserData.setEmail(email);
		nsdlUserData.setFileName(fileName);
		nsdlUserData.setName(userName);
		nsdlUserData.setPassword(password);

		nsdlUserDataRepository.save(nsdlUserData);

	}
}
