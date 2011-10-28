package de.cebitec.mgx.client.access.rest.util;

import de.cebitec.mgx.client.exception.MGXClientException;
import de.cebitec.mgx.dto.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sjaenick
 */
public class RESTPathResolver {

    protected final static Map<Class, String> objmappings = new HashMap<Class, String>();
    protected final static Map<String, String> methodmappings = new HashMap<String, String>();
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
        objmappings.put(MembershipDTO.class, "Project");

        methodmappings.put("create", "create");
        methodmappings.put("update", "update");
        methodmappings.put("fetch", "fetch");
        methodmappings.put("fetchall", "fetchall");
        methodmappings.put("delete", "delete");
    }

    protected RESTPathResolver() {
    }

    public static String objPath(Class c) {
        return objmappings.get(c);
    }

    public static String mPath(String c) {
        return methodmappings.get(c);
    }

    public final String resolve(Class c, String m) throws MGXClientException {
        if (!objmappings.containsKey(c) || ! methodmappings.containsKey(m))
            throw new MGXClientException("Missing REST path for "+c+"/"+m);

        return new StringBuilder("/").append(objmappings.get(c)).append("/").append(methodmappings.get(m)).append("/").toString();
    }

    public static RESTPathResolver getInstance() {
        return instance;
    }
}
