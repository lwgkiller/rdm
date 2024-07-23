/**
 * XgwjSapRdmZWJ_CRESET_MBBPortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.rdmZhgl.core.wsdl.materialadd;

public interface XgwjSapRdmZWJ_CRESET_MBBPortType extends java.rmi.Remote {
    public void ZWJ_SET_MB21(String AUFNR, String BDTER, String KOSTL, String MOVE_TYPE, String UMLGO, String WEMPF, String SAKNR, com.redxun.rdmZhgl.core.wsdl.materialadd.holders.ArrayOfITEMSHolder arrayOfITEMS, com.redxun.rdmZhgl.core.wsdl.materialadd.holders.ArrayOfITEMS_OUTHolder arrayOfITEMS_OUT, javax.xml.rpc.holders.StringHolder RETURNCODE, javax.xml.rpc.holders.StringHolder RETURNMSG, javax.xml.rpc.holders.StringHolder RSNUM) throws java.rmi.RemoteException;
}
