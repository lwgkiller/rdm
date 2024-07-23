/**
 * IXcmgReviewWebserviceServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.xcmgFinance.core.wsdl;

import com.redxun.sys.core.util.SysPropertiesUtil;

public class IXcmgReviewWebserviceServiceServiceLocator extends org.apache.axis.client.Service implements IXcmgReviewWebserviceServiceService {

    public IXcmgReviewWebserviceServiceServiceLocator() {
    }


    public IXcmgReviewWebserviceServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public IXcmgReviewWebserviceServiceServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for IXcmgReviewWebserviceServicePort
    private String IXcmgReviewWebserviceServicePort_address = SysPropertiesUtil.getGlobalProperty("oaFinanceUrl");

    public String getIXcmgReviewWebserviceServicePortAddress() {
        return IXcmgReviewWebserviceServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private String IXcmgReviewWebserviceServicePortWSDDServiceName = "IXcmgReviewWebserviceServicePort";

    public String getIXcmgReviewWebserviceServicePortWSDDServiceName() {
        return IXcmgReviewWebserviceServicePortWSDDServiceName;
    }

    public void setIXcmgReviewWebserviceServicePortWSDDServiceName(String name) {
        IXcmgReviewWebserviceServicePortWSDDServiceName = name;
    }

    public IXcmgReviewWebserviceService getIXcmgReviewWebserviceServicePort() throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(IXcmgReviewWebserviceServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getIXcmgReviewWebserviceServicePort(endpoint);
    }

    public IXcmgReviewWebserviceService getIXcmgReviewWebserviceServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            IXcmgReviewWebserviceServiceServiceSoapBindingStub _stub = new IXcmgReviewWebserviceServiceServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getIXcmgReviewWebserviceServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setIXcmgReviewWebserviceServicePortEndpointAddress(String address) {
        IXcmgReviewWebserviceServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (IXcmgReviewWebserviceService.class.isAssignableFrom(serviceEndpointInterface)) {
                IXcmgReviewWebserviceServiceServiceSoapBindingStub _stub = new IXcmgReviewWebserviceServiceServiceSoapBindingStub(new java.net.URL(IXcmgReviewWebserviceServicePort_address), this);
                _stub.setPortName(getIXcmgReviewWebserviceServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("IXcmgReviewWebserviceServicePort".equals(inputPortName)) {
            return getIXcmgReviewWebserviceServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.review.xcmg.kmss.landray.com/", "IXcmgReviewWebserviceServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.review.xcmg.kmss.landray.com/", "IXcmgReviewWebserviceServicePort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

        if ("IXcmgReviewWebserviceServicePort".equals(portName)) {
            setIXcmgReviewWebserviceServicePortEndpointAddress(address);
        }
        else
        { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
