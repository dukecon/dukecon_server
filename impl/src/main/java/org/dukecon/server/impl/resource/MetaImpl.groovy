package org.dukecon.server.impl.resource

import groovy.transform.TypeChecked

import javax.ws.rs.Path

import org.dukecon.server.api.model.Conference
import org.dukecon.server.api.model.MetaData
import org.dukecon.server.api.resource.Meta
import org.dukecon.server.api.resource.Meta.GetMetaResponse
import org.springframework.stereotype.Component

/**
 * @author ascheman
 *
 */
@Component
@TypeChecked
public class MetaImpl implements Meta {

	@Override
	public GetMetaResponse getMeta() throws Exception {
		Conference conference = new Conference()
			.withName("DukeCon Demo Workshop")
			.withUrl ("http://dukecon.org/demo")
		MetaData result = new MetaData().withConference(conference)

		return GetMetaResponse.withJsonOK(result)
	}

}
