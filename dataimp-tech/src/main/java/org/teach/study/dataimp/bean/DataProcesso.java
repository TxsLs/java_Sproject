package org.teach.study.dataimp.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.quincy.rock.core.concurrent.Processor;
import org.quincy.rock.core.dao.ManualJdbcDaoSupport;
import org.quincy.rock.core.lang.DataType;
import org.quincy.rock.core.util.MapUtil;
import org.quincy.rock.core.util.RockUtil;
import org.quincy.rock.core.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.teach.study.dataimp.DataReader;
import org.teach.study.dataimp.ImportException;
import org.teach.study.dataimp.Table;

@Component
public class DataProcesso implements Processor<List<Map<String, Object>>> {
	private static final Logger logger = RockUtil.getLogger(DataProcesso.class);
	@Autowired
	
private ManualJdbcDaoSupport  daoSupport;
	private Map<String, Object> cache_meta;
	@Override
	@Transactional
	public void process(List<Map<String, Object>> pkg) {
		//save
				try {
		Map<String,Object>map0=pkg.get(0);
		Table table = MapUtil.getObject(map0, DataReader.METADATA_ROW_TABLE_KEY);
		String tableName = table.getTableName();
		Map<String, Object> meta = createMetadata(tableName, map0);
		List<Object[]> dataList = new ArrayList<>(pkg.size());
		for (Map<String, Object> rowMap : pkg) {
			Object[] arr = getDataValues(rowMap, meta);
			dataList.add(arr);
		}
		batchInsert(tableName, meta, dataList);
	} catch (Exception e) {
		logger.error("Batch data package saving failure!", e);
		throw new ImportException(e.getMessage(), e);
	}
		
		
		
		
		
		
//		System.out.println(map0);
//		System.out.println(Thread.currentThread().getName()+"--"+Thread.currentThread().getId()+":"+pkg.size());
//		
	}
	
	
	//批量插入数据包
		private void batchInsert(String tableName, Map<String, Object> metaMap, List<Object[]> dataList) {
			String insertSql = MapUtil.getString(metaMap, "__insertSql");
			int[] sqlTypes = MapUtil.getObject(metaMap, "__sqlTypes");
			daoSupport.batch(insertSql, dataList, sqlTypes);
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
		private synchronized Map<String, Object> createMetadata(String tableName, Map<String, Object> row) {
			if (cache_meta == null) {
				Map<String, Object> metaMap = new LinkedHashMap<>(); ////保持原始顺序
				Table table = MapUtil.getObject(row, DataReader.METADATA_ROW_TABLE_KEY);
				DataReader.forEach(row, (index, entry) -> {
					String fName = entry.getKey();
					DataType dt = table.getDataType(fName);
					if (dt == null || dt == DataType.UNKNOW) {
						dt = DataType.of(row.get(fName));
					}
					metaMap.put(fName, dt);
				});
				metaMap.put("__fieldCount", metaMap.size());
				String insertSql = createInsertSql(tableName, metaMap);
				metaMap.put("__insertSql", insertSql);
				int[] sqlTypes = getSqlTypes(metaMap);
				metaMap.put("__sqlTypes", sqlTypes);
				cache_meta = metaMap;
			}
			return cache_meta;
		}

		private String createInsertSql(String tableName, Map<String, Object> metaMap) {
			StringBuilder sb = new StringBuilder("insert into ");
			StringBuilder questions = new StringBuilder();
			sb.append(tableName);
			sb.append(" (");
			DataReader.forEach(metaMap, (index, entry) -> {
				if (index != 0) {
					sb.append(StringUtil.CHAR_COMMA);
					questions.append(StringUtil.CHAR_COMMA);
				}
				String fName = entry.getKey();
				sb.append(fName);
				questions.append('?');
			});
			sb.append(")  values (");
			sb.append(questions);
			sb.append(")");
			return sb.toString();
		}
		
		private int[] getSqlTypes(Map<String, Object> metaMap) {
			int fieldCount = MapUtil.getInteger(metaMap, "__fieldCount");
			int[] sqlTypes = new int[fieldCount];
			DataReader.forEach(metaMap, (index, entry) -> {
				DataType dt = (DataType) entry.getValue();
				sqlTypes[index] = dt.sqlType();
			});
			return sqlTypes;
		}
		
		
		//获得数据值
		private Object[] getDataValues(Map<String, Object> row, Map<String, Object> metaMap) {
			int fieldCount = MapUtil.getInteger(metaMap, "__fieldCount");
			Object[] arr = new Object[fieldCount];
			DataReader.forEach(row, (index, entry) -> {
				String fName = entry.getKey();
				arr[index] = row.get(fName);
			});
			return arr;
		}
		
		
		private void test() {
			List<Object[]> dataList = new ArrayList<>();
			dataList.add(new Object[] { 1, "ww", new Date() });
			dataList.add(new Object[] { 2, "ww", new Date() });
			String sql = "insert into t_user (f_id,f_name,f_birthday) values (?,?,?)";
			daoSupport.batch(sql, dataList, new int[] { -5, 12, 91 });
		}
		
		
		

}
