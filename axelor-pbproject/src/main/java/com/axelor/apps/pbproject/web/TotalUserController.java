package com.axelor.apps.pbproject.web;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.auth.db.repo.UserRepository;
import com.google.common.collect.Lists;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public class TotalUserController {

    @Inject
    private UserRepository userRepository;

    public void reportTotalUsers(ActionRequest request, ActionResponse response) {
        long totalUsers = userRepository.all().count();

        Map<String, Object> data = new HashMap<>();
        data.put("total", totalUsers);

        response.setData(Lists.newArrayList(data));
    }
}
