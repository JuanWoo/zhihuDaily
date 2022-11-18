package main.common;

import main.story.StoryController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.ext.handler.ContextPathHandler;
import com.jfinal.server.undertow.UndertowServer;


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

	public void configPlugin(Plugins me) {}

	public void configInterceptor(Interceptors me) {}

	public void configHandler(Handlers me) {
		me.add(new ContextPathHandler("base"));
	}

    public static void main(String[] args) {
        UndertowServer.start(Config.class, 80, true);
    }
}
