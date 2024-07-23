/**
 * XgwjSapRdmZWJ_CRESET_MBBLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.rdmZhgl.core.wsdl.materialadd;

import com.redxun.sys.core.util.SysPropertiesUtil;
public class XgwjSapRdmZWJ_CRESET_MBBLocator extends org.apache.axis.client.Service implements XgwjSapRdmZWJ_CRESET_MBB {

    public XgwjSapRdmZWJ_CRESET_MBBLocator() {
    }


    public XgwjSapRdmZWJ_CRESET_MBBLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjSapRdmZWJ_CRESET_MBBLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjSapRdmZWJ_CRESET_MBBHttpPort
    String sapUrl = SysPropertiesUtil.getGlobalProperty("sapUrl");
    private String XgwjSapRdmZWJ_CRESET_MBBHttpPort_address = sapUrl+"xgwj.sap.rdm.ZWJ_CRESET_MBB";

    public String getXgwjSapRdmZWJ_CRESET_MBBHttpPortAddress() {
        return XgwjSapRdmZWJ_CRESET_MBBHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String XgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName = "xgwj.sap.rdm.ZWJ_CRESET_MBBHttpPort";

    public String getXgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName() {
        return XgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName;
    }

    public void setXgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName(String name) {
        XgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName = name;
    }

    public XgwjSapRdmZWJ_CRESET_MBBPortType getXgwjSapRdmZWJ_CRESET_MBBHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjSapRdmZWJ_CRESET_MBBHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjSapRdmZWJ_CRESET_MBBHttpPort(endpoint);
    }

    public XgwjSapRdmZWJ_CRESET_MBBPortType getXgwjSapRdmZWJ_CRESET_MBBHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            XgwjSapRdmZWJ_CRESET_MBBHttpBindingStub _stub = new XgwjSapRdmZWJ_CRESET_MBBHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjSapRdmZWJ_CRESET_MBBHttpPortEndpointAddress(String address) {
        XgwjSapRdmZWJ_CRESET_MBBHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (XgwjSapRdmZWJ_CRESET_MBBPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                XgwjSapRdmZWJ_CRESET_MBBHttpBindingStub _stub = new XgwjSapRdmZWJ_CRESET_MBBHttpBindingStub(new java.net.URL(XgwjSapRdmZWJ_CRESET_MBBHttpPort_address), this);
                _stub.setPortName(getXgwjSapRdmZWJ_CRESET_MBBHttpPortWSDDServiceName());
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
        if ("xgwj.sap.rdm.ZWJ_CRESET_MBBHttpPort".equals(inputPortName)) {
            return getXgwjSapRdmZWJ_CRESET_MBBHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwj.sap.rdm.ZWJ_CRESET_MBB");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwj.sap.rdm.ZWJ_CRESET_MBBHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("XgwjSapRdmZWJ_CRESET_MBBHttpPort".equals(portName)) {
            setXgwjSapRdmZWJ_CRESET_MBBHttpPortEndpointAddress(address);
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
