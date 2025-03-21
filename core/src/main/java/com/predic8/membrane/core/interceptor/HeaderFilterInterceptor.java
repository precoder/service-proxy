/* Copyright 2012 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package com.predic8.membrane.core.interceptor;

import com.predic8.membrane.annot.MCChildElement;
import com.predic8.membrane.annot.MCElement;
import com.predic8.membrane.annot.MCTextContent;
import com.predic8.membrane.annot.Required;
import com.predic8.membrane.core.exchange.Exchange;
import com.predic8.membrane.core.http.Header;
import com.predic8.membrane.core.http.HeaderField;
import com.predic8.membrane.core.http.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.predic8.membrane.core.interceptor.HeaderFilterInterceptor.Action.KEEP;
import static com.predic8.membrane.core.interceptor.HeaderFilterInterceptor.Action.REMOVE;
import static com.predic8.membrane.core.interceptor.Outcome.CONTINUE;

/**
 * @description Removes message headers matching a list of patterns.
 * The first matching child element will be acted upon by the filter.
 * @topic 2. Enterprise Integration Patterns
 */
@MCElement(name = "headerFilter")
public class HeaderFilterInterceptor extends AbstractInterceptor {

    private static final Logger log = LoggerFactory.getLogger(HeaderFilterInterceptor.class);

    private List<Rule> rules = new ArrayList<>();

    public HeaderFilterInterceptor() {
        name = "header filter";
    }

    @Override
    public String getShortDescription() {
        return "Filters message headers using a list of regular expressions.";
    }

    public enum Action {KEEP, REMOVE}

    public static class Rule {
        private final Action action;

        private String pattern;
        private Pattern p;

        public Rule(Action action) {
            this.action = action;
        }

        public Rule(String pattern, Action action) {
            this(action);
            setPattern(pattern);
        }

        public String getPattern() {
            return pattern;
        }

        @MCTextContent
        public void setPattern(String pattern) {
            this.pattern = pattern;
            p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        }

        public boolean matches(String header) {
            return p.matcher(header).matches();
        }

        public Action getAction() {
            return action;
        }

    }

    /**
     * @description Contains a Java regex for <i>including</i> message headers.
     */
    @MCElement(name = "include", mixed = true)
    public static class Include extends Rule {
        public Include() {
            super(KEEP);
        }
    }

    /**
     * @description Contains a Java regex for <i>excluding</i> message headers.
     */
    @MCElement(name = "exclude", mixed = true)
    public static class Exclude extends Rule {
        public Exclude() {
            super(REMOVE);
        }
    }

    @Override
    public Outcome handleRequest(Exchange exc) {
        handleMessage(exc.getRequest());
        return CONTINUE;
    }

    @Override
    public Outcome handleResponse(Exchange exc) {
        handleMessage(exc.getResponse());
        return CONTINUE;
    }

    @Override
    public void handleAbort(Exchange exchange) {
        handleMessage(exchange.getResponse());
    }

    private void handleMessage(Message msg) {
        Header h = msg.getHeader();

        for (HeaderField hf : h.getAllHeaderFields())
            for (Rule r : rules)
                if (r.matches(hf.getHeaderName().toString())) {
                    switch (r.getAction()) {
                        case REMOVE:
                            log.debug("Removing HTTP header {}", hf.getHeaderName().toString());
                            h.remove(hf);
                            break;
                        case KEEP:
                            break;
                    }
                    break;
                }
    }

    public List<Rule> getRules() {
        return rules;
    }

    /**
     * @description List of actions to take (either allowing or removing HTTP headers).
     */
    @Required
    @MCChildElement
    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

}
