package com.psd.rendering.openrender.oorender.module;


import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.psd.rendering.openrender.oorender.core.EParapherManager;
import org.apache.log4j.Logger;
import org.artofsolving.jodconverter.document.DocumentFamily;
import org.artofsolving.jodconverter.document.DocumentFormat;

import java.io.*;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


public class PDFConverterWrapper {
	
	private static Logger log = Logger.getLogger(PDFConverterWrapper.class);

	public static final String[] PDF_EXTENTION = { ".pdf" };
	public static final String   PDF_TEMP_CONVERTED_FILE = ".temp.pdf";

	public static final String[] TXT_EXTENTION = { ".txt", ".log" };
	public static final String[] XML_EXTENTION = { ".xml", ".dtd", ".xsl", ".xslt", ".xsl" };
	public static final String[] IMG_EXTENTION = { ".bmp", ".gif", ".jpg", ".png", ".tif", ".jpeg", ".tiff", ".ps" };
	public static final String[] OFF_EXTENTION = { ".doc", ".rtf", ".xls", ".ppt", ".pps" };
	public static final String[] OO_WRITER_EXTENTION = { ".odt", ".sxw", ".ott", ".stw" };
	public static final String[] OO_CALC_EXTENTION   = { ".ods", ".ots", ".sxc", ".stc" };
	public static final String[] OO_IMP_EXTENTION    = { ".odp", ".otp", ".sxi", ".sti" };
	public static final String[] OO_DRAW_EXTENTION   = { ".sxd", ".std", ".odg", ".otg"};
	public static final String[] WORD2K7_EXT   = { ".docx",".docm",".dotx",".dotm" };
	public static final String[] EXCEL2K7_EXT  = { ".xlsx",".xlsm",".xltx",".xltm",".xlsb",".xlam" };
	public static final String[] PPT2K7_EXT    = { ".pptx",".pptm",".ppsx",".ppsx",".potx",".potm",".ppam" };
	
	public PDFConverterWrapper() {
		super();
	}
	
	public String convert(String original_file) throws Exception {
		String generated_PDF = null;
		if (fileExtentionValidator(original_file, TXT_EXTENTION)
		 || fileExtentionValidator(original_file, XML_EXTENTION) )
			generated_PDF = TxtToPdf(original_file);
		else if ( fileExtentionValidator(original_file, IMG_EXTENTION) )
			generated_PDF = ImageToPdf(original_file);
		else if (  fileExtentionValidator(original_file, OFF_EXTENTION)
				|| fileExtentionValidator(original_file, OO_WRITER_EXTENTION)
				|| fileExtentionValidator(original_file, OO_CALC_EXTENTION)
				|| fileExtentionValidator(original_file, OO_IMP_EXTENTION)
				|| fileExtentionValidator(original_file, OO_DRAW_EXTENTION) )
			generated_PDF = AnyToPdf(original_file);
		else if ( fileExtentionValidator(original_file,  WORD2K7_EXT)
				|| fileExtentionValidator(original_file, EXCEL2K7_EXT)
				|| fileExtentionValidator(original_file, PPT2K7_EXT) )
			throw new Exception("Microsoft Office 2007 Documents convertion not implemented yet in Open Office.\r\n Sorry...");
		else throw new Exception("PDF convertion for " +original_file+ " failed. (not supported yet).");
		return generated_PDF;
	}
	
	private String ImageToPdf(String original_file) {
		
		Image img = null;
		Document doc = null;
		String converted_pdf = getoutputfilename(original_file);
		log.info("Generate PDF file " + converted_pdf + " from Image " + original_file);
		try {
			//Load Image
			img = Image.getInstance(original_file);
			img.setAlignment(Image.ALIGN_CENTER);
			
			//Create PDF Doc
			doc = new Document();
			if (img.getWidth()>img.getHeight()) {
				doc.setPageSize( PageSize.A4.rotate() );
				log.info("Setting PDF document in landscape.");
			} else {
				doc.setPageSize( PageSize.A4 );
			}
			PdfWriter docWriter = PdfWriter.getInstance(doc, new FileOutputStream(converted_pdf));
			doc.open();
			
			float maxWidth  = docWriter.getPageSize().getWidth();
			maxWidth = maxWidth - maxWidth/10;
			float maxHeigth = docWriter.getPageSize().getHeight();
			maxHeigth = maxHeigth - maxHeigth/10;
			
			//Scale image if it's larger than width and/or Heigth
			if ( (img.getScaledWidth() > maxWidth) || (img.getScaledHeight() > maxHeigth) )
				img.scaleToFit(maxWidth, maxHeigth);

			doc.add(img);
		} catch (BadElementException e) {
			log.error(""+e.getLocalizedMessage(),e);
		} catch (MalformedURLException e) {
			log.error(""+e.getLocalizedMessage(),e);
		} catch (IOException e) {
			log.error(""+e.getLocalizedMessage(),e);
		} catch (DocumentException e) {
			log.error(""+e.getLocalizedMessage(),e);
		}

	    doc.close();
	    return converted_pdf;
	}

