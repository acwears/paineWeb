package main.java.com.paine.core.util;

import java.io.Serializable;

/***
 * Json Message Result
 * 
 * @author Fernando Arruda
 *
 */
public class JsonMessageResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public enum State {
		SUCCESS, WARNNING, ERROR, FATAL
	}

	private State state;
	private String message;
	private Object result;

	public JsonMessageResult() {
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public JsonMessageResult result(Object result) {
		this.result = result;
		return this;
	}

	public JsonMessageResult message(String text) {
		this.message = text;
		return this;
	}

	public static JsonMessageResult success() {
		JsonMessageResult mr = new JsonMessageResult();
		mr.state = State.SUCCESS;
		return mr;
	}
	
	public static JsonMessageResult success(String success) {
		JsonMessageResult mr = new JsonMessageResult();
		mr.state = State.SUCCESS;
		mr.message = success;
		return mr;
	}	

	public static JsonMessageResult warnning() {
		JsonMessageResult mr = new JsonMessageResult();
		mr.state = State.WARNNING;
		return mr;
	}

	public static JsonMessageResult error() {
		JsonMessageResult mr = new JsonMessageResult();
		mr.state = State.ERROR;
		return mr;
	}

	public static JsonMessageResult error(String error) {
		JsonMessageResult mr = new JsonMessageResult();
		mr.state = State.ERROR;
		mr.message = error;		
		return mr;
	}

	public static JsonMessageResult fatal() {
		JsonMessageResult mr = new JsonMessageResult();
		mr.state = State.FATAL;
		return mr;
	}

//	public String toJson() {
//		return new Gson().toJson(this);
//	}
}