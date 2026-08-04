/*
 * Copyright 2004-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.slim3.gen.processor;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.seasar.aptina.unit.AptinaTestCase;
import org.seasar.aptina.unit.SourceNotGeneratedException;
import org.slim3.test.model.*;

/**
 * @author vvakame
 * 
 */
public class ModelProcessorTest extends AptinaTestCase {

    private static final String[] GENERATED_SOURCES = {
        "AttributeParameterSampleModelMeta.java",
        "AttributeSampleModelMeta.java",
        "BasicModelMeta.java",
        "ImplementComparableModelMeta.java",
        "ListenerModelMeta.java",
        "RefAModelMeta.java",
        "RefBModelMeta.java" };

    private final File generatedSourceDir = new File("org/slim3/test/meta");

    /**
     * Test for generate Meta class of {@link BasicModel}.
     * 
     * @throws Exception
     */
    @Test
    public void testForBasic() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(BasicModel.class);

        compile();
        {
            String sourceName = "org.slim3.test.meta.BasicModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
        }
        assertThat(getCompiledResult(), is(true));
    }
    @Test
    public void testForListnerModel() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(ListenerModel.class);

        compile();
        {
            String sourceName = "org.slim3.test.meta.ListenerModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
            System.out.println(source);
        }
        assertThat(getCompiledResult(), is(true));
    }

    /**
     * Test for generate Meta class of {@link AttributeSampleModel}.
     * 
     * @throws Exception
     */
    @Test
    public void testForAttributePrimitive() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(AttributeSampleModel.class);

        compile();
        {
            String sourceName = "org.slim3.test.meta.AttributeSampleModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
        }
        assertThat(getCompiledResult(), is(true));
    }

    /**
     * Test for generate Meta class of {@link AttributeSampleModel}.
     * 
     * @throws Exception
     */
    @Test
    public void testForAttributeNotSupported() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(AttributeNotSupportedSampleModel.class);

        compile();
        {
            String sourceName =
                "org.slim3.test.meta.AttributeNotSupportedSampleModelMeta";
            try {
                @SuppressWarnings("unused")
                String source = getGeneratedSource(sourceName);
                fail();
            } catch (SourceNotGeneratedException e) {
            }
        }
    }

    /**
     * Test for generate Meta class of {@link RefAModel} and {@link RefBModel}.
     * 
     * @throws Exception
     */
    @Test
    public void testForRefs() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(RefAModel.class);
        addCompilationUnit(RefBModel.class);

        compile();
        {
            String sourceName = "org.slim3.test.meta.RefAModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
        }
        {
            String sourceName = "org.slim3.test.meta.RefBModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
        }
    }

    /**
     * Test for generate Meta class of {@link AttributeParameterSampleModel}.
     * 
     * @throws Exception
     */
    @Test
    public void testForAttributeParametered() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(AttributeParameterSampleModel.class);

        compile();
        {
            String sourceName =
                "org.slim3.test.meta.AttributeParameterSampleModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
        }
    }

    /**
     * Test for generate Meta class of {@link ImplementComparableModel}.
     * 
     * @throws Exception
     */
    @Test
    public void testForHasInterfaceModel() throws Exception {
        ModelProcessor processor = new ModelProcessor();
        addProcessor(processor);

        addCompilationUnit(ImplementComparableModel.class);

        compile();
        {
            String sourceName =
                "org.slim3.test.meta.ImplementComparableModelMeta";
            @SuppressWarnings("unused")
            String source = getGeneratedSource(sourceName);
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        addSourcePath("src/test/java");
        addOption("-proc:only");
        setCharset("utf-8");
        prepareGeneratedSourcePlaceholders();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            deleteGeneratedSourcePlaceholders();
        } finally {
            super.tearDown();
        }
    }

    private void prepareGeneratedSourcePlaceholders() throws IOException {
        if (!generatedSourceDir.exists() && !generatedSourceDir.mkdirs()) {
            throw new IOException("Could not create " + generatedSourceDir);
        }
        for (String source : GENERATED_SOURCES) {
            File file = new File(generatedSourceDir, source);
            if (file.exists() && !file.delete()) {
                throw new IOException("Could not delete " + file);
            }
            if (!file.createNewFile()) {
                throw new IOException("Could not create " + file);
            }
        }
    }

    private void deleteGeneratedSourcePlaceholders() throws IOException {
        for (String source : GENERATED_SOURCES) {
            deleteIfExists(new File(generatedSourceDir, source));
        }
        deleteIfEmpty(generatedSourceDir);
        deleteIfEmpty(new File("org/slim3/test"));
        deleteIfEmpty(new File("org/slim3"));
        deleteIfEmpty(new File("org"));
    }

    private void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("Could not delete " + file);
        }
    }

    private void deleteIfEmpty(File dir) throws IOException {
        String[] files = dir.list();
        if (files != null && files.length == 0 && !dir.delete()) {
            throw new IOException("Could not delete " + dir);
        }
    }
}
