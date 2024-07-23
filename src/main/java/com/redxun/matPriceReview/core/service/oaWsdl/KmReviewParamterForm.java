/**
 * KmReviewParamterForm.java
 *
 * This file was auto-generated from WSDL by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.matPriceReview.core.service.oaWsdl;

public class KmReviewParamterForm implements java.io.Serializable {
    private AttachmentForm[] attachmentForms;

    private String docContent;

    private String docCreator;

    private String docProperty;

    private String docStatus;

    private String docSubject;

    private String fdKeyword;

    private String fdTemplateId;

    private String flowParam;

    private String formValues;

    public KmReviewParamterForm() {}

    public KmReviewParamterForm(AttachmentForm[] attachmentForms, String docContent, String docCreator,
        String docProperty, String docStatus, String docSubject, String fdKeyword, String fdTemplateId,
        String flowParam, String formValues) {
        this.attachmentForms = attachmentForms;
        this.docContent = docContent;
        this.docCreator = docCreator;
        this.docProperty = docProperty;
        this.docStatus = docStatus;
        this.docSubject = docSubject;
        this.fdKeyword = fdKeyword;
        this.fdTemplateId = fdTemplateId;
        this.flowParam = flowParam;
        this.formValues = formValues;
    }

    /**
     * Gets the attachmentForms value for this KmReviewParamterForm.
     * 
     * @return attachmentForms
     */
    public AttachmentForm[] getAttachmentForms() {
        return attachmentForms;
    }

    /**
     * Sets the attachmentForms value for this KmReviewParamterForm.
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
     * Gets the docContent value for this KmReviewParamterForm.
     * 
     * @return docContent
     */
    public String getDocContent() {
        return docContent;
    }

    /**
     * Sets the docContent value for this KmReviewParamterForm.
     * 
     * @param docContent
     */
    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    /**
     * Gets the docCreator value for this KmReviewParamterForm.
     * 
     * @return docCreator
     */
    public String getDocCreator() {
        return docCreator;
    }

    /**
     * Sets the docCreator value for this KmReviewParamterForm.
     * 
     * @param docCreator
     */
    public void setDocCreator(String docCreator) {
        this.docCreator = docCreator;
    }

    /**
     * Gets the docProperty value for this KmReviewParamterForm.
     * 
     * @return docProperty
     */
    public String getDocProperty() {
        return docProperty;
    }

    /**
     * Sets the docProperty value for this KmReviewParamterForm.
     * 
     * @param docProperty
     */
    public void setDocProperty(String docProperty) {
        this.docProperty = docProperty;
    }

    /**
     * Gets the docStatus value for this KmReviewParamterForm.
     * 
     * @return docStatus
     */
    public String getDocStatus() {
        return docStatus;
    }

    /**
     * Sets the docStatus value for this KmReviewParamterForm.
     * 
     * @param docStatus
     */
    public void setDocStatus(String docStatus) {
        this.docStatus = docStatus;
    }

    /**
     * Gets the docSubject value for this KmReviewParamterForm.
     * 
     * @return docSubject
     */
    public String getDocSubject() {
        return docSubject;
    }

    /**
     * Sets the docSubject value for this KmReviewParamterForm.
     * 
     * @param docSubject
     */
    public void setDocSubject(String docSubject) {
        this.docSubject = docSubject;
    }

    /**
     * Gets the fdKeyword value for this KmReviewParamterForm.
     * 
     * @return fdKeyword
     */
    public String getFdKeyword() {
        return fdKeyword;
    }

    /**
     * Sets the fdKeyword value for this KmReviewParamterForm.
     * 
     * @param fdKeyword
     */
    public void setFdKeyword(String fdKeyword) {
        this.fdKeyword = fdKeyword;
    }

    /**
     * Gets the fdTemplateId value for this KmReviewParamterForm.
     * 
     * @return fdTemplateId
     */
    public String getFdTemplateId() {
        return fdTemplateId;
    }

    /**
     * Sets the fdTemplateId value for this KmReviewParamterForm.
     * 
     * @param fdTemplateId
     */
    public void setFdTemplateId(String fdTemplateId) {
        this.fdTemplateId = fdTemplateId;
    }

    /**
     * Gets the flowParam value for this KmReviewParamterForm.
     * 
     * @return flowParam
     */
    public String getFlowParam() {
        return flowParam;
    }

    /**
     * Sets the flowParam value for this KmReviewParamterForm.
     * 
     * @param flowParam
     */
    public void setFlowParam(String flowParam) {
        this.flowParam = flowParam;
    }

    /**
     * Gets the formValues value for this KmReviewParamterForm.
     * 
     * @return formValues
     */
    public String getFormValues() {
        return formValues;
    }

    /**
     * Sets the formValues value for this KmReviewParamterForm.
     * 
     * @param formValues
     */
    public void setFormValues(String formValues) {
        this.formValues = formValues;
    }

    private Object __equalsCalc = null;

    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof KmReviewParamterForm))
            return false;
        KmReviewParamterForm other = (KmReviewParamterForm)obj;
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
            && ((this.docContent == null && other.getDocContent() == null)
                || (this.docContent != null && this.docContent.equals(other.getDocContent())))
            && ((this.docCreator == null && other.getDocCreator() == null)
                || (this.docCreator != null && this.docCreator.equals(other.getDocCreator())))
            && ((this.docProperty == null && other.getDocProperty() == null)
                || (this.docProperty != null && this.docProperty.equals(other.getDocProperty())))
            && ((this.docStatus == null && other.getDocStatus() == null)
                || (this.docStatus != null && this.docStatus.equals(other.getDocStatus())))
            && ((this.docSubject == null && other.getDocSubject() == null)
                || (this.docSubject != null && this.docSubject.equals(other.getDocSubject())))
            && ((this.fdKeyword == null && other.getFdKeyword() == null)
                || (this.fdKeyword != null && this.fdKeyword.equals(other.getFdKeyword())))
            && ((this.fdTemplateId == null && other.getFdTemplateId() == null)
                || (this.fdTemplateId != null && this.fdTemplateId.equals(other.getFdTemplateId())))
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
        if (getDocContent() != null) {
            _hashCode += getDocContent().hashCode();
        }
        if (getDocCreator() != null) {
            _hashCode += getDocCreator().hashCode();
        }
        if (getDocProperty() != null) {
            _hashCode += getDocProperty().hashCode();
        }
        if (getDocStatus() != null) {
            _hashCode += getDocStatus().hashCode();
        }
        if (getDocSubject() != null) {
            _hashCode += getDocSubject().hashCode();
        }
        if (getFdKeyword() != null) {
            _hashCode += getFdKeyword().hashCode();
        }
        if (getFdTemplateId() != null) {
            _hashCode += getFdTemplateId().hashCode();
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
        new org.apache.axis.description.TypeDesc(KmReviewParamterForm.class, true);

    static {
        typeDesc.setXmlType(
            new javax.xml.namespace.QName("http://webservice.review.km.kmss.landray.com/", "kmReviewParamterForm"));
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
        elemField.setFieldName("docProperty");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docProperty"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("docStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "docStatus"));
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
        elemField.setFieldName("fdKeyword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fdKeyword"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fdTemplateId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fdTemplateId"));
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
