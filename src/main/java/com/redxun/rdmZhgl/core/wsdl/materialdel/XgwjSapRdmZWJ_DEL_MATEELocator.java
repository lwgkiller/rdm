package com.redxun.rdmZhgl.core.wsdl.materialdel;

import com.redxun.sys.core.util.SysPropertiesUtil;

public class XgwjSapRdmZWJ_DEL_MATEELocator extends org.apache.axis.client.Service implements XgwjSapRdmZWJ_DEL_MATEE {

    public XgwjSapRdmZWJ_DEL_MATEELocator() {
    }


    public XgwjSapRdmZWJ_DEL_MATEELocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjSapRdmZWJ_DEL_MATEELocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjSapRdmZWJ_DEL_MATEEHttpPort
    String sapUrl = SysPropertiesUtil.getGlobalProperty("sapUrl");
    private String XgwjSapRdmZWJ_DEL_MATEEHttpPort_address = sapUrl+"xgwj.sap.rdm.ZWJ_DEL_MATEE";

    @Override
    public String getXgwjSapRdmZWJ_DEL_MATEEHttpPortAddress() {
        return XgwjSapRdmZWJ_DEL_MATEEHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String XgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName = "xgwj.sap.rdm.ZWJ_DEL_MATEEHttpPort";

    public String getXgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName() {
        return XgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName;
    }

    public void setXgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName(String name) {
        XgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName = name;
    }

    public XgwjSapRdmZWJ_DEL_MATEEPortType getXgwjSapRdmZWJ_DEL_MATEEHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjSapRdmZWJ_DEL_MATEEHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjSapRdmZWJ_DEL_MATEEHttpPort(endpoint);
    }

    public XgwjSapRdmZWJ_DEL_MATEEPortType getXgwjSapRdmZWJ_DEL_MATEEHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            XgwjSapRdmZWJ_DEL_MATEEHttpBindingStub _stub = new XgwjSapRdmZWJ_DEL_MATEEHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjSapRdmZWJ_DEL_MATEEHttpPortEndpointAddress(String address) {
        XgwjSapRdmZWJ_DEL_MATEEHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (XgwjSapRdmZWJ_DEL_MATEEPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                XgwjSapRdmZWJ_DEL_MATEEHttpBindingStub _stub = new XgwjSapRdmZWJ_DEL_MATEEHttpBindingStub(new java.net.URL(XgwjSapRdmZWJ_DEL_MATEEHttpPort_address), this);
                _stub.setPortName(getXgwjSapRdmZWJ_DEL_MATEEHttpPortWSDDServiceName());
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
        if ("xgwj.sap.rdm.ZWJ_DEL_MATEEHttpPort".equals(inputPortName)) {
            return getXgwjSapRdmZWJ_DEL_MATEEHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwj.sap.rdm.ZWJ_DEL_MATEE");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwj.sap.rdm.ZWJ_DEL_MATEEHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("XgwjSapRdmZWJ_DEL_MATEEHttpPort".equals(portName)) {
            setXgwjSapRdmZWJ_DEL_MATEEHttpPortEndpointAddress(address);
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
