/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sc.rhinosandbox.utils;

import com.sc.rhinosandbox.annotations.RhinoFunction;
import com.sc.rhinosandbox.misc.RhinoBaseFunction;
import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author lucifer
 */
@RhinoFunction("xml2json")
public class RhinoXML2JSON extends RhinoBaseFunction {

    @Override
    protected Object call() {
        Deque<ScriptableObject> stack = new ArrayDeque<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader xmlReader = factory.createXMLStreamReader(new StringReader(getArgument(0, String.class)));
            try {
                int event = xmlReader.getEventType();
                ScriptableObject so = null;
                Map<String, String> namespaces = new HashMap<>();

                while (true) {

                    switch (event) {
                        case XMLStreamConstants.START_DOCUMENT:
                            so = getContext().initStandardObjects();
                            break;
                        case XMLStreamConstants.END_DOCUMENT:
                            if (!stack.isEmpty()) {
                                throw new IllegalStateException("Malformed XML");
                            }
                            break;
                        case XMLStreamConstants.START_ELEMENT:
                            stack.push(so);
                            ScriptableObject child = getContext().initStandardObjects();
                            QName tagName = xmlReader.getName();
                            String tagStr = tagName.getLocalPart();
                            if (so.has(tagStr, so)) {
                                Scriptable local = (Scriptable) so.get(tagStr, so);
                                if (!(local instanceof NativeArray)) {
                                    NativeArray nr = (NativeArray) getContext().newArray(getContext().initStandardObjects(), 2);
                                    nr.set(0, local);
                                    nr.set(1, child);
                                } else {
                                    NativeArray nr = (NativeArray) local;
                                    nr.add(child);
                                }
                            } else {
                                so.put(tagStr, so, child);
                            }
                            so = child;
                            if (tagName.getNamespaceURI() != null && !tagName.getNamespaceURI().isEmpty()) {
                                namespaces.put(tagName.getPrefix(), tagName.getNamespaceURI());
                                child.put("!prefix", so, tagName.getPrefix());
                            }
                            break;
                        case XMLStreamConstants.END_ELEMENT:
                            so = stack.pop();
                            break;
                        case XMLStreamConstants.CDATA:
                            so.put("#cdata", so, xmlReader.getText());
                            break;
                        case XMLStreamConstants.CHARACTERS:
                            if (xmlReader.isWhiteSpace()) {
                                break;
                            }
                            so.put("#text", so, xmlReader.getText());
                            break;
                        case XMLStreamConstants.ATTRIBUTE:
                            for (int i = 0; i < xmlReader.getAttributeCount(); i++) {
                                QName attrName = xmlReader.getAttributeName(i);
                                String value = xmlReader.getAttributeValue(i);
                                ScriptableObject attr = getContext().initStandardObjects();
                                attr.put("value", attr, value);

                                if (attrName.getPrefix() != null && !attrName.getPrefix().isEmpty()) {
                                    namespaces.put(attrName.getPrefix(), attrName.getNamespaceURI());
                                    attr.put("!prefix", so, attrName.getPrefix());
                                }
                                so.put("@" + attrName.getLocalPart(), attr, attr);
                            }
//                            xmlReader.
//                            so.put("@", so, xmlReader.getAt);
                            break;
                    }

                    if (!xmlReader.hasNext()) {
                        break;
                    }
                    event = xmlReader.next();
                }
                ScriptableObject ret = getContext().initStandardObjects();
                ret.put("xml", ret, so);
                ret.put("namespaces", ret, putNamespaces(namespaces));
                return ret;
            } finally {
                xmlReader.close();
            }
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ScriptableObject putNamespaces(Map<String, String> namespaces) {
        ScriptableObject ns = getContext().initStandardObjects();
        for (String key : namespaces.keySet()) {
            ns.put(key, ns, namespaces.get(key));
        }
        return ns;
    }

}
