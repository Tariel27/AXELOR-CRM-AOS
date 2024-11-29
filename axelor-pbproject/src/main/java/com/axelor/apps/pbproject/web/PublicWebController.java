package com.axelor.apps.pbproject.web;


import com.axelor.apps.pbproject.db.Lunch;
import com.axelor.apps.pbproject.service.LunchService;
import com.axelor.rpc.Response;
import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("public")
public class PublicWebController {
    private final LunchService lunchService;

    @Inject
    public PublicWebController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GET
    @Path("dinner")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkExpenseLine() {
        Response response = new Response();
        Map<String, Object> data = new HashMap<>();
        List<Lunch> lunchYesterday = lunchService.getLunchYesterday();
        data.put("lunch",lunchYesterday);
        response.setData(data);
        return response;
    }

}
