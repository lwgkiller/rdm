/**
 * IKmReviewWebserviceServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.matPriceReview.core.service.oaWsdl;

import com.redxun.sys.core.util.SysPropertiesUtil;

public class IKmReviewWebserviceServiceServiceLocator extends org.apache.axis.client.Service
    implements IKmReviewWebserviceServiceService {

    public IKmReviewWebserviceServiceServiceLocator() {}

    public IKmReviewWebserviceServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IKmReviewWebserviceServiceServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName)
        throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IKmReviewWebserviceServicePort
    private String IKmReviewWebserviceServicePort_address = SysPropertiesUtil.getGlobalProperty("oaUrl");

    public String getIKmReviewWebserviceServicePortAddress() {
        return IKmReviewWebserviceServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private String IKmReviewWebserviceServicePortWSDDServiceName = "IKmReviewWebserviceServicePort";

    public String getIKmReviewWebserviceServicePortWSDDServiceName() {
        return IKmReviewWebserviceServicePortWSDDServiceName;
    }

    public void setIKmReviewWebserviceServicePortWSDDServiceName(String name) {
        IKmReviewWebserviceServicePortWSDDServiceName = name;
    }

    public IKmReviewWebserviceService getIKmReviewWebserviceServicePort() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IKmReviewWebserviceServicePort_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIKmReviewWebserviceServicePort(endpoint);
    }

    public IKmReviewWebserviceService getIKmReviewWebserviceServicePort(java.net.URL portAddress)
        throws javax.xml.rpc.ServiceException {
        try {
            IKmReviewWebserviceServiceServiceSoapBindingStub _stub =
                new IKmReviewWebserviceServiceServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getIKmReviewWebserviceServicePortWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIKmReviewWebserviceServicePortEndpointAddress(String address) {
        IKmReviewWebserviceServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (IKmReviewWebserviceService.class.isAssignableFrom(serviceEndpointInterface)) {
                IKmReviewWebserviceServiceServiceSoapBindingStub _stub =
                    new IKmReviewWebserviceServiceServiceSoapBindingStub(
                        new java.net.URL(IKmReviewWebserviceServicePort_address), this);
                _stub.setPortName(getIKmReviewWebserviceServicePortWSDDServiceName());
                return _stub;
            }
        } catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
            + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
        throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("IKmReviewWebserviceServicePort".equals(inputPortName)) {
            return getIKmReviewWebserviceServicePort();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub)_stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.review.km.kmss.landray.com/",
            "IKmReviewWebserviceServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.review.km.kmss.landray.com/",
                "IKmReviewWebserviceServicePort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

        if ("IKmReviewWebserviceServicePort".equals(portName)) {
            setIKmReviewWebserviceServicePortEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address)
        throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
