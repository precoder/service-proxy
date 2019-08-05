package com.predic8.membrane.core.interceptor.ntlm;

import com.predic8.membrane.annot.MCAttribute;
import com.predic8.membrane.annot.MCChildElement;
import com.predic8.membrane.annot.MCElement;
import com.predic8.membrane.core.Router;
import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.http.Header;
import com.predic8.membrane.core.http.HeaderField;
import com.predic8.membrane.core.http.HeaderName;
import com.predic8.membrane.core.http.Request;
import com.predic8.membrane.core.interceptor.AbstractInterceptor;
import com.predic8.membrane.core.interceptor.Outcome;
import com.predic8.membrane.core.transport.http.HttpClient;
import com.predic8.membrane.core.transport.http.client.HttpClientConfiguration;
import org.apache.http.impl.auth.NTLMEngineTrampoline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import static com.predic8.membrane.core.interceptor.Outcome.CONTINUE;
import static com.predic8.membrane.core.interceptor.Outcome.RETURN;

@MCElement(name = "ntlm")
public class NtlmInterceptor extends AbstractInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(NtlmInterceptor.class);

    NTLMRetriever NTLMRetriever;
    String userHeaderName;
    String passwordHeaderName;
    String domainHeaderName;
    String workstationHeaderName;
    private HttpClient httpClient;

    //temporary not configurable


    @Override
    public void init(Router router) throws Exception {
        super.init(router);
        if(NTLMRetriever == null)
            NTLMRetriever = new HeaderNTLMRetriever(userHeaderName,passwordHeaderName, domainHeaderName,workstationHeaderName);


    }

    @Override
    public Outcome handleResponse(Exchange exc) throws Exception {
        httpClient = createClient();

        String originalRequestUrl = buildRequestUrl(exc);

        if(exc.getResponse().getHeader().getWwwAuthenticate() == null)
            return CONTINUE;

        List<HeaderField> wwwAuthenticate = exc.getResponse().getHeader().getValues(new HeaderName(Header.WWW_AUTHENTICATE));
        if(wwwAuthenticate.stream().filter(h -> h.getValue().toLowerCase().equals("ntlm")).count() == 0)
            return CONTINUE;

        prepareStreamByEmptyingIt(exc);

        String user = getNTLMRetriever().fetchUsername(exc);
        String pass = getNTLMRetriever().fetchPassword(exc);
        String domain = getNTLMRetriever().fetchDomain(exc) != null ? getNTLMRetriever().fetchDomain(exc) : null;
        String workstation = getNTLMRetriever().fetchWorkstation(exc) != null ? getNTLMRetriever().fetchWorkstation(exc) : null;

        Exchange reqT1 = new Request.Builder().get(originalRequestUrl).header("Authorization", "NTLM " + NTLMEngineTrampoline.getResponseFor(null,null,null,null,null)).buildExchange();
        reqT1.getRequest().getHeader().add("Connection","keep-alive");
        reqT1.setTargetConnection(exc.getTargetConnection());

        Exchange resT1 = httpClient.call(reqT1);
        prepareStreamByEmptyingIt(resT1);

        Exchange reqT3 = new Request.Builder().get(originalRequestUrl).header("Authorization", "NTLM " + NTLMEngineTrampoline.getResponseFor(getT2Payload(resT1),user,pass,domain,workstation)).buildExchange();
        reqT3.getRequest().getHeader().add("Connection","keep-alive");
        reqT3.setTargetConnection(resT1.getTargetConnection());

        Exchange finalResult = httpClient.call(reqT3);
        exc.setResponse(finalResult.getResponse());
        exc.setTargetConnection(finalResult.getTargetConnection());

        return CONTINUE;
    }

    private String getT2Payload(Exchange resT1) {
        return resT1.getResponse().getHeader().getWwwAuthenticate().split(Pattern.quote(" "))[1];
    }

    private String buildRequestUrl(Exchange exc) {
        return (exc.getTargetConnection().getSslProvider() != null ? "https" : "http") + "://" + exc.getRequest().getHeader().getHost() + "/";
    }

    private void prepareStreamByEmptyingIt(Exchange exc) {
        try {
            exc.getResponse().getBody().getContent();
        } catch (IOException e) {
            LOG.warn("",e);
        }
    }

    private HttpClient createClient() {
        HttpClientConfiguration configuration = new HttpClientConfiguration();
        return new HttpClient(configuration);
    }

    @MCChildElement(order = 1)
    public NtlmInterceptor setNTLMRetriever(NTLMRetriever NTLMRetriever) {
        this.NTLMRetriever = NTLMRetriever;
        return this;
    }

    public NTLMRetriever getNTLMRetriever() {
        return NTLMRetriever;
    }

    public String getUserHeaderName() {
        return userHeaderName;
    }

    @MCAttribute(attributeName = "user")
    public void setUserHeaderName(String userHeaderName) {
        this.userHeaderName = userHeaderName;
    }

    public String getPasswordHeaderName() {
        return passwordHeaderName;
    }

    @MCAttribute(attributeName = "pass")
    public void setPasswordHeaderName(String passwordHeaderName) {
        this.passwordHeaderName = passwordHeaderName;
    }

    public String getDomainHeaderName() {
        return domainHeaderName;
    }

    @MCAttribute(attributeName = "domain")
    public void setDomainHeaderName(String domainHeaderName) {
        this.domainHeaderName = domainHeaderName;
    }

    public String getWorkstationHeaderName() {
        return workstationHeaderName;
    }

    @MCAttribute(attributeName = "workstation")
    public void setWorkstationHeaderName(String workstationHeaderName) {
        this.workstationHeaderName = workstationHeaderName;
    }
}