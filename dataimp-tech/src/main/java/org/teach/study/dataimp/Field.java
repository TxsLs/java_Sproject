package org.teach.study.dataimp;

import java.text.Format;

import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.util.StringUtil;
import org.quincy.rock.core.vo.Vo;

/**
 * <b>Field。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author quincy
 * @since 1.0
 */
public class Field extends Vo<String> {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -7544686815638035650L;

	String tableName;
	int colIndex;
	String name;
	DataType dataType;
	String descr;
	String format;
	String defValue;
	String errValue;

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 */
	public Field() {
	}

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param colIndex 列索引
	 */
	public Field(int colIndex) {
		this.colIndex = colIndex;
	}

	/** 
	 * id。
	 * @see org.quincy.rock.core.vo.Vo#id()
	 */
	@Override
	public String id() {
		return name;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getColIndex() {
		return colIndex;
	}

	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDefValue() {
		return defValue;
	}

	public void setDefValue(String defValue) {
		this.defValue = defValue;
	}

	/**
	 * <b>是否定义了缺省值。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 是否定义了缺省值
	 */
	public boolean hasDefValue() {
		return !StringUtil.isEmpty(defValue);
	}

	public String getErrValue() {
		return errValue;
	}

	public void setErrValue(String errValue) {
		this.errValue = errValue;
	}

	/**
	 * <b>是否定义了错误替代值。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 是否定义了错误替代值
	 */
	public boolean hasErrValue() {
		return !StringUtil.isEmpty(errValue);
	}

	/**
	 * <b>返回错误替代值。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 返回错误替代值
	 */
	public Object errValue() {
		return parseValue(errValue);
	}

	/**
	 * <b>返回缺省值。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 缺省值
	 */
	public Object defValue() {
		return parseValue(defValue);
	}

	private Object parseValue(String value) {
		if ("null".equals(value))
			return null;
		else if ("empty".endsWith(value))
			return "";
		else if ("\"null\"".equals(value))
			return "null";
		else if ("\"empty\"".equals(value))
			return "empty";
		else if (dataType == null || dataType == DataType.STRING || dataType == DataType.CLOB)
			return value;
		else
			return dataType.parse(value, getFormat(), true);
	}

	/**
	 * <b>返回Format。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return Format
	 */
	public Format format() {
		if (dataType == null || StringUtil.isBlank(format))
			return null;
		if (fmt == null) {
			fmt = dataType.getFormat(format);
		}
		return fmt;
	}

	//
	private Format fmt;
}
