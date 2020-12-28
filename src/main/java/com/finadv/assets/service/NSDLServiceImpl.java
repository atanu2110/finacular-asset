package com.finadv.assets.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.finadv.assets.entities.NSDLEquity;
import com.finadv.assets.entities.NSDLReponse;

/**
 * @author ATANU
 *
 */
@Service
public class NSDLServiceImpl implements NSDLService {

	@Override
	public NSDLReponse extractFromNSDL(MultipartFile nsdlFile, String password, Long userId) {
		String temDirectory = "java.io.tmpdir";
		File tempNSDLFile = new File(System.getProperty(temDirectory) + "/" + nsdlFile.getOriginalFilename());
		PDDocument doc;
		
		NSDLReponse nsdlReponse = new NSDLReponse();
		try {
			nsdlFile.transferTo(tempNSDLFile);

			doc = PDDocument.load(tempNSDLFile, password);
			String text = new PDFTextStripper().getText(doc);
			String lines[] = text.split("\\r?\\n");
			List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
			int linecounter = 0;
			for (String line : lines) {
				if (line.toLowerCase().contains("statement for the period")) {
					nsdlReponse.setPeriod((Stream.of(line.split(" ")).reduce((first, last) -> last).get()));
				}

				if (line.toLowerCase().contains("grand total")) {
					nsdlReponse.setAmount(Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
				}
				if (line.contains("CAS ID")) {
					nsdlReponse.setHolderName(lines[linecounter + 1]);
				}
				// Get all Equity shares
				String[] lineSplit = new String[0];
				if (line.trim().matches("^(INE)[a-zA-Z0-9]{9,}$")) {
					NSDLEquity nsdlEquity = new NSDLEquity();
					nsdlEquity.setIsin(line.trim());
					nsdlEquity.setStockSymbol(lines[linecounter + 1].trim());
					for (int i = 1; i <= 5; i++) {
						if (lines[linecounter + i + 1].trim().contains(".")
								|| lines[linecounter + i + 1].trim().contains(",")) {
							lineSplit = lines[linecounter + i + 1].split(" ");
							break;
						}

					}

					nsdlEquity.setShares(Long.parseLong(lineSplit[lineSplit.length - 3].replaceAll(",", "").trim()));
					nsdlEquities.add(nsdlEquity);
				}

				linecounter++;
				// System.out.println(line);
			}
			
			nsdlReponse.setNsdlEquities(nsdlEquities);
			
			doc.close();
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		} 

		return nsdlReponse;
	}

}
