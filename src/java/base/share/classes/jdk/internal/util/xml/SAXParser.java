/*
 * Copyright (c) 2023 Geo-Studios - All Rights Reserved.
 */

package java.base.share.classes.jdk.internal.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import jdk.internal.org.xml.sax.InputSource;
import jdk.internal.org.xml.sax.SAXException;
import jdk.internal.org.xml.sax.XMLReader;
import jdk.internal.org.xml.sax.helpers.DefaultHandler;


/**
 * Defines the API that wraps an {@link org.xml.sax.XMLReader}
 * implementation class. In JAXP 1.0, this class wrapped the
 * {@link org.xml.sax.Parser} interface, however this interface was
 * replaced by the {@link org.xml.sax.XMLReader}. For ease
 * of transition, this class continues to support the same name
 * and interface as well as supporting new methods.
 *
 * An instance of this class can be obtained from the
 * {@link javax.xml.parsers.SAXParserFactory#newSAXParser()} method.
 * Once an instance of this class is obtained, XML can be parsed from
 * a variety of input sources. These input sources are InputStreams,
 * Files, URLs, and SAX InputSources.<p>
 *
 * This static method creates a new factory instance based
 * on a system property setting or uses the platform default
 * if no property has been defined.<p>
 *
 * The system property that controls which Factory implementation
 * to create is named <code>&quot;javax.xml.parsers.SAXParserFactory&quot;</code>.
 * This property names a class that is a concrete subclass of this
 * abstract class. If no property is defined, a platform default
 * will be used.</p>
 *
 * As the content is parsed by the underlying parser, methods of the
 * given
 * {@link org.xml.sax.helpers.DefaultHandler} are called.<p>
 *
 * Implementors of this class which wrap an underlying implementation
 * can consider using the {@link org.xml.sax.helpers.ParserAdapter}
 * class to initially adapt their SAX1 implementation to work under
 * this revised class.
 *
 * @author <a href="mailto:Jeff.Suttor@Sun.com">Jeff Suttor</a>
 * @version $Revision: 1.8 $, $Date: 2010-11-01 04:36:09 $
 *
 * @author Joe Wang
 * This is a subset of that in JAXP, javax.xml.parsers.SAXParser
 *
 */
public abstract class SAXParser {

    /**
     * <p>Protected constructor to prevent instantiation.</p>
     */
    protected SAXParser() {
    }

    /**
     * Parse the content of the given {@link java.io.InputStream}
     * instance as XML using the specified
     * {@link org.xml.sax.helpers.DefaultHandler}.
     *
     * @param is InputStream containing the content to be parsed.
     * @param dh The SAX DefaultHandler to use.
     *
     * @throws IllegalArgumentException If the given InputStream is null.
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any SAX errors occur during processing.
     *
     * @see org.xml.sax.DocumentHandler
     */
    public void parse(InputStream is, DefaultHandler dh)
        throws SAXException, IOException
    {
        if (is == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }

        InputSource input = new InputSource(is);
        this.parse(input, dh);
    }

    /**
     * Parse the content described by the giving Uniform Resource
     * Identifier (URI) as XML using the specified
     * {@link org.xml.sax.helpers.DefaultHandler}.
     *
     * @param uri The location of the content to be parsed.
     * @param dh The SAX DefaultHandler to use.
     *
     * @throws IllegalArgumentException If the uri is null.
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any SAX errors occur during processing.
     *
     * @see org.xml.sax.DocumentHandler
     */
    public void parse(String uri, DefaultHandler dh)
        throws SAXException, IOException
    {
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }

        InputSource input = new InputSource(uri);
        this.parse(input, dh);
    }

    /**
     * Parse the content of the file specified as XML using the
     * specified {@link org.xml.sax.helpers.DefaultHandler}.
     *
     * @param f The file containing the XML to parse
     * @param dh The SAX DefaultHandler to use.
     *
     * @throws IllegalArgumentException If the File object is null.
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any SAX errors occur during processing.
     *
     * @see org.xml.sax.DocumentHandler
     */
    public void parse(File f, DefaultHandler dh)
        throws SAXException, IOException
    {
        if (f == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        //convert file to appropriate URI, f.toURI().toASCIIString()
        //converts the URI to string as per rule specified in
        //RFC 2396,
        InputSource input = new InputSource(f.toURI().toASCIIString());
        this.parse(input, dh);
    }

    /**
     * Parse the content given {@link org.xml.sax.InputSource}
     * as XML using the specified
     * {@link org.xml.sax.helpers.DefaultHandler}.
     *
     * @param is The InputSource containing the content to be parsed.
     * @param dh The SAX DefaultHandler to use.
     *
     * @throws IllegalArgumentException If the <code>InputSource</code> object
     *   is <code>null</code>.
     * @throws IOException If any IO errors occur.
     * @throws SAXException If any SAX errors occur during processing.
     *
     * @see org.xml.sax.DocumentHandler
     */
    public void parse(InputSource is, DefaultHandler dh)
        throws SAXException, IOException
    {
        if (is == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }

        XMLReader reader = this.getXMLReader();
        if (dh != null) {
            reader.setContentHandler(dh);
            reader.setEntityResolver(dh);
            reader.setErrorHandler(dh);
            reader.setDTDHandler(dh);
        }
        reader.parse(is);
    }

    /**
     * Returns the {@link org.xml.sax.XMLReader} that is encapsulated by the
     * implementation of this class.
     *
     * @return The XMLReader that is encapsulated by the
     *         implementation of this class.
     *
     * @throws SAXException If any SAX errors occur during processing.
     */
    public abstract XMLReader getXMLReader() throws SAXException;

    /**
     * Indicates whether or not this parser is configured to
     * understand namespaces.
     *
     * @return true if this parser is configured to
     *         understand namespaces; false otherwise.
     */
    public abstract boolean isNamespaceAware();

    /**
     * Indicates whether or not this parser is configured to
     * validate XML documents.
     *
     * @return true if this parser is configured to
     *         validate XML documents; false otherwise.
     */
    public abstract boolean isValidating();

    /**
     * <p>Get the XInclude processing mode for this parser.</p>
     *
     * @return
     *      the return value of
     *      the {@link SAXParserFactory#isXIncludeAware()}
     *      when this parser was created from factory.
     *
     * @throws UnsupportedOperationException When implementation does not
     *   override this method
     *
     * @since 1.5
     *
     * @see SAXParserFactory#setXIncludeAware(boolean)
     */
    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException(
                "This parser does not support specification \""
                + this.getClass().getPackage().getSpecificationTitle()
                + "\" version \""
                + this.getClass().getPackage().getSpecificationVersion()
                + "\"");
    }
}
