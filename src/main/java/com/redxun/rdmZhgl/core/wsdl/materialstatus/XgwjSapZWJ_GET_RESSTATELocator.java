package com.redxun.rdmZhgl.core.wsdl.materialstatus;

import com.redxun.sys.core.util.SysPropertiesUtil;

public class XgwjSapZWJ_GET_RESSTATELocator extends org.apache.axis.client.Service implements XgwjSapZWJ_GET_RESSTATE {

    public XgwjSapZWJ_GET_RESSTATELocator() {
    }
    public XgwjSapZWJ_GET_RESSTATELocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjSapZWJ_GET_RESSTATELocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjSapZWJ_GET_RESSTATEHttpPort
    String sapUrl = SysPropertiesUtil.getGlobalProperty("sapUrl");
    private String XgwjSapZWJ_GET_RESSTATEHttpPort_address = sapUrl+"xgwj.sap.ZWJ_GET_RESSTATE";

    @Override
    public String getXgwjSapZWJ_GET_RESSTATEHttpPortAddress() {
        return XgwjSapZWJ_GET_RESSTATEHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String XgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName = "xgwj.sap.ZWJ_GET_RESSTATEHttpPort";

    public String getXgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName() {
        return XgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName;
    }

    public void setXgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName(String name) {
        XgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName = name;
    }

    @Override
    public XgwjSapZWJ_GET_RESSTATEPortType getXgwjSapZWJ_GET_RESSTATEHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjSapZWJ_GET_RESSTATEHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjSapZWJ_GET_RESSTATEHttpPort(endpoint);
    }

    @Override
    public XgwjSapZWJ_GET_RESSTATEPortType getXgwjSapZWJ_GET_RESSTATEHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            XgwjSapZWJ_GET_RESSTATEHttpBindingStub _stub = new XgwjSapZWJ_GET_RESSTATEHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjSapZWJ_GET_RESSTATEHttpPortEndpointAddress(String address) {
        XgwjSapZWJ_GET_RESSTATEHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (XgwjSapZWJ_GET_RESSTATEPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                XgwjSapZWJ_GET_RESSTATEHttpBindingStub _stub = new XgwjSapZWJ_GET_RESSTATEHttpBindingStub(new java.net.URL(XgwjSapZWJ_GET_RESSTATEHttpPort_address), this);
                _stub.setPortName(getXgwjSapZWJ_GET_RESSTATEHttpPortWSDDServiceName());
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
    @Override
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("xgwj.sap.ZWJ_GET_RESSTATEHttpPort".equals(inputPortName)) {
            return getXgwjSapZWJ_GET_RESSTATEHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwj.sap.ZWJ_GET_RESSTATE");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwj.sap.ZWJ_GET_RESSTATEHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("XgwjSapZWJ_GET_RESSTATEHttpPort".equals(portName)) {
            setXgwjSapZWJ_GET_RESSTATEHttpPortEndpointAddress(address);
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
