package com.redxun.rdmZhgl.core.wsdl.materialdel;

import com.redxun.rdmZhgl.core.wsdl.materialdel.holders.ArrayOfRESB_OUTHolder;
import com.redxun.rdmZhgl.core.wsdl.materialdel.holders.ArrayOfRES_INHolder;

public interface XgwjSapRdmZWJ_DEL_MATEEPortType extends java.rmi.Remote {
    public void ZWJ_SET_RESDEL(ArrayOfRESB_OUTHolder arrayOfRESB_OUT, ArrayOfRES_INHolder arrayOfRES_IN, javax.xml.rpc.holders.StringHolder RETURNCODE, javax.xml.rpc.holders.StringHolder RETURNMSG) throws java.rmi.RemoteException;
}
