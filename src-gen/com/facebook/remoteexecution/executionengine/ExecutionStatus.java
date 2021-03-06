/**
 * Autogenerated by Thrift Compiler (0.11.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.facebook.remoteexecution.executionengine;


public enum ExecutionStatus implements org.apache.thrift.TEnum {
  UNKNOWN(0),
  OK(1),
  CANCELED(2),
  TIMEOUT(3);

  private final int value;

  private ExecutionStatus(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
  public int getValue() {
    return value;
  }

  /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
  public static ExecutionStatus findByValue(int value) { 
    switch (value) {
      case 0:
        return UNKNOWN;
      case 1:
        return OK;
      case 2:
        return CANCELED;
      case 3:
        return TIMEOUT;
      default:
        return null;
    }
  }
}
