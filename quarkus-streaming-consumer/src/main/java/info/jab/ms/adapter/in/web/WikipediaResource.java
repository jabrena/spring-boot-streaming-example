package info.jab.ms.adapter.in.web;

import info.jab.ms.application.port.in.RecentChangeQuery;
import info.jab.ms.application.port.in.StreamRecentChangesUseCase;
import io.smallrye.mutiny.Multi;
import io.vertx.core.http.HttpServerResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestStreamElementType;

@Path("/api/wikipedia/recent-changes")
@ApplicationScoped
public class WikipediaResource {

    private final StreamRecentChangesUseCase useCase;

    @Inject
    public WikipediaResource(StreamRecentChangesUseCase useCase) {
        this.useCase = useCase;
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<RecentChangeResponse> streamRecentChanges(
            @QueryParam("wiki") String wiki,
            @QueryParam("includeBots") @DefaultValue("false") Boolean includeBots,
            @QueryParam("limit") Long limit,
            @Context HttpServerResponse response
    ) {
        response.putHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
        response.putHeader("X-Accel-Buffering", "no");

        return useCase.streamRecentChanges(new RecentChangeQuery(wiki, includeBots, limit))
                .map(RecentChangeResponse::fromDomain);
    }
}
