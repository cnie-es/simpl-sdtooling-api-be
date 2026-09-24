package eu.europa.ec.simpl.sdtoolingbe.properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import eu.europa.ec.simpl.data1.common.enumeration.OfferType;
import eu.europa.ec.simpl.sdtoolingbe.model.client.resourceaddress.Template;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ResourceAddressPropertiesTest {

    @Test
    void testGetSharingMethodIdWhenTemplateIdExists() {
        Template t1 = Template.builder().id("tpl1").label("label1").build();
        Template t2 = Template.builder().id("tpl2").label("label2").build();
        Map<String, List<Template>> providerMap = new HashMap<>();
        providerMap.put("sharingKey1", Arrays.asList(t1, t2));
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.APPLICATION, providerMap);
        ResourceAddressProperties props = new ResourceAddressProperties();
        props.setTemplateMap(templateMap);
        assertEquals("sharingKey1", props.getSharingMethodId("tpl1"));
        assertEquals("sharingKey1", props.getSharingMethodId("tpl2"));
    }

    @Test
    void testGetSharingMethodIdWhenTemplateIdNotFound() {
        Template t1 = Template.builder().id("tpl1").label("label1").build();
        Map<String, List<Template>> providerMap = new HashMap<>();
        providerMap.put("sharingKey1", Collections.singletonList(t1));
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, providerMap);
        ResourceAddressProperties props = new ResourceAddressProperties();
        props.setTemplateMap(templateMap);
        assertNull(props.getSharingMethodId("notfound"));
    }

    @Test
    void testGetSharingMethodIdWhenTemplateMapIsNull() {
        ResourceAddressProperties props = new ResourceAddressProperties();
        props.setTemplateMap(null);
        assertNull(props.getSharingMethodId("tpl1"));
    }

    @Test
    void testGetSharingMethodIdWhenProviderMapIsEmpty() {
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.DATA, new HashMap<>());
        ResourceAddressProperties props = new ResourceAddressProperties();
        props.setTemplateMap(templateMap);
        assertNull(props.getSharingMethodId("tpl1"));
    }

    @Test
    void testGetSharingMethodIdWhenTemplatesListIsNull() {
        Map<String, List<Template>> providerMap = new HashMap<>();
        providerMap.put("sharingKey1", null);
        Map<OfferType, Map<String, List<Template>>> templateMap = new HashMap<>();
        templateMap.put(OfferType.APPLICATION, providerMap);
        ResourceAddressProperties props = new ResourceAddressProperties();
        props.setTemplateMap(templateMap);
        assertNull(props.getSharingMethodId("tpl1"));
    }
}
