package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.AttributeTypeDTOList;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTOList;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.FileDTOList;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTOList;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTOList;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.dto.dto.MappingDTOList;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTOList;
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
        objmappings.put(JobDTOList.class, "Job");
        objmappings.put(AttributeDTO.class, "Attribute");
        objmappings.put(AttributeTypeDTO.class, "AttributeType");
        objmappings.put(AttributeTypeDTOList.class, "AttributeType");
        objmappings.put(ObservationDTO.class, "Observation");
        objmappings.put(FileDTO.class, "File");
        objmappings.put(ReferenceDTO.class, "Reference");
        objmappings.put(MappingDTO.class, "Mapping");

        // list types
        objmappings.put(HabitatDTOList.class, "Habitat");
        objmappings.put(SampleDTOList.class, "Sample");
        objmappings.put(ToolDTOList.class, "Tool");
        objmappings.put(DNAExtractDTOList.class, "DNAExtract");
        objmappings.put(SeqRunDTOList.class, "SeqRun");
        objmappings.put(FileDTOList.class, "File");
        objmappings.put(TermDTOList.class, "Term");
        objmappings.put(ReferenceDTOList.class, "Reference");
        objmappings.put(MappingDTOList.class, "Mapping");

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
        methodmappings.put("byRead", "byRead");
        methodmappings.put("byReference", "byReference");
        methodmappings.put("bySeqRun", "bySeqRun");
        methodmappings.put("JobsAndAttributeTypes", "JobsAndAttributeTypes");
        methodmappings.put("getQC", "getQC");
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
