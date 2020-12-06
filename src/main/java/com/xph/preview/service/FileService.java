package com.xph.preview.service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jodconverter.DocumentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xph.preview.exception.MessageException;

@Service
public class FileService {

	/**
	 * 转成pdf的文件路径
	 */
	@Value("${preview.pdf.file.path}")
	private String pdfFilePath;

	private static final String PDF_SUFFIX = ".pdf";

	private static final List<String> suffixs = Arrays.asList("doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt");

	/**
	 * 转换器直接注入
	 */
	@Autowired
	private DocumentConverter converter;

	/**
	 * 将office文件转成pdf
	 * 
	 * @param fileName
	 *            文件名
	 * @param basePath
	 *            基础路径
	 * @param force
	 *            是否强制重新生成pdf
	 * @return
	 * @throws Exception
	 */
	public String convertPDF(String fileName, String basePath, boolean force) throws Exception {

		if (StringUtils.isAnyBlank(basePath, fileName)) {
			throw new MessageException("basePath 或  fileName 参数为空");
		}

		File file = new File(basePath + File.separator + fileName);// 需要转换的文件

		File newFile = new File(pdfFilePath);// 转换之后文件生成的地址
		if (!newFile.exists()) {
			newFile.mkdirs();
		}

		// 后缀名
		String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		// 文件名
		String orgFileName = fileName.substring(0, fileName.lastIndexOf("."));

		String outFilePath;
		if (suffixs.contains(suffix)) {
			// 进行转换成pdf
			outFilePath = pdfFilePath + File.separator + orgFileName + PDF_SUFFIX;
			// 判断pdf文件是否已经存在
			File pdfFile = new File(outFilePath);

			if (force || !pdfFile.exists()) {
				// 文件转化
				converter.convert(file).to(pdfFile).execute();
			}
		} else {
			// 直接在上传的目录获取文件
			outFilePath = basePath + File.separator + fileName;
		}
		return outFilePath;
	}

	public void fillData() {

	}
}
