package com.redxun.rdmZhgl.core.wsdl.materialdel;

public class RES_IN  implements java.io.Serializable {
    private String RSNUM;

    private String RSPOS;

    public RES_IN() {
    }

    public RES_IN(
           String RSNUM,
           String RSPOS) {
           this.RSNUM = RSNUM;
           this.RSPOS = RSPOS;
    }


    /**
     * Gets the RSNUM value for this RES_IN.
     *
     * @return RSNUM
     */
    public String getRSNUM() {
        return RSNUM;
    }


    /**
     * Sets the RSNUM value for this RES_IN.
     *
     * @param RSNUM
     */
    public void setRSNUM(String RSNUM) {
        this.RSNUM = RSNUM;
    }


    /**
     * Gets the RSPOS value for this RES_IN.
     *
     * @return RSPOS
     */
    public String getRSPOS() {
        return RSPOS;
    }


    /**
     * Sets the RSPOS value for this RES_IN.
     *
     * @param RSPOS
     */
    public void setRSPOS(String RSPOS) {
        this.RSPOS = RSPOS;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RES_IN)) return false;
        RES_IN other = (RES_IN) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.RSNUM==null && other.getRSNUM()==null) ||
             (this.RSNUM!=null &&
              this.RSNUM.equals(other.getRSNUM()))) &&
            ((this.RSPOS==null && other.getRSPOS()==null) ||
             (this.RSPOS!=null &&
              this.RSPOS.equals(other.getRSPOS())));
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
        if (getRSNUM() != null) {
            _hashCode += getRSNUM().hashCode();
        }
        if (getRSPOS() != null) {
            _hashCode += getRSPOS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RES_IN.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RES_IN"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RSNUM");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RSNUM"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RSPOS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RSPOS"));
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
