package com.xph.preview.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author xiaoph
 *
 */
@Controller
public class IndexController {

	/**
	 * 跳转到index页面
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/", "/index" })
	public String index() {
		return "index";
	}
}
