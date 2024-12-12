package com.axelor.apps.pbproject.web;

import com.axelor.apps.pbproject.service.LunchService;
import com.axelor.rpc.Response;
import com.google.inject.Inject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("public")
public class PublicWebController {
    private final LunchService lunchService;

    @Inject
    public PublicWebController(LunchService lunchService) {
        this.lunchService = lunchService;
    }

    @GET
    @Path("all-lunch")
    public Response checkExpenseLine() {
        Response response = new Response();
        response.setData(lunchService.getLunch());
        return response;
    }

}
