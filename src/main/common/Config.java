package main.common;

import main.story.ImgUrlModel;
import main.story.StoryController;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.CaseInsensitiveContainerFactory;
import com.jfinal.plugin.activerecord.dialect.OracleDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;

/**
 * @desc
 * @author	JuanWoo
 * @date	2015年11月28日
 **/

public class Config extends JFinalConfig {
	public void configConstant(Constants me) {
		me.setDevMode(true);
	}

	public void configRoute(Routes me) {
		me.add("/", StoryController.class, "/story");
	}

	public void configPlugin(Plugins me) {
		loadPropertyFile("property/db.properties");
		C3p0Plugin c3p0Plugin = new C3p0Plugin(getProperty("jdbcUrl"), getProperty("user"), getProperty("password"));
		c3p0Plugin.setDriverClass(getProperty("driverClass"));
		me.add(c3p0Plugin);
		ActiveRecordPlugin arp = new ActiveRecordPlugin(c3p0Plugin);
		arp.setContainerFactory(new CaseInsensitiveContainerFactory());
		arp.setDialect(new OracleDialect());
		arp.addMapping("img_url", "id", ImgUrlModel.class);
		me.add(arp);
	}

	public void configInterceptor(Interceptors me) {}

	public void configHandler(Handlers me) {}
}
