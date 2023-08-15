package org.teach.study.dataimp;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVFormat.Builder;
import org.apache.commons.io.FilenameUtils;
import org.quincy.rock.core.util.IOUtil;

/**
 * <b>ImportUtils。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author quincy
 * @since 1.0
 */
public abstract class ImpUtils {

	private ImpUtils() {
	}

	/**
	 * <b>createDataReader。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param dataFile 数据文件
	 * @param defDelimiter 默认字段分隔符，允许为null
	 * @param defEscape 默认转义字符，允许为null
	 * @param charset 字符集，允许为null
	 * @param defCfgFile 默认的配置文件，允许为null
	 * @return DataReader
	 * @throws IOException
	 */
	public static DataReader createDataReader(File dataFile, String defDelimiter, Character defEscape, Charset charset,
			File defCfgFile) throws IOException {
		File cfgFile = new File(FilenameUtils.removeExtension(dataFile.getAbsolutePath()) + ".cfg");
		if (!cfgFile.isFile())
			cfgFile = defCfgFile;
		//builder
		Builder builder = CSVFormat.DEFAULT.builder();
		if (defDelimiter != null)
			builder.setDelimiter(defDelimiter);
		if (defEscape != null)
			builder.setEscape(defEscape);
		builder.setAllowDuplicateHeaderNames(true);
		builder.setAllowMissingColumnNames(true);
		charset = IOUtil.detectEncode(dataFile, charset);
		//
		return new CSVDataReader(dataFile, builder.build(), charset, cfgFile);
	}
}
