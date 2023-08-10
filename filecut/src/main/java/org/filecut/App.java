package org.filecut;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.mutable.MutableLong;
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
	public static void main(String[] _args) throws Exception {
		CommandLineParser parser = new DefaultParser(false);
		Options options = createOptions();
		CommandLine cmdLine = parser.parse(options, _args);

		if (cmdLine.hasOption("locale")) {
			String langString = cmdLine.getOptionValue("locale");
			locale = LocaleUtils.toLocale(langString);

		}
		if (cmdLine.hasOption("encode")) {
			String encode = cmdLine.getOptionValue("encode");
			charset = Charset.forName(encode);

		}

		if (cmdLine.hasOption('h')) {
			printHelp(options, System.out);

		}
			else if (cmdLine.hasOption("cp")) {
				String dirString=cmdLine.getOptionValue("cp");
				File file=new File(dirString);
					if(!file.isDirectory()) 
						throw new CutException(getMessage("error.cmdline.dir.notfound", file.getAbsolutePath()));
						String cpString=gencp(file);
						System.out.println(cpString);
					
			}
		else if (cmdLine.hasOption("test")) {
			long rowCount = Integer.valueOf(cmdLine.getOptionValue("test"));
			String[] argStrings = cmdLine.getArgs();
			if (argStrings.length == 0)
				throw new CutException(getMessage("error.cmdline.datafile.miss"));
			createTestFile(new File(argStrings[0]), rowCount);

		} else {
			String arg[] = cmdLine.getArgs();
			if (arg.length == 0) {
				throw new CutException(getMessage("error.cmdline.datafile.miss"));
			}
			File file = new File(arg[0]);
			if (!file.isFile()) {
				throw new CutException(getMessage("error.cmdline.file.notfound", file.getAbsolutePath()));

			}
			if (cmdLine.hasOption('n')) {
				long count = getRowCount(file);
				System.out.println(count);
			} else if (cmdLine.hasOption("show")) {
				MutableLong from = new MutableLong(1);
				MutableLong to = new MutableLong(-1);
				StringUtil.split(cmdLine.getOptionValue("show"), new char[] { '-', ',' }, (index, ele) -> {
					if (index == 0) {
						from.setValue(Long.valueOf(ele.toString()));

					} else {
						to.setValue(Long.valueOf(ele.toString()));
					}
				});
				if (to.longValue() == -1) {
					to.setValue(from.longValue());
					from.setValue(1);
				}
				if (from.longValue() < 1) {
					from.setValue(1);
				}
				if (to.longValue() < 1) {
					to.setValue(1);
					long rowCount = showDateRow(file, from.longValue() - 1, to.longValue());
					System.out.println(getMessage("cmdline.show.done", rowCount));
				}
			}
		}
	}

	private static final MessageSourceAccessor MSA = RockUtil.getMessageSourceAccessor("i8n.fileslice-resources");
	public static Locale locale = Locale.getDefault();
	public static Charset charset = StringUtil.UTF_8;

	public static String getMessage(String key, Object... args) {
		return MSA.getMessage(key, args, locale);
	}

	public static Options createOptions() {

		Options options = new Options();
		options.addOption("h", "help", false, getMessage("help.cmdline.option.help"));
		options.addOption("n", "rowcount", false, getMessage("help.cmdline.option.rowcount"));
		options.addOption("cp", "classpath", true, getMessage("help.cmdline.option.classpath"));
		options.addOption("locale", true, "help.cmdline.option.locale");
		options.addOption("encode", true, getMessage("help.cmdline.option.encode"));
		options.addOption("test", true, "help.cmdline.option.test");
		options.addOption("show", true, "help.cmdline.option.show");
		options.addOption("cut", true, "help.cmdline.option.cut");
		options.addOption("split", true, "help.cmdline.option.split");
		return options;

	}

	private static void printHelp(Options options, PrintStream out) {
		out.println(getMessage("help.usage.title"));
		out.println("快速分割文件 [选项] [文件] [datefile or destdir]");
		out.println("help.usage.header");

		Option[] opts = options.getOptions().toArray(new Option[0]);
		int count = opts.length;
		StringBuilder[] sbsBuilders = new StringBuilder[count];
		int maxlen = 0;
		for (int i = 0; i < count; i++) {
			Option opt = opts[i];
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append(" -");
			sBuilder.append(opt.getOpt());
			if (opt.hasLongOpt()) {
				sBuilder.append(StringUtil.CHAR_COMMA);
				sBuilder.append("--");
				sBuilder.append(opt.getLongOpt());

			}
			if (opt.hasArg()) {
				sBuilder.append(StringUtil.CHAR_SPACE);
				sBuilder.append("<arg>");

			}
			maxlen = Math.max(maxlen, sBuilder.length());
			sbsBuilders[i] = sBuilder;

		}
		maxlen += 3;
		for (int i = 0; i < count; i++) {
			StringBuilder sb = sbsBuilders[i];
			sb.append(StringUtil.repeat(StringUtil.CHAR_SPACE, maxlen - sb.length()));
			sb.append(opts[i].getDescription());
			out.println(sb.toString());

		}
		out.println(getMessage("help.usage.footer"));
	}

	public static void createTestFile(File file, long rowCount) throws IOException {
		long begin = System.currentTimeMillis();
		System.out.println("开始生产文件........");
		OutputStream out = new FileOutputStream(file);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, charset));
			for (int i = 1; i <= rowCount; i++) {
				writer.write(String.valueOf(i));
				writer.write(StringUtil.CHAR_COMMA);
				writer.write("分割文件！");
				writer.write(String.valueOf(i));
				writer.write(StringUtil.CHAR_COMMA);
				writer.write("2023-8-9");
				writer.write(StringUtil.CHAR_COMMA);
				writer.write(String.valueOf(StringUtil.random().nextInt(100) + 100));
				writer.write(StringUtil.CHAR_COMMA);
				writer.write(NumberUtil.simpleDecimalPattern().format(StringUtil.random().nextDouble() * 10000));
				writer.write(StringUtil.CHAR_COMMA);
				writer.write("asdfghjkl");
				writer.write("\n");

			}
			IOUtil.closeQuietly(writer);
		} finally {
			IOUtil.closeQuietly(out);
		}
		double minutes = DateUtil.interval(begin, System.currentTimeMillis(), TimeUnit.MINUTES);
		System.out.println("生产文件完成，花费时间：" + minutes);

	}

	public static long getRowCount(File file) throws IOException {
		InputStream in = FileUtils.openInputStream(file);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
			long index = 0;
			while (reader.readLine() != null) {
				index++;
			}
			return index;
		} finally {
			IOUtil.closeQuietly(in);
		}

	}

	public static long showDateRow(File file, long fromIndex, long toIndex) throws IOException {
		InputStream in = FileUtils.openInputStream(file);
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
			long index = 0;
			String line;
			while ((line = reader.readLine()) != null && index < toIndex) {
				if (index++ < fromIndex)
					continue;
				System.out.println(line);
			}
			return index - fromIndex;
		} finally {
			IOUtil.closeQuietly(in);
		}

	}
	
	
	public static String gencp(File lib_dir) {
		StringBuilder sb =new StringBuilder("CLASSPATH=%CLASSPATH%");
		for(File jar :lib_dir.listFiles((dir,name)->FilenameUtils.isExtension(name, "jar"))) {
			sb.append(";%LIB_DIR%\\");
			sb.append(jar.getName());
			
		}
		sb.append("\n");
		return sb.toString();
	}
	
	
	private static void cutFile(File file,File destFile,long fromIndex,long toIndex) {
		OutputStream outputStream=null;
		InputStream intInputStream=null;
		
		try {
			
		} finally {
			// TODO: handle finally clause
		}
	}
	

}
