package com.axelor.apps.pbproject.web;


import com.axelor.apps.pbproject.service.LunchService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("lunch")
public class LunchWebController {

    private final LunchService lunchService;

    @Inject
    public LunchWebController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GET
    @Path("order")
    public Response checkExpenseLine(@QueryParam("l") Long lunch, @QueryParam("p") String portion) {
        if (Objects.isNull(lunch) || Objects.isNull(portion)) {
            return Response.status(400).build();
        }
        lunchService.orderLunch(lunch, portion);
        return Response.accepted().build();
    }
}
