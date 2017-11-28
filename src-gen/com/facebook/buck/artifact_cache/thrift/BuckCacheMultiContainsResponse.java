/**
 * Autogenerated by Thrift Compiler (0.10.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.facebook.buck.artifact_cache.thrift;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.10.0)", date = "2017-11-24")
public class BuckCacheMultiContainsResponse implements org.apache.thrift.TBase<BuckCacheMultiContainsResponse, BuckCacheMultiContainsResponse._Fields>, java.io.Serializable, Cloneable, Comparable<BuckCacheMultiContainsResponse> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("BuckCacheMultiContainsResponse");

  private static final org.apache.thrift.protocol.TField RESULTS_FIELD_DESC = new org.apache.thrift.protocol.TField("results", org.apache.thrift.protocol.TType.LIST, (short)1);
  private static final org.apache.thrift.protocol.TField STORES_LOOKED_UP_FIELD_DESC = new org.apache.thrift.protocol.TField("storesLookedUp", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new BuckCacheMultiContainsResponseStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new BuckCacheMultiContainsResponseTupleSchemeFactory();

  public java.util.List<ContainsResult> results; // optional
  public java.util.List<java.lang.String> storesLookedUp; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    RESULTS((short)1, "results"),
    STORES_LOOKED_UP((short)2, "storesLookedUp");

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
        case 1: // RESULTS
          return RESULTS;
        case 2: // STORES_LOOKED_UP
          return STORES_LOOKED_UP;
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
  private static final _Fields optionals[] = {_Fields.RESULTS,_Fields.STORES_LOOKED_UP};
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.RESULTS, new org.apache.thrift.meta_data.FieldMetaData("results", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ContainsResult.class))));
    tmpMap.put(_Fields.STORES_LOOKED_UP, new org.apache.thrift.meta_data.FieldMetaData("storesLookedUp", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING))));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(BuckCacheMultiContainsResponse.class, metaDataMap);
  }

  public BuckCacheMultiContainsResponse() {
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public BuckCacheMultiContainsResponse(BuckCacheMultiContainsResponse other) {
    if (other.isSetResults()) {
      java.util.List<ContainsResult> __this__results = new java.util.ArrayList<ContainsResult>(other.results.size());
      for (ContainsResult other_element : other.results) {
        __this__results.add(new ContainsResult(other_element));
      }
      this.results = __this__results;
    }
    if (other.isSetStoresLookedUp()) {
      java.util.List<java.lang.String> __this__storesLookedUp = new java.util.ArrayList<java.lang.String>(other.storesLookedUp);
      this.storesLookedUp = __this__storesLookedUp;
    }
  }

  public BuckCacheMultiContainsResponse deepCopy() {
    return new BuckCacheMultiContainsResponse(this);
  }

  @Override
  public void clear() {
    this.results = null;
    this.storesLookedUp = null;
  }

  public int getResultsSize() {
    return (this.results == null) ? 0 : this.results.size();
  }

  public java.util.Iterator<ContainsResult> getResultsIterator() {
    return (this.results == null) ? null : this.results.iterator();
  }

  public void addToResults(ContainsResult elem) {
    if (this.results == null) {
      this.results = new java.util.ArrayList<ContainsResult>();
    }
    this.results.add(elem);
  }

  public java.util.List<ContainsResult> getResults() {
    return this.results;
  }

  public BuckCacheMultiContainsResponse setResults(java.util.List<ContainsResult> results) {
    this.results = results;
    return this;
  }

  public void unsetResults() {
    this.results = null;
  }

  /** Returns true if field results is set (has been assigned a value) and false otherwise */
  public boolean isSetResults() {
    return this.results != null;
  }

  public void setResultsIsSet(boolean value) {
    if (!value) {
      this.results = null;
    }
  }

  public int getStoresLookedUpSize() {
    return (this.storesLookedUp == null) ? 0 : this.storesLookedUp.size();
  }

  public java.util.Iterator<java.lang.String> getStoresLookedUpIterator() {
    return (this.storesLookedUp == null) ? null : this.storesLookedUp.iterator();
  }

  public void addToStoresLookedUp(java.lang.String elem) {
    if (this.storesLookedUp == null) {
      this.storesLookedUp = new java.util.ArrayList<java.lang.String>();
    }
    this.storesLookedUp.add(elem);
  }

  public java.util.List<java.lang.String> getStoresLookedUp() {
    return this.storesLookedUp;
  }

  public BuckCacheMultiContainsResponse setStoresLookedUp(java.util.List<java.lang.String> storesLookedUp) {
    this.storesLookedUp = storesLookedUp;
    return this;
  }

  public void unsetStoresLookedUp() {
    this.storesLookedUp = null;
  }

  /** Returns true if field storesLookedUp is set (has been assigned a value) and false otherwise */
  public boolean isSetStoresLookedUp() {
    return this.storesLookedUp != null;
  }

  public void setStoresLookedUpIsSet(boolean value) {
    if (!value) {
      this.storesLookedUp = null;
    }
  }

  public void setFieldValue(_Fields field, java.lang.Object value) {
    switch (field) {
    case RESULTS:
      if (value == null) {
        unsetResults();
      } else {
        setResults((java.util.List<ContainsResult>)value);
      }
      break;

    case STORES_LOOKED_UP:
      if (value == null) {
        unsetStoresLookedUp();
      } else {
        setStoresLookedUp((java.util.List<java.lang.String>)value);
      }
      break;

    }
  }

  public java.lang.Object getFieldValue(_Fields field) {
    switch (field) {
    case RESULTS:
      return getResults();

    case STORES_LOOKED_UP:
      return getStoresLookedUp();

    }
    throw new java.lang.IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new java.lang.IllegalArgumentException();
    }

    switch (field) {
    case RESULTS:
      return isSetResults();
    case STORES_LOOKED_UP:
      return isSetStoresLookedUp();
    }
    throw new java.lang.IllegalStateException();
  }

  @Override
  public boolean equals(java.lang.Object that) {
    if (that == null)
      return false;
    if (that instanceof BuckCacheMultiContainsResponse)
      return this.equals((BuckCacheMultiContainsResponse)that);
    return false;
  }

  public boolean equals(BuckCacheMultiContainsResponse that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_results = true && this.isSetResults();
    boolean that_present_results = true && that.isSetResults();
    if (this_present_results || that_present_results) {
      if (!(this_present_results && that_present_results))
        return false;
      if (!this.results.equals(that.results))
        return false;
    }

    boolean this_present_storesLookedUp = true && this.isSetStoresLookedUp();
    boolean that_present_storesLookedUp = true && that.isSetStoresLookedUp();
    if (this_present_storesLookedUp || that_present_storesLookedUp) {
      if (!(this_present_storesLookedUp && that_present_storesLookedUp))
        return false;
      if (!this.storesLookedUp.equals(that.storesLookedUp))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetResults()) ? 131071 : 524287);
    if (isSetResults())
      hashCode = hashCode * 8191 + results.hashCode();

    hashCode = hashCode * 8191 + ((isSetStoresLookedUp()) ? 131071 : 524287);
    if (isSetStoresLookedUp())
      hashCode = hashCode * 8191 + storesLookedUp.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(BuckCacheMultiContainsResponse other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = java.lang.Boolean.valueOf(isSetResults()).compareTo(other.isSetResults());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetResults()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.results, other.results);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = java.lang.Boolean.valueOf(isSetStoresLookedUp()).compareTo(other.isSetStoresLookedUp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStoresLookedUp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.storesLookedUp, other.storesLookedUp);
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
    java.lang.StringBuilder sb = new java.lang.StringBuilder("BuckCacheMultiContainsResponse(");
    boolean first = true;

    if (isSetResults()) {
      sb.append("results:");
      if (this.results == null) {
        sb.append("null");
      } else {
        sb.append(this.results);
      }
      first = false;
    }
    if (isSetStoresLookedUp()) {
      if (!first) sb.append(", ");
      sb.append("storesLookedUp:");
      if (this.storesLookedUp == null) {
        sb.append("null");
      } else {
        sb.append(this.storesLookedUp);
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

  private static class BuckCacheMultiContainsResponseStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public BuckCacheMultiContainsResponseStandardScheme getScheme() {
      return new BuckCacheMultiContainsResponseStandardScheme();
    }
  }

  private static class BuckCacheMultiContainsResponseStandardScheme extends org.apache.thrift.scheme.StandardScheme<BuckCacheMultiContainsResponse> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, BuckCacheMultiContainsResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // RESULTS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list42 = iprot.readListBegin();
                struct.results = new java.util.ArrayList<ContainsResult>(_list42.size);
                ContainsResult _elem43;
                for (int _i44 = 0; _i44 < _list42.size; ++_i44)
                {
                  _elem43 = new ContainsResult();
                  _elem43.read(iprot);
                  struct.results.add(_elem43);
                }
                iprot.readListEnd();
              }
              struct.setResultsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // STORES_LOOKED_UP
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list45 = iprot.readListBegin();
                struct.storesLookedUp = new java.util.ArrayList<java.lang.String>(_list45.size);
                java.lang.String _elem46;
                for (int _i47 = 0; _i47 < _list45.size; ++_i47)
                {
                  _elem46 = iprot.readString();
                  struct.storesLookedUp.add(_elem46);
                }
                iprot.readListEnd();
              }
              struct.setStoresLookedUpIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, BuckCacheMultiContainsResponse struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.results != null) {
        if (struct.isSetResults()) {
          oprot.writeFieldBegin(RESULTS_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.results.size()));
            for (ContainsResult _iter48 : struct.results)
            {
              _iter48.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.storesLookedUp != null) {
        if (struct.isSetStoresLookedUp()) {
          oprot.writeFieldBegin(STORES_LOOKED_UP_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, struct.storesLookedUp.size()));
            for (java.lang.String _iter49 : struct.storesLookedUp)
            {
              oprot.writeString(_iter49);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class BuckCacheMultiContainsResponseTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public BuckCacheMultiContainsResponseTupleScheme getScheme() {
      return new BuckCacheMultiContainsResponseTupleScheme();
    }
  }

  private static class BuckCacheMultiContainsResponseTupleScheme extends org.apache.thrift.scheme.TupleScheme<BuckCacheMultiContainsResponse> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, BuckCacheMultiContainsResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetResults()) {
        optionals.set(0);
      }
      if (struct.isSetStoresLookedUp()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetResults()) {
        {
          oprot.writeI32(struct.results.size());
          for (ContainsResult _iter50 : struct.results)
          {
            _iter50.write(oprot);
          }
        }
      }
      if (struct.isSetStoresLookedUp()) {
        {
          oprot.writeI32(struct.storesLookedUp.size());
          for (java.lang.String _iter51 : struct.storesLookedUp)
          {
            oprot.writeString(_iter51);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, BuckCacheMultiContainsResponse struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list52 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.results = new java.util.ArrayList<ContainsResult>(_list52.size);
          ContainsResult _elem53;
          for (int _i54 = 0; _i54 < _list52.size; ++_i54)
          {
            _elem53 = new ContainsResult();
            _elem53.read(iprot);
            struct.results.add(_elem53);
          }
        }
        struct.setResultsIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list55 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRING, iprot.readI32());
          struct.storesLookedUp = new java.util.ArrayList<java.lang.String>(_list55.size);
          java.lang.String _elem56;
          for (int _i57 = 0; _i57 < _list55.size; ++_i57)
          {
            _elem56 = iprot.readString();
            struct.storesLookedUp.add(_elem56);
          }
        }
        struct.setStoresLookedUpIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

