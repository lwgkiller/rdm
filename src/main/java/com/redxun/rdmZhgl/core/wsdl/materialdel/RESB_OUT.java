package com.redxun.rdmZhgl.core.wsdl.materialdel;

public class RESB_OUT  implements java.io.Serializable {
    private String RETURNCODE;

    private String WQSL;

    private String RETURNMSG;

    private String KZEAR;

    private String RSNUM;

    private String RSPOS;

    private String XLOEK;

    private String XWAOK;

    public RESB_OUT() {
    }

    public RESB_OUT(
           String RETURNCODE,
           String WQSL,
           String RETURNMSG,
           String KZEAR,
           String RSNUM,
           String RSPOS,
           String XLOEK,
           String XWAOK) {
           this.RETURNCODE = RETURNCODE;
           this.WQSL = WQSL;
           this.RETURNMSG = RETURNMSG;
           this.KZEAR = KZEAR;
           this.RSNUM = RSNUM;
           this.RSPOS = RSPOS;
           this.XLOEK = XLOEK;
           this.XWAOK = XWAOK;
    }


    /**
     * Gets the RETURNCODE value for this RESB_OUT.
     *
     * @return RETURNCODE
     */
    public String getRETURNCODE() {
        return RETURNCODE;
    }


    /**
     * Sets the RETURNCODE value for this RESB_OUT.
     *
     * @param RETURNCODE
     */
    public void setRETURNCODE(String RETURNCODE) {
        this.RETURNCODE = RETURNCODE;
    }


    /**
     * Gets the WQSL value for this RESB_OUT.
     *
     * @return WQSL
     */
    public String getWQSL() {
        return WQSL;
    }


    /**
     * Sets the WQSL value for this RESB_OUT.
     *
     * @param WQSL
     */
    public void setWQSL(String WQSL) {
        this.WQSL = WQSL;
    }


    /**
     * Gets the RETURNMSG value for this RESB_OUT.
     *
     * @return RETURNMSG
     */
    public String getRETURNMSG() {
        return RETURNMSG;
    }


    /**
     * Sets the RETURNMSG value for this RESB_OUT.
     *
     * @param RETURNMSG
     */
    public void setRETURNMSG(String RETURNMSG) {
        this.RETURNMSG = RETURNMSG;
    }


    /**
     * Gets the KZEAR value for this RESB_OUT.
     *
     * @return KZEAR
     */
    public String getKZEAR() {
        return KZEAR;
    }


    /**
     * Sets the KZEAR value for this RESB_OUT.
     *
     * @param KZEAR
     */
    public void setKZEAR(String KZEAR) {
        this.KZEAR = KZEAR;
    }


    /**
     * Gets the RSNUM value for this RESB_OUT.
     *
     * @return RSNUM
     */
    public String getRSNUM() {
        return RSNUM;
    }


    /**
     * Sets the RSNUM value for this RESB_OUT.
     *
     * @param RSNUM
     */
    public void setRSNUM(String RSNUM) {
        this.RSNUM = RSNUM;
    }


    /**
     * Gets the RSPOS value for this RESB_OUT.
     *
     * @return RSPOS
     */
    public String getRSPOS() {
        return RSPOS;
    }


    /**
     * Sets the RSPOS value for this RESB_OUT.
     *
     * @param RSPOS
     */
    public void setRSPOS(String RSPOS) {
        this.RSPOS = RSPOS;
    }


    /**
     * Gets the XLOEK value for this RESB_OUT.
     *
     * @return XLOEK
     */
    public String getXLOEK() {
        return XLOEK;
    }


    /**
     * Sets the XLOEK value for this RESB_OUT.
     *
     * @param XLOEK
     */
    public void setXLOEK(String XLOEK) {
        this.XLOEK = XLOEK;
    }


    /**
     * Gets the XWAOK value for this RESB_OUT.
     *
     * @return XWAOK
     */
    public String getXWAOK() {
        return XWAOK;
    }


    /**
     * Sets the XWAOK value for this RESB_OUT.
     *
     * @param XWAOK
     */
    public void setXWAOK(String XWAOK) {
        this.XWAOK = XWAOK;
    }

    private Object __equalsCalc = null;
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof RESB_OUT)) return false;
        RESB_OUT other = (RESB_OUT) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true &&
            ((this.RETURNCODE==null && other.getRETURNCODE()==null) ||
             (this.RETURNCODE!=null &&
              this.RETURNCODE.equals(other.getRETURNCODE()))) &&
            ((this.WQSL==null && other.getWQSL()==null) ||
             (this.WQSL!=null &&
              this.WQSL.equals(other.getWQSL()))) &&
            ((this.RETURNMSG==null && other.getRETURNMSG()==null) ||
             (this.RETURNMSG!=null &&
              this.RETURNMSG.equals(other.getRETURNMSG()))) &&
            ((this.KZEAR==null && other.getKZEAR()==null) ||
             (this.KZEAR!=null &&
              this.KZEAR.equals(other.getKZEAR()))) &&
            ((this.RSNUM==null && other.getRSNUM()==null) ||
             (this.RSNUM!=null &&
              this.RSNUM.equals(other.getRSNUM()))) &&
            ((this.RSPOS==null && other.getRSPOS()==null) ||
             (this.RSPOS!=null &&
              this.RSPOS.equals(other.getRSPOS()))) &&
            ((this.XLOEK==null && other.getXLOEK()==null) ||
             (this.XLOEK!=null &&
              this.XLOEK.equals(other.getXLOEK()))) &&
            ((this.XWAOK==null && other.getXWAOK()==null) ||
             (this.XWAOK!=null &&
              this.XWAOK.equals(other.getXWAOK())));
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
        if (getRETURNCODE() != null) {
            _hashCode += getRETURNCODE().hashCode();
        }
        if (getWQSL() != null) {
            _hashCode += getWQSL().hashCode();
        }
        if (getRETURNMSG() != null) {
            _hashCode += getRETURNMSG().hashCode();
        }
        if (getKZEAR() != null) {
            _hashCode += getKZEAR().hashCode();
        }
        if (getRSNUM() != null) {
            _hashCode += getRSNUM().hashCode();
        }
        if (getRSPOS() != null) {
            _hashCode += getRSPOS().hashCode();
        }
        if (getXLOEK() != null) {
            _hashCode += getXLOEK().hashCode();
        }
        if (getXWAOK() != null) {
            _hashCode += getXWAOK().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RESB_OUT.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RESB_OUT"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RETURNCODE");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RETURNCODE"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("WQSL");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "WQSL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("RETURNMSG");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "RETURNMSG"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("KZEAR");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "KZEAR"));
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
        elemField.setFieldName("XLOEK");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "XLOEK"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("XWAOK");
        elemField.setXmlName(new javax.xml.namespace.QName("http://esb.primeton.com/sap-adaptor/rfc", "XWAOK"));
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
