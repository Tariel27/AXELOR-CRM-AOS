package com.axelor.apps.pbproject.web;

import com.axelor.auth.db.repo.UserRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class LunchController {

    @Inject
    private UserRepository userRepository;

    public void reportLunch(ActionRequest request, ActionResponse response) {
        // Подсчет общего количества пользователей
        long totalUsers = userRepository.all().count();

        // Логируем, что метод был вызван и данные подсчитаны
        System.out.println("Total users: " + totalUsers);

        // Создаем структуру данных для передачи в ответ
        Map<String, Object> data = new HashMap<>();
        data.put("total", totalUsers);

        // Передача данных через response
        response.setData(Lists.newArrayList(data));
    }
}
