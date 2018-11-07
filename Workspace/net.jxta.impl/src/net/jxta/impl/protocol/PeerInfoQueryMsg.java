/*
 * Copyright (c) 2001-2007 Sun Microsystems, Inc.  All rights reserved.
 *
 *  The Sun Project JXTA(TM) Software License
 *
 *  Redistribution and use in source and binary forms, with or without 
 *  modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice, 
 *     this list of conditions and the following disclaimer in the documentation 
 *     and/or other materials provided with the distribution.
 *
 *  3. The end-user documentation included with the redistribution, if any, must 
 *     include the following acknowledgment: "This product includes software 
 *     developed by Sun Microsystems, Inc. for JXTA(TM) technology." 
 *     Alternately, this acknowledgment may appear in the software itself, if 
 *     and wherever such third-party acknowledgments normally appear.
 *
 *  4. The names "Sun", "Sun Microsystems, Inc.", "JXTA" and "Project JXTA" must 
 *     not be used to endorse or promote products derived from this software 
 *     without prior written permission. For written permission, please contact 
 *     Project JXTA at http://www.jxta.org.
 *
 *  5. Products derived from this software may not be called "JXTA", nor may 
 *     "JXTA" appear in their name, without prior written permission of Sun.
 *
 *  THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 *  INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL SUN 
 *  MICROSYSTEMS OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 *  LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 *  OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  JXTA is a registered trademark of Sun Microsystems, Inc. in the United 
 *  States and other countries.
 *
 *  Please see the license information page at :
 *  <http://www.jxta.org/project/www/license.html> for instructions on use of 
 *  the license in source files.
 *
 *  ====================================================================
 *
 *  This software consists of voluntary contributions made by many individuals 
 *  on behalf of Project JXTA. For more information on Project JXTA, please see 
 *  http://www.jxta.org.
 *
 *  This license is based on the BSD license adopted by the Apache Foundation. 
 */

package net.jxta.impl.protocol;

import net.jxta.document.Attributable;
import net.jxta.document.Document;
import net.jxta.document.Element;
import net.jxta.document.MimeMediaType;
import net.jxta.document.StructuredDocumentFactory;
import net.jxta.document.StructuredDocumentUtils;
import net.jxta.document.StructuredTextDocument;
import net.jxta.document.TextElement;
import net.jxta.id.ID;
import net.jxta.id.IDFactory;
import net.jxta.peer.PeerID;
import net.jxta.protocol.PeerInfoQueryMessage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;

/**
 * This class implements {@link net.jxta.protocol.PeerInfoQueryMessage}.
 * <p/>
 * <p>This message is part of the Peer PeerInfoService protocol
 * <p/>
 * <pre>
 * &lt;xs:element name="<classname>PeerInfoQueryMessage</classname>" type="jxta:PeerInfoQueryMessage"/>
 * <p/>
 * &lt;xs:complexType name="PeerInfoQueryMessage">
 *     &lt;xs:element name="sourcePid" type="xs:anyURI"/>
 *     &lt;xs:element name="targetPid" type="xs:anyURI"/>
 *     &lt;!-- if no present then the response is the general peerinfo -->
 *     &lt;xs:element name="request" type="xs:anyType" minOccurs="0"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @since JXTA 1.0
 */
public class PeerInfoQueryMsg extends PeerInfoQueryMessage {

    public PeerInfoQueryMsg() {
        super();
    }

    public PeerInfoQueryMsg(Element<?> root) {
        initialize(root);
    }

    public void initialize(Element<?> root) {
        if (!TextElement.class.isInstance(root)) {
            throw new IllegalArgumentException(getClass().getName() + " only supports TextElement");
        }

        setSourcePid(null);
        setTargetPid(null);

        TextElement<?> doc = (TextElement<?>) root;

        Enumeration<?> elements = doc.getChildren();

        while (elements.hasMoreElements()) {
            TextElement<?> element = (TextElement<?>) elements.nextElement();
            String elementName = element.getName();

            if (elementName.equals("sourcePid")) {
                try {
                    URI peerid = new URI(element.getTextValue());
                    ID id = IDFactory.fromURI(peerid);

                    setSourcePid((PeerID) id);
                } catch (URISyntaxException badID) {
                    throw new IllegalArgumentException("Bad peerid ID in advertisement");
                } catch (ClassCastException badID) {
                    throw new IllegalArgumentException("Unusable ID in advertisement");
                }
                continue;
            }

            if (elementName.equals("targetPid")) {
                try {
                    URI peerid = new URI(element.getTextValue());
                    ID id = IDFactory.fromURI(peerid);

                    setTargetPid((PeerID) id);
                } catch (URISyntaxException badID) {
                    throw new IllegalArgumentException("Bad peerid ID in advertisement");
                } catch (ClassCastException badID) {
                    throw new IllegalArgumentException("Unusable ID in advertisement");
                }
            } else if (elementName.equals("request")) {
                Enumeration<?> elems = element.getChildren();

                if (elems.hasMoreElements()) {
                    setRequest(StructuredDocumentUtils.copyAsDocument((Element<?>) elems.nextElement()));
                }
            }
        }
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Document getDocument(MimeMediaType encodeAs) {
        StructuredTextDocument doc = (StructuredTextDocument<?>)
                StructuredDocumentFactory.newStructuredDocument(encodeAs, getMessageType());

        if (doc instanceof Attributable) {
            ((Attributable) doc).addAttribute("xmlns:jxta", "http://jxta.org");
        }

        Element<?> e = doc.createElement("sourcePid", getSourcePid().toString());

        doc.appendChild(e);

        e = doc.createElement("targetPid", getTargetPid().toString());
        doc.appendChild(e);

        Element<?> request = getRequest();

        if (null != request) {
            e = doc.createElement("request");
            doc.appendChild(e);

            StructuredDocumentUtils.copyElements(doc, e, request);
        }

        return doc;
    }

}

