/**
 * XcmgReviewUpdateParamterForm.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.xcmgFinance.core.wsdl;

public class XcmgReviewUpdateParamterForm  implements java.io.Serializable {
    private AttachmentForm[] attachmentForms;

    private String fdId;

    private String formValues;

    public XcmgReviewUpdateParamterForm() {
    }

    public XcmgReviewUpdateParamterForm(
           AttachmentForm[] attachmentForms,
           String fdId,
           String formValues) {
           this.attachmentForms = attachmentForms;
           this.fdId = fdId;
           this.formValues = formValues;
    }


    /**
     * Gets the attachmentForms value for this XcmgReviewUpdateParamterForm.
     * 
     * @return attachmentForms
     */
    public AttachmentForm[] getAttachmentForms() {
        return attachmentForms;
    }


    /**
     * Sets the attachmentForms value for this XcmgReviewUpdateParamterForm.
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
     * Gets the fdId value for this XcmgReviewUpdateParamterForm.
     * 
     * @return fdId
     */
    public String getFdId() {
        return fdId;
    }


    /**
     * Sets the fdId value for this XcmgReviewUpdateParamterForm.
     * 
     * @param fdId
     */
    public void setFdId(String fdId) {
        this.fdId = fdId;
    }


    /**
     * Gets the formValues value for this XcmgReviewUpdateParamterForm.
     * 
     * @return formValues
     */
    public String getFormValues() {
        return formValues;
    }


    /**
     * Sets the formValues value for this XcmgReviewUpdateParamterForm.
     * 
     * @param formValues
     */
    public void setFormValues(String formValues) {
        this.formValues = formValues;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof XcmgReviewUpdateParamterForm)) return false;
        XcmgReviewUpdateParamterForm other = (XcmgReviewUpdateParamterForm) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.attachmentForms==null && other.getAttachmentForms()==null) || 
             (this.attachmentForms!=null &&
              java.util.Arrays.equals(this.attachmentForms, other.getAttachmentForms()))) &&
            ((this.fdId==null && other.getFdId()==null) || 
             (this.fdId!=null &&
              this.fdId.equals(other.getFdId()))) &&
            ((this.formValues==null && other.getFormValues()==null) || 
             (this.formValues!=null &&
              this.formValues.equals(other.getFormValues())));
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
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAttachmentForms());
                 i++) {
                Object obj = java.lang.reflect.Array.get(getAttachmentForms(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getFdId() != null) {
            _hashCode += getFdId().hashCode();
        }
        if (getFormValues() != null) {
            _hashCode += getFormValues().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(XcmgReviewUpdateParamterForm.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.review.xcmg.kmss.landray.com/", "xcmgReviewUpdateParamterForm"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attachmentForms");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attachmentForms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://webservice.review.xcmg.kmss.landray.com/", "attachmentForm"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fdId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "fdId"));
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
    public static org.apache.axis.encoding.Serializer getSerializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           String mechType,
           Class _javaType,
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
