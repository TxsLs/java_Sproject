package org.stu.dataimp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.quincy.rock.core.bean.BeanUtil;
import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.util.MapUtil;
import org.quincy.rock.core.util.StringUtil;

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
public class CSVDataReader implements DataReader {
	protected File csvFile;
	protected CSVFormat csvFormat;
	protected Charset charset;

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param csvFile csv文件
	 * @param csvFormat csv文件格式，允许为null
	 * @param cs 字符集，允许为null
	 * @param cfgFile 配置文件，允许为null
	 * @throws IOException
	 */
	public CSVDataReader(File csvFile, CSVFormat csvFormat, Charset cs, File cfgFile) throws IOException {
		this.csvFile = csvFile;
		this.csvFormat = csvFormat == null ? CSVFormat.DEFAULT : csvFormat;
		this.charset = cs == null ? Charset.defaultCharset() : cs;
		loadConfig(cfgFile);
	}

	protected Map<String, Object> loadConfig(File cfgFile) throws IOException {
		//装载配置文件
		Map<String, Object> config = new HashMap<>();
		int maxIndex;
		if (cfgFile != null && cfgFile.isFile()) {
			maxIndex = loadConfig0(cfgFile, config);
		} else {
			maxIndex = this.loadEmptyConfig(config);
		}
		if (maxIndex > 0)
			this.initCSVHeader(maxIndex, config);
		return config;
	}

	//装载配置文件
	private int loadConfig0(File cfgFile, Map<String, Object> config) throws IOException {
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
		this.csvFormat = builder.build();

		//table
		Table table = new Table();
		table.tableIndex = 0;
		str = props.getProperty("table.tableName", FilenameUtils.getBaseName(csvFile.getName()));
		table.tableName = str;
		str = props.getProperty("table.tableDescr", table.tableName);
		table.tableDescr = str;
		str = props.getProperty("table.firstRowIndex", "0");
		table.firstRowIndex = Long.valueOf(str);
		str = props.getProperty("table.lastRowIndex", "-1");
		table.lastRowIndex = Long.valueOf(str);
		str = props.getProperty("table.totalCount", "-1");
		table.totalCount = Long.valueOf(str);

		//field
		str = props.getProperty("table.headerRowIndex", "-1");
		int headerRowIndex = Integer.parseInt(str);
		str = props.getProperty("table.titleRowIndex", "-1");
		int titleRowIndex = Integer.parseInt(str);
		Map<String, Field> fields = new LinkedHashMap<>();
		List<String> titles = new ArrayList<String>();

		boolean hasHeader = headerRowIndex > -1;
		boolean hasTitle = titleRowIndex > -1;

		if (hasTitle || hasHeader) {
			int maxRowIndex = Math.max(headerRowIndex, titleRowIndex);
			this.forEach(0, maxRowIndex, (index, record) -> {
				if (hasTitle && index == titleRowIndex) {
					record.forEach((title) -> titles.add(StringUtil.trimToEmpty(title)));
				}
				if (hasHeader && index == headerRowIndex) {
					for (int j = 0, l = record.size(); j < l; j++) {
						String name = StringUtil.trimToEmpty(record.get(j));
						if (!StringUtil.isEmpty(name)) {
							Field field = new Field();
							field.tableName = table.tableName;
							field.name = name;
							field.colIndex = j;
							fields.put(name.toLowerCase(), field);
						}
					}
				}
				return Boolean.TRUE;
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

		int maxColIndex = 0;
		for (Field field : fieldList) {
			maxColIndex = Math.max(maxColIndex, field.colIndex);
			if (hasTitle) {
				String title = titles.get(field.colIndex);
				if (!StringUtil.isEmpty(title)) {
					field.descr = title;
				}
			}
		}

		table.fields = fieldList.toArray(new Field[fieldList.size()]);
		Arrays.sort(table.fields, Table.fieldComparator);

		//primaryField
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

		config.put(METADATA_TABLES_KEY, new Table[] { table });
		return maxColIndex;
	}

	//装载空配置
	private int loadEmptyConfig(Map<String, Object> config) throws IOException {
		//csvFormat
		Builder builder = this.csvFormat.builder();
		builder.setIgnoreEmptyLines(true);
		builder.setAllowMissingColumnNames(true);
		this.csvFormat = builder.build();
		//table
		Table table = new Table();
		table.tableIndex = 0;
		table.tableName = FilenameUtils.removeExtension(csvFile.getName());
		table.tableDescr = table.tableName;
		table.firstRowIndex = 1;
		table.lastRowIndex = -1;
		table.totalCount = -1;

		//field
		MutableInt maxIndex = new MutableInt(0);
		List<Field> fieldList = new ArrayList<Field>();
		this.forEach(0, 0, (index, record) -> {
			for (int j = 0, l = record.size(); j < l; j++) {
				String name = record.get(j);
				if (!StringUtil.isEmpty(name)) {
					Field field = new Field();
					field.name = name;
					field.colIndex = j;
					fieldList.add(field);
					maxIndex.setValue(Math.max(maxIndex.intValue(), field.colIndex));
				}
			}
			return Boolean.FALSE;
		}, (ex) -> {
		});
		table.fields = fieldList.toArray(new Field[fieldList.size()]);
		config.put(METADATA_TABLES_KEY, new Table[] { table });
		return maxIndex.intValue();
	}

	private void initCSVHeader(int maxIndex, Map<String, Object> config) {
		//withHeaderName
		Builder builder = this.csvFormat.builder();
		Table table = getTable(config);
		List<Field> fieldList = Arrays.asList(table.fields);
		if (!fieldList.isEmpty()) {
			String[] headerNames = new String[maxIndex + 1];
			Map<Integer, String> map = (Map) BeanUtil.getMap(fieldList, "colIndex", "name", false);
			for (int i = 0; i <= maxIndex; i++) {
				String name = map.get(i);
				if (!StringUtil.isBlank(name))
					headerNames[i] = name;
			}
			builder.setHeader(headerNames);
		}
		this.csvFormat = builder.build();
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
	protected boolean forEach(long firstRowIndex, long lastRowIndex, BiFunction<Long, CSVRecord, Boolean> callback,
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
		// TODO Auto-generated method stub

	}

	/** 
	 * getRowCount。
	 * @see org.teach.study.dataimp.DataReader#getRowCount()
	 */
	@Override
	public long getRowCount() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	/** 
	 * getConfig。
	 * @see org.teach.study.dataimp.DataReader#getConfig()
	 */
	@Override
	public Map<String, Object> getConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	/** 
	 * forEach。
	 * @see org.teach.study.dataimp.DataReader#forEach(java.util.function.Function)
	 */
	@Override
	public void forEach(Function<String, Boolean> callback) throws IOException {
		// TODO Auto-generated method stub

	}

	/** 
	 * forEach。
	 * @see org.teach.study.dataimp.DataReader#forEach(java.util.function.Consumer, java.util.function.Consumer)
	 */
	@Override
	public void forEach(Consumer<Map<String, Object>> callback, Consumer<ParseFileException> catchParseError)
			throws IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * <b>获得表元数据信息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param config 元数据配置
	 * @return Table
	 */
	public static Table getTable(Map<String, Object> config) {
		Table[] tables = MapUtil.getObject(config, METADATA_TABLES_KEY);
		return tables[0];
	}
}
