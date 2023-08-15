package org.teach.study.dataimp;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.junit.Test;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.util.StringUtil;

public class CSVDataReaderTest {

	@Test
	public void testCSVDataReader() throws IOException {
		File file = new File("D:\\test\\test.csv");
		CSVDataReader reader = new CSVDataReader(file, CSVFormat.DEFAULT, StringUtil.UTF_8,
				new File("D:\\test\\test.cfg"));
		try {
			reader.forEach(map->{
				System.out.println(map);
			}, ex->{
				System.out.println(ex.getMessage());
			});
			
			/*
			Map<String, Object> map = reader.getConfig();
			System.out.println(map);
			reader.forEach((row) -> {
				System.out.println(row);
				return Boolean.FALSE;
			});
			*/
			/*
			reader.forEach(new Function<String, Boolean>() {
			
				@Override
				public Boolean apply(String row) {
					System.out.println(row);
					return Boolean.TRUE;
				}
			
			});
			*/
		} finally {
			IOUtil.closeQuietly(reader);
		}
	}
}
