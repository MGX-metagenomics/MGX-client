package de.cebitec.mgx.client.access.rest.util;

import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validiert ob, die XML wohlgeformt ist und ob der erste Knoten den String
 * "Conveyor.MGX.GetMGXJob" bei dem Attribut type enthaelt.
 *
 *
 * @author pbelmann
 */
public class XMLValidator {

    /**
     * ToolDocumentHandler fuer das melden von einzelnen Bestandteilen.
     */
    private final ToolDocumentHandler handler= new ToolDocumentHandler();
    /**
     * Sobald der "nodes" startet, wird dieses Flag auf true gesetzt.
     */
    private boolean nodesStart = false;
    /**
     * Falls die Bedingung eintritt, dass XML wohlgeformt ist und der erste
     * Knoten den String "Conveyor.MGX.GetMGXJob" bei dem Attribut type
     * enthaelt.
     *
     */
    private boolean valid = false;
    private int getmgxjobCnt = 0;

    /**
     *
     * Validiert die XML.
     *
     *
     * @param lXml
     * @return isValid
     */
    public boolean isValid(String lXml) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(new InputSource(new StringReader(lXml)), handler);
            return valid && getmgxjobCnt > 0;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            return false;
        }
    }

    /**
     * Beim Parsen der XML werden hier die Methoden aufgerufen, sobald ein Error
     * auftritt, ein Element startet oder endet.
     */
    private class ToolDocumentHandler extends DefaultHandler {

        /**
         * Bei einem Error beim Parsen der XML wird diese Methode aufgerufen.
         *
         * @param e
         * @throws SAXException
         */
        @Override
        public void error(SAXParseException e) throws SAXException {
            super.error(e);
            valid = false;
        }

        /**
         * Ein fatalError tritt auf, sobald die XML nicht mehr geparst werden
         * kann.
         *
         * @param e
         * @throws SAXException
         */
        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            super.fatalError(e);
            valid = false;
        }

        /**
         * Sobald ein Element startet, beim Parsen der XML, wird diese Methode
         * aufgerufen.
         *
         * @param uri Uri
         * @param localName Name des Tags
         * @param qName qualified Name des Tags
         * @param attributes Attribute des Tags.
         * @throws SAXException Fehler beim Parsen der XML.
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (nodesStart && qName.equals("node")) {

                if (qName.equals("node")) {

                    if (attributes.getValue("type").equals("Conveyor.MGX.GetMGXJob")) {
                        getmgxjobCnt++;
                        valid = true;
                        nodesStart = false;
                    } else {
                        valid = false;
                        nodesStart = false;
                    }
                } else {
                    valid = false;
                    nodesStart = false;
                }
            }

            nodesStart = qName.equals("nodes");
        }
    }
}
