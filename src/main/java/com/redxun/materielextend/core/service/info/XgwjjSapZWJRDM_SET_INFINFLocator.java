/**
 * XgwjjSapZWJRDM_SET_INFINFLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.materielextend.core.service.info;

import com.redxun.sys.core.util.SysPropertiesUtil;

public class XgwjjSapZWJRDM_SET_INFINFLocator extends org.apache.axis.client.Service implements com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINF {

    public XgwjjSapZWJRDM_SET_INFINFLocator() {
    }


    public XgwjjSapZWJRDM_SET_INFINFLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjjSapZWJRDM_SET_INFINFLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjjSapZWJRDM_SET_INFINFHttpPort
    String sapUrl = SysPropertiesUtil.getGlobalProperty("sapUrl");
    private String XgwjjSapZWJRDM_SET_INFINFHttpPort_address = sapUrl+"xgwjj.sap.ZWJRDM_SET_INFINF";

    public String getXgwjjSapZWJRDM_SET_INFINFHttpPortAddress() {
        return XgwjjSapZWJRDM_SET_INFINFHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String XgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName = "xgwjj.sap.ZWJRDM_SET_INFINFHttpPort";

    public String getXgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName() {
        return XgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName;
    }

    public void setXgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName(String name) {
        XgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName = name;
    }

    public com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFPortType getXgwjjSapZWJRDM_SET_INFINFHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjjSapZWJRDM_SET_INFINFHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjjSapZWJRDM_SET_INFINFHttpPort(endpoint);
    }

    public com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFPortType getXgwjjSapZWJRDM_SET_INFINFHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFHttpBindingStub _stub = new com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjjSapZWJRDM_SET_INFINFHttpPortEndpointAddress(String address) {
        XgwjjSapZWJRDM_SET_INFINFHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFHttpBindingStub _stub = new com.redxun.materielextend.core.service.info.XgwjjSapZWJRDM_SET_INFINFHttpBindingStub(new java.net.URL(XgwjjSapZWJRDM_SET_INFINFHttpPort_address), this);
                _stub.setPortName(getXgwjjSapZWJRDM_SET_INFINFHttpPortWSDDServiceName());
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
        if ("xgwjj.sap.ZWJRDM_SET_INFINFHttpPort".equals(inputPortName)) {
            return getXgwjjSapZWJRDM_SET_INFINFHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJRDM_SET_INFINF");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJRDM_SET_INFINFHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("XgwjjSapZWJRDM_SET_INFINFHttpPort".equals(portName)) {
            setXgwjjSapZWJRDM_SET_INFINFHttpPortEndpointAddress(address);
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
