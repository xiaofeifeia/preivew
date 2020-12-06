package com.xph.preview.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xwpf.usermodel.PositionInParagraph;
import org.apache.poi.xwpf.usermodel.TextSegment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * apache poi 的方式
 * @author xiaoph
 *
 */
public class WordPoiUtil {

	private static void replaceInWord(Map<String, String> replacements, XWPFDocument doc, File outfile) {
		long count1 = 0;
		long count2 = 0;
		List<XWPFParagraph> paragraphs = doc.getParagraphs();
		List<XWPFTable> tables = doc.getTables();
		count1 = replaceInParagraphs(replacements, paragraphs, false);
		count2 = replaceInTables(replacements, tables);
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(outfile);
			doc.write(fileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				doc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("段落替换数量累计：" + count1);
		System.out.println("表格替换数量累计：" + count2);
	}

	/**
	 * 1.替换段落中的文本
	 * 
	 * @param replacements
	 * @param paragraphs
	 * @param flag
	 * @return
	 */
	private static long replaceInParagraphs(Map<String, String> replacements, List<XWPFParagraph> paragraphs,
			boolean flag) {
		long count = 0;
		for (XWPFParagraph paragraph : paragraphs) {
			List<XWPFRun> runs = paragraph.getRuns();
			for (Map.Entry<String, String> replPair : replacements.entrySet()) {
				String newText = replPair.getValue();
				// 获取文本段
				TextSegment tSegement = paragraph.searchText(replPair.getKey(), new PositionInParagraph());
				if (tSegement != null) {
					int beginRun = tSegement.getBeginRun();
					int endRun = tSegement.getEndRun();
					System.out.println(beginRun + " " + endRun);
					count++;
					if (beginRun == endRun) {
						XWPFRun run = runs.get(beginRun);
						String runText = run.getText(0);
						System.out.println("runText:" + runText);
						String replaced = runText.replace(replPair.getKey(), newText);
						run.setText(replaced, 0);
					} else {
						StringBuilder b = new StringBuilder();
						for (int runPos = beginRun; runPos <= endRun; runPos++) {
							XWPFRun run = runs.get(runPos);
							b.append(run.getText(0));
						}
						String connectedRuns = b.toString();
						String replaced = connectedRuns.replace(replPair.getKey(), newText);

						XWPFRun partOne = runs.get(beginRun);
						partOne.setText(replaced, 0);
						for (int runPos = beginRun + 1; runPos <= endRun; runPos++) {
							XWPFRun partNext = runs.get(runPos);
							partNext.setText("", 0);
						}
					}
				}
			}
		}
		return count;
	}

	/**
	 * 1.替换表格中的文本
	 * 
	 * @param replacements
	 * @param tables
	 * @return
	 */
	private static long replaceInTables(Map<String, String> replacements, List<XWPFTable> tables) {

		long count = 0;
		for (XWPFTable table : tables) {
			for (XWPFTableRow row : table.getRows()) {
				for (XWPFTableCell cell : row.getTableCells()) {
					List<XWPFParagraph> paragraphs = cell.getParagraphs();
					count += replaceInParagraphs(replacements, paragraphs, true);
				}
			}
		}
		return count;
	}

	/**
	 * 写入.docx
	 * 
	 * @param path
	 * @param outpath
	 * @param map
	 * @param para
	 */
	public static void templateWriteDocx(String path, String outpath, Map<String, String> para) {
		InputStream is = null;
		XWPFDocument document = null;
		File out_file;
		try {
			is = new FileInputStream(path);
			document = new XWPFDocument(is);
			out_file = new File(outpath);
			replaceInWord(para, document, out_file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 写入 .doc
	 * 
	 * @param filePath
	 * @param outpath
	 * @param map
	 */
	public static void templateWriteDoc(String filePath, String outpath, Map<String, String> map) {
		// 读取word模板
		FileInputStream in = null;
		try {
			in = new FileInputStream(new File(filePath));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		HWPFDocument hdt = null;
		try {
			hdt = new HWPFDocument(in);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// 读取word文本内容
		Range range = hdt.getRange();
		// 替换文本内容
		for (Map.Entry<String, String> entry : map.entrySet()) {
			range.replaceText(entry.getKey(), entry.getValue());
		}
		ByteArrayOutputStream ostream = new ByteArrayOutputStream();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(outpath, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			hdt.write(ostream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 输出字节流
		try {
			out.write(ostream.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			ostream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * 根据模板生成新word文档 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
	 * 
	 * @param inputUrl
	 *            模板存放地址
	 * @param outPutUrl
	 *            新文档存放地址
	 * @param textMap
	 *            需要替换的信息集合
	 * @param tableList
	 *            需要插入的表格信息集合
	 * @return 成功返回true,失败返回false
	 */
	public static boolean changWord(String inputUrl, String outputUrl, Map<String, String> textMap,
			List<String[]> tableList) {

		// 模板转换默认成功
		boolean changeFlag = true;
		try {
			// 获取docx解析对象
			XWPFDocument document = new XWPFDocument(POIXMLDocument.openPackage(inputUrl));
			// 解析替换文本段落对象
			changeText(document, textMap);
			// 解析替换表格对象
			changeTable(document, textMap, tableList);

			// 生成新的word
			File file = new File(outputUrl);
			FileOutputStream stream = new FileOutputStream(file);
			document.write(stream);
			stream.close();

		} catch (IOException e) {
			e.printStackTrace();
			changeFlag = false;
		}

		return changeFlag;

	}

	/**
	 * 替换段落文本
	 * 
	 * @param document
	 *            docx解析对象
	 * @param textMap
	 *            需要替换的信息集合
	 */
	public static void changeText(XWPFDocument document, Map<String, String> textMap) {
		// 获取段落集合
		List<XWPFParagraph> paragraphs = document.getParagraphs();

		for (XWPFParagraph paragraph : paragraphs) {
			// 判断此段落时候需要进行替换
			String text = paragraph.getText();
			if (checkText(text)) {
				List<XWPFRun> runs = paragraph.getRuns();
				for (XWPFRun run : runs) {
					// 替换模板原来位置
					run.setText(changeValue(run.toString(), textMap), 0);
				}
			}
		}

	}

	/**
	 * 替换表格对象方法
	 * 
	 * @param document
	 *            docx解析对象
	 * @param textMap
	 *            需要替换的信息集合
	 * @param tableList
	 *            需要插入的表格信息集合
	 */
	public static void changeTable(XWPFDocument document, Map<String, String> textMap, List<String[]> tableList) {
		// 获取表格对象集合
		List<XWPFTable> tables = document.getTables();
		for (int i = 0; i < tables.size(); i++) {
			// 只处理行数大于等于2的表格，且不循环表头
			XWPFTable table = tables.get(i);
			if (table.getRows().size() > 1) {
				// 判断表格是需要替换还是需要插入，判断逻辑有$为替换，表格无$为插入
				if (checkText(table.getText())) {
					List<XWPFTableRow> rows = table.getRows();
					// 遍历表格,并替换模板
					eachTable(rows, textMap);
				} else {
					// System.out.println("插入"+table.getText());
					insertTable(table, tableList);
				}
			}
		}
	}

	/**
	 * 遍历表格
	 * 
	 * @param rows
	 *            表格行对象
	 * @param textMap
	 *            需要替换的信息集合
	 */
	public static void eachTable(List<XWPFTableRow> rows, Map<String, String> textMap) {
		for (XWPFTableRow row : rows) {
			List<XWPFTableCell> cells = row.getTableCells();
			for (XWPFTableCell cell : cells) {
				// 判断单元格是否需要替换
				if (checkText(cell.getText())) {
					List<XWPFParagraph> paragraphs = cell.getParagraphs();
					for (XWPFParagraph paragraph : paragraphs) {
						List<XWPFRun> runs = paragraph.getRuns();
						for (XWPFRun run : runs) {
							run.setText(changeValue(run.toString(), textMap), 0);
						}
					}
				}
			}
		}
	}

	/**
	 * 为表格插入数据，行数不够添加新行
	 * 
	 * @param table
	 *            需要插入数据的表格
	 * @param tableList
	 *            插入数据集合
	 */
	public static void insertTable(XWPFTable table, List<String[]> tableList) {
		// 创建行,根据需要插入的数据添加新行，不处理表头
		for (int i = 1; i < tableList.size(); i++) {
			XWPFTableRow row = table.createRow();
		}
		// 遍历表格插入数据
		List<XWPFTableRow> rows = table.getRows();
		for (int i = 1; i < rows.size(); i++) {
			XWPFTableRow newRow = table.getRow(i);
			List<XWPFTableCell> cells = newRow.getTableCells();
			for (int j = 0; j < cells.size(); j++) {
				XWPFTableCell cell = cells.get(j);
				cell.setText(tableList.get(i - 1)[j]);
			}
		}

	}

	/**
	 * 判断文本中时候包含$
	 * 
	 * @param text
	 *            文本
	 * @return 包含返回true,不包含返回false
	 */
	public static boolean checkText(String text) {
		boolean check = false;
		if (text.indexOf("$") != -1) {
			check = true;
		}
		return check;

	}

	/**
	 * 匹配传入信息集合与模板
	 * 
	 * @param value
	 *            模板需要替换的区域
	 * @param textMap
	 *            传入信息集合
	 * @return 模板需要替换区域信息集合对应值
	 */
	public static String changeValue(String value, Map<String, String> textMap) {
		Set<Entry<String, String>> textSets = textMap.entrySet();
		for (Entry<String, String> textSet : textSets) {
			// 匹配模板与替换值 格式${key}
			String key = "${" + textSet.getKey() + "}";
			if (value.indexOf(key) != -1) {
				value = textSet.getValue();
			}
		}
		// 模板未匹配到区域替换为空
		if (checkText(value)) {
			value = "";
		}
		return value;
	}


	public static void main(String[] args) throws Exception {

		String path = "D:\\1.doc";
		String out_path = "D:\\2.doc";
		Map<String, String> map = new HashMap<String, String>();
		map.put("{{reportDate}}", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
		map.put("{{applePrice}}", "100.00");
		map.put("{{bananaPrice}}", "200.00");
		// doc
		templateWriteDoc(path, out_path, map);
		// docx
		String pathx = "D:\\cc.docx";
		String out_pathx = "D:\\3.docx";
		// Map<String, String> map1 = templateMarkDocx(pathx);
		templateWriteDocx(pathx, out_pathx, map);
	}
}
