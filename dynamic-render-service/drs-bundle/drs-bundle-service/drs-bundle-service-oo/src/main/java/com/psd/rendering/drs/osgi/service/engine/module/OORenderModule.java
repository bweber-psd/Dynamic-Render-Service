package com.psd.rendering.openrender.oorender.module;

import com.psd.rendering.openrender.api.module.RenderModule;
import org.apache.commons.io.FilenameUtils;
import org.artofsolving.jodconverter.OfficeDocumentConverter;
import org.artofsolving.jodconverter.document.DocumentFormat;
import org.artofsolving.jodconverter.document.DocumentFormatRegistry;
import org.artofsolving.jodconverter.office.DefaultOfficeManagerConfiguration;
import org.artofsolving.jodconverter.office.OfficeManager;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: Burkhard
 * Date: 16.11.14
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
public class OORenderModule implements RenderModule {
    @Override
    public InputStream render() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void initialize() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void terminate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    public void runAllPossibleConversions() throws IOException {
        OfficeManager officeManager=new DefaultOfficeManagerConfiguration().buildOfficeManager();
        OfficeDocumentConverter converter=new OfficeDocumentConverter(officeManager);
        DocumentFormatRegistry formatRegistry=converter.getFormatRegistry();
        officeManager.start();
        try {
            File dir=new File("src/test/resources/documents");
            File[] files=dir.listFiles(new FilenameFilter(){
                public boolean accept(      File dir,      String name){
                    return !name.startsWith(".");
                }
            }
            );
            for (    File inputFile : files) {
                String inputExtension=FilenameUtils.getExtension(inputFile.getName());
                DocumentFormat inputFormat=formatRegistry.getFormatByExtension(inputExtension);
                assertNotNull(inputFormat,"unknown input format: " + inputExtension);
                Set<DocumentFormat> outputFormats=formatRegistry.getOutputFormats(inputFormat.getInputFamily());
                for (      DocumentFormat outputFormat : outputFormats) {
                    File outputFile=File.createTempFile("test","." + outputFormat.getExtension());
                    outputFile.deleteOnExit();
                    System.out.printf("-- converting %s to %s... ",inputFormat.getExtension(),outputFormat.getExtension());
                    converter.convert(inputFile,outputFile,outputFormat);
                    System.out.printf("done.\n");
                    assertTrue(outputFile.isFile() && outputFile.length() > 0);
                }
            }
        }
        finally {
            officeManager.stop();
        }
    }
}
