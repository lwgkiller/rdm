package com.redxun.rdmZhgl.core.wsdl.materialstatus;

import com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRESB_OUTHolder;
import com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRES_INHolder;

public class XgwjSapZWJ_GET_RESSTATEHttpBindingStub extends org.apache.axis.client.Stub implements XgwjSapZWJ_GET_RESSTATEPortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[1];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ZWJ_GET_RESSTATE");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ArrayOfRESB_OUT"), org.apache.axis.description.ParameterDesc.INOUT, new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "ArrayOfRESB_OUT"), RESB_OUT[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RESB_OUT"));
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ArrayOfRES_IN"), org.apache.axis.description.ParameterDesc.INOUT, new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "ArrayOfRES_IN"), RES_IN[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RES_IN"));
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "RETURNCODE"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "RETURNMSG"), org.apache.axis.description.ParameterDesc.OUT, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

    }

    public XgwjSapZWJ_GET_RESSTATEHttpBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public XgwjSapZWJ_GET_RESSTATEHttpBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public XgwjSapZWJ_GET_RESSTATEHttpBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.1");
            Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "ArrayOfRES_IN");
            cachedSerQNames.add(qName);
            cls = RES_IN[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RES_IN");
            qName2 = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RES_IN");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "ArrayOfRESB_OUT");
            cachedSerQNames.add(qName);
            cls = RESB_OUT[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RESB_OUT");
            qName2 = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RESB_OUT");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RES_IN");
            cachedSerQNames.add(qName);
            cls = RES_IN.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RESB_OUT");
            cachedSerQNames.add(qName);
            cls = RESB_OUT.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        Class cls = (Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            Class sf = (Class)
                                 cachedSerFactories.get(i);
                            Class df = (Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public void ZWJ_GET_RESSTATE(ArrayOfRESB_OUTHolder arrayOfRESB_OUT, ArrayOfRES_INHolder arrayOfRES_IN, javax.xml.rpc.holders.StringHolder RETURNCODE, javax.xml.rpc.holders.StringHolder RETURNMSG) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ZWJ_GET_RESSTATE"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        Object _resp = _call.invoke(new Object[] {arrayOfRESB_OUT.value, arrayOfRES_IN.value});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            java.util.Map _output;
            _output = _call.getOutputParams();
            try {
                arrayOfRESB_OUT.value = (RESB_OUT[]) _output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ArrayOfRESB_OUT"));
            } catch (Exception _exception) {
                arrayOfRESB_OUT.value = (RESB_OUT[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ArrayOfRESB_OUT")), RESB_OUT[].class);
            }
            try {
                arrayOfRES_IN.value = (RES_IN[]) _output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ArrayOfRES_IN"));
            } catch (Exception _exception) {
                arrayOfRES_IN.value = (RES_IN[]) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "ArrayOfRES_IN")), RES_IN[].class);
            }
            try {
                RETURNCODE.value = (String) _output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "RETURNCODE"));
            } catch (Exception _exception) {
                RETURNCODE.value = (String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "RETURNCODE")), String.class);
            }
            try {
                RETURNMSG.value = (String) _output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "RETURNMSG"));
            } catch (Exception _exception) {
                RETURNMSG.value = (String) org.apache.axis.utils.JavaUtils.convert(_output.get(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor", "RETURNMSG")), String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