	private static boolean fileExtentionValidator(String mfilename, String[] extentions) {
		for (int i = 0; i < extentions.length; i++) {
			if (mfilename.toLowerCase().endsWith(extentions[i]))
				return true;
		}
		return false;
	}
	public static boolean isConvertibleToPDF(File f) {

		String filename = f.getName();

		if ( fileExtentionValidator(filename,  PDF_EXTENTION) )
			return true;
		if ( fileExtentionValidator(filename,  TXT_EXTENTION)
          || fileExtentionValidator(filename,  XML_EXTENTION) )
			return true;
		if ( fileExtentionValidator(filename,  IMG_EXTENTION) )
			return true;
		if (  fileExtentionValidator(filename, OFF_EXTENTION)
		   || fileExtentionValidator(filename, OO_WRITER_EXTENTION)
		   || fileExtentionValidator(filename, OO_CALC_EXTENTION)
		   || fileExtentionValidator(filename, OO_IMP_EXTENTION)
		   || fileExtentionValidator(filename, OO_DRAW_EXTENTION) )
			return true;
		if ( fileExtentionValidator(filename,  WORD2K7_EXT)
		  || fileExtentionValidator(filename,  EXCEL2K7_EXT)
		  || fileExtentionValidator(filename,  PPT2K7_EXT) )
			return false;
		return false;
	}

	public String AnyToPdf(String mfileName) throws IOException, InterruptedException {
		
    	if (!EParapherManager.getInstance().getSettings().isOpenOfficeAutostart() && 
    		EParapherManager.getInstance().getSettings().useLocalOpenOffice() ) {
			OODaemonManager.getInstance().start();
    	}
    	
		File inputFile = new File(mfileName);
		File outputFile = new File(getoutputfilename(mfileName));
		int    ooDaemonPort = EParapherManager.getInstance().getSettings().getOpenOfficeServerPort();
		String ooDaemonHost = EParapherManager.getInstance().getSettings().getOpenOfficeServerName();
		log.debug("Create OpenOffice client connexion on " + ooDaemonHost + ":" + ooDaemonPort);
		// connect to an OpenOffice.org instance
		// :: OpenOfficeConnection connection = new SocketOpenOfficeConnection(ooDaemonHost,ooDaemonPort);
		//OpenOfficeConnection connection = new PipeOpenOfficeConnection( System.getProperty("user.name") + "_eParapher");
		// ::try {
			OODaemonManager.getInstance().addOOLibPath();
			// :: connection.connect();
		// ::} catch (ConnectException e) {
			// ::log.error("error while connecting to OpenOffice daemon : "+e.getLocalizedMessage(),e);

			//EParapherManager.getInstance().getUI().warnMessage(e.getLocalizedMessage());
	    	if ( EParapherManager.getInstance().getSettings().useLocalOpenOffice() && 
	    		!EParapherManager.getInstance().getSettings().isOpenOfficeAutostart() &&
	    		 OODaemonManager.getInstance().isOpenOfficeRunning() ) {
				OODaemonManager.getInstance().stop();
	    	}
			// ::return null;
		// ::}
		
		//TODO : Manage More OO PDF Export Options
		// see : http://www.artofsolving.com/node/18

		// create a PDF DocumentFormat (as normally configured in document-formats.xml)
		DocumentFormat customPdfFormat = new DocumentFormat("Portable Document Format", "application/pdf", "pdf");
		DocumentFamily df = null;
		if ( fileExtentionValidator(mfileName, OO_WRITER_EXTENTION) ) {
			df = DocumentFamily.TEXT;
			// ::customPdfFormat.setExportFilter(df, "writer_pdf_Export");
		}
		if ( fileExtentionValidator(mfileName, OO_CALC_EXTENTION) ) {
			df = DocumentFamily.SPREADSHEET;
			// :: customPdfFormat.setExportFilter(df, "calc_pdf_Export");
		}
		if ( fileExtentionValidator(mfileName, OO_IMP_EXTENTION ) ) {
			df = DocumentFamily.PRESENTATION;
			// :: customPdfFormat.setExportFilter(df, "impress_pdf_Export");
		}
		if ( fileExtentionValidator(mfileName, OO_DRAW_EXTENTION) ) {
			df = DocumentFamily.DRAWING;
			// ::customPdfFormat.setExportFilter(df, "draw_pdf_Export");
		}

		// now set our custom options
		Map pdfOptions = new HashMap();
		//pdfOptions.put("EncryptFile", Boolean.TRUE);
		//pdfOptions.put("DocumentOpenPassword", "mysecretpassword");
		//0 for PDF1.4 and 1 for PDF/A1
		pdfOptions.put("SelectPdfVersion", 1);  
		//:: customPdfFormat.setExportOption(df, "FilterData", pdfOptions);

		// convert
		log.info("Generate PDF file " + outputFile + " from document " + inputFile);
		// ::DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		// ::if (df!=null)
			// :: converter.convert(inputFile, outputFile, customPdfFormat);
		// ::else converter.convert(inputFile, outputFile);
		 
		// close the connection
		log.debug("Disconnect OpenOffice client");
		// ::connection.disconnect();
		
		try {
			//TODO : stop only if no other job need the PDF converter
	    	if ( EParapherManager.getInstance().getSettings().useLocalOpenOffice() && 
	        	!EParapherManager.getInstance().getSettings().isOpenOfficeAutostart() )
	    		OODaemonManager.getInstance().stop();
			return outputFile.getCanonicalPath();
		} catch (IOException ioe) {
			String msg = "error in AnyToPdf while stopping OO : " + ioe.getMessage();
			//EParapherManager.getInstance().getUI().errorMessage(msg,ioe);
			log.error(msg, ioe);
		}
		
		return null;
	}
	private String getoutputfilename(String mfileName) {
		String generatedpdffilename = "";
		int indexextention = mfileName.lastIndexOf(".");
		generatedpdffilename = mfileName.substring(0, indexextention) + PDF_TEMP_CONVERTED_FILE;
		return generatedpdffilename;
	}

