/**
 * IXcmgReviewWebserviceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.xcmgFinance.core.wsdl;

import javax.jws.WebService;

@WebService
public interface IXcmgReviewWebserviceService extends java.rmi.Remote {
    public String wakeupReview(String arg0) throws java.rmi.RemoteException, Exception;
    public String updateFormData(XcmgReviewUpdateParamterForm arg0) throws java.rmi.RemoteException, Exception;
}
