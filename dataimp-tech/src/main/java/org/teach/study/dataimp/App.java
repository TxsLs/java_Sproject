package org.teach.study.dataimp;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
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
import org.quincy.rock.core.concurrent.Processor;
import org.quincy.rock.core.util.DateUtil;
import org.quincy.rock.core.util.IOUtil;
import org.quincy.rock.core.util.NumberUtil;
import org.quincy.rock.core.util.RockUtil;
import org.quincy.rock.core.util.StringUtil;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * Hello world!
 *
 */
public class App {
	/**
	 * VERSION。
	 */
	public static final String VERSION = "1.0.0";

	/**
	 * messageSource。
	 */
	private static final MessageSourceAccessor MSA = RockUtil.getMessageSourceAccessor("i8n.dataimp-resources");

	/**
	 * fmt4dec。
	 */
	private static DecimalFormat fmt4dec = new DecimalFormat("#0.0");

	/**
	 * locale。
	 */
	private static Locale locale = Locale.SIMPLIFIED_CHINESE;

	/**
	 * batch大小。
	 */
	private static int batchSize = 200;
	/**
	 * 字符集编码。
	 */
	private static Charset charset = null;

	/**
	 * context。
	 */
	public static SpringContext context = SpringContext.createSpringContext("/spring-dataimp.xml");

	/**
	 * 批次号。
	 */
	private static long pkgno = 0;

	/**
	 * 数据队列处理服务。
	 */
	private static ArrayQueueProcessService<Long, List<Map<String, Object>>> processService;

	public static void main(String[] _args) throws Exception {
		CommandLineParser parser = new DefaultParser(false);
		Options opts = createOptions();
		CommandLine cmdLine = parser.parse(opts, _args);

		if (cmdLine.hasOption('h')) {
			printHelp(opts, System.out);
		} else if (cmdLine.hasOption('v')) {
			System.out.println(VERSION);
		} else {
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
	}

	/**
	 * <b>启动数据队列处理服务。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param tc 线程数
	 * @param capacity 队列大小
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void startProcessService(int tc, int capacity) {
		processService = new ArrayQueueProcessService(tc, capacity);
		processService.setName("dataImportService");
		processService.setTimeout(30);
		//
		processService.setProcessor( context.getBean(Processor.class));
		
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
			
			processService.waitAllDone();
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
	 * <b>获得资源消息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param key 资源key
	 * @param args 参数
	 * @return 消息字符串
	 */
	private static String getMessage(String key, Object... args) {
		return MSA.getMessage(key, args, locale);
	}

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

	/**
	 * <b>打印帮助信息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param options Options
	 */
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
		MutableLong seqno = new MutableLong(0);
		DataReader reader = ImpUtils.createDataReader(file, null, null, charset, null);
		try {
			long beginTime = System.currentTimeMillis();
			out.println(getMessage("import.file.begin", file.getCanonicalPath(),
					DateUtil.formatDateTime(new Date(beginTime))));
			MutableObject<List<Map<String, Object>>> mutablePackage = new MutableObject<>(new ArrayList<>(batchSize));
			//显示初始进度
			long planRowCount = reader.getRowCount();
			MutableInt progress = new MutableInt(0); //放大100倍的进度 10000
			StringBuilder sbProgress = new StringBuilder(); //存放一行进度字符串
			MutableLong next = new MutableLong(); //存放下一次更新进度时间
			showProgressInfo(out, progress.intValue(), sbProgress, next);
			//迭代
			reader.forEach((row) -> {
				seqno.increment();
				row.put(DataReader.METADATA_ROW_SEQNO_KEY, seqno.longValue());
				List<Map<String, Object>> batchPackage = mutablePackage.getValue();
				batchPackage.add(row);
				if (batchPackage.size() == batchSize) {
					putDataPackage(batchPackage);
					mutablePackage.setValue(new ArrayList<>(batchSize));
				}
				//检查进度
				int percent = (int) (seqno.longValue() * 10000 / planRowCount);
				if (percent != progress.intValue()) {
					progress.setValue(percent);
					showProgressInfo(out, progress.intValue(), sbProgress, next);
				} else if (System.currentTimeMillis() >= next.longValue()) {
					showProgressInfo(out, progress.intValue(), sbProgress, next);
				}
			}, (ex) -> {
				throw ex;
			});
			putDataPackage(mutablePackage.getValue());
			//显示满进度
			progress.setValue(10000);
			showProgressInfo(out, progress.intValue(), sbProgress, next);
		} finally {
			IOUtil.closeQuietly(reader);
		}
		return seqno.longValue();
	}

	private static void putDataPackage(List<Map<String, Object>> batchPackage) {
		if (batchPackage.size() == 0)
			return;
		System.out.println(batchPackage.size());
		//放入处理队列
	processService.put(pkgno++, batchPackage);
	}

	/**
	 * <b>显示进度信息。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 无。
	 * @param out 控制台
	 * @param progress 放大100倍的进度，100%是10000
	 * @param sb 进度信息缓冲区(return)
	 * @param next 下一次显示进度的时间(return)
	 */
	private static void showProgressInfo(PrintStream out, int progress, StringBuilder sb, MutableLong next) {
		DecimalFormat fmt4int = NumberUtil.simpleIndegerPattern();
		Runtime r = Runtime.getRuntime();
		int lastLen = sb.length();
		sb.setLength(0);
		sb.append(fmt4dec.format(progress / 100.0));
		sb.append("%,free memory:");
		double freeMem = (r.maxMemory() - (r.totalMemory() - r.freeMemory())) / (IOUtil.IO_BINARY_UNIT_G * 1.0);
		sb.append(fmt4dec.format(freeMem));
		sb.append("G/");
		double maxMem = r.maxMemory() / (IOUtil.IO_BINARY_UNIT_G * 1.0);
		sb.append(fmt4dec.format(maxMem));
		sb.append("G,wait count:");
		sb.append(fmt4int.format(processService.count()));
		sb.append("/");
		sb.append(fmt4int.format(processService.getCapacity()));
		sb.append(",wait time:");
		sb.append(fmt4int.format(processService.waitSeconds()));
		sb.append("s    ");
		if ((freeMem / maxMem) < 0.2)
			System.gc();

		//out
		//StringUtil.backspace(out, lastLen);
		//out.print(sb);

		//next
		next.setValue(System.currentTimeMillis() + 10000);
	}
}
