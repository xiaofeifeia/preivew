package com.xph.preview.controller;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xph.preview.model.BaseReturnVO;
import com.xph.preview.service.FileService;
import com.xph.preview.utils.WordPoiTlUtil;

@RestController
public class FillDataController {

	private static final Logger logger = LoggerFactory.getLogger(FillDataController.class);

	/**
	 * 文件路径
	 */
	@Value("${preview.fill.data.file.path}")
	private String fillDataFilePath;

	/**
	 * 文件路径
	 */
	@Value("${preview.upload.file.path}")
	private String uploadFilePath;

	@Autowired
	private FileService fileService;

	/**
	 * 填充数据
	 * 
	 * @param fileName
	 * @param response
	 * @return
	 */
	@RequestMapping("fillData/{fileName}")
	public BaseReturnVO fillData(@PathVariable String fileName, HttpServletResponse response) {
		// 没时间搞啦，我这里写死docx，你可以传入这行数据的id，从数据库获取模板id
		// 要建立三个表一个数据tb_data表，模板表tb_template，还有一个模板参数表tb_template_param
		String templatePath = uploadFilePath + File.separator + fileName;
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("number", 123456);
		param.put("lessee", "啊啊啊");
		param.put("lessee_legal", "ad表");
		param.put("essee_id_no", "werw");
		try {
			// 修改下名称 添加fill_前缀,因为会和模板转成pdf的重合，区分加filldata_
			fileName = "filldata_" + fileName;
			// 填充数据
			String wordPath = WordPoiTlUtil.createWord(templatePath, fillDataFilePath, fileName, param);
			logger.info("填充数据后的路径-->", wordPath);
			// 转成pdf预览
			String outFilePath = fileService.convertPDF(fileName, fillDataFilePath, true);

			ServletOutputStream out = response.getOutputStream();

			// 使用response,将pdf文件以流的方式发送的前段
			FileInputStream in = new FileInputStream(outFilePath);// 读取文件
			// copy文件
			IOUtils.copy(in, out);
			out.close();
			in.close();
		} catch (Exception e) {
			logger.error("填充数据并预览出错", e);
		}
		return BaseReturnVO.success();
	}
}
