/**
 * ITEMS_OUT.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.redxun.rdmZhgl.core.wsdl.materialadd;

public class ITEMS_OUT  implements java.io.Serializable {
    private String BDMNG;

    private String LGORT;

    private String MAKTX;

    private String MATNR;

    private String MEINS;

    private String RSNUM;

    private String RSPOS;

    private String SAKNR;

    public ITEMS_OUT() {
    }

    public ITEMS_OUT(
           String BDMNG,
           String LGORT,
           String MAKTX,
           String MATNR,
           String MEINS,
           String RSNUM,
           String RSPOS,
           String SAKNR) {
           this.BDMNG = BDMNG;
           this.LGORT = LGORT;
           this.MAKTX = MAKTX;
           this.MATNR = MATNR;
           this.MEINS = MEINS;
           this.RSNUM = RSNUM;
           this.RSPOS = RSPOS;
           this.SAKNR = SAKNR;
    }


    /**
     * Gets the BDMNG value for this ITEMS_OUT.
     * 
     * @return BDMNG
     */
    public String getBDMNG() {
        return BDMNG;
    }


    /**
     * Sets the BDMNG value for this ITEMS_OUT.
     * 
     * @param BDMNG
     */
    public void setBDMNG(String BDMNG) {
        this.BDMNG = BDMNG;
    }


    /**
     * Gets the LGORT value for this ITEMS_OUT.
     * 
     * @return LGORT
     */
    public String getLGORT() {
        return LGORT;
    }


    /**
     * Sets the LGORT value for this ITEMS_OUT.
     * 
     * @param LGORT
     */
    public void setLGORT(String LGORT) {
        this.LGORT = LGORT;
    }


    /**
     * Gets the MAKTX value for this ITEMS_OUT.
     * 
     * @return MAKTX
     */
    public String getMAKTX() {
        return MAKTX;
    }


    /**
     * Sets the MAKTX value for this ITEMS_OUT.
     * 
     * @param MAKTX
     */
    public void setMAKTX(String MAKTX) {
        this.MAKTX = MAKTX;
    }


    /**
     * Gets the MATNR value for this ITEMS_OUT.
     * 
     * @return MATNR
     */
    public String getMATNR() {
        return MATNR;
    }


    /**
     * Sets the MATNR value for this ITEMS_OUT.
     * 
     * @param MATNR
     */
    public void setMATNR(String MATNR) {
        this.MATNR = MATNR;
    }


    /**
     * Gets the MEINS value for this ITEMS_OUT.
     * 
     * @return MEINS
     */
    public String getMEINS() {
        return MEINS;
    }


    /**
     * Sets the MEINS value for this ITEMS_OUT.
     * 
     * @param MEINS
     */
    public void setMEINS(String MEINS) {
        this.MEINS = MEINS;
    }


    /**
     * Gets the RSNUM value for this ITEMS_OUT.
     * 
     * @return RSNUM
     */
    public String getRSNUM() {
        return RSNUM;
    }


    /**
     * Sets the RSNUM value for this ITEMS_OUT.
     * 
     * @param RSNUM
     */
    public void setRSNUM(String RSNUM) {
        this.RSNUM = RSNUM;
    }


    /**
     * Gets the RSPOS value for this ITEMS_OUT.
     * 
     * @return RSPOS
     */
    public String getRSPOS() {
        return RSPOS;
    }


    /**
     * Sets the RSPOS value for this ITEMS_OUT.
     * 
     * @param RSPOS
     */
    public void setRSPOS(String RSPOS) {
        this.RSPOS = RSPOS;
    }


    /**
     * Gets the SAKNR value for this ITEMS_OUT.
     * 
     * @return SAKNR
     */
    public String getSAKNR() {
        return SAKNR;
    }


    /**
     * Sets the SAKNR value for this ITEMS_OUT.
     * 
     * @param SAKNR
     */
    public void setSAKNR(String SAKNR) {
        this.SAKNR = SAKNR;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof ITEMS_OUT)) return false;
        ITEMS_OUT other = (ITEMS_OUT) obj;
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
              this.MEINS.equals(other.getMEINS()))) &&
            ((this.RSNUM==null && other.getRSNUM()==null) || 
             (this.RSNUM!=null &&
              this.RSNUM.equals(other.getRSNUM()))) &&
            ((this.RSPOS==null && other.getRSPOS()==null) || 
             (this.RSPOS!=null &&
              this.RSPOS.equals(other.getRSPOS()))) &&
            ((this.SAKNR==null && other.getSAKNR()==null) || 
             (this.SAKNR!=null &&
              this.SAKNR.equals(other.getSAKNR())));
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
        if (getRSNUM() != null) {
            _hashCode += getRSNUM().hashCode();
        }
        if (getRSPOS() != null) {
            _hashCode += getRSPOS().hashCode();
        }
        if (getSAKNR() != null) {
            _hashCode += getSAKNR().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ITEMS_OUT.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "ITEMS_OUT"));
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
        elemField = new org.apache.axis.description.ElementDesc();
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
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("SAKNR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "SAKNR"));
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
