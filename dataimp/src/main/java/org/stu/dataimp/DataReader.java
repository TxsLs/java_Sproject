package org.stu.dataimp;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <b>DataReader。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author mex2000
 * @since 1.0
 */
public interface DataReader extends Closeable {
	/**
	 * 存放表元数据的key。
	 */
	public static final String METADATA_TABLES_KEY = "meta.tables";
	/**
	 * 数据文件。
	 */
	public static final String METADATA_DATAFILE_KEY = "meta.datafile";
	
	/**
	 * <b>获得行数。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 数据行数
	 */
	public long getRowCount() throws IOException;
	
	/**
	 * <b>获得参数配置。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 元数据。
	 * @return 参数配置
	 */
	public Map<String, Object> getConfig();
	
	/**
	 * <b>迭代每一行(line)。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 标准csv行字符串。
	 * @param callback,返回true代表继续，false代表中断
	 */
	public void forEach(Function<String, Boolean> callback) throws IOException;
	
	/**
	 * <b>迭代每一行(row)。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param callback 回调函数
	 * @param catchParseError 捕获解析错误
	 */
	public void forEach(Consumer<Map<String, Object>> callback, Consumer<ParseFileException> catchParseError)
			throws IOException;
	
}
