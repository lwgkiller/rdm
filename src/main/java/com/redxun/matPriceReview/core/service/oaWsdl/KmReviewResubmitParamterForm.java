/**
 * KmReviewResubmitParamterForm.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.matPriceReview.core.service.oaWsdl;

public class KmReviewResubmitParamterForm implements java.io.Serializable {
    private AttachmentForm[] attachmentForms;

    private String auditNote;

    private String docContent;

    private String docCreator;

    private String docSubject;

    private String fdId;

    private String flowParam;

    private String formValues;

    public KmReviewResubmitParamterForm() {}

    public KmReviewResubmitParamterForm(AttachmentForm[] attachmentForms, String auditNote, String docContent,
        String docCreator, String docSubject, String fdId, String flowParam, String formValues) {
        this.attachmentForms = attachmentForms;
        this.auditNote = auditNote;
        this.docContent = docContent;
        this.docCreator = docCreator;
        this.docSubject = docSubject;
        this.fdId = fdId;
        this.flowParam = flowParam;
        this.formValues = formValues;
    }

    /**
     * Gets the attachmentForms value for this KmReviewResubmitParamterForm.
     * 
     * @return attachmentForms
     */
    public AttachmentForm[] getAttachmentForms() {
        return attachmentForms;
    }

    /**
     * Sets the attachmentForms value for this KmReviewResubmitParamterForm.
     * 
     * @param attachmentForms
     */
    public void setAttachmentForms(AttachmentForm[] attachmentForms) {
        this.attachmentForms = attachmentForms;
    }

    public AttachmentForm getAttachmentForms(int i) {
        return this.attachmentForms[i];
    }

    public void setAttachmentForms(int i, AttachmentForm _value) {
        this.attachmentForms[i] = _value;
    }

    /**
     * Gets the auditNote value for this KmReviewResubmitParamterForm.
     * 
     * @return auditNote
     */
    public String getAuditNote() {
        return auditNote;
    }

    /**
     * Sets the auditNote value for this KmReviewResubmitParamterForm.
     * 
     * @param auditNote
     */
    public void setAuditNote(String auditNote) {
        this.auditNote = auditNote;
    }

    /**
     * Gets the docContent value for this KmReviewResubmitParamterForm.
     * 
     * @return docContent
     */
    public String getDocContent() {
        return docContent;
    }

    /**
     * Sets the docContent value for this KmReviewResubmitParamterForm.
     * 
     * @param docContent
     */
    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    /**
     * Gets the docCreator value for this KmReviewResubmitParamterForm.
     * 
     * @return docCreator
     */
    public String getDocCreator() {
        return docCreator;
    }

    /**
     * Sets the docCreator value for this KmReviewResubmitParamterForm.
     * 
     * @param docCreator
     */
    public void setDocCreator(String docCreator) {
        this.docCreator = docCreator;
    }

    /**
     * Gets the docSubject value for this KmReviewResubmitParamterForm.
     * 
     * @return docSubject
     */
    public String getDocSubject() {
        return docSubject;
    }

    /**
     * Sets the docSubject value for this KmReviewResubmitParamterForm.
     * 
     * @param docSubject
     */
    public void setDocSubject(String docSubject) {
        this.docSubject = docSubject;
    }

    /**
     * Gets the fdId value for this KmReviewResubmitParamterForm.
     * 
     * @return fdId
     */
    public String getFdId() {
        return fdId;
    }

    /**
     * Sets the fdId value for this KmReviewResubmitParamterForm.
     * 
     * @param fdId
     */
    public void setFdId(String fdId) {
        this.fdId = fdId;
    }

    /**
     * Gets the flowParam value for this KmReviewResubmitParamterForm.
     * 
     * @return flowParam
     */
    public String getFlowParam() {
        return flowParam;
    }

    /**
     * Sets the flowParam value for this KmReviewResubmitParamterForm.
     * 
     * @param flowParam
     */
    public void setFlowParam(String flowParam) {
        this.flowParam = flowParam;
    }

    /**
     * Gets the formValues value for this KmReviewResubmitParamterForm.
     * 
     * @return formValues
     */
    public String getFormValues() {
        return formValues;
    }

    /**
     * Sets the formValues value for this KmReviewResubmitParamterForm.
     * 
     * @param formValues
     */
    public void setFormValues(String formValues) {
        this.formValues = formValues;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof KmReviewResubmitParamterForm))
            return false;
        KmReviewResubmitParamterForm other = (KmReviewResubmitParamterForm)obj;
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true
            && ((this.attachmentForms == null && other.getAttachmentForms() == null) || (this.attachmentForms != null
                && java.util.Arrays.equals(this.attachmentForms, other.getAttachmentForms())))
            && ((this.auditNote == null && other.getAuditNote() == null)
                || (this.auditNote != null && this.auditNote.equals(other.getAuditNote())))
            && ((this.docContent == null && other.getDocContent() == null)
                || (this.docContent != null && this.docContent.equals(other.getDocContent())))
            && ((this.docCreator == null && other.getDocCreator() == null)
                || (this.docCreator != null && this.docCreator.equals(other.getDocCreator())))
            && ((this.docSubject == null && other.getDocSubject() == null)
                || (this.docSubject != null && this.docSubject.equals(other.getDocSubject())))
            && ((this.fdId == null && other.getFdId() == null)
                || (this.fdId != null && this.fdId.equals(other.getFdId())))
            && ((this.flowParam == null && other.getFlowParam() == null)
                || (this.flowParam != null && this.flowParam.equals(other.getFlowParam())))
            && ((this.formValues == null && other.getFormValues() == null)
                || (this.formValues != null && this.formValues.equals(other.getFormValues())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;

    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getAttachmentForms() != null) {
            for (int i = 0; i < java.lang.reflect.Array.getLength(getAttachmentForms()); i++) {
                Object obj = java.lang.reflect.Array.get(getAttachmentForms(), i);
                if (obj != null && !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAuditNote() != null) {
            _hashCode += getAuditNote().hashCode();
        }
        if (getDocContent() != null) {
            _hashCode += getDocContent().hashCode();
        }
        if (getDocCreator() != null) {
            _hashCode += getDocCreator().hashCode();
        }
        if (getDocSubject() != null) {
            _hashCode += getDocSubject().hashCode();
        }
        if (getFdId() != null) {
            _hashCode += getFdId().hashCode();
        }
        if (getFlowParam() != null) {
            _hashCode += getFlowParam().hashCode();
        }
        if (getFormValues() != null) {
            _hashCode += getFormValues().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(KmReviewResubmitParamterForm.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.review.km.kmss.landray.com/",
            "kmReviewResubmitParamterForm"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attachmentForms");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attachmentForms"));
        elemField.setXmlType(
            new javax.xml.namespace.QName("http://webservice.review.km.kmss.landray.com/", "attachmentForm"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditNote");
        elemField.setXmlName(new javax.xml.namespace.QName("", "auditNote"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docContent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docContent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docCreator");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docCreator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docSubject");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docSubject"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fdId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fdId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flowParam");
        elemField.setXmlName(new javax.xml.namespace.QName("", "flowParam"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formValues");
        elemField.setXmlName(new javax.xml.namespace.QName("", "formValues"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(String mechType, Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(String mechType, Class _javaType,
        javax.xml.namespace.QName _xmlType) {
        return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
    }

}
