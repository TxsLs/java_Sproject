package org.teach.study.boot.config;

import org.quincy.rock.core.dao.MybatisSQLProvider;
import org.quincy.rock.core.util.StringUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * <b>IdGeneratorProperties。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "rock.idgen")
public class IdGeneratorProperties {
	private String table; //维护自增主键的表名
	private long initial; //初始值
	private int cache; //缓冲主键个数
	private String KeyFieldName; //主键key字段名
	private String valueFieldName; //主键值字段名

	public IdGeneratorProperties() {
		MybatisSQLProvider.setTableNameConverter(
				(objName) -> StringUtil.objName2DbName(objName, "t_", StringUtil.CHAR_UNDERLINE));
		MybatisSQLProvider.setFieldNameConverter(
				(objName) -> StringUtil.objName2DbName(objName, "f_", StringUtil.CHAR_UNDERLINE));
	}

	public void setSqlQuote(String sqlQuote) {
		MybatisSQLProvider.setSqlQuote(sqlQuote);
	}

	public String getSqlQuote() {
		return MybatisSQLProvider.getSqlQuote();
	}
}
