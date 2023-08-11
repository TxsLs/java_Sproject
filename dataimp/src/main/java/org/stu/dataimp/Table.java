package org.stu.dataimp;

import java.util.Comparator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.vo.Vo;

/**
 * <b>Table。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author quincy
 * @since 1.0
 */
public class Table extends Vo<String> {
	/**
	 * serialVersionUID。
	 */
	private static final long serialVersionUID = -929895414746525177L;

	public static final Comparator<Field> fieldComparator = new Comparator<Field>() {

		@Override
		public int compare(Field f1, Field f2) {
			if (f1.colIndex == f2.colIndex)
				return 0;
			else if (f1.colIndex == -1)
				return 1;
			else if (f2.colIndex == -1)
				return -1;
			else
				return f1.colIndex - f1.colIndex;
		}
	};

	int tableIndex;
	String tableName;
	String tableDescr;
	long firstRowIndex;
	long lastRowIndex;
	Field[] fields;
	Field[] primaryFields;
	/**
	 * 文件中总共记录数。
	 */
	long totalCount;

	/** 
	 * id。
	 * @see org.quincy.rock.core.vo.Vo#id()
	 */
	@Override
	public String id() {
		return tableName;
	}

	public int getTableIndex() {
		return tableIndex;
	}

	public void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDescr() {
		return tableDescr;
	}

	public void setTableDescr(String tableDescr) {
		this.tableDescr = tableDescr;
	}

	public long getFirstRowIndex() {
		return firstRowIndex;
	}

	public void setFirstRowIndex(long firstRowIndex) {
		this.firstRowIndex = firstRowIndex;
	}

	public long getLastRowIndex() {
		return lastRowIndex;
	}

	public void setLastRowIndex(long lastRowIndex) {
		this.lastRowIndex = lastRowIndex;
	}

	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

	/**
	 * <b>查找字段。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param colIndex 列索引
	 * @return Field
	 */
	public Field findField(int colIndex) {
		if (ArrayUtils.isNotEmpty(fields)) {
			for (int i = 0, l = fields.length; i < l; i++) {
				Field f = fields[i];
				if (f.colIndex == colIndex)
					return f;
			}
		}
		return null;
	}

	/**
	 * <b>查找字段。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param colName 列名称
	 * @return Field
	 */
	public Field findField(String colName) {
		if (ArrayUtils.isNotEmpty(fields)) {
			for (int i = 0, l = fields.length; i < l; i++) {
				Field f = fields[i];
				if (f.name.equalsIgnoreCase(colName))
					return f;
			}
		}
		return null;
	}

	/**
	 * <b>获得主键字段。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 主键字段
	 */
	public Field[] getPrimaryFields() {
		return this.primaryFields;
	}

	/**
	 * <b>设置主键字段。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param primaryFields 主键字段
	 */
	public void setPrimaryFields(Field[] primaryFields) {
		this.primaryFields = primaryFields;
	}

	/**
	 * <b>获得字段的数据类型。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param name 字段名
	 * @return 数据类型
	 */
	public DataType getDataType(String name) {
		DataType dt = DataType.UNKNOW;
		if (!ArrayUtils.isEmpty(fields)) {
			for (Field f : fields) {
				if (StringUtils.equals(name, f.name)) {
					dt = f.dataType;
					break;
				}
			}
		}
		return dt;
	}
}
