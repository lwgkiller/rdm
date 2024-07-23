/**
 * IKmReviewWebserviceService.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.matPriceReview.core.service.oaWsdl;

public interface IKmReviewWebserviceService extends java.rmi.Remote {
    public String resubmitProcess(KmReviewResubmitParamterForm arg0)
        throws java.rmi.RemoteException, com.redxun.matPriceReview.core.service.oaWsdl.Exception;

    public String addReview(KmReviewParamterForm arg0)
        throws java.rmi.RemoteException, com.redxun.matPriceReview.core.service.oaWsdl.Exception;

    public String updateReview(KmReviewUpdateParamterForm arg0)
        throws java.rmi.RemoteException, com.redxun.matPriceReview.core.service.oaWsdl.Exception;

    public String findOpinion(String arg0)
        throws java.rmi.RemoteException, com.redxun.matPriceReview.core.service.oaWsdl.Exception;
}
