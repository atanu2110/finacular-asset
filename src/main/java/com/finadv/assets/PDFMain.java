package com.finadv.assets;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

public class PDFMain {

	public static void main(String[] args) {
		
			PDDocument doc;
			try {
				doc = PDDocument.load(
						new File("C:/Users/admin/Desktop/Atanu/projects/NSDL_Sanchit_AUSPG0308D.PDF"),
						"AUSPG0308D");
				String text = new PDFTextStripper().getText(doc);
				String lines[] = text.split("\\r?\\n");
				
				for (String line : lines) {
					if (line.toLowerCase().contains("statement for the period")) {
						System.out.println(Stream.of(line.split(" ")).reduce((first, last) -> last).get());
					}

					if (line.toLowerCase().contains("grand total")) {
						System.out.println(
								Double.parseDouble(line.toLowerCase().split("grand total")[1].trim().replace(",", "")));
						break;
					}

				}
				 System.out.println(text);
				
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
