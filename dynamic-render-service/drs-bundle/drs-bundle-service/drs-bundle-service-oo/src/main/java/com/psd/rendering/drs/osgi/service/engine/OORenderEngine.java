package com.psd.rendering.drs.osgi.service.engine;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.OfficeManager;

public class OORenderEngine extends BaseRenderEngine {
	OfficeManager officeManager;
	
	public OORenderEngine(OfficeManager officeManager) {
		this.officeManager = officeManager;
	}

	@Override
	public Set<SourceFormat> getSupportedSourceFormats() {
		OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry = converter.getFormatRegistry();
		return null;
	}

	@Override
	public Set<TargetFormat> getSupportedTargetFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Rendition> safeRender(InputStreamDescriptor input, SourceFormat sourceFormat, Options options)
			throws TimeoutException, RenderingException {
		 File outputFile = File.createTempFile(input."test", "." + outputFormat.getExtension());
         outputFile.deleteOnExit();
         System.out.printf("-- converting %s to %s... ", inputFormat.getExtension(), outputFormat.getExtension());
         OfficeDocumentConverter converter = new OfficeDocumentConverter(officeManager);
         converter.convert(inputFile.toFile(), outputFile, outputFormat);
         System.out.printf("done.\n");
         
         DocumentFormat documentFormat = new DocumentFormat();
         
		return null;
	}

}
