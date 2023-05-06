package com.past.common;

import com.past.domain.vo.BannersVO;
import com.past.domain.vo.GoodsTypesVO;
import com.past.domain.vo.MemoryVO;
import com.past.service.BannersService;
import com.past.service.GoodsTypesService;
import com.past.service.MemoryService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;
import java.util.stream.Collectors;

/**
 * springboot容器的监听器
 */
@WebListener
public class ApplicationContextListener implements ServletContextListener {

    /**
     * 监听器初始化
     * @param sce 容器事件
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext app = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
        ServletContext application = sce.getServletContext();
        if (app != null) {
            GoodsTypesService goodsTypesService = app.getBean(GoodsTypesService.class);
            MemoryService memoryService = app.getBean(MemoryService.class);
            BannersService bannersService = app.getBean(BannersService.class);
            List<GoodsTypesVO> typesVOList = goodsTypesService.selectAll()
                    .stream().filter(gt -> gt.getStatus() == 0).collect(Collectors.toList());
            List<MemoryVO> memoryVOList = memoryService.selectAll()
                    .stream().filter(m -> m.getStatus() == 0).collect(Collectors.toList());
            List<BannersVO> bannersVOList = bannersService.selectAll()
                    .stream().filter(b -> b.getStatus() == 0).collect(Collectors.toList());
            application.setAttribute("goodsTypeList", typesVOList);
            application.setAttribute("memoryList", memoryVOList);
            application.setAttribute("bannerList", bannersVOList);
        }

    }

    /**
     * 监听器销毁
     * @param sce 容器事件
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
