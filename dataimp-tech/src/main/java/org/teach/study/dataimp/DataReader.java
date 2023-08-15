package org.teach.study.dataimp;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
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
	public static final String METADATA_ROW_VAR_PREFIX = "__";
	public static final String METADATA_ROW_TABLE_KEY = "__table";
	public static final String METADATA_ROW_RECORDNUMBER_KEY = "__recordNumber";
	public static final String METADATA_ROW_SEQNO_KEY = "__seqno";

	/**
	 * <b>获得行数。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @return 数据行数
	 */
	public long getRowCount() throws IOException;

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

	/**
	 * <b>迭代行Map，忽略内部变量。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param rowMap 行Map
	 * @param callback 回调函数
	 */
	public static void forEach(Map<String, Object> rowMap, BiConsumer<Integer, Entry<String, Object>> callback) {
		int index = 0;
		for (Entry<String, Object> entry : rowMap.entrySet()) {
			if (!entry.getKey().startsWith(DataReader.METADATA_ROW_VAR_PREFIX)) {
				callback.accept(index++, entry);
			}
		}
	}

}
