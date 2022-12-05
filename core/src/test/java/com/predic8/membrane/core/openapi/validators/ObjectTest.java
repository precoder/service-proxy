package com.predic8.membrane.core.openapi.validators;

import com.fasterxml.jackson.databind.*;
import com.predic8.membrane.core.openapi.*;
import com.predic8.membrane.core.openapi.model.*;
import org.junit.*;

import java.math.*;
import java.util.*;

import static com.predic8.membrane.core.openapi.util.JsonUtil.*;
import static com.predic8.membrane.core.openapi.util.TestUtils.getResourceAsStream;
import static org.junit.Assert.*;


public class ObjectTest {

    OpenAPIValidator validator;

    @Before
    public void setUp() {
        validator = new OpenAPIValidator(getResourceAsStream(this,"/openapi/object.yml"));
    }

    @Test
    public void numberAsObject() {
        // Delete new JsonBody
        JsonNode object = getNumbers("object", new BigDecimal(7));
        ValidationErrors errors = validator.validate(Request.post().path("/object").body(new JsonBody(object)));
//        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
        ValidationError e = errors.get(0);
        assertEquals(400,e.getContext().getStatusCode());
        assertEquals("REQUEST/BODY#/object", e.getContext().getLocationForRequest());
        assertTrue(e.getMessage().contains("not an object"));
    }

    @Test
    public void stringAsObject() {
        ValidationErrors errors = validator.validate(Request.post().path("/object").body(new JsonBody(getStrings("object","Hossa"))));
        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
    }

    @Test
    public void additionalPropertiesTrue() {

        Map<String,String> nm = new HashMap<>();
        nm.put("a","foo");
        nm.put("b","bar");

        Map<String,Map<String,String>> m = new HashMap<>();
        m.put("additionalPropertiesTrue",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(0,errors.size());
    }

    @Test
    public void additionalPropertiesFalse() {

        Map<String,String> nm = new HashMap<>();
        nm.put("a","foo");
        nm.put("b","bar");

        Map<String,Map<String,String>> m = new HashMap<>();
        m.put("additionalPropertiesFalse",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
        ValidationError e = errors.get(0);
        assertEquals("REQUEST/BODY#/additionalPropertiesFalse", e.getContext().getLocationForRequest());
    }

    @Test
    public void additionalPropertiesStringValid() {

        Map<String,String> nm = new HashMap<>();
        nm.put("foo", "bar");
        nm.put("unbekannt1","abc");

        Map<String,Map<String,String>> m = new HashMap<>();
        m.put("additionalPropertiesString",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(0,errors.size());
    }

    @Test
    public void additionalPropertiesStringInvalid() {

        Map<String,Object> nm = new HashMap<>();
        nm.put("foo", "bar");
        nm.put("illegal", 7);
        nm.put("unbekannt1","abc");

        Map<String,Map<String,Object>> m = new HashMap<>();
        m.put("additionalPropertiesString",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
        ValidationError e = errors.get(0);
        assertEquals("/additionalPropertiesString/illegal",e.getContext().getJSONpointer());
    }

    @Test
    public void additionalPropertiesObjectValid() {

        Map<String,Object> unbekannt = new HashMap<>();
        unbekannt.put("a","foo");
        unbekannt.put("b",7);

        Map<String,Object> nm = new HashMap<>();
        nm.put("foo", "bar");
        nm.put("unbekannt",unbekannt);

        Map<String,Object> m = new HashMap<>();
        m.put("additionalPropertiesComplex",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(0,errors.size());
    }

    @Test
    public void additionalPropertiesObjectInvalid() {

        Map<String,Object> unbekannt = new HashMap<>();
        unbekannt.put("a","foo");
        unbekannt.put("b",7);

        Map<String,Object> nm = new HashMap<>();
        nm.put("foo", "bar");
        nm.put("unbekannt",unbekannt);
        nm.put("illegal",3);

        Map<String,Map<String,Object>> m = new HashMap<>();
        m.put("additionalPropertiesComplex",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
        assertEquals(1,errors.size());
        ValidationError e = errors.get(0);
        assertEquals("/additionalPropertiesComplex/illegal",e.getContext().getJSONpointer());
    }

    @Test
    public void minMaxPropertiesTooLessInvalid() {

        Map<String,String> nm = new HashMap<>();
        nm.put("a", "bar");

        Map<String,Map<String,String>> m = new HashMap<>();
        m.put("minMaxProperties",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
        ValidationError e = errors.get(0);
        assertEquals("/minMaxProperties",e.getContext().getJSONpointer());
        assertEquals("REQUEST/BODY#/minMaxProperties", e.getContext().getLocationForRequest());

    }

    @Test
    public void minMaxPropertiesTooMuchInvalid() {

        Map<String,String> nm = new HashMap<>();
        nm.put("a", "bar");
        nm.put("b", "bar");
        nm.put("c", "bar");
        nm.put("d", "bar");
        nm.put("e", "bar");
        nm.put("f", "bar");

        Map<String,Map<String,String>> m = new HashMap<>();
        m.put("minMaxProperties",nm);

        ValidationErrors errors = validator.validate(Request.post().path("/object").body(mapToJson(m)));
//        System.out.println("errors = " + errors);
        assertEquals(1,errors.size());
        ValidationError e = errors.get(0);
        assertEquals("/minMaxProperties",e.getContext().getJSONpointer());
    }
}