/**
 * XgwjjSapZWJXERDM_MARALocator.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.materielextend.core.service.wsdltest;

public class XgwjjSapZWJXERDM_MARALocator extends org.apache.axis.client.Service
    implements com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARA {

    public XgwjjSapZWJXERDM_MARALocator() {}

    public XgwjjSapZWJXERDM_MARALocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjjSapZWJXERDM_MARALocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName)
        throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjjSapZWJXERDM_MARAHttpPort
    private java.lang.String XgwjjSapZWJXERDM_MARAHttpPort_address = "HTTP://10.15.8.175:9091/xgwjj.sap.ZWJXERDM_MARA";

    public java.lang.String getXgwjjSapZWJXERDM_MARAHttpPortAddress() {
        return XgwjjSapZWJXERDM_MARAHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String XgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName = "xgwjj.sap.ZWJXERDM_MARAHttpPort";

    public java.lang.String getXgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName() {
        return XgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName;
    }

    public void setXgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName(java.lang.String name) {
        XgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName = name;
    }

    public com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAPortType getXgwjjSapZWJXERDM_MARAHttpPort()
        throws javax.xml.rpc.ServiceException {
        java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjjSapZWJXERDM_MARAHttpPort_address);
        } catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjjSapZWJXERDM_MARAHttpPort(endpoint);
    }

    public com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAPortType
        getXgwjjSapZWJXERDM_MARAHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAHttpBindingStub _stub =
                new com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName());
            return _stub;
        } catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjjSapZWJXERDM_MARAHttpPortEndpointAddress(java.lang.String address) {
        XgwjjSapZWJXERDM_MARAHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation. If this service has no port for the given interface, then
     * ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAPortType.class
                .isAssignableFrom(serviceEndpointInterface)) {
                com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAHttpBindingStub _stub =
                    new com.redxun.materielextend.core.service.wsdltest.XgwjjSapZWJXERDM_MARAHttpBindingStub(
                        new java.net.URL(XgwjjSapZWJXERDM_MARAHttpPort_address), this);
                _stub.setPortName(getXgwjjSapZWJXERDM_MARAHttpPortWSDDServiceName());
                return _stub;
            }
        } catch (java.lang.Throwable t) {
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
        java.lang.String inputPortName = portName.getLocalPart();
        if ("xgwjj.sap.ZWJXERDM_MARAHttpPort".equals(inputPortName)) {
            return getXgwjjSapZWJXERDM_MARAHttpPort();
        } else {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub)_stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJXERDM_MARA");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor",
                "xgwjj.sap.ZWJXERDM_MARAHttpPort"));
        }
        return ports.iterator();
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address)
        throws javax.xml.rpc.ServiceException {

        if ("XgwjjSapZWJXERDM_MARAHttpPort".equals(portName)) {
            setXgwjjSapZWJXERDM_MARAHttpPortEndpointAddress(address);
        } else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
     * Set the endpoint address for the specified port name.
     */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address)
        throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
