/**
 * XgwjjSapZWJXERDM_MODIFY_MARALocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.materielextend.core.service.wsdl;
import com.redxun.saweb.util.WebAppUtil;
import com.redxun.sys.core.util.SysPropertiesUtil;
public class XgwjjSapZWJXERDM_MODIFY_MARALocator extends org.apache.axis.client.Service 
implements XgwjjSapZWJXERDM_MODIFY_MARA {

    public XgwjjSapZWJXERDM_MODIFY_MARALocator() {
    }


    public XgwjjSapZWJXERDM_MODIFY_MARALocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public XgwjjSapZWJXERDM_MODIFY_MARALocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for XgwjjSapZWJXERDM_MODIFY_MARAHttpPort
    String sapUrl = SysPropertiesUtil.getGlobalProperty("sapUrl");
    private String XgwjjSapZWJXERDM_MODIFY_MARAHttpPort_address = sapUrl+ "xgwjj.sap.ZWJXERDM_MODIFY_MARA";

    public String getXgwjjSapZWJXERDM_MODIFY_MARAHttpPortAddress() {
        return XgwjjSapZWJXERDM_MODIFY_MARAHttpPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String XgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName = "xgwjj.sap.ZWJXERDM_MODIFY_MARAHttpPort";

    public String getXgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName() {
        return XgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName;
    }

    public void setXgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName(String name) {
        XgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName = name;
    }

    public XgwjjSapZWJXERDM_MODIFY_MARAPortType getXgwjjSapZWJXERDM_MODIFY_MARAHttpPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(XgwjjSapZWJXERDM_MODIFY_MARAHttpPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getXgwjjSapZWJXERDM_MODIFY_MARAHttpPort(endpoint);
    }

    public XgwjjSapZWJXERDM_MODIFY_MARAPortType getXgwjjSapZWJXERDM_MODIFY_MARAHttpPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            XgwjjSapZWJXERDM_MODIFY_MARAHttpBindingStub _stub = new XgwjjSapZWJXERDM_MODIFY_MARAHttpBindingStub(portAddress, this);
            _stub.setPortName(getXgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setXgwjjSapZWJXERDM_MODIFY_MARAHttpPortEndpointAddress(String address) {
        XgwjjSapZWJXERDM_MODIFY_MARAHttpPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (XgwjjSapZWJXERDM_MODIFY_MARAPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                XgwjjSapZWJXERDM_MODIFY_MARAHttpBindingStub _stub = new XgwjjSapZWJXERDM_MODIFY_MARAHttpBindingStub(new java.net.URL(XgwjjSapZWJXERDM_MODIFY_MARAHttpPort_address), this);
                _stub.setPortName(getXgwjjSapZWJXERDM_MODIFY_MARAHttpPortWSDDServiceName());
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
        if ("xgwjj.sap.ZWJXERDM_MODIFY_MARAHttpPort".equals(inputPortName)) {
            return getXgwjjSapZWJXERDM_MODIFY_MARAHttpPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJXERDM_MODIFY_MARA");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "xgwjj.sap.ZWJXERDM_MODIFY_MARAHttpPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {
        
if ("XgwjjSapZWJXERDM_MODIFY_MARAHttpPort".equals(portName)) {
            setXgwjjSapZWJXERDM_MODIFY_MARAHttpPortEndpointAddress(address);
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
