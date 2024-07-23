/**
 * ITEMS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.rdmZhgl.core.wsdl.materialadd;

public class ITEMS  implements java.io.Serializable {
    private String BDMNG;

    private String LGORT;

    private String MAKTX;

    private String MATNR;

    private String MEINS;

    public ITEMS() {
    }

    public ITEMS(
           String BDMNG,
           String LGORT,
           String MAKTX,
           String MATNR,
           String MEINS) {
           this.BDMNG = BDMNG;
           this.LGORT = LGORT;
           this.MAKTX = MAKTX;
           this.MATNR = MATNR;
           this.MEINS = MEINS;
    }


    /**
     * Gets the BDMNG value for this ITEMS.
     * 
     * @return BDMNG
     */
    public String getBDMNG() {
        return BDMNG;
    }


    /**
     * Sets the BDMNG value for this ITEMS.
     * 
     * @param BDMNG
     */
    public void setBDMNG(String BDMNG) {
        this.BDMNG = BDMNG;
    }


    /**
     * Gets the LGORT value for this ITEMS.
     * 
     * @return LGORT
     */
    public String getLGORT() {
        return LGORT;
    }


    /**
     * Sets the LGORT value for this ITEMS.
     * 
     * @param LGORT
     */
    public void setLGORT(String LGORT) {
        this.LGORT = LGORT;
    }


    /**
     * Gets the MAKTX value for this ITEMS.
     * 
     * @return MAKTX
     */
    public String getMAKTX() {
        return MAKTX;
    }


    /**
     * Sets the MAKTX value for this ITEMS.
     * 
     * @param MAKTX
     */
    public void setMAKTX(String MAKTX) {
        this.MAKTX = MAKTX;
    }


    /**
     * Gets the MATNR value for this ITEMS.
     * 
     * @return MATNR
     */
    public String getMATNR() {
        return MATNR;
    }


    /**
     * Sets the MATNR value for this ITEMS.
     * 
     * @param MATNR
     */
    public void setMATNR(String MATNR) {
        this.MATNR = MATNR;
    }


    /**
     * Gets the MEINS value for this ITEMS.
     * 
     * @return MEINS
     */
    public String getMEINS() {
        return MEINS;
    }


    /**
     * Sets the MEINS value for this ITEMS.
     * 
     * @param MEINS
     */
    public void setMEINS(String MEINS) {
        this.MEINS = MEINS;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ITEMS)) return false;
        ITEMS other = (ITEMS) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.BDMNG==null && other.getBDMNG()==null) || 
             (this.BDMNG!=null &&
              this.BDMNG.equals(other.getBDMNG()))) &&
            ((this.LGORT==null && other.getLGORT()==null) || 
             (this.LGORT!=null &&
              this.LGORT.equals(other.getLGORT()))) &&
            ((this.MAKTX==null && other.getMAKTX()==null) || 
             (this.MAKTX!=null &&
              this.MAKTX.equals(other.getMAKTX()))) &&
            ((this.MATNR==null && other.getMATNR()==null) || 
             (this.MATNR!=null &&
              this.MATNR.equals(other.getMATNR()))) &&
            ((this.MEINS==null && other.getMEINS()==null) || 
             (this.MEINS!=null &&
              this.MEINS.equals(other.getMEINS())));
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
        if (getBDMNG() != null) {
            _hashCode += getBDMNG().hashCode();
        }
        if (getLGORT() != null) {
            _hashCode += getLGORT().hashCode();
        }
        if (getMAKTX() != null) {
            _hashCode += getMAKTX().hashCode();
        }
        if (getMATNR() != null) {
            _hashCode += getMATNR().hashCode();
        }
        if (getMEINS() != null) {
            _hashCode += getMEINS().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ITEMS.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "ITEMS"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("BDMNG");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "BDMNG"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("LGORT");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "LGORT"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MAKTX");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "MAKTX"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MATNR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "MATNR"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("MEINS");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "MEINS"));
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
