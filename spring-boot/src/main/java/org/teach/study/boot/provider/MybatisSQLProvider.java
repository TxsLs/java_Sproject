package org.teach.study.boot.provider;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.jdbc.SQL;
import org.quincy.rock.core.dao.DaoUtil;
import org.quincy.rock.core.dao.EntityMetadata;
import org.quincy.rock.core.dao.EntityMetadataEx;
import org.quincy.rock.core.dao.annotation.IgnoreInsert;
import org.quincy.rock.core.dao.annotation.IgnoreSelect;
import org.quincy.rock.core.dao.annotation.IgnoreUpdate;
import org.quincy.rock.core.dao.annotation.Temporary;
import org.quincy.rock.core.function.Function;
import org.quincy.rock.core.util.StringUtil;
import org.quincy.rock.core.vo.Sort;

/**
 * <b>MybatisSQLProvider。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MybatisSQLProvider {
	public static final String ENTITY_TYPE_VAR_NAME = "entityType";
	public static final String ENTITY_VAR_NAME = "entity";
	public static final String VO_MAP_VAR_NAME = "voMap";
	public static final String ID_VAR_NAME = "id";
	public static final String NAME_VAR_NAME = "name";
	public static final String VALUE_VAR_NAME = "value";
	public static final String IGNORE_ID_VAR_NAME = "ignoreId";
	public static final String IGNORE_NULL_VALUE_VAR_NAME = "ignoreNullValue";

	public static final String SORT_VAR_NAME = "sort";
	public static final String TABLE_PREFIX = "t_";
	public static final String FIELD_PREFIX = "f_";
	public static final String ID_FIELD_NAME = "f_id";
	public static final char SEPARATOR = StringUtil.CHAR_UNDERLINE;

	public static final Function<String, String, RuntimeException> CONVERTER_TABLE_NAME = (objName) -> StringUtil
			.objName2DbName(objName, TABLE_PREFIX, SEPARATOR);
	public static final Function<String, String, RuntimeException> CONVERTER_FIELD_NAME = (objName) -> StringUtil
			.objName2DbName(objName, FIELD_PREFIX, SEPARATOR);

	private final static Map<String, EntityMetadata> entiMataMap = new HashMap<>();

	public String deleteById(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		String idVarName = getParamName(paramMap, ID_VAR_NAME, "param2");
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		StringBuilder sb = new StringBuilder("delete from ").append(meta.tableName()).append(" where ")
				.append(ID_FIELD_NAME).append("=#{").append(idVarName).append('}');
		return sb.toString();
	}

	public String deleteMoreById(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		Iterator<? extends Number> it = ((Iterable) getParamValue(paramMap, "ids", "param2")).iterator();
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		SQL sql = new SQL().DELETE_FROM(meta.tableName()).WHERE(whereInById(it));
		return sql.toString();
	}

	public String deleteAll(Class<?> clazz) {
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		SQL sql = new SQL().DELETE_FROM(meta.tableName());
		return sql.toString();
	}

	public String total(Class<?> clazz) {
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		return "select count(*) from " + meta.tableName();
	}

	public String countByIds(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		Iterator<? extends Number> it = ((Iterable) getParamValue(paramMap, "ids", "param2")).iterator();
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		StringBuilder sql = new StringBuilder("select count(*) from ").append(meta.tableName()).append(" where ")
				.append(whereInById(it));
		return sql.toString();
	}

	public String findById(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		String idVarName = getParamName(paramMap, ID_VAR_NAME, "param2");
		StringBuilder sql = getSelectSQL(meta).append(" where ").append(ID_FIELD_NAME).append("=#{").append(idVarName)
				.append('}');
		return sql.toString();
	}

	public String findMoreById(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		Iterator<? extends Number> it = ((Iterable) getParamValue(paramMap, "ids", "param2")).iterator();
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		StringBuilder sql = getSelectSQL(meta).append(" where ").append(whereInById(it));
		return sql.toString();
	}

	public String findAll(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		Sort sort = getParamValue(paramMap, SORT_VAR_NAME, "param2");
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		StringBuilder sql = getSelectSQL(meta).append(" order by ").append(meta.orderBy(sort));
		return sql.toString();
	}

	public String findByName(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		String name = getParamValue(paramMap, NAME_VAR_NAME, "param2");
		String valueVarName = getParamName(paramMap, VALUE_VAR_NAME, "param3");
		Sort sort = getParamValue(paramMap, SORT_VAR_NAME, "param4");
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		StringBuilder sql = getSelectSQL(meta).append(" where ").append(meta.columnName(name)).append("=#{")
				.append(valueVarName).append('}');
		if (Sort.isSorted(sort)) {
			sql.append(meta.orderBy(sort));
		}
		return sql.toString();
	}

	public String existsById(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		String idVarName = getParamName(paramMap, ID_VAR_NAME, "param2");
		EntityMetadata meta = getEntityMetadata4Select(clazz);
		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ").append(meta.tableName()).append(" where ")
				.append(ID_FIELD_NAME).append("=#{").append(idVarName).append('}');
		return sb.toString();
	}

	public String existsByName(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		String name = getParamValue(paramMap, NAME_VAR_NAME, "param2");
		String valueVarName = getParamName(paramMap, VALUE_VAR_NAME, "param3");
		String ignoreIdVarName = getParamName(paramMap, IGNORE_ID_VAR_NAME, "param4");
		boolean ignoreId = getParamValue(paramMap, IGNORE_ID_VAR_NAME, "param4") != null;
		EntityMetadata meta = getEntityMetadata4Select(clazz);

		StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM ").append(meta.tableName()).append(" where ")
				.append(meta.columnName(name)).append("=#{").append(valueVarName).append('}');
		if (ignoreId) {
			sb.append(" and ").append(ID_FIELD_NAME).append("!=#{").append(ignoreIdVarName).append('}');
		}
		return sb.toString();
	}

	public String insertOne(Map<String, Object> paramMap) {
		Object vo = getParamValue(paramMap, ENTITY_VAR_NAME, "param1");
		String voVarName = getParamName(paramMap, ENTITY_VAR_NAME, "param1");
		boolean ignoreNullValue = getParamValue(paramMap, IGNORE_NULL_VALUE_VAR_NAME, "param2");
		EntityMetadata meta = getEntityMetadata(vo.getClass(), Temporary.class, IgnoreInsert.class);
		List<String> fList = new ArrayList<>(meta.propertyCount());
		List<String> vList = new ArrayList<>(meta.propertyCount());
		for (String name : meta.persistNames()) {
			if (!meta.canRead(name))
				continue;
			boolean insert = false;
			if ("id".equals(name)) {
				if (!meta.isNullPropertyValue(vo, name)) {
					insert = true;
				}
			} else if (ignoreNullValue) {
				if (!meta.isNullPropertyValue(vo, name)) {
					insert = true;
				}
			} else {
				insert = true;
			}
			if (insert) {
				fList.add(meta.columnName(name));
				vList.add(new StringBuilder("#{").append(voVarName).append(StringUtil.CHAR_DOT).append(name).append('}')
						.toString());
			}
		}
		SQL sql = new SQL().INSERT_INTO(meta.tableName()).INTO_COLUMNS(fList.toArray(ArrayUtils.EMPTY_STRING_ARRAY))
				.INTO_VALUES(vList.toArray(ArrayUtils.EMPTY_STRING_ARRAY));
		return sql.toString();
	}

	public String updateOne(Map<String, Object> paramMap) {
		Object vo = getParamValue(paramMap, ENTITY_VAR_NAME, "param1");
		String voVarName = getParamName(paramMap, ENTITY_VAR_NAME, "param1");
		boolean ignoreNullValue = getParamValue(paramMap, IGNORE_NULL_VALUE_VAR_NAME, "param2");
		EntityMetadata meta = getEntityMetadata(vo.getClass(), Temporary.class, IgnoreUpdate.class);
		List<String> list = new ArrayList<>(meta.propertyCount());
		for (String name : meta.persistNames()) {
			if ("id".equals(name) || !meta.canRead(name))
				continue;
			boolean update = false;
			if (ignoreNullValue) {
				update = !meta.isNullPropertyValue(vo, name);
			} else {
				update = true;
			}
			if (update) {
				CharSequence setV = new StringBuilder(meta.columnName(name)).append("=#{").append(voVarName)
						.append(StringUtil.CHAR_DOT).append(name).append('}');
				list.add(setV.toString());
			}
		}
		CharSequence where = new StringBuilder(ID_FIELD_NAME).append("=#{").append(voVarName).append(".id}");
		SQL sql = new SQL().UPDATE(meta.tableName()).SET(list.toArray(ArrayUtils.EMPTY_STRING_ARRAY))
				.WHERE(where.toString());
		return sql.toString();
	}

	public String updateSpecific(Map<String, Object> paramMap) {
		Class clazz = getParamValue(paramMap, ENTITY_TYPE_VAR_NAME, "param1");
		Map<String, Object> partMap = getParamValue(paramMap, VO_MAP_VAR_NAME, "param2");
		String voMapVarName = getParamName(paramMap, VO_MAP_VAR_NAME, "param2");
		EntityMetadata meta = getEntityMetadata(clazz, Temporary.class, IgnoreUpdate.class);
		List<String> list = new ArrayList<>(partMap.size());
		for (String name : partMap.keySet()) {
			if (!"id".equals(name)) {
				CharSequence setV = new StringBuilder(meta.columnName(name)).append("=#{").append(voMapVarName)
						.append(StringUtil.CHAR_DOT).append(name).append('}');
				list.add(setV.toString());
			}
		}
		CharSequence where = new StringBuilder(ID_FIELD_NAME).append("=#{").append(voMapVarName).append(".id}");
		SQL sql = new SQL().UPDATE(meta.tableName()).SET(list.toArray(ArrayUtils.EMPTY_STRING_ARRAY))
				.WHERE(where.toString());
		return sql.toString();
	}

	private static EntityMetadata getEntityMetadata(Class<?> clazz, Class<? extends Annotation>... transientClasses) {
		String key;
		if (ArrayUtils.isEmpty(transientClasses)) {
			key = clazz.getName();
		} else {
			StringBuilder sb = new StringBuilder(clazz.getName());
			for (Class<? extends Annotation> transientClass : transientClasses) {
				sb.append(StringUtil.CHAR_COLON);
				sb.append(transientClass.getSimpleName());
			}
			key = sb.toString();
		}
		EntityMetadata meta = entiMataMap.get(key);
		if (meta == null) {
			EntityMetadataEx tmp = new EntityMetadataEx(clazz, transientClasses);
			tmp.setDefaultTableNameConverter(CONVERTER_TABLE_NAME);
			tmp.setDefaultFieldNameConverter(CONVERTER_FIELD_NAME);
			entiMataMap.put(key, tmp);
			meta = tmp;
		}
		return meta;
	}

	private static EntityMetadata getEntityMetadata4Select(Class<?> clazz) {
		return getEntityMetadata(clazz, Temporary.class, IgnoreSelect.class);
	}

	private static String whereInById(Iterator<? extends Number> it) {
		StringBuilder where = new StringBuilder(ID_FIELD_NAME);
		where.append(" IN (");
		if (it.hasNext()) {
			where.append(it.next().longValue());
		}
		while (it.hasNext()) {
			where.append(',');
			where.append(it.next().longValue());
		}
		where.append(")");
		return where.toString();
	}

	private static <T> T getParamValue(Map paramMap, String key1, String key2) {
		Object value = paramMap.containsKey(key1) ? paramMap.get(key1)
				: paramMap.containsKey(key2) ? paramMap.get(key2) : null;
		return (T) value;
	}

	private static String getParamName(Map paramMap, String key1, String key2) {
		return paramMap.containsKey(key1) ? key1 : paramMap.containsKey(key2) ? key2 : null;
	}

	private static StringBuilder getSelectSQL(EntityMetadata meta) {
		StringBuilder sql = DaoUtil.getSelectSQL(meta, false,
				(name, isObjName) -> StringUtil.surround(name, '`').toString());
		return sql;
	}
}
