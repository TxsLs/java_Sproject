package org.stu.dataimp;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.quincy.rock.core.SpringContext;
import org.quincy.rock.core.dao.ManualJdbcDaoSupport;
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

	private static Options createOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, getMessage("help.cmdline.option.help"));
		options.addOption("v", "version", false, getMessage("help.cmdline.option.version"));

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
		ManualJdbcDaoSupport daoSupport = context.getBean(ManualJdbcDaoSupport.class);
		List<Map<String, Object>> data = daoSupport.queryList("select * from s_user");

		//     for (Map<String, Object> map : data) {
		//    	 System.out.println(map);
		//	}
		data.forEach((s) -> System.out.println(s));

	}
}
