package com.finadv.assets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.finadv.assets.entities.NSDLEquity;

public class PDFMain {

	public static void main(String[] args) {
		
			PDDocument doc;
			try {
				doc = PDDocument.load(
						new File("D:/Atanu/projects/NSDL_Sanchit_AUSPG0308D.PDF"),
						"AUSPG0308D");
				String text = new PDFTextStripper().getText(doc);
				String lines[] = text.split("\\r?\\n");
				 List<NSDLEquity> nsdlEquities = new ArrayList<NSDLEquity>();
				int linecounter = 0;
				for (String line : lines) {
					if (line.toLowerCase().contains("statement for the period")) {
						System.out.println(Stream.of(line.split(" ")).reduce((first, last) -> last).get());
					}

					if (line.toLowerCase().contains("grand total")) {
						System.out.println(
								Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
					}
					if(line.contains("CAS ID")) {
						System.out.println(lines[linecounter + 1]);
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

						nsdlEquity
								.setShares(Long.parseLong(lineSplit[lineSplit.length - 3].replaceAll(",", "").trim()));

						nsdlEquities.add(nsdlEquity);
					}

					linecounter++;

					// System.out.println(line);
				}

				/*
				 * ObjectExtractor oe = new ObjectExtractor(doc); SpreadsheetExtractionAlgorithm
				 * sea = new SpreadsheetExtractionAlgorithm();
				 * 
				 * Page page = oe.extract(3); List<Table> table = sea.extract(page);
				 * 
				 * for(Table tables: table) { List<List<RectangularTextContainer>> rows =
				 * tables.getRows();
				 * 
				 * for(int i=0; i<rows.size(); i++) {
				 * 
				 * List<RectangularTextContainer> cells = rows.get(i);
				 * 
				 * for(int j=0; j<cells.size(); j++) {
				 * System.out.print(cells.get(j).getText()+"|"); }
				 * 
				 * System.out.println(); } }
				 */
				
				 
				 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		

	}

}
