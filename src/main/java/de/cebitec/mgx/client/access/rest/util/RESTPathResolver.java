package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTOList;
import de.cebitec.mgx.dto.dto.FileOrDirectory;
import de.cebitec.mgx.dto.dto.FoDList;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTOList;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTOList;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.TermDTOList;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.dto.dto.ToolDTOList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class RESTPathResolver {

    protected final static Map<Class, String> objmappings = new HashMap<>();
    protected final static Map<String, String> methodmappings = new HashMap<>();
    protected final static RESTPathResolver instance = new RESTPathResolver();

    static {
        objmappings.put(HabitatDTO.class, "Habitat");
        objmappings.put(SampleDTO.class, "Sample");
        objmappings.put(DNAExtractDTO.class, "DNAExtract");
        objmappings.put(SeqRunDTO.class, "SeqRun");
        objmappings.put(SequenceDTO.class, "Sequence");
        objmappings.put(ToolDTO.class, "Tool");
        objmappings.put(JobDTO.class, "Job");
        objmappings.put(AttributeDTO.class, "Attribute");
        objmappings.put(FileOrDirectory.class, "File");

        // list types
        objmappings.put(HabitatDTOList.class, "Habitat");
        objmappings.put(SampleDTOList.class, "Sample");
        objmappings.put(ToolDTOList.class, "Tool");
        objmappings.put(DNAExtractDTOList.class, "DNAExtract");
        objmappings.put(SeqRunDTOList.class, "SeqRun");
        objmappings.put(FoDList.class, "File");
        objmappings.put(TermDTOList.class, "Term");

        // methods
        methodmappings.put("create", "create");
        methodmappings.put("update", "update");
        methodmappings.put("fetch", "fetch");
        methodmappings.put("fetchall", "fetchall");
        methodmappings.put("delete", "delete");
        //
        methodmappings.put("byHabitat", "byHabitat");
        methodmappings.put("byExtract", "byExtract");
        methodmappings.put("bySample", "bySample");
        methodmappings.put("byJob", "byJob");
        methodmappings.put("byCategory", "byCategory");
        methodmappings.put("JobsAndAttributeTypes", "JobsAndAttributeTypes");
    }

    private RESTPathResolver() {
    }

    public static String objPath(Class c) {
        return objmappings.get(c);
    }

    public static String mPath(String c) {
        return methodmappings.get(c);
    }

    public final String resolve(Class c, String m) throws MGXClientException {
        if (!objmappings.containsKey(c)) {
            throw new MGXClientException("Missing REST object mapping path for class " + c.getName() + "/" + m);
        }

        if (!methodmappings.containsKey(m)) {
            throw new MGXClientException("Missing REST method mapping for class " + c.getName() + ", method call " + m);
        }

        return new StringBuilder("/").append(objmappings.get(c)).append("/").append(methodmappings.get(m)).append("/").toString();
    }

    public static RESTPathResolver getInstance() {
        return instance;
    }
}
