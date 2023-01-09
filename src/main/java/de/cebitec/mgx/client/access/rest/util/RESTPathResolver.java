package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.dto.dto;
import de.cebitec.mgx.dto.dto.AssembledRegionDTO;
import de.cebitec.mgx.dto.dto.AssembledRegionDTOList;
import de.cebitec.mgx.dto.dto.AssemblyDTO;
import de.cebitec.mgx.dto.dto.AssemblyDTOList;
import de.cebitec.mgx.dto.dto.AttributeDTO;
import de.cebitec.mgx.dto.dto.AttributeTypeDTO;
import de.cebitec.mgx.dto.dto.AttributeTypeDTOList;
import de.cebitec.mgx.dto.dto.BinDTO;
import de.cebitec.mgx.dto.dto.BinDTOList;
import de.cebitec.mgx.dto.dto.BulkObservationDTOList;
import de.cebitec.mgx.dto.dto.ContigDTO;
import de.cebitec.mgx.dto.dto.ContigDTOList;
import de.cebitec.mgx.dto.dto.DNAExtractDTO;
import de.cebitec.mgx.dto.dto.DNAExtractDTOList;
import de.cebitec.mgx.dto.dto.FileDTO;
import de.cebitec.mgx.dto.dto.FileDTOList;
import de.cebitec.mgx.dto.dto.GeneCoverageDTO;
import de.cebitec.mgx.dto.dto.GeneCoverageDTOList;
import de.cebitec.mgx.dto.dto.GeneObservationDTO;
import de.cebitec.mgx.dto.dto.GeneObservationDTOList;
import de.cebitec.mgx.dto.dto.HabitatDTO;
import de.cebitec.mgx.dto.dto.HabitatDTOList;
import de.cebitec.mgx.dto.dto.JobDTO;
import de.cebitec.mgx.dto.dto.JobDTOList;
import de.cebitec.mgx.dto.dto.MappingDTO;
import de.cebitec.mgx.dto.dto.MappingDTOList;
import de.cebitec.mgx.dto.dto.ObservationDTO;
import de.cebitec.mgx.dto.dto.ObservationDTOList;
import de.cebitec.mgx.dto.dto.ReferenceDTO;
import de.cebitec.mgx.dto.dto.ReferenceDTOList;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTO;
import de.cebitec.mgx.dto.dto.ReferenceRegionDTOList;
import de.cebitec.mgx.dto.dto.SampleDTO;
import de.cebitec.mgx.dto.dto.SampleDTOList;
import de.cebitec.mgx.dto.dto.SeqRunDTO;
import de.cebitec.mgx.dto.dto.SeqRunDTOList;
import de.cebitec.mgx.dto.dto.SequenceDTO;
import de.cebitec.mgx.dto.dto.SequenceDTOList;
import de.cebitec.mgx.dto.dto.TermDTOList;
import de.cebitec.mgx.dto.dto.ToolDTO;
import de.cebitec.mgx.dto.dto.ToolDTOList;
import java.util.Arrays;
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
        objmappings.put(ReferenceRegionDTO.class, "ReferenceRegion");
        objmappings.put(MappingDTO.class, "Mapping");
        objmappings.put(AssemblyDTO.class, "Assembly");
        objmappings.put(BinDTO.class, "Bin");
        objmappings.put(ContigDTO.class, "Contig");
        objmappings.put(AssembledRegionDTO.class, "AssembledRegion");
        objmappings.put(GeneCoverageDTO.class, "GeneCoverage");
        objmappings.put(GeneObservationDTO.class, "GeneObservation");

        objmappings.put(dto.BinSearchResultDTO.class, "AssembledRegion");

        // list types
        objmappings.put(HabitatDTOList.class, "Habitat");
        objmappings.put(SampleDTOList.class, "Sample");
        objmappings.put(ToolDTOList.class, "Tool");
        objmappings.put(DNAExtractDTOList.class, "DNAExtract");
        objmappings.put(SeqRunDTOList.class, "SeqRun");
        objmappings.put(SequenceDTOList.class, "Sequence");
        objmappings.put(FileDTOList.class, "File");
        objmappings.put(TermDTOList.class, "Term");
        objmappings.put(ReferenceDTOList.class, "Reference");
        objmappings.put(ReferenceRegionDTOList.class, "ReferenceRegion");
        objmappings.put(MappingDTOList.class, "Mapping");
        objmappings.put(ObservationDTOList.class, "Observation");
        objmappings.put(BulkObservationDTOList.class, "Observation");
        objmappings.put(AssemblyDTOList.class, "Assembly");
        objmappings.put(BinDTOList.class, "Bin");
        objmappings.put(ContigDTOList.class, "Contig");
        objmappings.put(AssembledRegionDTOList.class, "AssembledRegion");
        objmappings.put(GeneCoverageDTOList.class, "GeneCoverage");
        objmappings.put(GeneObservationDTOList.class, "GeneObservation");

        objmappings.put(dto.BinSearchResultDTOList.class, "AssembledRegion");

        // methods
        methodmappings.put("create", "create");
        methodmappings.put("update", "update");
        methodmappings.put("fetch", "fetch");
        methodmappings.put("fetchall", "fetchall");
        methodmappings.put("delete", "delete");
        //
        methodmappings.put("fetchByIds", "fetchByIds");
        methodmappings.put("byHabitat", "byHabitat");
        methodmappings.put("byExtract", "byExtract");
        methodmappings.put("bySample", "bySample");
        methodmappings.put("byJob", "byJob");
        methodmappings.put("hasQuality", "hasQuality");
        methodmappings.put("getDefinition", "getDefinition");
        methodmappings.put("byCategory", "byCategory");
        methodmappings.put("byRead", "byRead");
        methodmappings.put("byReference", "byReference");
        methodmappings.put("byReferenceInterval", "byReferenceInterval");
        methodmappings.put("bySeqRun", "bySeqRun");
        methodmappings.put("byAssembly", "byAssembly");
        methodmappings.put("byBin", "byBin");
        methodmappings.put("byContig", "byContig");
        methodmappings.put("byGene", "byGene");
        methodmappings.put("getDNASequence", "getDNASequence");
        methodmappings.put("JobsAndAttributeTypes", "JobsAndAttributeTypes");
        methodmappings.put("getQC", "getQC");
        methodmappings.put("createBulk", "createBulk");
        methodmappings.put("search", "search");

    }

    private RESTPathResolver() {
    }

    public static String objPath(Class c) {
        return objmappings.get(c);
    }

    public static String mPath(String c) {
        return methodmappings.get(c);
    }

    public final String[] resolve(Class c, String methodName, String... opts) throws MGXClientException {
        String[] s = resolve(c, methodName);
        if (opts != null && opts.length > 0) {
            s = Arrays.copyOf(s, s.length + opts.length);
            System.arraycopy(opts, 0, s, 2, opts.length);
        }
        return s;
    }

    public final String[] resolve(Class c, String methodName) throws MGXClientException {
        if (!objmappings.containsKey(c)) {
            throw new MGXClientException("Missing REST object mapping path for class " + c.getName() + "/" + methodName);
        }

        if (!methodmappings.containsKey(methodName)) {
            throw new MGXClientException("Missing REST method mapping for class " + c.getName() + ", method call " + methodName);
        }

        return new String[]{objmappings.get(c), methodmappings.get(methodName)};
        //return new StringBuilder("/").append(objmappings.get(c)).append("/").append(methodmappings.get(m)).append("/").toString();
    }

    public final String resolveClass(Class c) throws MGXClientException {
        if (!objmappings.containsKey(c)) {
            throw new MGXClientException("Missing REST object mapping path for class " + c.getName());
        }
        return objmappings.get(c);
    }

    public static RESTPathResolver getInstance() {
        return instance;
    }
}
