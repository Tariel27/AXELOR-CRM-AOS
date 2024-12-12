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
    public Response checkExpenseLine(@QueryParam("d") Integer dishPosition, @QueryParam("p") String portion) {
        if (Objects.isNull(dishPosition) || Objects.isNull(portion)) {
            return Response.status(400).build();
        }
        lunchService.orderLunch(dishPosition, portion);
        return Response.accepted().build();
    }
}
