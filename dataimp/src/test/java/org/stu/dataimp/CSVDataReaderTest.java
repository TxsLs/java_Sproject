package org.stu.dataimp;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.csv.CSVFormat;
import org.junit.Test;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.util.StringUtil;

/**
 * <b>CSVDataReaderTest。</b>
 * <p><b>详细说明：</b></p>
 * <!-- 在此添加详细说明 -->
 * 无。
 * 
 * @version 1.0
 * @author 刘
 * @since 1.0
 */
public class CSVDataReaderTest {

	@Test
	public void CSVDataReader() throws IOException {
		File file = new File("D:\\文档\\StudyTxs\\shixun\\test.csv");
		CSVDataReader reader = new CSVDataReader(file, CSVFormat.DEFAULT, StringUtil.UTF_8,
				new File("D:\\文档\\StudyTxs\\shixun\\test.cfg"));
		try {
			
			/*	reader.forEach(map->{
					System.out.println(map);
				},ex->{
					System.out.println(ex.getMessage());
				});*/
			
			
			/*Map<String, Object> map = reader.getConfig();
			System.out.println(map);
			
			
			reader.forEach((row)->{
				System.out.println(row);
				return Boolean.TRUE;
			}); */
			
			
			
			
			reader.forEach(new Function<String, Boolean>() {
			
				@Override
				public Boolean apply(String row) {
					System.out.println(row);
					return Boolean.TRUE;
				}
			
			});
			
			
		} finally {
			IOUtil.closeQuietly(reader);
		}
	}
}
