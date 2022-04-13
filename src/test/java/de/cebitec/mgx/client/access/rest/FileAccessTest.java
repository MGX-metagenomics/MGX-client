package de.cebitec.mgx.client.access.rest;

import de.cebitec.mgx.client.MGXDTOMaster;
import de.cebitec.mgx.client.datatransfer.FileDownloader;
import de.cebitec.mgx.client.datatransfer.FileUploader;
import de.cebitec.mgx.client.datatransfer.PluginDumpDownloader;
import de.cebitec.mgx.client.datatransfer.TransferBase;
import de.cebitec.mgx.client.exception.MGXDTOException;
import de.cebitec.mgx.client.exception.MGXServerException;
import de.cebitec.mgx.client.mgxtestclient.TestMaster;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.TaskDTO;
import de.cebitec.mgx.dto.dto.TaskDTO.TaskState;
import de.cebitec.mgx.testutils.PropCounter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author sjaenick
 */
//@RunWith(PaxExam.class)
public class FileAccessTest {

//    @Configuration
//    public static Option[] configuration() {
//        return options(
//                junitBundles(),
//                MGXOptions.clientBundles(),
//                systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("WARN"),
//                bundle("reference:file:target/classes")
//        );
//    }

    @Test
    public synchronized void testListRoot() throws Exception {
        System.out.println("listRoot");
        MGXDTOMaster master = TestMaster.getRO();
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
        assertEquals(3, numFiles);
        assertEquals(1, numDirectories);
        assertEquals(4, tmp.size());
    }

    @Test
    public synchronized void testListDir() throws Exception {
        System.out.println("listDir");
        MGXDTOMaster master = TestMaster.getRO();
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
    public synchronized void testCreateDir() {
        System.out.println("createDirGuest");
        MGXDTOMaster master = TestMaster.getRO();
        FileDTO newDir = FileDTO.newBuilder()
                .setName(FileAccess.ROOT + "testDir")
                .setIsDirectory(true)
                .setSize(0)
                .build();
        try {
            long create = master.File().create(newDir);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().trim().equals("Resource access denied.")) {
                return;
            }
            fail(ex.getMessage());
        }
        fail("guest user must not be able to create a new directory.");
    }

    @Test
    public synchronized void testCreateDirUser() {
        System.out.println("createDirUser");
        MGXDTOMaster m = TestMaster.getRW();
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
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        //cleanup
        if (1 == create) {
            try {
                m.File().delete(newDir);
            } catch (MGXDTOException ex) {
                fail(ex.getMessage());
            }
        }
        assertEquals(1, create); // success
    }

    @Test
    public synchronized void testCreateExistingDir() {
        System.out.println("createExistingDir");
        MGXDTOMaster m = TestMaster.getRW();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }
        FileDTO newDir = FileDTO.newBuilder()
                .setName(FileAccess.ROOT + FileAccess.separator + "dir1")
                .setIsDirectory(true)
                .setSize(0)
                .build();
        long create = 0;
        try {
            create = m.File().create(newDir);
        } catch (MGXDTOException ex) {
            if (ex.getMessage().trim().endsWith("dir1 already exists.")) {
                return;
            }
            fail(ex.getMessage());
        }