	public String TxtToPdf(String mfileName) {
	    
		Document doc = new Document();
		PdfWriter docWriter = null;
		String converted_pdf = getoutputfilename(mfileName);
		log.info("Generate PDF file " + converted_pdf + " from text document " + mfileName);
		
		//Init PDF Converter
		try {
			docWriter = PdfWriter.getInstance(doc, new FileOutputStream(converted_pdf));
			doc.open();
		} catch (DocumentException e) {
			//EParapherManager.getInstance().getUI().errorMessage("error in TxtToPdf : " + e.getMessage());
			log.error(""+e.getLocalizedMessage(),e);
		} catch (FileNotFoundException e) {
			//EParapherManager.getInstance().getUI().errorMessage("error in TxtToPdf : " + e.getMessage());
			log.error(""+e.getLocalizedMessage(),e);
		}

	    StringBuffer contents = new StringBuffer();
	    BufferedReader input = null;
	    String line = null;
		try {
			input = new BufferedReader( new FileReader(mfileName) );
		    while (( line = input.readLine()) != null)
		    	doc.add(new Paragraph(line + System.getProperty("line.separator")));
		} catch (DocumentException e) {

			//EParapherManager.getInstance().getUI().errorMessage("Error while converting flat text file to PDF : " + e.getMessage(),e);
			log.error(""+e.getLocalizedMessage(),e);
		} catch (IOException e) {
			//EParapherManager.getInstance().getUI().errorMessage("Error while converting flat text file to PDF : " + e.getMessage(),e);
			log.error(""+e.getLocalizedMessage(),e);
		}
	    finally {
		      try {
		        if (input!= null) {
		          //flush and close both "input" and its underlying FileReader
		          input.close();
		        }
		      }
		      catch (IOException e) {
		    	//EParapherManager.getInstance().getUI().errorMessage("Error while closing flat text file : " + e.getMessage(),e);
		    	log.error(""+e.getLocalizedMessage(),e);
		      }
		}
	    doc.close();
	    return converted_pdf;
	}

	
	/**
	 * The following method should return a multi-page tiff 
	 * @param bai
	 * @param imageType
	 * @return
	public static BufferedImage[] PDFToImage(String file, String imageType) {

		BufferedImage[] bi = new BufferedImage[0];
		try {
			PdfDecoder decoder = new PdfDecoder();
			decoder.openPdfFile(file);

			 bi = new BufferedImage[decoder.getPageCount()];
			for (int i = 0; i < decoder.getPageCount(); i++) {
				bi[i] = decoder.getPageAsImage(i);
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return bi;
	}
	*/

}
