package org.teach.study.dataimp;

import org.apache.commons.lang3.ArrayUtils;
import org.quincy.rock.core.vo.Vo;

/**
 * <b>文件行记录解析异常。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * @version 1.0
 * @author quincy
 * @since 1.0
 */
public class ParseFileException extends RuntimeException {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = 5222554569231579753L;

	//字段值解析错误
	public static class FieldError extends Vo<Integer> {
		/**
		 * serialVersionUID。
		 */
		private static final long serialVersionUID = -6126940544821932529L;
		private int index;
		private String name;
		private String value;
		private Exception cause;

		public FieldError(int index, String name, String value, Exception cause) {
			this.index = index;
			this.name = name;
			this.value = value;
			this.cause = cause;
		}

		@Override
		public Integer id() {
			return index;
		}

		public int getIndex() {
			return index;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public Exception getCause() {
			return cause;
		}

		public String getErrorMessage() {
			StringBuilder sb = new StringBuilder();
			sb.append("Error parsing field [");
			sb.append(this.getName());
			sb.append("](colIndex ");
			sb.append(this.getIndex());
			sb.append("):");
			sb.append(this.getValue());
			sb.append(".");
			if (this.getCause() != null) {
				sb.append(createMessage(this.getCause()));
			}
			return sb.toString();
		}
	}

	/**
	 * 行索引。
	 */
	private long rowIndex;
	/**
	 * 完整的出错行。
	 */
	private String line;
	/**
	 * 错误字段信息数组。
	 */
	private FieldError[] errFields;

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param fileName 文件名
	 * @param rowIndex 行索引
	 * @param cause 异常原因
	 */
	public ParseFileException(String fileName, long rowIndex, Throwable cause) {
		super(createMessage(fileName, rowIndex, cause), cause);
		this.rowIndex = rowIndex;
	}

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param fileName 文件名
	 * @param rowIndex 行索引
	 * @param line 完整的出错行
	 * @param errFields 错误字段信息数组
	 */
	public ParseFileException(String fileName, long rowIndex, String line, FieldError... errFields) {
		super(createMessage(fileName, rowIndex, errFields));
		this.rowIndex = rowIndex;
		this.line = line;
		this.errFields = errFields;
	}

	/**
	 * <b>获得行索引。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 行索引
	 */
	public long getRowIndex() {
		return rowIndex;
	}

	/**
	 * <b>获得出错行文本字符串。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 出错行文本字符串
	 */
	public String getLine() {
		return line;
	}

	/**
	 * <b>获得错误字段信息数组。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 错误字段信息数组
	 */
	public FieldError[] getErrFields() {
		return errFields;
	}

	/**
	 * <b>有行字符串。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 有行字符串意味着可以解析出完整行。
	 * @return 有行字符串
	 */
	public boolean hasLine() {
		return line != null;
	}

	private static String createMessage(String fileName, long rowIndex, FieldError... errFields) {
		StringBuilder sb = new StringBuilder();
		sb.append("Error parsing [");
		sb.append(fileName);
		sb.append("] file(line ");
		sb.append(rowIndex + 1);
		sb.append(").");
		if (ArrayUtils.isNotEmpty(errFields)) {
			sb.append("errFields:[");
			for (int i = 0, l = errFields.length; i < l; i++) {
				if (i != 0)
					sb.append(";");
				sb.append("[");
				sb.append(errFields[i].getErrorMessage());
				sb.append("]");
			}
			sb.append("]");
		}
		return sb.toString();
	}

	private static String createMessage(String fileName, long rowIndex, Throwable cause) {
		StringBuilder sb = new StringBuilder();
		sb.append("Error parsing [");
		sb.append(fileName);
		sb.append("] file(line ");
		sb.append(rowIndex + 1);
		sb.append(").");
		if (cause != null) {
			sb.append(createMessage(cause));
		}
		return sb.toString();
	}

	private static String createMessage(Throwable cause) {
		StringBuilder sb = new StringBuilder();
		sb.append("error:[");
		sb.append(cause.getMessage());
		sb.append("].");
		cause = cause.getCause();
		if (cause != null) {
			sb.append("cause:[");
			sb.append(cause.getMessage());
			sb.append("].");
		}
		return sb.toString();
	}
}