        //cleanup
        if (1 == create) {
            try {
                m.File().delete(newDir);
            } catch (MGXDTOException ex) {
                fail(ex.getMessage());
            }
        }
    }

    @Test
    public synchronized void testUploadFail() throws Exception {
        System.out.println("upload_Guest");

        File f = File.createTempFile("uploadTest", "xx");
        FileWriter fw = new FileWriter(f);
        fw.write("Unit Test DATA");
        fw.close();

        MGXDTOMaster master = TestMaster.getRO();

        FileUploader up = master.File().createUploader(f, FileAccess.ROOT + "Unittest.txt");

        PropCounter pc = new PropCounter();
        up.addPropertyChangeListener(pc);

        boolean success = up.upload();
        f.delete();

        // test executed with guest user, so upload should fail
        assertFalse(success);

        assertEquals(TransferBase.TRANSFER_FAILED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public synchronized void testDeleteInvalid() {
        System.out.println("deleteInvalid");
        MGXDTOMaster m = TestMaster.getRW();
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
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        fail("this should not happen");
    }

    @Test
    public synchronized void testDeleteDir() {
        System.out.println("testDeleteDir");
        MGXDTOMaster m = TestMaster.getRW();

        FileDTO newDir = FileDTO.newBuilder()
                .setName(FileAccess.ROOT + FileAccess.separator + "delMe")
                .setIsDirectory(true)
                .setSize(0)
                .build();
        long create = 0;
        try {
            create = m.File().create(newDir);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertEquals(1, create); // success

        //cleanup
        if (1 == create) {
            try {
                UUID uuid = m.File().delete(newDir);
                TaskDTO task = m.Task().get(uuid);
                
                int numRefreshes = 10;
                System.err.println(" state is "+ task.getState());
                while (numRefreshes > 0 && task.getState() != TaskState.FINISHED) {
                    System.err.println(" state is "+ task.getState());
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FileAccessTest.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    task = m.Task().get(uuid);
                    numRefreshes--;
                }
                
                assertEquals("directory was not deleted within 200ms", TaskState.FINISHED, task.getState());
            } catch (MGXDTOException ex) {
                fail(ex.getMessage());
            }
        }

    }

    @Test
    public synchronized void testUploadSuccess() throws IOException {
        System.out.println("upload_Success");
        MGXDTOMaster m = TestMaster.getRW();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }

        File f = File.createTempFile("uploadTest", "xx");
        try (FileWriter fw = new FileWriter(f)) {
            fw.write("Unit Test DATA");
            for (int i = 0; i < 10000; i++) {
                fw.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        String targetName = FileAccess.ROOT + "Uploadtest.txt";

        FileUploader up = null;
        try {
            up = m.File().createUploader(f, targetName);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(up);

        PropCounter pc = new PropCounter();
        up.addPropertyChangeListener(pc);

        boolean success = up.upload();

        up.removePropertyChangeListener(pc);

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
            } catch (MGXDTOException | InterruptedException ex) {
                fail(ex.getMessage());
            }

        }
        if (!success) {
            fail(up.getErrorMessage());
        }

        long fileSize = f.length();
        f.delete(); // delete local file

        assertEquals(fileSize, pc.getLastEvent().getNewValue());
        assertEquals(TransferBase.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public synchronized void testUploadEmptyFile() throws IOException {
        System.out.println("testUploadEmptyFile");
        MGXDTOMaster m = TestMaster.getRW();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }

        // create empty file
        File f = File.createTempFile("UploadEmpty", "xx");
        assertTrue(f.exists());

        String targetName = FileAccess.ROOT + "UploadEmpty.txt";

        FileUploader up = null;
        try {
            up = m.File().createUploader(f, targetName);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(up);

        boolean success = up.upload();
        assertTrue(success);

        if (success) {
            // cleanup
            FileDTO dto = FileDTO.newBuilder()
                    .setName(targetName)
                    .setIsDirectory(false)
                    .setSize(0)
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
            } catch (MGXDTOException | InterruptedException ex) {
                fail(ex.getMessage());
            }

        }
        if (!success) {
            fail(up.getErrorMessage());
        }

        long fileSize = f.length();
        f.delete(); // delete local file
    }

    @Test
    public synchronized void testUploadSameName() throws IOException {
        System.out.println("testUploadSameName");
        MGXDTOMaster m = TestMaster.getRW();
        if (m == null) {
            System.err.println("  private test, skipped");
            return;
        }

        File f = File.createTempFile("testUpload", "xxx");
        try (FileWriter fw = new FileWriter(f)) {
            fw.write("Unit Test DATA");
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        // a directory with this name already exists
        String targetName = FileAccess.ROOT + FileAccess.separator + "dir1";

        FileUploader up = null;
        try {
            up = m.File().createUploader(f, targetName);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }

        assertNotNull(up);

        PropCounter pc = new PropCounter();
        up.addPropertyChangeListener(pc);

        boolean success = up.upload();
        f.delete(); // delete local file

        assertFalse(success);
        assertEquals(TransferBase.TRANSFER_FAILED, pc.getLastEvent().getPropertyName());
        assertEquals("File already exists: ./dir1", up.getErrorMessage());
    }

    @Test
    public synchronized void testDownloadMissingFile() throws IOException {
        System.out.println("DownloadMissingFile");
        MGXDTOMaster m = TestMaster.getRO();

        OutputStream os = null;

        File f = File.createTempFile("testDownload", "xx");
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            fail(ex.getMessage());
        }

        String serverFile = ".|doesnotexist";

        FileDownloader down = null;
        try {
            down = m.File().createDownloader(serverFile, os);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(down);

        PropCounter pc = new PropCounter();
        down.addPropertyChangeListener(pc);

        boolean success = down.download();
        assertFalse(success);

        try {
            os.close();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        assertEquals("File does not exist: doesnotexist", down.getErrorMessage());

        // cleanup
        if (f.exists()) {
            f.delete();
        }

        assertEquals(1, pc.getCount());
        assertEquals(TransferBase.TRANSFER_FAILED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public synchronized void testDownloadFile() throws IOException {
        System.out.println("DownloadFile");
        MGXDTOMaster m = TestMaster.getRO();

        OutputStream os = null;
        File f = File.createTempFile("testDownload", "xx");
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            fail(ex.getMessage());
        }

        String serverFile = ".|test1";

        FileDownloader down = null;
        try {
            down = m.File().createDownloader(serverFile, os);
        } catch (MGXDTOException ex) {
            fail(ex.getMessage());
        }
        assertNotNull(down);

        PropCounter pc = new PropCounter();
        down.addPropertyChangeListener(pc);

        boolean success = down.download();
        assertTrue(success);

        try {
            os.close();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        if (!success) {
            fail(down.getErrorMessage());
        }

        try {
            String md5 = getMD5Checksum(f.getAbsolutePath());
            assertEquals("037db883cd8236c30242da3468cf8a19", md5);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

        // cleanup
        if (f.exists()) {
            f.delete();
        }

        assertTrue(10 < pc.getCount());
        assertEquals(TransferBase.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
    }

    @Test
    public synchronized void testDownloadPluginDump() throws Exception {
        System.out.println("DownloadPluginDump");
        MGXDTOMaster m = TestMaster.getRO();

        OutputStream os = null;
        File f = File.createTempFile("testDownload", "xx");
        try {
            os = new FileOutputStream(f);
        } catch (FileNotFoundException ex) {
            fail(ex.getMessage());
        }

        PluginDumpDownloader down = null;
        down = m.File().createPluginDumpDownloader(os);

        assertNotNull(down);

        PropCounter pc = new PropCounter();
        down.addPropertyChangeListener(pc);

        boolean success = down.download();

        assertTrue(success);

        try {
            os.close();
        } catch (IOException ex) {
            fail(ex.getMessage());
        }

        if (!success) {
            fail(down.getErrorMessage());
        }

        assertTrue(f.length() > 5000);

        // cleanup
        if (f.exists()) {
            f.delete();
        }

        assertTrue(pc.getCount() > 100);
        assertEquals(TransferBase.TRANSFER_COMPLETED, pc.getLastEvent().getPropertyName());
    }

    private static byte[] createChecksum(String filename) throws Exception {
        InputStream fis = new FileInputStream(filename);

        byte[] buffer = new byte[1024];
        MessageDigest complete = MessageDigest.getInstance("MD5");
        int numRead;

        do {
            numRead = fis.read(buffer);
            if (numRead > 0) {
                complete.update(buffer, 0, numRead);
            }
        } while (numRead != -1);

        fis.close();
        return complete.digest();
    }

    private static String getMD5Checksum(String filename) throws Exception {
        byte[] b = createChecksum(filename);
        String result = "";

        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}
