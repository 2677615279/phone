package com.past;

import com.past.dao.UsersDAO;
import com.past.service.*;
import com.past.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class PhoneApplicationTests {

    @Autowired
    UsersDAO usersDAO;

    @Autowired
    UsersService usersService;

    @Autowired
    ProvincesService provincesService;

    @Autowired
    CitiesService citiesService;

    @Autowired
    AreasService areasService;

    @Autowired
    GoodsTypesService goodsTypesService;

    @Autowired
    MemoryService memoryService;

    @Autowired
    BannersService bannersService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    PermissionsService permissionsService;

    @Autowired
    RolesService rolesService;

    @Autowired
    EvaluatesService evaluatesService;

    @Test
    void contextLoads() {
//        List<Users> all = usersDAO.selectAll();
//        PageRequest<UsersDTO> pageRequest = new PageRequest<>();
//        pageRequest.setPageNum(9);
//        pageRequest.setPageSize(10);
//        PageResult<Users> pageResult = new PageResult<>(pageRequest, all);
//        System.out.println(pageResult);

//        PageRequest<UsersDTO> pageRequest = new PageRequest<>();
//        pageRequest.setPageNum(2);
//        pageRequest.setPageSize(3);
//        UsersDTO dto = new UsersDTO();
//        dto.setEmail("@qq.com");
//        pageRequest.setQuery(dto);
//        List<Users> list = usersDAO.selectByPrincipalContaining(pageRequest);
//        PageResult<Users> pageResult = new PageResult<>(pageRequest, list);
//        pageResult.getData().forEach(System.out::println);

//        usersService.selectAll().forEach(System.out::println);

        provincesService.selectAll();
        citiesService.selectAll();
        areasService.selectAll();
        goodsTypesService.selectAll();
        memoryService.selectAll();
        bannersService.selectAll();
        goodsService.selectAll();
        permissionsService.selectAll();
        rolesService.selectAll();

        System.out.println(IpUtil.getLocalIP());
    }

}
