package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
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
    public void testListRoot() throws Exception {
        System.out.println("listRoot");
        Iterator<FileDTO> iter = master.File().fetchall();

        int numFiles = 0;
        int numDirectories = 0;
        List<FileDTO> tmp = new ArrayList<>();
        while (iter.hasNext()) {
            FileDTO f = iter.next();
            tmp.add(f);
            if (f.getIsDirectory()) {
                numDirectories++;
            } else {
                numFiles++;
            }
        }
        assertEquals(4, tmp.size());
        assertEquals(3, numFiles);
        assertEquals(1, numDirectories);
    }

    @Test
    public void testListDir() throws Exception {
        System.out.println("listDir");
        Iterator<FileDTO> iter = master.File().fetchall();

        int numFiles = 0;
        int numDirectories = 0;
        while (iter.hasNext()) {
            FileDTO f = iter.next();
            System.err.println(f.getName());
            if (f.getIsDirectory()) {
                Iterator<FileDTO> iter2 = master.File().fetchall(f.getName());
                while (iter2.hasNext()) {
                    FileDTO ff = iter2.next();
                    System.err.println(" `-- " + ff.getName());
                    if (ff.getIsDirectory()) {
                        numDirectories++;
                    } else {
                        numFiles++;
                    }
                }
            }
        }
        assertEquals(1, numFiles);
        assertEquals(1, numDirectories);
    }

    @Test
    public void testCreateDir() {
        System.out.println("createDirGuest");
        FileDTO newDir = FileDTO.newBuilder()
                .setName(FileAccess.ROOT + "testDir")
                .setIsDirectory(true)
                .setSize(0)
                .build();
        try {
            long create = master.File().create(newDir);
        } catch (MGXServerException | MGXClientException ex) {
            if (ex.getMessage().trim().equals("Resource access denied.")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("guest user must not be able to create a new directory.");
    }

    @Test
    public void testCreateDirUser() {
        System.out.println("createDirUser");
        MGXDTOMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }
        FileDTO newDir = FileDTO.newBuilder()
                .setName(FileAccess.ROOT + FileAccess.separator + "testDir")
                .setIsDirectory(true)
                .setSize(0)
                .build();
        long create = 0;
        try {
            create = m.File().create(newDir);
        } catch (MGXServerException | MGXClientException ex) {
            fail(ex.getMessage());
        }

        //cleanup
        if (1 == create) {
            try {
                m.File().delete(newDir);
            } catch (MGXServerException | MGXClientException ex) {
                fail(ex.getMessage());
            }
        }
        assertEquals(1, create); // success
    }

    @Test
    public void testUploadFail() throws Exception {
        System.out.println("upload_Guest");

        File f = new File("/tmp/testUpload");
        FileWriter fw = new FileWriter(f);
        fw.write("Unit Test DATA");
        fw.close();

        FileUploader up = master.File().createUploader(f, FileAccess.ROOT + "Unittest.txt");
        boolean success = up.upload();
        f.delete();

        // test executed with guest user, so upload should fail
        assertFalse(success);
    }

    @Test
    public void testDeleteInvalid() {
        System.out.println("deleteInvalid");
        MGXDTOMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }

        FileDTO invalid = FileDTO.newBuilder()
                .setName(FileAccess.ROOT + FileAccess.separator + "DOES_NOT_EXIST")
                .setIsDirectory(true)
                .setSize(0)
                .build();
        UUID taskId;
        try {
            taskId = m.File().delete(invalid);
        } catch (MGXServerException ex) {
            if (ex.getMessage().trim().equals("Nonexisting path: DOES_NOT_EXIST")) {
                return;
            }
            fail(ex.getMessage());
        } catch (MGXClientException ex) {
            fail(ex.getMessage());
        }
        fail("this should not happen");
    }

    @Test
    public void testUploadSuccess() {
        System.out.println("upload_Success");
        MGXDTOMaster m = TestMaster.get2();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }

        File f = new File("/tmp/testUpload");
        try (FileWriter fw = new FileWriter(f)) {
            fw.write("Unit Test DATA");
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        String targetName = FileAccess.ROOT + "Uploadtest.txt";

        FileUploader up = null;
        try {
            up = m.File().createUploader(f, targetName);
        } catch (MGXClientException ex) {
            fail(ex.getMessage());
        }
        boolean success = up.upload();
        f.delete(); // delete local file

        if (success) {
            // cleanup
            FileDTO dto = FileDTO.newBuilder()
                    .setName(targetName)
                    .setIsDirectory(false)
                    .setSize(42)
                    .build();
            UUID taskId;
            try {
                taskId = m.File().delete(dto);
                TaskDTO task = m.Task().get(taskId);
                while ((task.getState() != TaskState.FINISHED) || (task.getState() != TaskState.FAILED)) {
                    System.err.println(" --> " + task.getState());
                    Thread.sleep(1000);
                    if ((task.getState() == TaskState.FINISHED) || (task.getState() == TaskState.FAILED)) {
                        break;
                    } else {
                        task = m.Task().get(taskId);
                    }
                }
            } catch (MGXServerException | MGXClientException | InterruptedException ex) {
                fail(ex.getMessage());
            }

        }
        if (!success) {
            fail(up.getErrorMessage());
        }

    }
}
