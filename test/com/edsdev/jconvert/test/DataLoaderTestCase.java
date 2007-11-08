package com.edsdev.jconvert.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import com.edsdev.jconvert.domain.ConversionType;
import com.edsdev.jconvert.persistence.DataLoader;
import com.edsdev.jconvert.presentation.ConversionTypeData;
import com.edsdev.jconvert.util.Logger;

/**
 * @author Ed Sarrazin
 */
public class DataLoaderTestCase extends TestCase {

    private final static Logger log = Logger.getInstance(DataLoaderTestCase.class);

    private final static String CONVERT_DAT = "convert.dat";

    private final static String CONVERT_DAT_BACKUP = "convert_testBackup.dat";

    private final static String CONVERT_CUSTOM_DAT = "convert_custom.dat";

    private final static String CONVERT_CUSTOM_DAT_BACKUP = "convert_custom_testBackup.dat";

    private final static String RESOURCE_DIR = "../resource/";

    private final static String TEST_DIR = "../test/";

    protected void setUp() throws Exception {
        super.setUp();

        //backup convert.dat and custom_convert.dat files in the resource folder
        copyFile(new File(RESOURCE_DIR + CONVERT_DAT), new File(RESOURCE_DIR + CONVERT_DAT_BACKUP));
        copyFile(new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT), new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT_BACKUP));

        //copy convert.dat and custom_convert.dat files to the resource folder
        copyFile(new File(TEST_DIR + CONVERT_DAT), new File(RESOURCE_DIR + CONVERT_DAT));
        copyFile(new File(TEST_DIR + CONVERT_CUSTOM_DAT), new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT));
    }

    private void copyFile(File origination, File destination) {
        try {
            InputStream in = new FileInputStream(origination);
            OutputStream out = new FileOutputStream(destination);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            log.error("Failed to copy files ", e);
        }

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        //delete the test files convert.dat and custom_convert.dat files from the resource folder
        new File(RESOURCE_DIR + CONVERT_DAT).delete();
        new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT).delete();
        //restore the backup files from the resource folder
        copyFile(new File(RESOURCE_DIR + CONVERT_DAT_BACKUP), new File(RESOURCE_DIR + CONVERT_DAT));
        copyFile(new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT_BACKUP), new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT));
        //delete the backups made
        new File(RESOURCE_DIR + CONVERT_DAT_BACKUP).delete();
        new File(RESOURCE_DIR + CONVERT_CUSTOM_DAT_BACKUP).delete();

    }

    public void testMergingOfData() throws Exception {
        List domainData = new DataLoader().loadData();

        Iterator iter = domainData.iterator();
        while (iter.hasNext()) {
            ConversionType type = (ConversionType) iter.next();
            ConversionTypeData ctd = new ConversionTypeData(type);

            if (type.getTypeName().equals("Time")) {
                checkTime(ctd);
            }

            if (type.getTypeName().equals("TestType")) {
                checkTestType(ctd);
            }

            if (type.getTypeName().equals("Punishment")) {
                checkPunishment(ctd);
            }
        }

    }

    private void checkPunishment(ConversionTypeData ctd) {
        //verify that TestType has 25 entries
        assertTrue("Punishment should have 25 entries. (" + ctd.getType().getConversions().size() + ")",
            ctd.getType().getConversions().size() == 25);
    }

    private void checkTestType(ConversionTypeData ctd) {
        //verify that TestType has 16 entries
        assertTrue("TestType should have 16 entries. (" + ctd.getType().getConversions().size() + ")",
            ctd.getType().getConversions().size() == 16);
    }

    private void checkTime(ConversionTypeData ctd) {
        //verify that time has 256 entries
        log.debug("Size of time " + ctd.getType().getConversions().size());
        //there are 16 entries in the base data and 1 in the custom = 17*17=289
        //but the build does not copy the custom out (dont want it in the jar), so it is
        //only 16 - 16 * 16 = 256 - running this test through eclipse can
        //result in a differnt number if you have custom conversions for time
        assertTrue("Time should have 256 entries. (" + ctd.getType().getConversions().size() + ")",
            ctd.getType().getConversions().size() == 256);
    }
}
