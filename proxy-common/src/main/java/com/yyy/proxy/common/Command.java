package com.yyy.proxy.common;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yyy.proxy.common.client.DoorResponseCommand;
import com.yyy.proxy.common.client.ErrorResponseCommand;
import com.yyy.proxy.common.client.RegisterCommand;
import com.yyy.proxy.common.server.DoorRequestCommand;
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,include = JsonTypeInfo.As.PROPERTY,property = "command") 
@JsonSubTypes({  
    @JsonSubTypes.Type(value = RegisterCommand.class, name = "PROXY_REGISTER"),
    @JsonSubTypes.Type(value = DoorRequestCommand.class, name = "DOOR_REQUEST"),
    @JsonSubTypes.Type(value = DoorResponseCommand.class, name = "DOOR_RESPONSE"),
    @JsonSubTypes.Type(value = ErrorResponseCommand.class, name = "DOOR_ERROR_RESPONSE")}) 
public abstract class Command {
	private static ObjectMapper mapper = new ObjectMapper();
	
	public static Command deserialize(byte[] content) throws IOException{
		return mapper.readValue(content, Command.class);
	}
	public static Command deserializeS(String content) throws IOException{
		return mapper.readValue(content, Command.class);
	}
	public byte[] serialize() throws JsonProcessingException{
		return mapper.writeValueAsBytes(this);
	}
	public String serializeS() throws JsonProcessingException{
		return mapper.writeValueAsString(this);
	}
	@JsonIgnore
	abstract public String getSecret();
}
