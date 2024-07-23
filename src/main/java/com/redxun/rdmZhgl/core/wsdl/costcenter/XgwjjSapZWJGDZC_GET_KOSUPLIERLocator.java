package com.redxun.rdmZhgl.core.wsdl.costcenter;

import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;

public class XgwjjSapZWJGDZC_GET_KOSUPLIERLocator extends org.apache.axis.client.Service implements XgwjjSapZWJGDZC_GET_KOSUPLIER {

    public XgwjjSapZWJGDZC_GET_KOSUPLIERLocator() {
    }


    public XgwjjSapZWJGDZC_GET_KOSUPLIERLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjjSapZWJGDZC_GET_KOSUPLIERLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort
    String sapUrl = SysPropertiesUtil.getGlobalProperty("sapUrl");
    private String XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort_address = sapUrl+"xgwjj.sap.ZWJGDZC_GET_KOSUPLIER";

    @Override
    public String getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortAddress() {
        return XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName = "xgwjj.sap.ZWJGDZC_GET_KOSUPLIERHttpPort";

    public String getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName() {
        return XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName;
    }

    public void setXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName(String name) {
        XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName = name;
    }

    @Override
    public XgwjjSapZWJGDZC_GET_KOSUPLIERPortType getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort(endpoint);
    }

    @Override
    public XgwjjSapZWJGDZC_GET_KOSUPLIERPortType getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            XgwjjSapZWJGDZC_GET_KOSUPLIERHttpBindingStub _stub = new XgwjjSapZWJGDZC_GET_KOSUPLIERHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortEndpointAddress(String address) {
        XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @Override
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (XgwjjSapZWJGDZC_GET_KOSUPLIERPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                XgwjjSapZWJGDZC_GET_KOSUPLIERHttpBindingStub _stub = new XgwjjSapZWJGDZC_GET_KOSUPLIERHttpBindingStub(new java.net.URL(XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort_address), this);
                _stub.setPortName(getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortWSDDServiceName());
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
        if ("xgwjj.sap.ZWJGDZC_GET_KOSUPLIERHttpPort".equals(inputPortName)) {
            return getXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    @Override
    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJGDZC_GET_KOSUPLIER");
    }

    private java.util.HashSet ports = null;

    @Override
    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJGDZC_GET_KOSUPLIERHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("XgwjjSapZWJGDZC_GET_KOSUPLIERHttpPort".equals(portName)) {
            setXgwjjSapZWJGDZC_GET_KOSUPLIERHttpPortEndpointAddress(address);
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
