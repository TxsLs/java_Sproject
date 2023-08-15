package org.stu.dataimp;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.mutable.MutableObject;
import org.quincy.rock.core.SpringContext;
import org.quincy.rock.core.concurrent.ArrayQueueProcessService;
import org.quincy.rock.core.dao.ManualJdbcDaoSupport;
import org.quincy.rock.core.util.DateUtil;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.util.RockUtil;
import org.quincy.rock.core.util.StringUtil;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Hello world!
 *
 */
public class App {
	public static final String VERSION = "1.0.0";
	private static final MessageSourceAccessor MSA = RockUtil.getMessageSourceAccessor("i8n.dataimp-resources");
	private static Locale locale = Locale.SIMPLIFIED_CHINESE;

	public static SpringContext context = SpringContext.createSpringContext("/spring-dataimp.xml");

	/**
	 * batch大小。
	 */
	private static int batchSize = 2000;
	/**
	 * 字符集编码。
	 */
	private static Charset charset = null;
	/**
	 * 批次号。
	 */
	private static long pkgno = 0;

	
	private static Options createOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, getMessage("help.cmdline.option.help"));
		options.addOption("v", "version", false, getMessage("help.cmdline.option.version"));
		options.addOption("n", "rowcount", false, getMessage("help.cmdline.option.rowcount"));
		options.addOption("show", true, getMessage("help.cmdline.option.show"));
		options.addOption("l", "locale", true, getMessage("help.cmdline.option.locale"));
		options.addOption("encode", "charset", true, getMessage("help.cmdline.option.charset"));
		options.addOption("qs", "queuesize", true, getMessage("help.cmdline.option.queuesize"));
		options.addOption("tc", "threadcount", true, getMessage("help.cmdline.option.threadcount"));
		options.addOption("b", "batch", true, getMessage("help.cmdline.option.batch"));
		return options;
	}

	private static void printHelp(Options options, PrintStream out) {
		out.print(getMessage("help.usage.title"));
		out.println("fastimp [-options] [filename|directory]");
		out.println(getMessage("help.usage.header"));
		//
		Option[] opts = options.getOptions().toArray(new Option[0]);
		int count = opts.length;
		StringBuilder[] sbs = new StringBuilder[count];
		int maxLen = 0;
		for (int i = 0; i < count; i++) {
			Option opt = opts[i];
			StringBuilder sb = new StringBuilder();
			sb.append(" -");
			sb.append(opt.getOpt());
			if (opt.hasLongOpt()) {
				sb.append(StringUtil.CHAR_COMMA);
				sb.append("--");
				sb.append(opt.getLongOpt());
			}
			if (opt.hasArg()) {
				sb.append(StringUtil.CHAR_SPACE);
				sb.append("<arg>");
			}
			maxLen = Math.max(maxLen, sb.length());
			sbs[i] = sb;
		}
		maxLen += 3;
		for (int i = 0; i < count; i++) {
			StringBuilder sb = sbs[i];
			sb.append(StringUtil.repeat(StringUtil.CHAR_SPACE, maxLen - sb.length()));
			sb.append(opts[i].getDescription());
			out.println(sb.toString());
		}
		out.println(getMessage("help.usage.footer"));
	}
	
	
	private static long importData(File file, PrintStream out) throws IOException {
		MutableLong total = new MutableLong(0);
		MutableLong seqno = new MutableLong(0);
		DataReader reader = ImpUtils.createDataReader(file, null, null, charset, null);
		try {
			long beginTime = System.currentTimeMillis();
			out.println(getMessage("import.file.begin", file.getCanonicalPath(),
					DateUtil.formatDateTime(new Date(beginTime))));
			MutableObject<List<Map<String, Object>>> mutablePackage = new MutableObject<>(new ArrayList<>(batchSize));
			reader.forEach((row) -> {
				row.put(DataReader.METADATA_ROW_SEQNO_KEY, seqno.getAndIncrement());
				List<Map<String, Object>> batchPackage = mutablePackage.getValue();
				batchPackage.add(row);
				if (batchPackage.size() == batchSize) {
					putDataPackage(batchPackage);
					mutablePackage.setValue(new ArrayList<>(batchSize));
				}
			}, (ex) -> {
				throw ex;
			});
			putDataPackage(mutablePackage.getValue());
		} finally {
			IOUtil.closeQuietly(reader);
		}
		return total.longValue();
	}
	private static void putDataPackage(List<Map<String, Object>> batchPackage) {
		if (batchPackage.size() == 0)
			return;
		//放入处理队列
		//processService.put(pkgno++, batchPackage);
	}


	
	
	/**
	 * 数据队列处理服务。
	 */
	private static ArrayQueueProcessService<Long, List<Map<String, Object>>> processService;

	/**
	 * <b>启动数据队列处理服务。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param tc 线程数
	 * @param capacity 队列大小
	 */
	private static void startProcessService(int tc, int capacity) {
		processService = new ArrayQueueProcessService(tc, capacity);
		processService.setName("dataImportService");
		processService.setTimeout(30);
		//
		processService.start();
	}
	
	/**
	 * <b>停止数据队列处理服务。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 */
	private synchronized static void stopProcessService() {
		if (processService != null) {
			processService.destroy();
			processService = null;
		}
	}


	private static long getRowCount(File file) throws IOException {
		DataReader reader = ImpUtils.createDataReader(file, null, null, charset, null);
		try {
			return reader.getRowCount();
		} finally {
			IOUtils.close(reader);
		}
	}
	

	private static int showDataRow(File file, int rowCount, PrintStream out) throws IOException {
		MutableInt count = new MutableInt(0);
		DataReader reader = ImpUtils.createDataReader(file, null, null, charset, null);
		try {
			reader.forEach((line) -> {
				out.println(line);
				//返回true则是继续
				return count.incrementAndGet() < rowCount;
			});
		} finally {
			IOUtils.close(reader);
		}
		return count.intValue();
	}
	
	
	/**
	 * <b>获取资源消息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param key资源
	 * @param args 参数
	 * @return
	 */
	private static String getMessage(String key, Object... args) {
		return MSA.getMessage(key, args, locale);
	}
	
	
	
	
	

	public static void main(String[] _args) throws Exception {
		CommandLineParser parser = new DefaultParser(false);
		Options opts = createOptions();
		CommandLine cmdLine = parser.parse(opts, _args);

		if (cmdLine.hasOption('h')) {
			printHelp(opts, System.out);
		} else if (cmdLine.hasOption('v')) {
			System.out.println(VERSION);
		}
		
		else {
			//batchSize
			if (cmdLine.hasOption('b')) {
				int size = Integer.valueOf(cmdLine.getOptionValue('b'));
				if (size > 0) {
					batchSize = size;
				}
			}
			//locale	
			if (cmdLine.hasOption('l')) {
				String lang = cmdLine.getOptionValue('l');
				locale = LocaleUtils.toLocale(lang);
			}
			//字符集编码
			if (cmdLine.hasOption("encode")) {
				String encode = cmdLine.getOptionValue("encode");
				charset = Charset.forName(encode);
			}

			String[] args = cmdLine.getArgs();
			if (args.length == 0)
				throw new ImportException(getMessage("error.cmdline.datafile.miss"));
			File file = new File(args[0]);
			if (!file.exists()) {
				throw new ImportException(getMessage("error.cmdline.datafile.notfound", args[0]));
			}
			//检查其他参数
			if (cmdLine.hasOption('n')) {
				//显示行数
				long rowCount = getRowCount(file);
				System.out.println(rowCount);
			} else if (cmdLine.hasOption("show")) {
				//显示数据内容
				int rowCount = Integer.parseInt(cmdLine.getOptionValue("show"));
				if (rowCount < 1)
					rowCount = 1;
				rowCount = showDataRow(file, rowCount, System.out);
				System.out.println(getMessage("cmdline.show.done", rowCount));
			} else {
				int tc = cmdLine.hasOption("tc") ? Integer.parseInt(cmdLine.getOptionValue("tc")) : 8;
				int qs = cmdLine.hasOption("qs") ? Integer.parseInt(cmdLine.getOptionValue("qs")) : 16;
				//导入数据
				long beginTime = System.currentTimeMillis();
				long total = 0;

				startProcessService(tc, qs);
				try {
					total = importData(file, System.out);
					System.out.println(getMessage("cmdline.import.clean"));
				} finally {
					stopProcessService();
				}
				double minute = DateUtil.interval(beginTime, System.currentTimeMillis(), TimeUnit.MINUTES);
				System.out.println(getMessage("cmdline.import.done", total, minute));
			}
		}
		
		
		/*ManualJdbcDaoSupport daoSupport = context.getBean(ManualJdbcDaoSupport.class);
		List<Map<String, Object>> data = daoSupport.queryList("select * from s_user");
		
		//     for (Map<String, Object> map : data) {
		//    	 System.out.println(map);
		//	}
		data.forEach((s) -> System.out.println(s));*/

	}
}
