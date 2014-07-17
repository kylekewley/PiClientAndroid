package com.kylekewley.piclient.protocolbuffers;// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ProtocolBuffers/PiHeader.proto
import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Datatype.UINT32;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class PiHeader extends Message {

  public static final Integer DEFAULT_MESSAGELENGTH = 0;
  public static final Integer DEFAULT_PARSERID = 0;
  public static final Integer DEFAULT_MESSAGEID = 0;
  public static final Integer DEFAULT_FLAGS = 0;
  public static final Boolean DEFAULT_SUCCESSRESPONSE = false;

  @ProtoField(tag = 1, type = UINT32, label = REQUIRED)
  public final Integer messageLength;

  @ProtoField(tag = 2, type = UINT32, label = REQUIRED)
  public final Integer parserID;

  @ProtoField(tag = 3, type = UINT32)
  public final Integer messageID;

  @ProtoField(tag = 4, type = UINT32)
  public final Integer flags;

  @ProtoField(tag = 5, type = BOOL)
  public final Boolean successResponse;

  public PiHeader(Integer messageLength, Integer parserID, Integer messageID, Integer flags, Boolean successResponse) {
    this.messageLength = messageLength;
    this.parserID = parserID;
    this.messageID = messageID;
    this.flags = flags;
    this.successResponse = successResponse;
  }

  private PiHeader(@NotNull Builder builder) {
    this(builder.messageLength, builder.parserID, builder.messageID, builder.flags, builder.successResponse);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof PiHeader)) return false;
    PiHeader o = (PiHeader) other;
    return equals(messageLength, o.messageLength)
        && equals(parserID, o.parserID)
        && equals(messageID, o.messageID)
        && equals(flags, o.flags)
        && equals(successResponse, o.successResponse);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = messageLength != null ? messageLength.hashCode() : 0;
      result = result * 37 + (parserID != null ? parserID.hashCode() : 0);
      result = result * 37 + (messageID != null ? messageID.hashCode() : 0);
      result = result * 37 + (flags != null ? flags.hashCode() : 0);
      result = result * 37 + (successResponse != null ? successResponse.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<PiHeader> {

    public Integer messageLength;
    public Integer parserID;
    public Integer messageID;
    public Integer flags;
    public Boolean successResponse;

    public Builder() {
    }

    public Builder(@Nullable PiHeader message) {
      super(message);
      if (message == null) return;
      this.messageLength = message.messageLength;
      this.parserID = message.parserID;
      this.messageID = message.messageID;
      this.flags = message.flags;
      this.successResponse = message.successResponse;
    }

    @NotNull
    public Builder messageLength(Integer messageLength) {
      this.messageLength = messageLength;
      return this;
    }

    @NotNull
    public Builder parserID(Integer parserID) {
      this.parserID = parserID;
      return this;
    }

    @NotNull
    public Builder messageID(Integer messageID) {
      this.messageID = messageID;
      return this;
    }

    @NotNull
    public Builder flags(Integer flags) {
      this.flags = flags;
      return this;
    }

    @NotNull
    public Builder successResponse(Boolean successResponse) {
      this.successResponse = successResponse;
      return this;
    }

    @NotNull
    @Override
    public PiHeader build() {
      checkRequiredFields();
      return new PiHeader(this);
    }
  }
}
