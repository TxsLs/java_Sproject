package org.teach.study.dataimp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.text.StringEscapeUtils;
import org.quincy.rock.core.bean.BeanUtil;
import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.util.StringUtil;
import org.teach.study.dataimp.ParseFileException.FieldError;

/**
 * <b>CSVDataReader。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CSVDataReader implements DataReader {
	private File csvFile;
	private CSVFormat csvFormat;
	private Charset charset;
	private Table tableMeta;

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param csvFile csv文件
	 * @param csvFormat csv文件格式，允许为null
	 * @param cs 字符集，允许为null
	 * @param cfgFile 配置文件
	 * @throws IOException
	 */
	public CSVDataReader(File csvFile, CSVFormat csvFormat, Charset cs, File cfgFile) throws IOException {
		Objects.requireNonNull(csvFile);
		Objects.requireNonNull(cfgFile);
		this.csvFormat = csvFormat == null ? CSVFormat.DEFAULT : csvFormat;
		this.charset = cs == null ? Charset.defaultCharset() : cs;
		this.csvFile = csvFile;
		this.tableMeta = loadConfig(cfgFile);
	}

	private Table loadConfig(File cfgFile) throws IOException {
		//装载配置文件
		String str;
		Properties props = IOUtil.loadProperties(cfgFile, StringUtil.UTF_8.name());
		//csv		
		Builder builder = this.csvFormat.builder();
		str = props.getProperty("csv.ignoreEmptyLines");
		if (!StringUtil.isBlank(str)) {
			builder.setIgnoreEmptyLines(Boolean.parseBoolean(str));
		}
		str = props.getProperty("csv.recordSeparator");
		if (!StringUtil.isBlank(str)) {
			builder.setRecordSeparator(str);
		}
		str = props.getProperty("csv.delimiter");
		if (!StringUtil.isEmpty(str)) {
			builder.setDelimiter(str);
		}
		str = props.getProperty("csv.quote");
		if (!StringUtil.isBlank(str)) {
			builder.setQuote(str.charAt(0));
		}
		str = props.getProperty("csv.escape");
		if (!StringUtil.isBlank(str)) {
			builder.setEscape(str.charAt(0));
		}
		str = props.getProperty("csv.trim");
		if (!StringUtil.isBlank(str)) {
			builder.setTrim(Boolean.parseBoolean(str));
		}
		str = props.getProperty("csv.nullString");
		if (str != null) {
			builder.setNullString(str);
		}
		builder.setAllowMissingColumnNames(true);
		this.csvFormat = builder.build();

		//table
		Table table = new Table();
		table.tableIndex = 0;
		str = props.getProperty("table.tableName", FilenameUtils.getBaseName(csvFile.getName()));
		table.tableName = str;
		str = props.getProperty("table.firstRowIndex", "0");
		table.firstRowIndex = Long.valueOf(str);
		str = props.getProperty("table.lastRowIndex", "-1");
		table.lastRowIndex = Long.valueOf(str);
		str = props.getProperty("table.totalCount", "-1");
		table.totalCount = Long.valueOf(str);

		//field
		str = props.getProperty("table.headerRowIndex", "-1");
		int headerRowIndex = Integer.parseInt(str);
		Map<String, Field> fields = new LinkedHashMap<>();
		boolean hasHeader = headerRowIndex > -1;
		//先读csv文件中的标题行
		if (hasHeader) {
			this.forEach(headerRowIndex, headerRowIndex, (rowIndex, record) -> {
				for (int colIndex = 0, len = record.size(); colIndex < len; colIndex++) {
					String name = StringUtil.trimToEmpty(record.get(colIndex));
					if (!StringUtil.isEmpty(name)) {
						Field field = new Field();
						field.tableName = table.tableName;
						field.name = name;
						field.colIndex = colIndex;
						fields.put(name.toLowerCase(), field);
					}
				}
				return Boolean.FALSE;
			}, (ex) -> {
			});
		}
		//
		List<Field> fieldList = new ArrayList<>();
		String fieldPrefix = "table.field.";
		for (String key : props.stringPropertyNames()) {
			if (key.startsWith(fieldPrefix)) {
				String name = key.substring(fieldPrefix.length());
				final Field field = fields.containsKey(name.toLowerCase()) ? fields.get(name.toLowerCase())
						: new Field(-1);
				field.tableName = table.tableName;
				field.name = name;
				String value = props.getProperty(key);
				StringUtil.split(value, 0, value.length(), StringUtil.CHAR_COMMA, (index, ele) -> {
					if (index == 0) {
						String colIndex = StringUtil.trimToEmpty(ele);
						if (!(colIndex.length() == 0 || "-1".equals(colIndex)))
							field.colIndex = Integer.parseInt(colIndex);
					} else if (index == 1)
						field.dataType = DataType.of(StringUtil.trim(ele));
					else if (index == 2)
						field.format = StringUtil.trim(ele);
					else if (index == 3) {
						field.defValue = String.valueOf(ele);
					} else if (index == 4) {
						field.errValue = String.valueOf(ele);
					}
					return Boolean.FALSE;
				});
				fieldList.add(field);
			}
		}

		if (fieldList.size() == 0) {
			fieldList.addAll(fields.values());
		}

		table.fields = fieldList.toArray(new Field[fieldList.size()]);
		Arrays.sort(table.fields, Table.fieldComparator);

		//主键字段
		List<Field> pkFields = new ArrayList<>();
		str = props.getProperty("table.primaryFieldName", StringUtil.EMPTY);
		if (str.equals(StringUtil.EMPTY)) {
			str = props.getProperty("table.primaryFieldIndex", "-1");
			StringUtil.split(str, StringUtil.CHAR_COMMA, (i, ele) -> {
				int colIndex = Integer.parseInt(ele.toString());
				Field f = table.findField(colIndex);
				if (f != null)
					pkFields.add(f);
			});
		} else {
			StringUtil.split(str, StringUtil.CHAR_COMMA, (i, ele) -> {
				String colName = ele.toString();
				Field f = table.findField(colName);
				if (f != null)
					pkFields.add(f);
			});
		}
		table.primaryFields = pkFields.toArray(new Field[pkFields.size()]);

		return table;
	}

	/**
	 * <b>迭代数据行记录。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param firstRowIndex 首行索引(include)
	 * @param firstRowIndex 最后一行索引，如果不确定则为-1(include)
	 * @param callback 回调函数，返回true代表继续，否则中断
	 * @param catchParseError 捕获解析错误
	 * @return 返回true代表继续，否则中断
	 * @throws IOException
	 */
	private boolean forEach(long firstRowIndex, long lastRowIndex, BiFunction<Long, CSVRecord, Boolean> callback,
			Consumer<ParseFileException> catchParseError) throws IOException {
		if (lastRowIndex == -1)
			lastRowIndex = Long.MAX_VALUE;
		long index = 0;
		CSVParser parser = CSVParser.parse(csvFile, charset, csvFormat);
		try {
			for (CSVRecord record : parser) {
				if (index > lastRowIndex)
					break;
				if (index >= firstRowIndex) {
					Boolean cont = callback.apply(index, record);
					boolean cancel = !Boolean.TRUE.equals(cont);
					if (cancel)
						return false;
				}
				index++;
			}
		} catch (ParseFileException e) {
			catchParseError.accept(e);
		} catch (IllegalStateException e) {
			ParseFileException pe = new ParseFileException(csvFile.getAbsolutePath(), index, e);
			catchParseError.accept(pe);
		} finally {
			IOUtil.closeQuietly(parser);
		}
		return true;
	}

	/** 
	 * close。
	 * @see java.io.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
	}

	/** 
	 * getRowCount。
	 * @see org.teach.study.dataimp.DataReader#getRowCount()
	 */
	@Override
	public long getRowCount() throws IOException {
		if (tableMeta.totalCount > 0)
			return tableMeta.totalCount;
		MutableLong rowCount = new MutableLong(0);
		this.forEach(tableMeta.firstRowIndex, tableMeta.lastRowIndex, (index, record) -> {
			rowCount.increment();
			return Boolean.TRUE;
		}, (ex) -> {
		});
		return rowCount.longValue();
	}

	/** 
	 * forEach。
	 * @see org.teach.study.dataimp.DataReader#forEach(java.util.function.Function)
	 */
	@Override
	public void forEach(Function<String, Boolean> callback) throws IOException {
		this.forEach(tableMeta.firstRowIndex, tableMeta.lastRowIndex, (index, record) -> {
			StringBuilder sb = joinRowString(record, new StringBuilder());
			return callback.apply(sb.toString());
		}, (ex) -> {
		});
	}

	/** 
	 * forEach。
	 * @see org.teach.study.dataimp.DataReader#forEach(java.util.function.Consumer, java.util.function.Consumer)
	 */
	@Override
	public void forEach(Consumer<Map<String, Object>> callback, Consumer<ParseFileException> catchParseError)
			throws IOException {
		Map<Integer, Field> fieldMap = (Map) BeanUtil.getMap(Arrays.asList(tableMeta.fields), "colIndex", null, false);
		this.forEach(tableMeta.firstRowIndex, tableMeta.lastRowIndex, (rowIndex, record) -> {
			parseRowRecord(tableMeta, fieldMap, rowIndex, record, callback, catchParseError);
			return Boolean.TRUE;
		}, catchParseError);
	}

	private void parseRowRecord(Table table, Map<Integer, Field> fieldMap, long rowIndex, CSVRecord record,
			Consumer<Map<String, Object>> callback, Consumer<ParseFileException> catchParseError) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(METADATA_ROW_RECORDNUMBER_KEY, record.getRecordNumber());
		map.put(METADATA_ROW_TABLE_KEY, table);
		for (int colIndex = 0, len = record.size(); colIndex < len; colIndex++) {
			Field field = fieldMap.get(colIndex);
			if (field != null) {
				String colName = field.getName();
				String strValue = record.get(colIndex);
				DataType dataType = field == null ? null : field.dataType;
				Format fmt = field == null ? null : field.format();
				try {
					Object value = (strValue == null || dataType == null || dataType == DataType.STRING
							|| dataType == DataType.CLOB || dataType == DataType.UNKNOW) ? strValue
									: dataType.parse(strValue, fmt, true);
					map.put(colName, value);
				} catch (Exception e) {
					FieldError errField = new FieldError(field.colIndex, field.name, strValue, e);
					String line = joinRowString(record, new StringBuilder()).toString();
					ParseFileException ex = new ParseFileException(csvFile.getAbsolutePath(), rowIndex, line, errField);
					catchParseError.accept(ex);
				}
			}
		}
		callback.accept(map);
	}

	/**
	 * <b>拼接行字符串。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param record 行记录
	 * @param buf 缓冲区
	 * @return 缓冲区
	 */
	public static StringBuilder joinRowString(CSVRecord record, StringBuilder buf) {
		String str;
		Iterator<String> iter = record.iterator();

		if (iter.hasNext()) {
			str = StringEscapeUtils.escapeCsv(iter.next());
			buf.append(str == null ? StringUtil.EMPTY : str);
		}
		while (iter.hasNext()) {
			buf.append(StringUtil.CHAR_COMMA);
			str = StringEscapeUtils.escapeCsv(iter.next());
			buf.append(str == null ? StringUtil.EMPTY : str);
		}
		return buf;
	}
}
