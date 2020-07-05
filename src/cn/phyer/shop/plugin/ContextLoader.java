package cn.phyer.shop.plugin;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class ContextLoader extends ContextLoaderListener {

    public ContextLoader() {
    }

    public ContextLoader(WebApplicationContext context) {
        super(context);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()){
            try {
                DriverManager.deregisterDriver(drivers.nextElement());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        super.contextDestroyed(event);
    }
}
