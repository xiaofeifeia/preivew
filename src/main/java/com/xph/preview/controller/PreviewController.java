package com.xph.preview.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xph.preview.exception.MessageException;
import com.xph.preview.model.BaseReturnVO;
import com.xph.preview.service.FileService;

/**
 * @author : xph
 */
@RestController
public class PreviewController {

	private static final Logger logger = LoggerFactory.getLogger(PreviewController.class);

	/**
	 * 文件路径
	 */
	@Value("${preview.upload.file.path}")
	private String uploadFilePath;

	@Autowired
	private FileService fileService;

	/**
	 * 获取上传文件列表
	 * 
	 * @return
	 */
	@RequestMapping("fileList")
	public BaseReturnVO fileList() {

		File newFile = new File(uploadFilePath);

		if (!newFile.exists()) {
			newFile.mkdirs();
		}

		List<String> files = new ArrayList<>();

		File[] listFiles = newFile.listFiles();// 不要有层级，不然要递归调用
		if (listFiles != null && listFiles.length > 0) {
			for (File file : listFiles) {
				files.add(file.getName());
			}
		}
		return new BaseReturnVO(files);
	}

	/**
	 * 根据文件名预览
	 * 
	 * @param fileName
	 * @param response
	 * @return
	 */
	@RequestMapping("previewPDF/{fileName}")
	public BaseReturnVO previewPDF(@PathVariable String fileName, HttpServletResponse response) {
		InputStream in = null;
		ServletOutputStream out = null;
		try {
			if (StringUtils.isBlank(fileName)) {
				throw new MessageException("fileName 参数为空");
			}

			String outFilePath = fileService.convertPDF(fileName, uploadFilePath, true);

			out = response.getOutputStream();

			// 使用response,将pdf文件以流的方式发送的前段
			in = new FileInputStream(outFilePath);// 读取文件
			// copy文件
			IOUtils.copy(in, out);

		} catch (Exception e) {
			logger.error("预览pdf出错", e);
		} finally {
			try {
				out.close();
				in.close();
			} catch (Exception e) {
				logger.error("close error", e);
			}
		}
		return BaseReturnVO.success();
	}

}
