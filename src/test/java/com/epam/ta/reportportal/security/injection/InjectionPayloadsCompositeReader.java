package com.epam.ta.reportportal.security.injection;

import org.springframework.util.CompositeIterator;

import java.util.Iterator;

public class InjectionPayloadsCompositeReader implements Iterable<String> {

    private CompositeIterator<String> payloadsIterator = new CompositeIterator();

    private void initReaders() {
        payloadsIterator.add(new GenericInjectionPayloadsReader().iterator());
        payloadsIterator.add(new PostgreInjectionPayloadsReader().iterator());
    }

    public InjectionPayloadsCompositeReader() {
        initReaders();
    }


    @Override
    public Iterator<String> iterator() {
        return payloadsIterator;
    }
}
