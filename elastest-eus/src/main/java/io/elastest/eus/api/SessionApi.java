package io.elastest.eus.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.elastest.eus.api.model.AudioLevel;
import io.elastest.eus.api.model.ColorValue;
import io.elastest.eus.api.model.Event;
import io.elastest.eus.api.model.EventSubscription;
import io.elastest.eus.api.model.EventValue;
import io.elastest.eus.api.model.Latency;
import io.elastest.eus.api.model.Quality;
import io.elastest.eus.api.model.StatsValue;
import io.elastest.eus.api.model.UserMedia;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "session", description = "the session API")
public interface SessionApi {

    @ApiOperation(value = "Remove a subscription", notes = "", response = Void.class, tags = {
            "event subscription", })

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = Void.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = Void.class),
            @ApiResponse(code = 404, message = "Subscription not found", response = Void.class) })

    @RequestMapping(value = "/session/{sessionId}/event/{subscriptionId}", method = RequestMethod.DELETE)
    ResponseEntity<Void> deleteSubscription(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Subscription identifier (previously subscribed)", required = true) @PathVariable("subscriptionId") String subscriptionId

    );

    @ApiOperation(value = "Read the audio level of a given element (audio|video tag)", notes = "", response = AudioLevel.class, tags = {
            "basic media evaluation", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = AudioLevel.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = AudioLevel.class),
            @ApiResponse(code = 404, message = "No such element", response = AudioLevel.class) })
    @RequestMapping(value = "/session/{sessionId}/element/{elementId}/audio", produces = {
            "application/json" }, method = RequestMethod.GET)
    ResponseEntity<AudioLevel> getAudioLevel(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Element identifier (previously located)", required = true) @PathVariable("elementId") String elementId

    );

    @ApiOperation(value = "Read the RGB color of the coordinates of a given element", notes = "", response = ColorValue.class, tags = {
            "basic media evaluation", })

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = ColorValue.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = ColorValue.class),
            @ApiResponse(code = 404, message = "No such element", response = ColorValue.class) })

    @RequestMapping(value = "/session/{sessionId}/element/{elementId}/color", produces = {
            "application/json" }, method = RequestMethod.GET)

    ResponseEntity<ColorValue> getColorByCoordinates(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Element identifier (previously located)", required = true) @PathVariable("elementId") String elementId,
            @ApiParam(value = "Coordinate in x-axis", defaultValue = "0") @RequestParam(value = "x", required = false, defaultValue = "0") Integer x,
            @ApiParam(value = "Coordinate in y-axis", defaultValue = "0") @RequestParam(value = "y", required = false, defaultValue = "0") Integer y);

    @ApiOperation(value = "Read the value of event for a given subscription", notes = "", response = EventValue.class, tags = {
            "event subscription", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = EventValue.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = EventValue.class),
            @ApiResponse(code = 404, message = "No such subscription", response = EventValue.class) })
    @RequestMapping(value = "/session/{sessionId}/event/{subscriptionId}", produces = {
            "application/json" }, method = RequestMethod.GET)
    ResponseEntity<List<StatsValue>> getStats(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Subscription identifier (previously subscribed)", required = true) @PathVariable("subscriptionId") String subscriptionId);

    @ApiOperation(value = "Read the WebRTC stats", notes = "", response = StatsValue.class, responseContainer = "List", tags = {
            "WebRTC stats", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = StatsValue.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = StatsValue.class),
            @ApiResponse(code = 404, message = "No such subscription", response = StatsValue.class) })
    @RequestMapping(value = "/session/{sessionId}/stats", produces = {
            "application/json" }, method = RequestMethod.GET)
    ResponseEntity<EventValue> getSubscriptionValue(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Identifier of peerconnection") @RequestParam(value = "peerconnectionId", required = false) String peerconnectionId

    );

    @ApiOperation(value = "Set user media for WebRTC", notes = "", response = Void.class, tags = {
            "user media", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = Void.class),
            @ApiResponse(code = 400, message = "Invalid media", response = Void.class),
            @ApiResponse(code = 404, message = "URL not found", response = Void.class) })
    @RequestMapping(value = "/session/{sessionId}/usermedia", consumes = {
            "application/json" }, method = RequestMethod.POST)
    ResponseEntity<Void> setUserMedia(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Media URL to take WebRTC user media", required = true) @RequestBody UserMedia body);

    @ApiOperation(value = "Measure end-to-end latency of a WebRTC session", notes = "The E2E latency calculation is done comparing the media in P2P WebRTC communication (presenter-viewer)", response = EventSubscription.class, tags = {
            "advance media evaluation", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = EventSubscription.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = EventSubscription.class),
            @ApiResponse(code = 404, message = "No such element", response = EventSubscription.class) })
    @RequestMapping(value = "/session/{sessionId}/element/{elementId}/latency", produces = {
            "application/json" }, consumes = {
                    "application/json" }, method = RequestMethod.POST)
    ResponseEntity<EventSubscription> subscribeToEvent(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Element identifier (previously located)", required = true) @PathVariable("elementId") String elementId,
            @ApiParam(value = "Definition of WebRTC producer (presenter) and sample rate (in ms)", required = true) @RequestBody Latency body

    );

    @ApiOperation(value = "Measure quality (audio|video) of a WebRTC session", notes = "The quality indicator is calculated comparing the media in P2P WebRTC communication (presenter-viewer)", response = EventSubscription.class, tags = {
            "advance media evaluation", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = EventSubscription.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = EventSubscription.class),
            @ApiResponse(code = 404, message = "No such element", response = EventSubscription.class) })
    @RequestMapping(value = "/session/{sessionId}/element/{elementId}/quality", produces = {
            "application/json" }, consumes = {
                    "application/json" }, method = RequestMethod.POST)
    ResponseEntity<EventSubscription> subscribeToLatency(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Element identifier (previously located)", required = true) @PathVariable("elementId") String elementId,
            @ApiParam(value = "Definition of WebRTC producer (presenter), selection of QoE algorithm, and sample rate (in ms)", required = true) @RequestBody Quality body

    );

    @ApiOperation(value = "Subscribe to a given event within an element", notes = "", response = EventSubscription.class, tags = {
            "event subscription", })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = EventSubscription.class),
            @ApiResponse(code = 400, message = "Invalid session identifier", response = EventSubscription.class),
            @ApiResponse(code = 403, message = "Forbidden", response = EventSubscription.class),
            @ApiResponse(code = 404, message = "No such element", response = EventSubscription.class) })
    @RequestMapping(value = "/session/{sessionId}/element/{elementId}/event", produces = {
            "application/json" }, consumes = {
                    "application/json" }, method = RequestMethod.POST)
    ResponseEntity<EventSubscription> subscribeToQuality(
            @ApiParam(value = "Session identifier (previously established)", required = true) @PathVariable("sessionId") String sessionId,
            @ApiParam(value = "Element identifier (previously located)", required = true) @PathVariable("elementId") String elementId,
            @ApiParam(value = "Event name to be subscribed", required = true) @RequestBody Event body);

}
