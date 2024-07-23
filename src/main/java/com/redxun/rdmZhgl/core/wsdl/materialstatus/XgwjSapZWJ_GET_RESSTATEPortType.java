package com.redxun.rdmZhgl.core.wsdl.materialstatus;

import com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRESB_OUTHolder;
import com.redxun.rdmZhgl.core.wsdl.materialstatus.holders.ArrayOfRES_INHolder;

public interface XgwjSapZWJ_GET_RESSTATEPortType extends java.rmi.Remote {
    public void ZWJ_GET_RESSTATE(ArrayOfRESB_OUTHolder arrayOfRESB_OUT, ArrayOfRES_INHolder arrayOfRES_IN, javax.xml.rpc.holders.StringHolder RETURNCODE, javax.xml.rpc.holders.StringHolder RETURNMSG) throws java.rmi.RemoteException;
}
