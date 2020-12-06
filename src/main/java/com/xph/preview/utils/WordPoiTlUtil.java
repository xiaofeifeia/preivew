package com.xph.preview.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deepoove.poi.XWPFTemplate;

/**
 * @author xph
 * @Description: Poi-tl 基于Apache POI的
 */
public class WordPoiTlUtil {

	private static Logger logger = LoggerFactory.getLogger(WordPoiTlUtil.class);

	/**
	 * 根据模板填充内容生成word 调用方法参考下面的main方法，详细文档参考官方文档
	 * Poi-tl模板引擎官方文档：http://deepoove.com/poi-tl/
	 *
	 * @param templatePath
	 *            word模板文件路径
	 * @param fileDir
	 *            生成的文件存放地址
	 * @param fileName
	 *            生成的文件名,不带格式。假如要生成abc.docx，则fileName传入abc即可
	 * @param param
	 *            替换的参数集合
	 * @return 生成word成功返回生成的文件的路径，失败返回空字符串
	 */
	public static String createWord(String templatePath, String fileDir, String fileName, Map<String, Object> param) {

		// 生成的word格式,只能是docx格式

		File dir = new File(fileDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		String filePath = fileDir + File.separator + fileName;
		// 读取模板templatePath并将paramMap的内容填充进模板，即编辑模板+渲染数据
		XWPFTemplate template = XWPFTemplate.compile(templatePath).render(param);
		try {
			// 将填充之后的模板写入filePath
			template.writeToFile(filePath);
			template.close();
		} catch (Exception e) {
			logger.error("生成word异常", e);
			return "";
		}
		return filePath;
	}

	public static void main(String[] args) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("number", 123456);
		param.put("lessee", "啊啊啊");
		param.put("lessee_legal", "ad表");
		param.put("essee_id_no", "werw");
		// 渲染图片
		// params.put("picture", new PictureRenderData(120, 120, "D:\\wx.png"));
		String templatePath = "D:/模板1.docx";
		String fileDir = "D:/template/";
		String fileName = "测试文档1";

		String wordPath = WordPoiTlUtil.createWord(templatePath, fileDir, fileName, param);
		System.out.println("生成文档路径：" + wordPath);
	}
}
