package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.LunchService;
import com.axelor.rpc.Response;
import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("public")
public class PublicWebController {
    private final LunchService lunchService;

    @Inject
    public PublicWebController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GET
    @Path("all-lunch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkExpenseLine() {
        Response response = new Response();
        String lunch = lunchService.getLunch();
        response.setData(lunch.isEmpty() ? "Пусто" : lunch);
        return response;
    }

}
