package com.redxun.rdmZhgl.core.wsdl.costcenter;

public class OUT_DATA  implements java.io.Serializable {
    private String KOSTL;

    private String KTEXT;

    public OUT_DATA() {
    }

    public OUT_DATA(
           String KOSTL,
           String KTEXT) {
           this.KOSTL = KOSTL;
           this.KTEXT = KTEXT;
    }


    /**
     * Gets the KOSTL value for this OUT_DATA.
     *
     * @return KOSTL
     */
    public String getKOSTL() {
        return KOSTL;
    }


    /**
     * Sets the KOSTL value for this OUT_DATA.
     *
     * @param KOSTL
     */
    public void setKOSTL(String KOSTL) {
        this.KOSTL = KOSTL;
    }


    /**
     * Gets the KTEXT value for this OUT_DATA.
     *
     * @return KTEXT
     */
    public String getKTEXT() {
        return KTEXT;
    }


    /**
     * Sets the KTEXT value for this OUT_DATA.
     *
     * @param KTEXT
     */
    public void setKTEXT(String KTEXT) {
        this.KTEXT = KTEXT;
    }

    private Object __equalsCalc = null;
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof OUT_DATA)) {
            return false;
        }
        OUT_DATA other = (OUT_DATA) obj;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.KOSTL==null && other.getKOSTL()==null) ||
             (this.KOSTL!=null &&
              this.KOSTL.equals(other.getKOSTL()))) &&
            ((this.KTEXT==null && other.getKTEXT()==null) ||
             (this.KTEXT!=null &&
              this.KTEXT.equals(other.getKTEXT())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    @Override
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getKOSTL() != null) {
            _hashCode += getKOSTL().hashCode();
        }
        if (getKTEXT() != null) {
            _hashCode += getKTEXT().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OUT_DATA.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "OUT_DATA"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KOSTL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "KOSTL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KTEXT");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "KTEXT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
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
