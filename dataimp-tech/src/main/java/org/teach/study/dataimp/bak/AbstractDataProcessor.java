package org.teach.study.dataimp.bak;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.quincy.rock.core.cache.DefaultObjectCachePool;
import org.quincy.rock.core.cache.ObjectCachePool;
import org.quincy.rock.core.concurrent.Processor;
import org.quincy.rock.core.dao.ManualJdbcDaoSupport;
import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.util.MapUtil;
import org.quincy.rock.core.util.RockUtil;
import org.quincy.rock.core.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.teach.study.dataimp.DataReader;
import org.teach.study.dataimp.ImportException;
import org.teach.study.dataimp.Table;

/**
 * <b>数据处理器抽象基类。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * @version 1.0
 * @author quincy
 * @since 1.0
 */
public abstract class AbstractDataProcessor implements Processor<List<Map<String, Object>>> {
	/**
	 * logger。
	 */
	private static final Logger logger = RockUtil.getLogger(AbstractDataProcessor.class);

	/**
	 * TEMPLATE_TABLE_NAME。
	 */
	public static final String TEMPLATE_TABLE_NAME = "template_sql";

	/**
	 * 元数据缓冲。
	 * 缓冲sql语句、字段名、字段类型
	 */
	private final ObjectCachePool<Map<String, Object>> metaCache = new DefaultObjectCachePool<>();

	/**
	 * daoSupport。
	 */
	@Autowired
	protected ManualJdbcDaoSupport daoSupport;

	/**
	 * <b>构造方法。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 */
	public AbstractDataProcessor() {
		metaCache.setName(getClass().getSimpleName() + ".metaCache");
	}

	/** 
	 * process。
	 * @see org.quincy.rock.core.concurrent.Processor#process(java.lang.Object)
	 */
	@Override
	public final void process(List<Map<String, Object>> batchPackage) {
		//save
		try {
			Map<String, Object> row0 = batchPackage.get(0);
			Table table = MapUtil.getObject(row0, DataReader.METADATA_ROW_TABLE_KEY);
			String tableName = table.getTableName();
			Map<String, Object> metadata = createMetadata(tableName, row0);
			List<Object[]> dataList = new ArrayList<>(batchPackage.size());
			for (Map<String, Object> data : batchPackage) {
				Object[] arr = getDataValues(data, metadata);
				dataList.add(arr);
			}
			batchInsert(tableName, metadata, dataList);
		} catch (Exception ex) {
			logger.error("Batch data package saving failure!", ex);
			throw new ImportException(ex.getMessage(), ex);
		}
	}

	/**
	 * <b>创建元数据。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param tableName 表名
	 * @param row 行数据Map
	 * @return 元数据Map
	 */
	public Map<String, Object> createMetadata(String tableName, Map<String, Object> row) {
		Map<String, Object> metadata = metaCache.getBufferValue(tableName);
		if (metadata == null) {
			synchronized (metaCache) {
				metadata = metaCache.getBufferValue(tableName);
				if (metadata == null) {
					this.createTable(tableName);
					Map<String, Object> metaMap = new LinkedHashMap<>(); //保持原始顺序
					Table table = MapUtil.getObject(row, DataReader.METADATA_ROW_TABLE_KEY);
					row.forEach((fName, fValue) -> {
						DataType dt = table.getDataType(fName);
						if (dt == null || dt == DataType.UNKNOW) {
							dt = DataType.of(row.get(fName));
						}
						metaMap.put(fName, dt == null ? DataType.UNKNOW : dt);
					});
					metaMap.put("__fieldCount", metaMap.size());
					String insertSql = createInsertSql(tableName, metaMap);
					metaMap.put("__insertsql", insertSql);
					int[] sqlTypes = getSQLTypes(metaMap);
					metaMap.put("__sqlTypes", sqlTypes);
					metaCache.putBufferValue(tableName, metaMap);
					metadata = metaMap;
				}
			}
		}
		return metadata;
	}

	/**
	 * <b>批量插入一组数据。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param tableName 表名
	 * @param metadata 元数据map
	 * @param dataList 数据列表
	 */
	protected void batchInsert(String tableName, Map<String, Object> metadata, List<Object[]> dataList) {
		String insertSql = MapUtil.getString(metadata, "__insertsql");
		int[] sqlTypes = MapUtil.getObject(metadata, "__sqlTypes");
		daoSupport.batch(insertSql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Object[] values = dataList.get(i);
				int colIndex = 0;
				for (Object value : values) {
					colIndex++;
					int colType;
					if (sqlTypes.length < colIndex) {
						colType = SqlTypeValue.TYPE_UNKNOWN;
					} else {
						colType = sqlTypes[colIndex - 1];
					}
					if (colType == Types.BLOB && value instanceof Blob) {
						Blob v = (Blob) value;
						ps.setBlob(colIndex, v.getBinaryStream(), v.length());
					} else if (colType == Types.BINARY && value instanceof byte[]) {
						ps.setBytes(colIndex, (byte[]) value);
					} else if (colType == Types.CLOB && value instanceof Clob) {
						Clob v = (Clob) value;
						ps.setClob(colIndex, v.getCharacterStream(), v.length());
					} else {
						StatementCreatorUtils.setParameterValue(ps, colIndex, colType, value);
					}
				}
			}

			@Override
			public int getBatchSize() {
				return dataList.size();
			}
		});
	}

	//无论建表是否成功，都不会抛出异常
	private void createTable(String tableName) {
		String prefix = "create_table.";
		String sql = daoSupport.getNamedSql(prefix + tableName);
		if (!StringUtil.isBlank(sql)) {
			try {
				daoSupport.execute(sql);
			} catch (Exception e) {
				logger.warn("Failed to create a table.", e);
			}
		}
	}

	/**
	 * <b>根据元数据创建insert sql语句。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param tableName 表名
	 * @param metadata 元数据Map
	 * @return insert sql语句
	 */
	private static String createInsertSql(String tableName, Map<String, Object> metadata) {
		StringBuilder sb = new StringBuilder("insert into ");
		StringBuilder questions = new StringBuilder();
		sb.append(tableName);
		sb.append(" (");
		DataReader.forEach(metadata, (index, entry) -> {
			if (index != 0) {
				sb.append(StringUtil.CHAR_COMMA);
				questions.append(StringUtil.CHAR_COMMA);
			}
			sb.append(entry.getKey());
			questions.append('?');
		});
		sb.append(") values(");
		sb.append(questions);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * <b>获得sql参数类型。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param metadata 元数据Map
	 * @return sql参数类型
	 */
	private static int[] getSQLTypes(Map<String, Object> metadata) {
		int fieldCount = (Integer) metadata.get("__fieldCount");
		int[] sqlTypes = new int[fieldCount];
		DataReader.forEach(metadata, (index, entry) -> {
			DataType dt = (DataType) entry.getValue();
			sqlTypes[index] = dt.sqlType();
		});
		return sqlTypes;
	}

	/**
	 * <b>获得数据值数组。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param row 行数据Map
	 * @param metadata 元数据Map
	 * @return 数据值数组
	 */
	public static Object[] getDataValues(Map<String, Object> row, Map<String, Object> metadata) {
		int fieldCount = MapUtil.getInteger(metadata, "__fieldCount");
		Object[] dvs = new Object[fieldCount];
		DataReader.forEach(metadata, (index, entry) -> {
			dvs[index] = row.get(entry.getKey());
		});
		return dvs;
	}
}