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

	private static final MessageSourceAccessor MSA = RockUtil.getMessageSourceAccessor("i8n.fileslice-resources");
	public static Locale locale = Locale.getDefault();
	public static Charset charset = StringUtil.UTF_8;

	public static String getMessage(String key, Object... args) {
		return MSA.getMessage(key, args, locale);
	}

	/**
	 * <b>createOptions。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 创建选项。
	 * @return
	 */
	public static Options createOptions() {

		Options options = new Options();
		options.addOption("h", "help", false, getMessage("help.cmdline.option.help"));
		options.addOption("n", "rowcount", false, getMessage("help.cmdline.option.rowcount"));
		options.addOption("locale", true, getMessage("help.cmdline.option.locale"));
		options.addOption("encode", true, getMessage("help.cmdline.option.encode"));
		options.addOption("test", true, getMessage("help.cmdline.option.test"));
		options.addOption("show", true, getMessage("help.cmdline.option.show"));
		options.addOption("cut", true, getMessage("help.cmdline.option.cut"));
		options.addOption("split", true, getMessage("help.cmdline.option.split"));
		options.addOption("cp", "classpath", true, getMessage("help.cmdline.option.classpath"));
		return options;

	}

	/**
	 * <b>printHelp。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 打印帮助。
	 * @param options
	 * @param out
	 */
	private static void printHelp(Options options, PrintStream out) {
		out.print(getMessage("help.usage.title"));
		out.println("快速分割文件 [选项] [文件] [文件 or 目录]");
		out.println(getMessage("help.usage.header"));

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

	/**
	 * <b>createTestFile。</b>
	 * <p><b>详细说明：</b></p>
	 * <!--  -->
	 * 创建测试文件。
	 * @param file
	 * @param rowCount
	 * @throws IOException
	 */
	public static void createTestFile(File file, long rowCount) throws IOException {
		long begin = System.currentTimeMillis();
		System.out.println("开始生产文件........");
		OutputStream out = new FileOutputStream(file);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, charset));
			for (int i = 1; i <= rowCount; i++) {
				writer.write(String.valueOf(i));
				writer.write(StringUtil.CHAR_COMMA);
				writer.write(String.valueOf(StringUtil.random().nextInt(100) + 100)+"code");
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

	/**
	 * <b>getRowCount。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 读取文件行数 -->
	 * 读取文件行数。
	 * @param file
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * <b>showDateRow。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 读取指定行数 -->
	 * 读取指定行数。
	 * @param file
	 * @param fromIndex
	 * @param toIndex
	 * @return
	 * @throws IOException
	 */
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

	/**
	 * <b>copy。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 根据jar文件输出类路径 -->
	 * 根据jar文件输出类路径。
	 * @param lib_dir
	 * @return
	 */
	public static String copy(File lib_dir) {
		StringBuilder sb = new StringBuilder("CLASSPATH=%CLASSPATH%");
		for (File jar : lib_dir.listFiles((dir, name) -> FilenameUtils.isExtension(name, "jar"))) {
			sb.append(";%LIB_DIR%\\");
			sb.append(jar.getName());

		}
		sb.append("\n");
		return sb.toString();
	}

	/**
	 * <b>cutFile。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 剪切出部分数据。
	 * @param file
	 * @param destFile
	 * @param fromIndex
	 * @param toIndex
	 * @throws IOException
	 */
	private static void cutFile(File file, File destFile, long fromIndex, long toIndex) throws IOException {
		OutputStream outputStream = null;
		InputStream intInputStream = null;

		try {
			intInputStream = FileUtils.openInputStream(file);
			outputStream = FileUtils.openOutputStream(destFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(intInputStream, charset));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset));
			String line;
			long index = 0;
			while ((line = reader.readLine()) != null && index < toIndex) {
				if (index++ < fromIndex)
					continue;
				writer.write(line);
				writer.newLine();

			}
			writer.flush();
		} finally {
			IOUtil.closeQuietly(intInputStream);
			IOUtil.closeQuietly(outputStream);
		}
	}

	/**
	 * <b>splitFile。</b>
	 * <p><b>详细说明：</b></p>
	 * <!-- 在此添加详细说明 -->
	 * 分割成多个小文件。
	 * @param srcFile
	 * @param destDir
	 * @param rowCount
	 * @return
	 * @throws IOException
	 */
	private static int splitFile(File srcFile, File destDir, int rowCount) throws IOException {
		String destFileName = FilenameUtils.removeExtension(srcFile.getName()) + "_";
		String extNameString = "." + FilenameUtils.getExtension(srcFile.getName());
		OutputStream outputStream = null;
		InputStream intInputStream = null;
		try {
			intInputStream = FileUtils.openInputStream(srcFile);
			BufferedReader reader = new BufferedReader(new InputStreamReader(intInputStream, charset));
			BufferedWriter writer = null;
			String line;
			int index = 0;

			while ((line = reader.readLine()) != null) {
				if (outputStream == null) {
					File destFile = new File(destDir, destFileName + (index / rowCount + 1) + extNameString);
					outputStream = FileUtils.openOutputStream(destFile);
					writer = new BufferedWriter(new OutputStreamWriter(outputStream, charset));

				}
				writer.write(line);
				writer.newLine();
				index++;
				if ((index % rowCount) == 0) {
					writer.flush();
					IOUtil.closeQuietly(outputStream);
					outputStream = null;
					writer = null;
				}
			}
			if (writer != null) {
				writer.flush();
				IOUtil.closeQuietly(outputStream);
				outputStream = null;
				writer = null;
				return (index / rowCount) + 1;

			} else
				return index / rowCount;
		} finally {
			IOUtil.closeQuietly(intInputStream);
			IOUtil.closeQuietly(outputStream);
		}
	}

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

		} else if (cmdLine.hasOption("cp")) {
			String dirString = cmdLine.getOptionValue("cp");
			File file = new File(dirString);
			if (!file.isDirectory())
				throw new CutException(getMessage("error.cmdline.dir.notfound", file.getAbsolutePath()));
			String cpString = copy(file);
			System.out.println(cpString);

		} else if (cmdLine.hasOption("test")) {
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
					if (index == 0)
						from.setValue(Long.valueOf(ele.toString()));

					else
						to.setValue(Long.valueOf(ele.toString()));

				});
				if (to.longValue() == -1) {
					to.setValue(from.longValue());
					from.setValue(1);
				}
				if (from.longValue() < 1)
					from.setValue(1);

				if (to.longValue() < 1)
					to.setValue(1);
				long rowCount = showDateRow(file, from.longValue() - 1, to.longValue());
				System.out.println(getMessage("cmdline.show.done", rowCount));
			} else if (cmdLine.hasOption("cut")) {
				MutableLong from = new MutableLong(1);
				MutableLong to = new MutableLong(-1);
				StringUtil.split(cmdLine.getOptionValue("cut"), new char[] { '-', ',' }, (index, ele) -> {
					if (index == 0)
						from.setValue(Long.valueOf(ele.toString()));
					else
						to.setValue(Long.valueOf(ele.toString()));

				});
				if (to.longValue() == -1) {
					to.setValue(from.longValue());
					from.setValue(1);
				}
				if (from.longValue() < 1)
					from.setValue(1);

				if (to.longValue() < 1)
					to.setValue(1);
				if (arg.length == 1) {
					throw new CutException(getMessage("error.cmdline.destfile.miss"));
				}
				File destFile = new File(arg[1]);
				System.out.println(getMessage("cmdline.splitfile.start"));
				cutFile(file, destFile, from.longValue() - 1, to.longValue());
				System.out.println(getMessage(("cmdline.cutfile.done"), destFile.getAbsoluteFile()));
			} else if (cmdLine.hasOption("split")) {
				int rowCount = Integer.parseInt(cmdLine.getOptionValue("split"));
				if (rowCount < 100)
					rowCount = 100;
				File destDir;
				if (arg.length > 1) {
					destDir = new File(arg[1]);
					if (!destDir.exists())
						destDir.mkdirs();
				} else {
					destDir = file.getParentFile();
				}
				if (!destDir.isDirectory()) {
					throw new CutException(getMessage("error.cmdline.destdirectory.isfile", destDir.getAbsolutePath()));

				}
				System.out.println(getMessage("cmdline.splitfile.start"));
				int count = splitFile(file, destDir, rowCount);
				System.out.println(
						getMessage("cmdline.splitfile.done", file.getAbsolutePath(), count, destDir.getAbsolutePath()));

			} else {
				long count = getRowCount(file);
				System.out.println(count);
			}

		}
	}

}
