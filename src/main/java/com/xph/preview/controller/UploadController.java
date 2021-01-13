package com.xph.preview.controller;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xph.preview.exception.MessageException;
import com.xph.preview.model.BaseReturnVO;

@RestController
public class UploadController {

	private static final Logger logger = LoggerFactory.getLogger(PreviewController.class);

	/**
	 * 文件路径
	 */
	@Value("${preview.upload.file.path}")
	private String uploadFilePath;

	/**
	 * 文件上传
	 * 
	 * @param file
	 * @return
	 */
	@RequestMapping("upload")
	public BaseReturnVO upload(@RequestParam("files") MultipartFile[] files) {

		if (files.length == 0) {
			throw new MessageException("上传的文件为空");
		}

		try {
			for (MultipartFile file : files) {
				String fileName = file.getOriginalFilename();
				File newFile = new File(uploadFilePath);
				if (!newFile.exists()) {
					newFile.mkdirs();
				}
				File dest = new File(newFile.getAbsolutePath() + File.separator + fileName);
				dest.deleteOnExit();// 如果存在删除
				file.transferTo(dest);
			}
		} catch (IOException e) {

			logger.error("文件上传失败", e);
			return BaseReturnVO.error();
		}

		logger.info("文件上传成功");
		return BaseReturnVO.success();

	}
}
