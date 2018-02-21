package org.dukecon.server.core

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import groovy.transform.TypeChecked

import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.ext.MessageBodyWriter
import javax.ws.rs.ext.Provider
import java.lang.annotation.Annotation
import java.lang.reflect.Type

/**
 * @author Falk Sippach, falk@jug-da.de, @sippsack
 */
@TypeChecked
@Provider
@Produces("text/csv")
class CsvMessageBodyWriter implements MessageBodyWriter<List> {
    @Override
    boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return List.isAssignableFrom(List.class)
    }

    @Override
    long getSize(List events, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0
    }

    @Override
    void writeTo(List data, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        if (data) {
            CsvMapper mapper = new CsvMapper()
            CsvSchema schema = mapper.schemaFor(data.first().getClass()).withHeader()
            mapper.writer(schema).writeValue(outputStream, data)
        }
    }
}
