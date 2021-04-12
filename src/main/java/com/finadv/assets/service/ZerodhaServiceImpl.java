package com.finadv.assets.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.AssetInstrument;
import com.finadv.assets.entities.AssetType;
import com.finadv.assets.entities.Institution;
import com.finadv.assets.entities.NSDLEquity;
import com.finadv.assets.entities.NSDLMutualFund;
import com.finadv.assets.entities.UserAsset;
import com.finadv.assets.entities.UserAssets;
import com.finadv.assets.entities.ZerodhaResponse;

@Service
public class ZerodhaServiceImpl implements ZerodhaService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ZerodhaServiceImpl.class);
	
	private AssetService assetService;

	@Autowired
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}
	
	@Override
	public ZerodhaResponse extractFromZerodhaExcel(MultipartFile excelFile, Long userId, String source) {
		ZerodhaResponse zerodhaResponse=new ZerodhaResponse();
		
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile.getInputStream());
			
			List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
			List<NSDLMutualFund> mutualFunds = new ArrayList<NSDLMutualFund>();
			List<UserAssets> userAssetList = new ArrayList<UserAssets>();
			
		    XSSFSheet worksheet = workbook.getSheetAt(0);
		    
		    // Get Client Id
		    XSSFRow row = worksheet.getRow(6);
		    String clientId = row.getCell(2).toString();
		    zerodhaResponse.setClientId(clientId);;
		    
		    //Get period
		    row = worksheet.getRow(10);
		    zerodhaResponse.setPeriod((Stream.of(row.getCell(1).toString().split(" ")).reduce((first, last) -> last).get()));;
		    
		    //Get all Equities
		    for(int i=15;i<=worksheet.getLastRowNum() ;i++) {
		    	NSDLEquity nsdlEquity = new NSDLEquity();
		            
		        XSSFRow tempRow = worksheet.getRow(i);
		        
		        nsdlEquity.setIsin(tempRow.getCell(2).toString());
		        nsdlEquity.setStockSymbol(tempRow.getCell(1).toString());
		        nsdlEquity.setShares((long)Double.parseDouble(tempRow.getCell(4).toString()));
		        nsdlEquity.setCurrentValue(Double.parseDouble(tempRow.getCell(10).toString()) * nsdlEquity.getShares());
		        
		        nsdlEquities.add(nsdlEquity);
		        createAssetForEquities(nsdlEquity, userId, userAssetList, zerodhaResponse.getClientId());
		    }
		    
		  //Mutual Fund Data
		    worksheet = workbook.getSheetAt(1);
		    
		    for(int i=15;i<=worksheet.getLastRowNum() ;i++) {
		    	NSDLMutualFund nsdlMutualFund = new NSDLMutualFund();
		            
		        XSSFRow tempRow = worksheet.getRow(i);
		        
		        String schemeSymbol = tempRow.getCell(1).toString();
		        nsdlMutualFund.setIsin(tempRow.getCell(2).toString());
		        nsdlMutualFund.setUnits(Float.parseFloat(tempRow.getCell(4).toString()));
		        nsdlMutualFund.setCurrentValue(Double.parseDouble(tempRow.getCell(9).toString()) * nsdlMutualFund.getUnits());
		        nsdlMutualFund.setIsinDescription(schemeSymbol);
		        
		        mutualFunds.add(nsdlMutualFund);
		        createAssetForMutualFund(nsdlMutualFund, schemeSymbol, userId, userAssetList, zerodhaResponse.getClientId());
		    }
		    
		    zerodhaResponse.setNsdlEquities(nsdlEquities);
		    zerodhaResponse.setNsdlMutualFunds(mutualFunds);
		    
		    // Saving the User Data
		    String nick = clientId;
			UserAsset userAsset = new UserAsset();
			userAsset.setUserId(userId);
			userAssetList.forEach(ua -> ua.setNickName(nick));
			userAsset.setAssets(userAssetList);
			assetService.saveUserAssetsByUserId(userAsset, "zerodha");
			
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	    return zerodhaResponse;
	}
	
	private void createAssetForEquities(NSDLEquity nsdlEquity, Long userId, List<UserAssets> userAssetList,
			String clientId) {
		if (nsdlEquity.getCurrentValue() != 0) {
			UserAssets userAssets = new UserAssets();
			userAssets.setAmount(nsdlEquity.getCurrentValue());
			userAssets.setHolderName(clientId);
			userAssets.setCreatedAt(LocalDateTime.now());
			Institution institution = new Institution();
			institution.setId(1);
			userAssets.setAssetProvider(institution);
			AssetType assetType = new AssetType();
			assetType.setId(4);
			assetType.setTypeName("equity");
			userAssets.setAssetType(assetType);
			AssetInstrument assetInstrument = new AssetInstrument();
			assetInstrument.setId(7);
			userAssets.setAssetInstrument(assetInstrument);
			userAssets.setExpectedReturn(10);
			userAssets.setEquityDebtName(nsdlEquity.getStockSymbol());
			userAssets.setCode(nsdlEquity.getIsin());
			userAssets.setUnits((int) nsdlEquity.getShares());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}
	}
	
	private void createAssetForMutualFund(NSDLMutualFund nsdlMutualFund, String schemeSymbol, Long userId, List<UserAssets> userAssetList,
			String clientId) {
		if (nsdlMutualFund.getCurrentValue() != 0) {
			UserAssets userAssets = new UserAssets();
			userAssets.setAmount(nsdlMutualFund.getCurrentValue());
			userAssets.setHolderName(clientId);
			userAssets.setCreatedAt(LocalDateTime.now());
			Institution institution = new Institution();
			institution.setId(1);
			userAssets.setAssetProvider(institution);
			AssetType assetType = new AssetType();
			assetType.setId(4);
			assetType.setTypeName("equity");
			userAssets.setAssetType(assetType);
			AssetInstrument assetInstrument = new AssetInstrument();
			assetInstrument.setId(8);
			userAssets.setAssetInstrument(assetInstrument);
			userAssets.setExpectedReturn(12);
			userAssets.setEquityDebtName(schemeSymbol);
			userAssets.setCode(nsdlMutualFund.getIsin());
			userAssets.setUnits((int) nsdlMutualFund.getUnits());

			userAssets.setUserId(userId);
			userAssetList.add(userAssets);
		}
	}
}
