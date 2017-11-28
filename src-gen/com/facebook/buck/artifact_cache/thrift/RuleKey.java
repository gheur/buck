/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.facebook.buck.artifact_cache.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-11-24")
public class RuleKey implements org.apache.thrift.TBase<RuleKey, RuleKey._Fields>, java.io.Serializable, Cloneable, Comparable<RuleKey> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("RuleKey");

  private static final org.apache.thrift.protocol.TField HASH_STRING_FIELD_DESC = new org.apache.thrift.protocol.TField("hashString", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new RuleKeyStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new RuleKeyTupleSchemeFactory();

  public java.lang.String hashString; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    HASH_STRING((short)1, "hashString");

    private static final java.util.Map<java.lang.String, _Fields> byName = new java.util.HashMap<java.lang.String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // HASH_STRING
          return HASH_STRING;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new java.lang.IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(java.lang.String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final java.lang.String _fieldName;

    _Fields(short thriftId, java.lang.String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public java.lang.String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final _Fields optionals[] = {_Fields.HASH_STRING};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.HASH_STRING, new org.apache.thrift.meta_data.FieldMetaData("hashString", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(RuleKey.class, metaDataMap);
  }

  public RuleKey() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public RuleKey(RuleKey other) {
    if (other.isSetHashString()) {
      this.hashString = other.hashString;
    }
  }

  public RuleKey deepCopy() {
    return new RuleKey(this);
  }

  @Override
  public void clear() {
    this.hashString = null;
  }

  public java.lang.String getHashString() {
    return this.hashString;
  }

  public RuleKey setHashString(java.lang.String hashString) {
    this.hashString = hashString;
    return this;
  }

  public void unsetHashString() {
    this.hashString = null;
  }

  /** Returns true if field hashString is set (has been assigned a value) and false otherwise */
  public boolean isSetHashString() {
    return this.hashString != null;
  }

  public void setHashStringIsSet(boolean value) {
    if (!value) {
      this.hashString = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case HASH_STRING:
      if (value == null) {
        unsetHashString();
      } else {
        setHashString((java.lang.String)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case HASH_STRING:
      return getHashString();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case HASH_STRING:
      return isSetHashString();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof RuleKey)
      return this.equals((RuleKey)that);
    return false;
  }

  public boolean equals(RuleKey that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_hashString = true && this.isSetHashString();
    boolean that_present_hashString = true && that.isSetHashString();
    if (this_present_hashString || that_present_hashString) {
      if (!(this_present_hashString && that_present_hashString))
        return false;
      if (!this.hashString.equals(that.hashString))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetHashString()) ? 131071 : 524287);
    if (isSetHashString())
      hashCode = hashCode * 8191 + hashString.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(RuleKey other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetHashString()).compareTo(other.isSetHashString());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetHashString()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.hashString, other.hashString);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public java.lang.String toString() {
    java.lang.StringBuilder sb = new java.lang.StringBuilder("RuleKey(");
    boolean first = true;

    if (isSetHashString()) {
      sb.append("hashString:");
      if (this.hashString == null) {
        sb.append("null");
      } else {
        sb.append(this.hashString);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, java.lang.ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class RuleKeyStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RuleKeyStandardScheme getScheme() {
      return new RuleKeyStandardScheme();
    }
  }

  private static class RuleKeyStandardScheme extends org.apache.thrift.scheme.StandardScheme<RuleKey> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, RuleKey struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // HASH_STRING
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.hashString = iprot.readString();
              struct.setHashStringIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, RuleKey struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.hashString != null) {
        if (struct.isSetHashString()) {
          oprot.writeFieldBegin(HASH_STRING_FIELD_DESC);
          oprot.writeString(struct.hashString);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class RuleKeyTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public RuleKeyTupleScheme getScheme() {
      return new RuleKeyTupleScheme();
    }
  }

  private static class RuleKeyTupleScheme extends org.apache.thrift.scheme.TupleScheme<RuleKey> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, RuleKey struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetHashString()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetHashString()) {
        oprot.writeString(struct.hashString);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, RuleKey struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.hashString = iprot.readString();
        struct.setHashStringIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

