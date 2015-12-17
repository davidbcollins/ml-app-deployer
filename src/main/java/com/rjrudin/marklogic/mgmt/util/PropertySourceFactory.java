package com.rjrudin.marklogic.mgmt.util;

import com.rjrudin.marklogic.client.LoggingObject;

/**
 * Helper class for factories that depend on a PropertySource for configuring the objects they produce.
 */
public abstract class PropertySourceFactory extends LoggingObject {

    private PropertySource propertySource;

    protected PropertySourceFactory() {
        this(new SystemPropertySource());
    }

    public PropertySourceFactory(PropertySource propertySource) {
        this.propertySource = propertySource;
    }

    protected String getProperty(String name) {
        return propertySource.getProperty(name);
    }

    public void setPropertySource(PropertySource propertySource) {
        this.propertySource = propertySource;
    }
}
