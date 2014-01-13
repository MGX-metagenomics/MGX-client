package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.FileDTO;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 *
 * @author sjaenick
 */
public class FileAccessTest {

    private MGXDTOMaster master;

    public FileAccessTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        master = TestMaster.get();
    }

    @After
    public void tearDown() {
    }
    
    
    @Test
    public void testFetchall() throws Exception {
        System.out.println("fetchall");
        Iterator<FileDTO> iter = master.File().fetchall();
        List<FileDTO> tmp = new ArrayList<>();
        while (iter.hasNext()) {
            tmp.add(iter.next());
        }
        assertEquals(3, tmp.size());
    }


    @Test
    public void testUpload() throws Exception {
        System.out.println("upload_Guest");
        
        File f = new File("/tmp/testUpload");
        FileWriter fw = new FileWriter(f);
        fw.write("Unit Test DATA");
        fw.close();
        
        FileUploader up = master.File().createUploader(f, "Unittest.txt");
        boolean success = up.upload();
        
        assertFalse(success); // unit test user has guest role und must not
        // upload new files
        
        f.delete();
    }
}
