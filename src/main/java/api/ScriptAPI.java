package api;

import api.domain.Script;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import common.domain.PageableParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.ScriptService;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by i303874 on 12/23/14.
 */
@Path("/dynamicmashup/script")
public class ScriptAPI implements ExceptionMapper<Exception> {
    private final static Logger logger = LoggerFactory.getLogger(ScriptAPI.class);

    private ScriptService scriptService;

    public ScriptAPI(ScriptService scriptService) {
        this.scriptService = scriptService;
    }

    @GET
    @Path("/{id}/run")
    @Produces(MediaType.APPLICATION_JSON)
    public void runScripts(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
        Futures.addCallback(scriptService.runScript(id), new FutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                asyncResponse.resume(Response.ok(result).build());
            }

            @Override
            public void onFailure(Throwable throwable) {
                asyncResponse.resume(throwable);
            }
        });
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getScript(@Suspended final AsyncResponse asyncResponse, @PathParam("id") String id) {
        Futures.addCallback(scriptService.getScript(id), new FutureCallback<service.domain.Script>() {
            @Override
            public void onSuccess(service.domain.Script script) {
                asyncResponse.resume(Response.ok(new Script(script)).build());
            }

            @Override
            public void onFailure(Throwable throwable) {
                asyncResponse.resume(throwable);
            }
        });
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public void listScripts(@Suspended final AsyncResponse asyncResponse, @QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        Futures.addCallback(scriptService.listScripts(new PageableParameter(page, pageSize)), new FutureCallback<List<service.domain.Script>>() {
            @Override
            public void onSuccess(List<service.domain.Script> scripts) {
                asyncResponse.resume(Response.ok(scripts.stream().map((script) -> {
                    return new Script(script);
                }).collect(Collectors.toList())).build());
            }

            @Override
            public void onFailure(Throwable throwable) {
                asyncResponse.resume(throwable);
            }
        });
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public void newScript(@Suspended final AsyncResponse asyncResponse, @Context UriInfo uriInfo, Script script) {
        Futures.addCallback(scriptService.newScript(script), new FutureCallback<service.domain.Script>() {
            @Override
            public void onSuccess(service.domain.Script script) {
                asyncResponse.resume(Response.ok().contentLocation(uriInfo.getAbsolutePathBuilder().path(script.getId()).build()).entity(new Script(script)).build());
            }

            @Override
            public void onFailure(Throwable throwable) {
                asyncResponse.resume(throwable);
            }
        });
    }

    @Override
    public Response toResponse(Exception e) {
        logger.error("an API call caused an exception", e);
        
        if (e instanceof NoSuchElementException) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } else if (e instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }
    }
}
