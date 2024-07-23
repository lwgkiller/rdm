
package com.redxun.serviceEngineering.core.webservice;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.webservice.oa2 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Data_QNAME = new QName("http://peie.webservice.waji.xcmg.kmss.landray.com/", "data");
    private final static QName _DataResponse_QNAME = new QName("http://peie.webservice.waji.xcmg.kmss.landray.com/", "dataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.webservice.oa2
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Data }
     * 
     */
    public Data createData() {
        return new Data();
    }

    /**
     * Create an instance of {@link DataResponse }
     * 
     */
    public DataResponse createDataResponse() {
        return new DataResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Data }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://peie.webservice.waji.xcmg.kmss.landray.com/", name = "data")
    public JAXBElement<Data> createData(Data value) {
        return new JAXBElement<Data>(_Data_QNAME, Data.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://peie.webservice.waji.xcmg.kmss.landray.com/", name = "dataResponse")
    public JAXBElement<DataResponse> createDataResponse(DataResponse value) {
        return new JAXBElement<DataResponse>(_DataResponse_QNAME, DataResponse.class, null, value);
    }

}
