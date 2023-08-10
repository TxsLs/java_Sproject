package org.filecut;

/**
 * <b>CutException。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */
public class CutException extends RuntimeException {

	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = 7294692343916525822L;
	
	public CutException() {
		super();
	}

	public CutException(String message,Throwable cause,boolean enableSupperession,boolean writableStackTrace) {
		super(message,cause,enableSupperession,writableStackTrace);
	}
	public CutException(String message,Throwable cause) {
		super(message,cause);
	}
	public CutException(String message) {
		super(message);
	}
	public CutException(Throwable cause) {
		super(cause);
	}
}
