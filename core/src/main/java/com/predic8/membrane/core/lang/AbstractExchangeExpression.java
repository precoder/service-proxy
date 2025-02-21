/* Copyright 2025 predic8 GmbH, www.predic8.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package com.predic8.membrane.core.lang;

public abstract class AbstractExchangeExpression implements ExchangeExpression {

    /**
     * String from which the expression was created
     */
    protected final String expression;

    /**
     * Should only called from subclasses cause ExchangeExpression offers a getInstance method
     * @param expression String with expression like "foo ${property.baz} bar"
     */
    protected AbstractExchangeExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return expression;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractExchangeExpression aee)) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        return expression.equals(aee.expression);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + expression.hashCode();
    }
}
